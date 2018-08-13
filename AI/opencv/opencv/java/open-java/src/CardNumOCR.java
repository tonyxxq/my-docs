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

	// 必须添加这一行，取到opencv的dll文件
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		Mat mat = Highgui.imread("./src.jpg");
		Mat grayMat = new Mat();
		// 转化为灰度图像
		Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY);
		// 二值化处理
		Mat binaryMat = new Mat(grayMat.height(), grayMat.width(), CvType.CV_8UC1);
		Imgproc.threshold(grayMat, binaryMat, 60, 255, Imgproc.THRESH_BINARY);
		// 图像腐蚀
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
		Mat morphMat = new Mat(grayMat.height(), grayMat.width(), CvType.CV_8UC1);
		Imgproc.erode(binaryMat, morphMat, element);
		// 找到图片的上界和下界
		int[] boundry = getBoundary(morphMat, 270000);
		// 截取图片
		cutImg(morphMat, boundry);
		Rect roi = new Rect(0, boundry[0] - 40, morphMat.width(), boundry[1] - boundry[0] + 80);
		Mat dest = new Mat(morphMat, roi);
		// 保存图片
		Highgui.imwrite("./dest.jpg", dest);
		// 显示图片
		ImageGUI ig = new ImageGUI(dest, "MAT");
		ig.imshow();
		//ig.waitKey(0);
		// 使用OCR识别图片内容
		String result = RecognizeCardNum(new File("./dest.jpg"));
		System.out.println(result);
	}

	/**
	 * 识别图片内容
	 * @param img
	 * @return
	 */
	public static String RecognizeCardNum(File img) {
		ITesseract instance = new Tesseract();
		instance.setDatapath(".\\tessdata");
		// 默认是英文（识别字母和数字），如果要识别中文(数字 + 中文），需要制定语言包
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
	 * 获取图片的上边界和下边界
	 * 
	 * @param mat（二值图像）
	 * @param threshold（阈值）
	 * @return Long[0]:上边界 Long[0]:下边界
	 */
	public static int[] getBoundary(Mat mat, double threshold) {
		// 查找上边界
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
		// 查找下边界
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
	 * BufferedImage转换为mat
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
	 * Mat转换成BufferedImage
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
