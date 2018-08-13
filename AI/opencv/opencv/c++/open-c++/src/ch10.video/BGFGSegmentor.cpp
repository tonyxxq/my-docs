/*
 * BGFGSegmentor.cpp
 *
 *  Created on: 2018年2月28日
 *      Author: Administrator
 */

#include "BGFGSegmentor.h"

#include <core/core.hpp>
#include <core/mat.hpp>
#include <core/types_c.h>
#include <imgproc/imgproc.hpp>
#include <imgproc/types_c.h>

BGFGSegmentor::BGFGSegmentor() :
		learningrate(0.01), thres(60) {
	// TODO Auto-generated constructor stub

}

BGFGSegmentor::~BGFGSegmentor() {
	// TODO Auto-generated destructor stub
}

void BGFGSegmentor::process(Mat & frame, Mat & output) {
	cvtColor(frame, gray, CV_BGR2GRAY);
	if(background.empty()){
		gray.convertTo(background, CV_32F);
	}
	// 因为北京图像是浮点值，需要转化为整数值
	background.convertTo(backimg, CV_8U);
	// 计算两幅图像的差异
	absdiff(backimg, gray, foreground);
    // 二值化前景图像
	threshold(foreground, output, thres, 255, CV_THRESH_BINARY_INV);
	// 使用滑动平均计算背景图像，使用output作为掩码是为了不更新被认为是前景的像素
	accumulateWeighted(gray, background, learningrate, output);
}

