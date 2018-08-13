/*
 * VideoProcessor.h
 *
 *  Created on: 2018��2��27��
 *      Author: Administrator
 */

#ifndef CH10_VIDEO_VIDEOPROCESSOR_H_
#define CH10_VIDEO_VIDEOPROCESSOR_H_

#include <core/core.hpp>
#include <highgui/highgui.hpp>
#include <string>

class FrameProcessor;

using namespace cv;

class VideoProcessor {

private:
	// ��Ƶ��׽����
	VideoCapture capture;
	// ÿ֡���õĻص�����
	void (*process)(Mat &, Mat &);
	// ÿ֡���õĻص�ʵ��
	FrameProcessor * frameProcessor;
	// ȷ���Ƿ���ûص�����
	bool callIt;
	// ���봰�ڵ�����
	string windowNameInput;
	// ������ڵ�����
	string windoNameOutput;
	// �ӳ�
	int delay;
	// �Ѵ����֡��
	long fnumber;
	// �ڸ�ֹ֡ͣ
	long frameToStop;
	// �Ƿ�ֹͣ����
	bool stop;
	/*******д����Ƶ�������*********/
	// ��Ƶд����
	VideoWriter writer;
	// ����ļ�����
	string outputFile;
	// ���ͼ��ĵ�ǰ����
	int currentIndex;
	// ���ͼ�������е�����λ��
	int digits;
	// ���ͼ�����չ��
    string extensions;
public:
	VideoProcessor();
	// ���ô�����
	void setFrameProcessor(void (*frameProcessingCallback)(Mat &, Mat &));
	void setFrameProcessor(FrameProcessor * frameProcessingCallback);
	virtual ~VideoProcessor();
	// ������������
	bool setInput(string filename);
	// ����������������
	void displayInput(String input);
	void displayOutput(String output);
	// ������ʾ������֡
	void dontDisplay(String output);
	void run();
	bool isOpened();
	int getFrameNum();
	void setDelay(int delay);
	long getRate();
	int getCodec(char cc[4]);
	Size getFramSize();
	void setExtension(string extension);
	void setOutputFile(const string &filename);
	void setDigits(int digits);
	/*******д����Ƶ�������*********/
	bool setOutput(const string &filename, int codec = 0, double framerate = 0.0, bool iscolor = true);
	bool writeNextFrame(Mat &frame);
};
#endif /* CH10_VIDEO_VIDEOPROCESSOR_H_ */
