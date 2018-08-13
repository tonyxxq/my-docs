/*
 * VideoProcessor.h
 *
 *  Created on: 2018年2月27日
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
	// 视频捕捉对象
	VideoCapture capture;
	// 每帧调用的回调函数
	void (*process)(Mat &, Mat &);
	// 每帧调用的回调实例
	FrameProcessor * frameProcessor;
	// 确认是否调用回调函数
	bool callIt;
	// 输入窗口的名字
	string windowNameInput;
	// 输出窗口的名字
	string windoNameOutput;
	// 延迟
	int delay;
	// 已处理的帧数
	long fnumber;
	// 在该帧停止
	long frameToStop;
	// 是否停止处理
	bool stop;
	/*******写入视频序列添加*********/
	// 视频写对象
	VideoWriter writer;
	// 输出文件名称
	string outputFile;
	// 输出图像的当前索引
	int currentIndex;
	// 输出图像名称中的数字位数
	int digits;
	// 输出图像的扩展名
    string extensions;
public:
	VideoProcessor();
	// 设置处理函数
	void setFrameProcessor(void (*frameProcessingCallback)(Mat &, Mat &));
	void setFrameProcessor(FrameProcessor * frameProcessingCallback);
	virtual ~VideoProcessor();
	// 设置输入数据
	bool setInput(string filename);
	// 创建输入和输出窗口
	void displayInput(String input);
	void displayOutput(String output);
	// 不再显示处理后的帧
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
	/*******写入视频序列添加*********/
	bool setOutput(const string &filename, int codec = 0, double framerate = 0.0, bool iscolor = true);
	bool writeNextFrame(Mat &frame);
};
#endif /* CH10_VIDEO_VIDEOPROCESSOR_H_ */
