/*
 * CameraCalibrator.cpp
 *
 *  Created on: 2018��3��1��
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
 * �ҵ�ͼ���ϵĽǵ�
 */
int CameraCalibrator::addCheeseBoardPoint(vector<string> & filelist, Size boardsize) {
	// ��������
	vector<Point2f> imageCorners;
	vector<Point3f> objectCorners;
	// 3d�����еĵ㣬��ʼ��objectConners
	for (int i = 0; i < boardsize.height; i++) {
		for (int j = 0; j < boardsize.width; j++) {
			objectCorners.push_back(Point3f(i, j, 0.0f));
		}
	}
	// 2d�����еĵ㣬��ʼ��objectConners
	bool success = 0;
	Mat image;
	for (int i = 0; i < filelist.size(); i++) {
		// ��ͼ�񲢻�ȡ�ǵ�
		Mat img = imread(filelist[i], 0);
		bool found = findChessboardCorners(img, boardsize, imageCorners);
		// ����ǵ��������
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
 *��ӳ��������Ӧ��ͼ���
 */
void CameraCalibrator::addPonits(vector<Point2f> &imageConners, vector<Point3f> &objectConners) {
	imagePoints.push_back(imageConners);
	objectPoints.push_back(objectConners);
}

/**
 * ��������궨
 */
double CameraCalibrator::calibrate(Size & imageSize) {
	// ���½���ȥ����
	mustInitUnDistort = true;
	// �����ת��ƽ��
	vector<Mat> rvecs;
	vector<Mat> tvecs;
	// ���б궨
	return calibrateCamera(
			objectPoints,
			imagePoints,
			imageSize,
			cameraMatrix, // ������������
			distCoeffs,  // ����Ļ������
			rvecs, tvecs,
			flag);
}

/**
 * ȥ������
 */
Mat CameraCalibrator::remap(Mat & image) {
	Mat undistorted;
	// ÿ�α궨ֻ���ʼ��һ��
	if (mustInitUnDistort) {
		initUndistortRectifyMap(
				cameraMatrix,// �������
				distCoeffs,// �������
				Mat(), // ��ѡ��rectification����
				Mat(), // ��������undistorted���������
				Size(600,400),// ���ͼ��Ĵ�С
				CV_32FC1, // �����ӳ��ͼ�������
				map1, map2 // x��y����ӳ�亯��
				);
		mustInitUnDistort = false;
	}
	// Ӧ��ӳ�亯����ȥ���������
	cv::remap(image,
			undistorted,
			map1,
			map2,
			INTER_LINEAR // ��ֵ����
			);
	return undistorted;
}

