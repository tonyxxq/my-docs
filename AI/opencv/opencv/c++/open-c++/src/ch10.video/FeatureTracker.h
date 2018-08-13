/*
 * FeatureTracker.h
 *
 *  Created on: 2018年2月28日
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
	// 当前图像
	Mat gray;
	// 之前的图像
	Mat gray_prev;
	// 两幅图像间跟踪的特征点
	vector<Point2f> points[2];
	// 检测到的特征
	vector<Point2f> features;
	// 需要跟踪的最大特征数目
	int max_count;
	// 特征检测中的质量等级
	double q_level;
	// 两点之间的最小距离
	double min_dist;
	// 检测到的特征状态
    vector<uchar> status;
	// 跟踪中的错误
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
