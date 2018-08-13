/*
 * FeatureTracker.h
 *
 *  Created on: 2018��2��28��
 *      Author: Administrator
 */

#ifndef CH10_VIDEO_FEATURETRACKER_H_
#define CH10_VIDEO_FEATURETRACKER_H_

#include <core/core.hpp>
#include <core/types_c.h>
#include <vector>

#include "FrameProcessor.h"

using namespace cv;

class FeatureTracker : public FrameProcessor{
private:
	// ��ǰͼ��
	Mat gray;
	// ֮ǰ��ͼ��
	Mat gray_prev;
	// ����ͼ�����ٵ�������
	vector<Point2f> points[2];
	// ��⵽������
	vector<Point2f> features;
	// ��Ҫ���ٵ����������Ŀ
	int max_count;
	// ��������е������ȼ�
	double q_level;
	// ����֮�����С����
	double min_dist;
	// ��⵽������״̬
    vector<uchar> status;
	// �����еĴ���
    vector<float> err;
public:
	FeatureTracker();
	void process(Mat &frame, Mat &output);
	virtual ~FeatureTracker();
	void detectFeaturePoints();
	bool addNewPoints();
	bool acceptTrackedPonit(int i);
	void handleTrackedPonints(Mat &frame, Mat &output);
};

#endif /* CH10_VIDEO_FEATURETRACKER_H_ */
