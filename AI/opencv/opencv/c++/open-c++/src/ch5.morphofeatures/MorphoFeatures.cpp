/*
 * Histogram1D.cpp
 *
 *  Created on: 2018��2��7��
 *      Author: Administrator
 */

#include "MorphoFeatures.h"

#include <core/mat.hpp>
#include <imgproc/imgproc.hpp>


using namespace cv;

cv::Mat MorphoFeatures::getEdges(cv::Mat &image) {
	cv::Mat result;
	cv::morphologyEx(image, result, MORPH_GRADIENT, cv::Mat());
	// ��ͼ����ж�ֵ��
	cv::threshold(result, result, threshold, 255, THRESH_BINARY);
	return result;
}

MorphoFeatures::MorphoFeatures(int thresh) {
	threshold = thresh;
	cv::Mat cross;
	cv::Mat diamond;
	cv::Mat square;
	cv::Mat x;
}

MorphoFeatures::~MorphoFeatures() {
	// TODO Auto-generated destructor stub
}

