/*
 * BGFGSegmentor.cpp
 *
 *  Created on: 2018��2��28��
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
	// ��Ϊ����ͼ���Ǹ���ֵ����Ҫת��Ϊ����ֵ
	background.convertTo(backimg, CV_8U);
	// ��������ͼ��Ĳ���
	absdiff(backimg, gray, foreground);
    // ��ֵ��ǰ��ͼ��
	threshold(foreground, output, thres, 255, CV_THRESH_BINARY_INV);
	// ʹ�û���ƽ�����㱳��ͼ��ʹ��output��Ϊ������Ϊ�˲����±���Ϊ��ǰ��������
	accumulateWeighted(gray, background, learningrate, output);
}

