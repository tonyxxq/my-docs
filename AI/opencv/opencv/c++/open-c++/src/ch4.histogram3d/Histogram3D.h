/*
 * Histogram1D.h
 *
 *  Created on: 2018��2��7��
 *      Author: Administrator
 */
#ifndef CH4_HISTOGRAM1D_HISTOGRAM1D_H_
#define CH4_HISTOGRAM1D_HISTOGRAM1D_H_

#include <core/core.hpp>

using namespace cv;
class Histogram3D {
private:
	// bin�Ĵ�С
	int histSize[3] = { 256, 256, 256 };
	// ÿһ��ͨ����������Сֵ
	float range[2] = { 0, 255 };
	const float * ranges[3] = { range, range, range };
	// ��ʹ����һ��ͨ��
	int channels[3] = { 0, 1, 2 };

public:
	Histogram3D();
	virtual ~Histogram3D();
	MatND getHistogram(const Mat &img);
	MatND getHistogramImg(const cv::Mat &img);
};

#endif /* CH4_HISTOGRAM1D_HISTOGRAM1D_H_ */
