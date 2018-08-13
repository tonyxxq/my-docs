/*
 * Histogram1D.cpp
 *
 *  Created on: 2018��2��7��
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
			&img, // ����ͼ�񣬶���ͼ����������ʽ
			1, // ����ͼ�������
			channels, // ÿһ��ͼ���ͨ������
			Mat(), // ��ʹ������
			hist, // ���ص�ֱ��ͼ
			3, // ֱ��ͼ��ά��
			histSize, // ���ÿ��ά�ȵ�ֱ��ͼ�ߴ������
			ranges // ÿһά��ֵ��ȡֵ��Χ
		    );
	return hist;
}

cv::MatND Histogram3D::getHistogramImg(const cv::Mat &img) {
	MatND outputHist = getHistogram(img);
	// ����һ��ͼƬ���趨��ߣ����ñ���ɫΪ��ɫ����ͼƬ��Ϊ���յ�ֱ��ͼ
	//cv::Mat histPic(histSize[0], hranges[1], CV_8U, cv::Scalar(255));
	// �ҵ����ֵ����Сֵ
	double maxValue = 0;
	double minValue = 0;
	// �ҵ�ͼ������ֵ����Сֵ
	cv::minMaxLoc(img, &minValue, &maxValue, NULL, NULL);
	// ����������ű��� value/maxvalue*hranges[0]
	//float rate = (hranges[1] / maxValue) * 0.1;
	// ����ÿһ��bin
	for (int i = 0; i < histSize[0]; i++) {
		//float value = outputHist.at<float>(i);
		// ��������Ͻǿ�ʼ�����
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

