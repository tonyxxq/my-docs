/*
 * FeatureTracker.cpp
 *
 *  Created on: 2018��2��28��
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
 * ��������ͼ��������㣬ע���������Ե�һ��ͼ��Ϊ׼�����ݵ�һ��ͼ����������ҵڶ���ͼ���������
 */
void FeatureTracker::process(Mat &frame, Mat &output) {
	cvtColor(frame, gray, CV_BGR2GRAY);
	frame.copyTo(output);
	// ���ڵ�һ��ͼ��û��ǰ��ͼ��
	if (gray_prev.empty()) {
		gray.copyTo(gray_prev);
	}
	// �����Ҫ����µ�������,�����������������ʱ��ͻ��ٴμ��
	if (addNewPoints()) {
		// ���м�⵱ǰͼ���µ�������
		detectFeaturePoints();
		// ��Ӽ�⵽�������㵽����������
		points[0].insert(points[0].end(), features.begin(), features.end());
	}
	// ����������
	calcOpticalFlowPyrLK(
			gray_prev, gray,
			points[0], // ͼ1���������
			points[1], // ͼ2���������
			status, // ���ٳɹ�
			err  // ����ʧ��
			);
	// �������и��ٵĵ����ɸѡ,���жϸõ��Ƿ���ٳɹ�
	int k = 0;
	for (int i = 0; i < points[1].size(); i++) {
		if (acceptTrackedPonit(i)) {
			points[1][k] = points[1][i];
			k++;
		}
	}
	// ȥ�����ɹ��ĵ�
	points[1].resize(k);
	// ������ܵĸ��ٵ�
	handleTrackedPonints(frame, output);
	// ��ǰ֡��Ϊ��һ֡
	swap(points[1], points[0]);
	swap(gray_prev, gray);
}

/**
 * ����ǰ���ٵ�
 */
void FeatureTracker::handleTrackedPonints(Mat &frame, Mat &output) {
	// ����ֱ�ߺ�Բ
	for (int i = 0; i < points[1].size(); i++) {
		line(output, points[0][i], points[1][i], Scalar(255, 255, 255));
		circle(output, points[0][i], 3, Scalar(255, 255, 255), -1);
		circle(output, points[1][i], 3, Scalar(255, 255, 255), -1);
	}
}

/**
 * ������Щ��Ӧ�ø���
 */
bool FeatureTracker::acceptTrackedPonit(int i) {
	return status[i] && (abs(points[1][i].x - points[0][i].x) + abs(points[1][i].y - points[0][i].y) > 2);
}

/**
 * ���������
 */
void FeatureTracker::detectFeaturePoints() {
	goodFeaturesToTrack(gray_prev, features, max_count, q_level, min_dist);
}

/**
 * �ж��Ƿ���Ҫ����µ�������
 */
bool FeatureTracker::addNewPoints() {
	return points[0].size() < 10;
}

