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
class MorphoFeatures {
private:
	// �������ɶ�ֵͼ�����ֵ
	int threshold;
	cv::Mat cross;
	cv::Mat diamond;
	cv::Mat square;
	cv::Mat x;

public:
	MorphoFeatures(int thresh);
	MorphoFeatures();
	virtual ~MorphoFeatures();
	cv::Mat getEdges(cv::Mat &image);
};

#endif /* CH4_HISTOGRAM1D_HISTOGRAM1D_H_ */
