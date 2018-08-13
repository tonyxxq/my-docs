/*
 * CameraCalibrator.h
 *
 *  Created on: 2018��3��1��
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
	// �����
	// λ����������ĵ�
	vector<vector<Point3f> > objectPoints;
	// ��������ĵ�
	vector<vector<Point2f> > imagePoints;
	// �������
	Mat cameraMatrix;
	Mat distCoeffs;
	// �궨�ķ�ʽ
	int flag;
	// ����ͼ��ȥ����
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
