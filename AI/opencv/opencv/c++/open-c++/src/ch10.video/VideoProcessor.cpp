/*
 * VideoProcessor.cpp
 *
 *  Created on: 2018年2月27日
 *      Author: Administrator
 */

#include "VideoProcessor.h"

#include <core/mat.hpp>
#include <core/operations.hpp>
#include <highgui/highgui_c.h>
#include <iomanip>
#include <iostream>
#include <sstream>
#include <iostream>

#include "FrameProcessor.h"

using namespace std;

VideoProcessor::VideoProcessor() :
		callIt(true), delay(20), fnumber(0), frameToStop(-1), stop(false) {
}

VideoProcessor::~VideoProcessor() {
	// TODO Auto-generated destructor stub
}

void VideoProcessor::setFrameProcessor(void (*frameProcessingCallback)(Mat &, Mat &)) {
	frameProcessor= 0;
	process = frameProcessingCallback;
}

void VideoProcessor::setFrameProcessor(FrameProcessor * frameProcessingCallback) {
	process = 0;
	frameProcessor = frameProcessingCallback;
}

bool VideoProcessor::setInput(string filename) {
	fnumber = 1;
	// 释放之前的资源
	capture.release();
	return capture.open(filename);
}

void VideoProcessor::displayInput(String input) {
	windowNameInput = input;
	namedWindow(input);
}

void VideoProcessor::displayOutput(String output) {
	windoNameOutput = output;
	namedWindow(output);
}

void VideoProcessor::dontDisplay(String output) {
	destroyWindow(windoNameOutput);
	destroyWindow(windowNameInput);
	windoNameOutput.clear();
	windowNameInput.clear();
}

bool VideoProcessor::isOpened() {
	return capture.isOpened();
}

void VideoProcessor::run() {
	// 当前帧
	Mat frame;
	// 输出帧
	Mat output;
	if (!isOpened()) {
		return;
	}
	stop = false;
	while (!stop) {
		if (!capture.read(frame)) {
			break;
		}
		imshow(windowNameInput, frame);
		// 图像处理
		if (callIt) {
			if (process) {
				process(frame, output);
			} else {
				frameProcessor->process(frame, output);
			}
			fnumber++;
		} else {
			output = frame;
		}
		imshow(windoNameOutput, output);
		/**********输出图像序列***********/
		writeNextFrame(frame);
		// 判断是否触发了键盘
		if (delay > 0 && waitKey(delay) >= 0) {
			stop = true;
		}
		// 判断是否已经到结束帧
		if (frameToStop >= 0 and getFrameNum() == frameToStop) {
			stop = true;
		}
	}
}

/**
 * 获取当前帧
 */
int VideoProcessor::getFrameNum() {
	return static_cast<long>(capture.get(CV_CAP_PROP_POS_FRAMES));
}

/**
 * 获取帧率
 */
long VideoProcessor::getRate() {
	return static_cast<long>(capture.get(CV_CAP_PROP_FPS));
}

/**
 * 获取编码格式
 */
int VideoProcessor::getCodec(char codec[4]) {
	union {
		int value;
		char code[4];
	} returned;
	returned.value = static_cast<int>(capture.get(CV_CAP_PROP_FOURCC));
	codec[0] = returned.code[0];
	codec[1] = returned.code[1];
	codec[2] = returned.code[2];
	codec[3] = returned.code[3];
	return returned.value;
}

void VideoProcessor::setDelay(int d) {
	delay = d;
}

bool VideoProcessor::setOutput(const string &filename, int codec, double framerate, bool isColor) {
	outputFile = filename;
	// extensions.clear();
	if (framerate == 0) {
		framerate = getRate();
	}
	char cc[4];
	if (codec == 0) {
		codec = getCodec(cc);
	}
	return writer.open(outputFile, codec, framerate, getFramSize(), isColor);
}

void VideoProcessor::setOutputFile(const string &filename) {
	outputFile = filename;
}

void VideoProcessor::setExtension(string extension) {
	extensions = extension;
}

void VideoProcessor::setDigits(int d) {
	digits = d;
}

Size VideoProcessor::getFramSize() {
	int frameHeight = static_cast<int>(capture.get(CV_CAP_PROP_FRAME_HEIGHT));
	int frameWidth = static_cast<int>(capture.get(CV_CAP_PROP_FRAME_WIDTH));
	return Size(frameWidth, frameHeight);
}

bool VideoProcessor::writeNextFrame(Mat &frame) {
	if (extensions.length()) {
		stringstream ss;
		// 下边使用0填充currentIndex,使得总长度为digits位
		// 默认setw（不使用setfill）使用空格填充
		// 不能有空值
		ss << outputFile << std::setfill('0') << std::setw(digits) << currentIndex++ << extensions;
		imwrite(ss.str(), frame);
	} else {
		writer.write(frame);
	}
	return true;
}

