/*
 * CameraCalibrator.cpp
 *
 *  Created on: 2018年3月1日
 *      Author: Administrator
 */

#include "CameraCalibrator.h"

#include <calib3d/calib3d.hpp>
#include <core/mat.hpp>
#include <core/operations.hpp>
#include <core/types_c.h>
#include <highgui/highgui.hpp>
#include <imgproc/imgproc.hpp>
#include <iostream>

CameraCalibrator::CameraCalibrator() :
		flag(0), mustInitUnDistort(true) {
	// TODO Auto-generated constructor stub
}

CameraCalibrator::~CameraCalibrator() {
	// TODO Auto-generated destructor stub
}

/**
 * 找到图像上的角点
 */
int CameraCalibrator::addCheeseBoardPoint(vector<string> & filelist, Size boardsize) {
	// 两种坐标
	vector<Point2f> imageCorners;
	vector<Point3f> objectCorners;
	// 3d场景中的点，初始化objectConners
	for (int i = 0; i < boardsize.height; i++) {
		for (int j = 0; j < boardsize.width; j++) {
			objectCorners.push_back(Point3f(i, j, 0.0f));
		}
	}
	// 2d场景中的点，初始化objectConners
	bool success = 0;
	Mat image;
	for (int i = 0; i < filelist.size(); i++) {
		// 打开图像并获取角点
		Mat img = imread(filelist[i], 0);
		bool found = findChessboardCorners(img, boardsize, imageCorners);
		// 计算角点的亚像素
		/*cornerSubPix(img, imageCorners, Size(5, 5), Size(-1, -1),
				TermCriteria(TermCriteria::MAX_ITER + TermCriteria::EPS, 30, 0.1));*/
		if (imageCorners.size() == boardsize.area()) {
			addPonits(imageCorners, objectCorners);
			success++;
		}
	}
	return success;
}

/**
 *添加场景点与对应的图像点
 */
void CameraCalibrator::addPonits(vector<Point2f> &imageConners, vector<Point3f> &objectConners) {
	imagePoints.push_back(imageConners);
	objectPoints.push_back(objectConners);
}

/**
 * 进行相机标定
 */
double CameraCalibrator::calibrate(Size & imageSize) {
	// 重新进行去畸变
	mustInitUnDistort = true;
	// 输出旋转和平移
	vector<Mat> rvecs;
	vector<Mat> tvecs;
	// 进行标定
	return calibrateCamera(
			objectPoints,
			imagePoints,
			imageSize,
			cameraMatrix, // 输出的相机矩阵
			distCoeffs,  // 输出的畸变矩阵
			rvecs, tvecs,
			flag);
}

/**
 * 去除畸变
 */
Mat CameraCalibrator::remap(Mat & image) {
	Mat undistorted;
	// 每次标定只需初始化一次
	if (mustInitUnDistort) {
		initUndistortRectifyMap(
				cameraMatrix,// 相机矩阵
				distCoeffs,// 畸变矩阵
				Mat(), // 可选的rectification矩阵
				Mat(), // 用于生成undistorted的相机矩阵
				Size(600,400),// 输出图像的大小
				CV_32FC1, // 输出的映射图像的类型
				map1, map2 // x和y坐标映射函数
				);
		mustInitUnDistort = false;
	}
	// 应用映射函数，去除径向畸变
	cv::remap(image,
			undistorted,
			map1,
			map2,
			INTER_LINEAR // 插值类型
			);
	return undistorted;
}

