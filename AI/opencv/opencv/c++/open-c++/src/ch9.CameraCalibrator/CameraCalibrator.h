/*
 * CameraCalibrator.h
 *
 *  Created on: 2018年3月1日
 *      Author: Administrator
 */

#ifndef CH9_CAMERACALIBRATOR_CAMERACALIBRATOR_H_
#define CH9_CAMERACALIBRATOR_CAMERACALIBRATOR_H_

#include <core/core.hpp>
#include <string>
#include <vector>

using namespace std;
using namespace cv;

class CameraCalibrator {
private:
	// 输入点
	// 位于世界坐标的点
	vector<vector<Point3f> > objectPoints;
	// 像素坐标的点
	vector<vector<Point2f> > imagePoints;
	// 输出矩阵
	Mat cameraMatrix;
	Mat distCoeffs;
	// 标定的方式
	int flag;
	// 用于图像去畸变
	Mat map1, map2;
	bool mustInitUnDistort;
public:
	CameraCalibrator();
	virtual ~CameraCalibrator();
	int addCheeseBoardPoint(vector<string> & filelist, Size boardsize);
	void addPonits(vector<Point2f> &imageConners,vector<Point3f> &objectConners);
	double calibrate(Size & imageSize);
	Mat remap(Mat & image);
};

#endif /* CH9_CAMERACALIBRATOR_CAMERACALIBRATOR_H_ */
