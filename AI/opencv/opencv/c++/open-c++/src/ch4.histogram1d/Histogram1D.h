/*
 * Histogram1D.h
 *
 *  Created on: 2018��2��7��
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
	// �������,bin
	int histSize[1];
	// ���ص�������Сֵ
	float hranges[2];
	// ֻ��һ��ͨ��
	const float * ranges[1];
	// ��ʹ����һ��ͨ��
	int channels[1];

public:
	Histogram1D();
	virtual ~Histogram1D();
	MatND getHistogram(const Mat &img);
	MatND getHistogramImg(const cv::Mat &img);
};

#endif /* CH4_HISTOGRAM1D_HISTOGRAM1D_H_ */
