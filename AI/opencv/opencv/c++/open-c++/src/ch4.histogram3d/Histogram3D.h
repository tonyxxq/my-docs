/*
 * Histogram1D.h
 *
 *  Created on: 2018年2月7日
 *      Author: Administrator
 */
#ifndef CH4_HISTOGRAM1D_HISTOGRAM1D_H_
#define CH4_HISTOGRAM1D_HISTOGRAM1D_H_

#include <core/core.hpp>

using namespace cv;
class Histogram3D {
private:
	// bin的大小
	int histSize[3] = { 256, 256, 256 };
	// 每一个通道的最大和最小值
	float range[2] = { 0, 255 };
	const float * ranges[3] = { range, range, range };
	// 仅使用了一个通道
	int channels[3] = { 0, 1, 2 };

public:
	Histogram3D();
	virtual ~Histogram3D();
	MatND getHistogram(const Mat &img);
	MatND getHistogramImg(const cv::Mat &img);
};

#endif /* CH4_HISTOGRAM1D_HISTOGRAM1D_H_ */
