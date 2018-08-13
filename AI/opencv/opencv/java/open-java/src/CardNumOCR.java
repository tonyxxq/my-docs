import java.awt.image.BufferedImage;
import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class CardNumOCR {

	// ���������һ�У�ȡ��opencv��dll�ļ�
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		Mat mat = Highgui.imread("./src.jpg");
		Mat grayMat = new Mat();
		// ת��Ϊ�Ҷ�ͼ��
		Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY);
		// ��ֵ������
		Mat binaryMat = new Mat(grayMat.height(), grayMat.width(), CvType.CV_8UC1);
		Imgproc.threshold(grayMat, binaryMat, 60, 255, Imgproc.THRESH_BINARY);
		// ͼ��ʴ
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
		Mat morphMat = new Mat(grayMat.height(), grayMat.width(), CvType.CV_8UC1);
		Imgproc.erode(binaryMat, morphMat, element);
		// �ҵ�ͼƬ���Ͻ���½�
		int[] boundry = getBoundary(morphMat, 270000);
		// ��ȡͼƬ
		cutImg(morphMat, boundry);
		Rect roi = new Rect(0, boundry[0] - 40, morphMat.width(), boundry[1] - boundry[0] + 80);
		Mat dest = new Mat(morphMat, roi);
		// ����ͼƬ
		Highgui.imwrite("./dest.jpg", dest);
		// ��ʾͼƬ
		ImageGUI ig = new ImageGUI(dest, "MAT");
		ig.imshow();
		//ig.waitKey(0);
		// ʹ��OCRʶ��ͼƬ����
		String result = RecognizeCardNum(new File("./dest.jpg"));
		System.out.println(result);
	}

	/**
	 * ʶ��ͼƬ����
	 * @param img
	 * @return
	 */
	public static String RecognizeCardNum(File img) {
		ITesseract instance = new Tesseract();
		instance.setDatapath(".\\tessdata");
		// Ĭ����Ӣ�ģ�ʶ����ĸ�����֣������Ҫʶ������(���� + ���ģ�����Ҫ�ƶ����԰�
		instance.setLanguage("chi_sim");
		try {
			return instance.doOCR(img);
		} catch (TesseractException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void cutImg(Mat mat, int[] boundry) {
		for (int i = 0; i < mat.rows(); i++) {
			if (i < boundry[0] || i > boundry[1]) {
				for (int j = 0; j < mat.cols(); j++) {
					mat.put(i, j, 255);
				}
			}
		}
	}

	/**
	 * ��ȡͼƬ���ϱ߽���±߽�
	 * 
	 * @param mat����ֵͼ��
	 * @param threshold����ֵ��
	 * @return Long[0]:�ϱ߽� Long[0]:�±߽�
	 */
	public static int[] getBoundary(Mat mat, double threshold) {
		// �����ϱ߽�
		int i = 120;
		int[] boundry = new int[2];
		for (; i < mat.rows(); i++) {
			double totalValue = 0d;
			for (int j = 0; j < mat.cols(); j++) {
				totalValue += mat.get(i, j)[0];
			}
			if (totalValue < threshold) {
				boundry[0] = i;
				break;
			}
			totalValue = 0d;
		}
		// �����±߽�
		for (; i < mat.rows(); i++) {
			double totalValue = 0d;
			for (int j = 0; j < mat.cols(); j++) {
				totalValue += mat.get(i, j)[0];
			}
			if (totalValue > threshold) {
				boundry[1] = i;
				break;
			}
			totalValue = 0d;
		}
		return boundry;
	}

	/**
	 * BufferedImageת��Ϊmat
	 * 
	 * @param img
	 * @param type
	 * @return
	 */
	public static Mat bufferedImgCvtMat(BufferedImage img, int type) {
		Mat srcMat = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
		return srcMat;
	}

	/**
	 * Matת����BufferedImage
	 * 
	 * @param mat
	 * @param img
	 * @param type
	 */
	public static BufferedImage matCvtBufferedImg(Mat mat, int type) {
		byte[] data = new byte[mat.rows() * mat.cols() * (int) (mat.elemSize())];
		mat.get(0, 0, data);
		BufferedImage img = new BufferedImage(mat.cols(), mat.rows(), type);
		img.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
		return img;
	}
}
