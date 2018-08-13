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
class MorphoFeatures {
private:
	// 用于生成二值图像的阈值
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
