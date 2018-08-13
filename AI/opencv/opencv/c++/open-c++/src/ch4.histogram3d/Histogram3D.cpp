/*
 * Histogram1D.cpp
 *
 *  Created on: 2018年2月7日
 *      Author: Administrator
 */

#include "Histogram3D.h"

#include <core/mat.hpp>
#include <core/operations.hpp>
#include <core/types_c.h>
#include <imgproc/imgproc.hpp>
#include <stddef.h>

using namespace cv;

MatND Histogram3D::getHistogram(const Mat &img) {
	cv::MatND hist;
	cv::calcHist(
			&img, // 传入图像，多张图像以数组形式
			1, // 传入图像的数量
			channels, // 每一张图像的通道数量
			Mat(), // 不使用掩码
			hist, // 返回的直方图
			3, // 直方图的维度
			histSize, // 存放每个维度的直方图尺寸的数组
			ranges // 每一维数值的取值范围
		    );
	return hist;
}

cv::MatND Histogram3D::getHistogramImg(const cv::Mat &img) {
	MatND outputHist = getHistogram(img);
	// 创建一张图片，设定宽高，设置背景色为白色，该图片即为最终的直方图
	//cv::Mat histPic(histSize[0], hranges[1], CV_8U, cv::Scalar(255));
	// 找到最大值和最小值
	double maxValue = 0;
	double minValue = 0;
	// 找到图像的最大值和最小值
	cv::minMaxLoc(img, &minValue, &maxValue, NULL, NULL);
	// 纵坐标的缩放比例 value/maxvalue*hranges[0]
	//float rate = (hranges[1] / maxValue) * 0.1;
	// 遍历每一个bin
	for (int i = 0; i < histSize[0]; i++) {
		//float value = outputHist.at<float>(i);
		// 坐标从左上角开始好理解
		/*cv::line(histPic, cv::Point(i, hranges[1]), cv::Point(i, hranges[1] - value*rate),
				cv::Scalar(0));*/
	}
	return outputHist;
}

Histogram3D::Histogram3D(){

}


Histogram3D::~Histogram3D() {
	// TODO Auto-generated destructor stub
}

