/*
 * Histogram1D.h
 *
 *  Created on: 2018年2月7日
 *      Author: Administrator
 */
#ifndef CH4_HISTOGRAM1D_HISTOGRAM1D_H_
#define CH4_HISTOGRAM1D_HISTOGRAM1D_H_

#include <core/core.hpp>
#include <core/mat.hpp>
#include <imgproc/imgproc.hpp>

using namespace cv;
class Histogram1D {
private:
	// 项的数量,bin
	int histSize[1];
	// 像素的最大和最小值
	float hranges[2];
	// 只有一个通道
	const float * ranges[1];
	// 仅使用了一个通道
	int channels[1];

public:
	Histogram1D();
	virtual ~Histogram1D();
	MatND getHistogram(const Mat &img);
	MatND getHistogramImg(const cv::Mat &img);
};

#endif /* CH4_HISTOGRAM1D_HISTOGRAM1D_H_ */
