package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.util.List;

import it.sauronsoftware.jave.Encoder;

/**
 * 
 * @Author: HONGLINCHEN
 * @Description:��ȡ��Ƶ��ߴ�Сʱ��
 * @Date: 2017-9-29 14:02
 */
public class test {

	public static void main(String[] args) {
		getVideoInfo();
		String ffmpeg_path = "C:\\ffmpeg.exe";
		String veido_path = "C:\\1.mp4";
		processImg(veido_path, ffmpeg_path);
	}
	
	public static void getVideoInfo(){
		File source = new File("c:\\1.mp4");
		Encoder encoder = new Encoder();
		FileChannel fc = null;
		String size = "";
		try {
			it.sauronsoftware.jave.MultimediaInfo m = encoder.getInfo(source);
			long ls = m.getDuration();
			System.out.println("����Ƶʱ��Ϊ:" + ls / 60000 + "��" + (ls) / 1000 + "�룡");
			System.out.println("����Ƶ�߶�Ϊ:" + m.getVideo().getSize().getHeight());
			System.out.println("����Ƶ���Ϊ:" + m.getVideo().getSize().getWidth());
			System.out.println("����Ƶ��ʽΪ:" + m.getFormat());
			FileInputStream fis = new FileInputStream(source);
			fc = fis.getChannel();
			BigDecimal fileSize = new BigDecimal(fc.size());
			size = fileSize.divide(new BigDecimal(1048576), 2, RoundingMode.HALF_UP) + "MB";
			System.out.println("����Ƶ��СΪ" + size);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != fc) {
				try {
					fc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static boolean processImg(String veido_path, String ffmpeg_path) {
		File file = new File(veido_path);
		if (!file.exists()) {
			System.err.println("·��[" + veido_path + "]��Ӧ����Ƶ�ļ�������!");
			return false;
		}
		List<String> commands = new java.util.ArrayList<String>();
		commands.add(ffmpeg_path);
		commands.add("-i");
		commands.add(veido_path);
		//commands.add("-y");
		commands.add("-f");
		commands.add("avi");
		commands.add("-ss");// ������������ý�ȡ��Ƶ������ʱ�Ļ���
		commands.add("0");
		commands.add("-t");// ��ȡ��ʱ�䳤��
		commands.add("6");
		commands.add("-s");// ��X��
		commands.add("540x960");
		commands.add(veido_path.substring(0, veido_path.lastIndexOf(".")).replaceFirst("vedio", "file") + ".avi");
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commands);
			builder.start();
			System.out.println("��ȡ�ɹ�");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
