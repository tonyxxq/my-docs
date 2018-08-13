/*
 * FeatureTracker.cpp
 *
 *  Created on: 2018年2月28日
 *      Author: Administrator
 */

#include "FeatureTracker.h"

#include <core/mat.hpp>
#include <core/operations.hpp>
#include <imgproc/imgproc.hpp>
#include <imgproc/types_c.h>
#include <video/tracking.hpp>
#include <cstdlib>
#include <iostream>

FeatureTracker::FeatureTracker() :
		max_count(500), q_level(0.01), min_dist(10.) {
}

FeatureTracker::~FeatureTracker() {
	// TODO Auto-generated destructor stub
}

/**
 * 查找两幅图像的特征点，注意特征点以第一副图像为准，根据第一幅图像的特征点找第二幅图像的特征点
 */
void FeatureTracker::process(Mat &frame, Mat &output) {
	cvtColor(frame, gray, CV_BGR2GRAY);
	frame.copyTo(output);
	// 对于第一幅图像没有前驱图像
	if (gray_prev.empty()) {
		gray.copyTo(gray_prev);
	}
	// 如果需要添加新的特征点,当特征点个数不够的时候就会再次检测
	if (addNewPoints()) {
		// 进行检测当前图像新的特征点
		detectFeaturePoints();
		// 添加检测到的特征点到跟踪特征中
		points[0].insert(points[0].end(), features.begin(), features.end());
	}
	// 跟踪特征点
	calcOpticalFlowPyrLK(
			gray_prev, gray,
			points[0], // 图1输出点坐标
			points[1], // 图2输入点坐标
			status, // 跟踪成功
			err  // 跟踪失败
			);
	// 遍历所有跟踪的点进行筛选,并判断该点是否跟踪成功
	int k = 0;
	for (int i = 0; i < points[1].size(); i++) {
		if (acceptTrackedPonit(i)) {
			points[1][k] = points[1][i];
			k++;
		}
	}
	// 去除不成功的点
	points[1].resize(k);
	// 处理接受的跟踪点
	handleTrackedPonints(frame, output);
	// 当前帧变为上一帧
	swap(points[1], points[0]);
	swap(gray_prev, gray);
}

/**
 * 处理当前跟踪点
 */
void FeatureTracker::handleTrackedPonints(Mat &frame, Mat &output) {
	// 绘制直线和圆
	for (int i = 0; i < points[1].size(); i++) {
		line(output, points[0][i], points[1][i], Scalar(255, 255, 255));
		circle(output, points[0][i], 3, Scalar(255, 255, 255), -1);
		circle(output, points[1][i], 3, Scalar(255, 255, 255), -1);
	}
}

/**
 * 决定哪些点应该跟踪
 */
bool FeatureTracker::acceptTrackedPonit(int i) {
	return status[i] && (abs(points[1][i].x - points[0][i].x) + abs(points[1][i].y - points[0][i].y) > 2);
}

/**
 * 检测特征点
 */
void FeatureTracker::detectFeaturePoints() {
	goodFeaturesToTrack(gray_prev, features, max_count, q_level, min_dist);
}

/**
 * 判断是否需要添加新的特征点
 */
bool FeatureTracker::addNewPoints() {
	return points[0].size() < 10;
}

