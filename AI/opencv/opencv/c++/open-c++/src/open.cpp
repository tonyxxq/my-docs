//============================================================================
// Name        : open.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <core/core.hpp>
#include <core/mat.hpp>
#include <core/types_c.h>
#include <highgui/highgui.hpp>
#include <imgproc/imgproc.hpp>
#include <imgproc/types_c.h>
#include <io.h>
#include <cstdlib>
#include <cstring>
#include <iostream>
#include <string>
#include <vector>

#include "ch9.CameraCalibrator/CameraCalibrator.h"



using namespace std;
using namespace cv;

void ch1();
void ch2();
void update_pixl_mat_(Mat_<Vec3b> &img, int n);
void color_reduce(Mat &img, Mat &result,int n);
void canny(Mat &img, Mat &result);



void getFiles(const std::string & path, std::vector<std::string> & files) {
	//�ļ����
	long hFile = 0;
	//�ļ���Ϣ��_finddata_t��Ҫio.hͷ�ļ�
	struct _finddata_t fileinfo;
	std::string p;
	if ((hFile = _findfirst(p.assign(path).append("\\*").c_str(), &fileinfo)) != -1) {
		do {
			//�����Ŀ¼,����֮
			//�������,�����б�
			if ((fileinfo.attrib & _A_SUBDIR)) {
				if (strcmp(fileinfo.name, ".") != 0 && strcmp(fileinfo.name, "..") != 0)
					getFiles(p.assign(path).append("\\").append(fileinfo.name), files);
			} else {
				files.push_back(p.assign(path).append("\\").append(fileinfo.name));
			}
		} while (_findnext(hFile, &fileinfo) == 0);
		_findclose(hFile);
	}
}

int main() {
	/*result.create(200, 200, img.type());*/
	//Mat result;
	//color_reduce(img, result, 64);
	Mat img = imread("../images/chessboards/chessboard01.jpg", 0);
	//cout<<img.isContinuous();
	/*Mat result = img.reshape(1, img.rows * img.cols);
	cout << result.cols << endl;
	cout << result.rows << endl;
	cout << result.channels() << endl;*/

	// ʹ��ָ���������
	/*uchar * data = img.data;
	for (int i = 0; i < img.rows; i++) {
		data += img.step;
	}*/
    // ʹ�õ�����
   /* Mat_<Vec3b>::iterator it= img.begin<Vec3b>();
    Mat_<Vec3b>::iterator end= img.end<Vec3b>();
	for (; it != end; it++) {
		(*it)[0] = (*it)[0] / 64 * 64 + 32;
		(*it)[1] = (*it)[1] / 64 * 64 + 32;
		(*it)[2] = (*it)[2] / 64 * 64 + 32;
	}*/
	// ��ȡʱ������������ת��Ϊ��
	/*double duration;
	duration = static_cast<double>(getTickCount());
	duration = static_cast<double>(getTickCount() - duration);
	cout << duration / getTickFrequency();*/
	// ʹ��������˹���ӽ�����
    /*Mat result;
	result.create(img.size(), img.type());
    for (int i = 1; i < img.rows - 1; i++) {
		uchar * previous = img.ptr<uchar>(i - 1);
		uchar * current = img.ptr<uchar>(i);
		uchar * next = img.ptr<uchar>(i + 1);
		uchar * result_row = result.ptr(i);
		for (int j = 1; j < img.cols-1; j++) {
			result_row[j] = saturate_cast<uchar>(5 * current[j] - next[j] - previous[j] - current[j - 1] - current[j + 1]);
		}
	}
	// ���ñ�Ϊ0
    result.row(0).setTo(0);
    result.row(img.rows - 1).setTo(0);
    result.col(0).setTo(0);
    result.col(img.cols - 1).setTo(0);
	imshow("lena", img);
	imshow("lena2", result);*/
	// ʹ��filter2D
	/*Mat result;
	result.create(img.size(), img.type());
	Mat kernel(3, 3, CV_32F, Scalar(0));
	kernel.at<float>(1, 1) = 5.0;
	kernel.at<float>(1, 0) = -1.0;
	kernel.at<float>(1, 2) = -1.0;
	kernel.at<float>(2, 1) = -1.0;
	kernel.at<float>(0, 1) = -1.0;
	filter2D(img, result, img.depth(), kernel);
	imshow("lena2", img);
	imshow("lena", result);*/
	// add
	//Mat result;
	// addWeighted(img, 0.1, img, 0.1, 0., result);
	// add(img, Scalar(40), result);
	// scaleAdd(img, 0.5, img, result);
	// mask
	/*Mat result;
	Mat mask;
	mask.create(img.size(), img.type());
	add(img, img, result, mask);
	imshow("lena2", result);*/
	// �������Ȥ�㣬��ԭͼ��Ļ����Ͻ��У���������ͬһ�ڴ�(����С��Χ�ϲ�����ע�⣺������ʾ���Ǵ�ͼ)
	// Mat logo = imread("../logo.jpg",  0);
	// ���ֻ�Ƕ�Դͼ���С���ֵ�����
	// Mat roi = img(Rect(250, 250, logo.cols, logo.rows));
	/*addWeighted(roi, 1.0, logo, 0.5, 0., roi);
	//logo.copyTo(roi, logo);
	imshow("lena2", img);*/
	// ��ɫ�ռ�ת��
	/*Mat result;
	result.create(img.size(),img.type());
	cvtColor(img, result, CV_BGR2HSV);
    imshow("lena2", result);*/
    // ֱ��ͼ
	/*Histogram1D hist;
	MatND matND = hist.getHistogram(img);*/
	//imshow("lena2", matND);
    // ������ұ���ȡԭͼ��ĸ�Ƭ��ԭͼ���е������ڲ��ұ����ҵ�key���滻Ϊ��valueֵ
	/*Mat lookup(1, 256, CV_8U);
	for (int i = 0; i < 256; i++) {
		lookup.at<uchar>(i) = 255-i;
	}
	Mat result;
	LUT(img, lookup, result);*/
	// ֱ��ͼ���⻯��ʹ���ظ���ƽ��
	/*Mat result;
	equalizeHist(img, result);*/
	/*Histogram1D hist;
	MatND matND = hist.getHistogramImg(result);*/
    // ����ROI
    /*Mat roi = img(Rect(200,200,250,250));
    Histogram1D hist;
    MatND matND = hist.getHistogram(roi);
	normalize(matND, matND, 1);
	int channels[] = {0};
	Mat result;
	float range[2] = { 1.0, 255.0 };
	const float * ranges[1] = { range };
	calcBackProject(&img,
              1,
			  channels,
			  matND,
			  result,
			  ranges,
			  255.0
			);*/
    // ����ͼ�����ֵ
	/*Mat result;
	threshold(img, result, 150, 255.0, CV_THRESH_BINARY);
    imshow("lena2", result);*/
	// ��ʴ�ѵ�ǰ����λ�滻Ϊ������С��ֵ�����Ͱѵ�ǰ����λ���滻Ϊ��������ֵ
	/*// ͼ��ʴ
	Mat result;
	erode(img, result, Mat());
	imshow("lena2", result);
	// ͼ������
	Mat result2;
	dilate(img, result2, Mat());
	imshow("lena3", result2);*/
	// ʹ����̬ѧ�˲����п�������
	// �����㣺�ȸ�ʴ������
	// �����㣺�����ͺ�ʴ
	/*Mat closed;
	Mat element5(5, 5, CV_8U, Scalar(1));
	morphologyEx(img, closed, MORPH_OPEN, element5);
	imshow("lena3", closed);*/
	//
	/*MorphoFeatures feature(50);
	Mat result = feature.getEdges(img);*/
	// ��ͨ�˲�blur,GaussianBlur
	// ��Ƶ��ͼ���ǿ�ȱ仯����
	// ��Ƶ��ͼ���ǿ�ȱ仯����
	// blur(img, result, Size(5, 5));
	// GaussianBlur(img, result, Size(5, 5), 1.5);
	// ͼ����СΪԭ����һ�룬�ҽ��е�ͨ�˲� ��ȥ������
	// pyrDown(img,result);
	// ͼ��Ŵ�Ϊԭ����һ��
	// pyrUp(img,result);
	// resize(img, result, Size(img.cols / 5, img.rows / 5));
	// ��ֵ�˲�
	/*medianBlur(img, result, 5);*/
	// ��ͨ�˲�
	/*Sobel(img, result, CV_16S, 1, 0, 3);
	double min;
	double max;
	minMaxLoc(result, &min, &max);
	result.convertTo(result, CV_8U, 0.5, 128.0);
	threshold(result, result, 105, 255, CV_THRESH_BINARY);*/
	// Canny����
	/*Mat result;
	Canny(img, result, 320, 350);
	// ��ͼ����з�ת
	threshold(result, result, 128, 255, CV_THRESH_BINARY_INV);
	imshow("lena3", result);*/
	// ���fast����
	/*vector<KeyPoint> keypoints;
	FastFeatureDetector detector(40);
	detector.detect(img, keypoints);
	cout << keypoints.size();*/
	// drawKeypoints(img, keypoints, img, Scalar(255, 255, 255), DrawMatchesFlags::DRAW_OVER_OUTIMG);
    // ���ߴ粻�������/����SURF����,����ͼ�������ת����С��һ�¶������ҵ�ƥ���������
	// ����SURF��������� ������ֵ����Ϊ250
	/*SurfFeatureDetector detector(250.0);
	vector<KeyPoint> keypoints;
	vector<KeyPoint> keypoints1;
	Mat img1 = imread("../house_1.jpg", 0);
	detector.detect(img, keypoints);
	detector.detect(img1, keypoints1);
	SurfDescriptorExtractor surfDesc;
	Mat descriptor;
	surfDesc.compute(img, keypoints, descriptor);
	Mat descriptor1;
	surfDesc.compute(img1, keypoints1, descriptor1);
	BruteForceMatcher<L2<float>> matcher;
	vector<DMatch> matches;
	matcher.match(descriptor, descriptor1, matches);
	nth_element(matches.begin(), matches.begin() + 24, matches.end());
	matches.erase(matches.begin() + 25, matches.end());
	Mat imgMatches;
	drawMatches(img, keypoints, img1, keypoints1, matches, imgMatches, Scalar(255,255,255));*/
	// ��ȡ��Ƶ����
	/*VideoCapture capture("../1.mp4");
	if (!capture.isOpened()) {
		cout << "��Ƶ������";
		return 0;
	}
	// ��ȡ��Ƶ����������Ƶ��һЩ������Ϣ
	capture.set(CV_CAP_PROP_POS_FRAMES, 10);
	int rate = capture.get(CV_CAP_PROP_FPS);
	// ָ����ȡÿһ֡���ӳ�ʱ��
	int delay = 3000 / rate;
	Mat frame;
	bool stop(false);
	while (!stop) {
		capture.read(frame);
		imshow("lena3", frame);
		if (waitKey(delay) >= 0) {
			stop = true;
		}
	}*/
	// ��ȡ��Ƶ�е�������
	/*VideoProcessor processor;
	processor.setInput("../bike.avi");
	// processor.setOutput("../2.mp4");
	// processor.setDigits(10);
	processor.setDelay(1000 / processor.getRate());
	processor.displayInput("current frame");
	processor.displayOutput("output frame");
	// processor.setFrameProcessor(canny);
	// FeatureTracker tracker;
	BGFGSegmentor segmentor;
	processor.setFrameProcessor(&segmentor);
	processor.run();*/
	// ��Ⲣ�������̽ǵ�
	/*vector<Point2f> conners;
	Size bodersize(6, 4);
	bool find = findChessboardCorners(img, bodersize, conners);
	drawChessboardCorners(img, bodersize, conners, find);*/
	// ����궨��ͨ�������岻ͬ�Ƕ������ͼƬ����ȡ��ʵ���굽ͶӰ�����ӳ���ϵ��
	CameraCalibrator calibrator;
	vector<string> filenames;
	getFiles("../images/chessboards", filenames);
	calibrator.addCheeseBoardPoint(filenames, Size(6, 4));
	Size imagesize(6, 4);
	calibrator.calibrate(imagesize);
	Mat origin = imread("../images/chessboards/chessboard37.jpg");
	imshow("origin", origin);
	Mat result = calibrator.remap(origin);
	imshow("result", result);
	waitKey(0);
	return 0;
}

void canny(Mat &img, Mat &result){
	// ��ͨ��ת��Ϊ�Ҷ�ͼ
	if (img.channels() == 3) {
		cvtColor(img, img, CV_BGR2GRAY);
	}
	Canny(img, result, 128, 200);
	threshold(result, result, 100, 255, CV_THRESH_BINARY);
}

void ch2() {
	Mat img = imread("../lena.jpg");
	//color_reduce(img, 64);
	imshow("lena", img);
	waitKey(0);
}

void color_reduce(Mat &img, Mat &result, int n = 8) {
	for (int i = 0; i < img.rows; i++) {
		uchar * data_in = img.ptr<uchar>(i);
		uchar * data_out = result.ptr<uchar>(i);
		for (int j = 0; j < img.cols * img.channels(); j++) {
			data_out[j] = data_in[j] / n * n + n / 2;
		}
	}
}

void update_pixl_mat(Mat &img, int n) {
	for (int k = 0; k < n; ++k) {
		int i = rand() % img.rows;
		int j = rand() % img.cols;
		if (img.channels() == 1) {
			img.at<uchar>(i, j) = 255;
		} else {
			img.at<Vec3b>(i, j)[0] = 255;
			img.at<Vec3b>(i, j)[1] = 255;
			img.at<Vec3b>(i, j)[2] = 255;
		}
	}
}

// ��ȡ����ֵ,����������е�һЩ���ص�Ϊ255
void update_pixl_mat_(Mat_<Vec3b> &img, int n) {
	for (int k = 0; k < n; ++k) {
		int i = rand() % img.rows;
		int j = rand() % img.cols;
		if (img.channels() == 1) {
			img(i, j) = 255;
		} else {
			img(i, j)[0] = 255;
			img(i, j)[1] = 255;
			img(i, j)[2] = 255;
		}
	}
}

void ch1() {
	Mat img = imread("../lena.jpg");
	if (!img.data) {
		cout << "ͼƬ������";
	}
	cout << "��ȣ�" << img.size().width;
	cout << "�߶ȣ�" << img.size().height;
	Mat result1, result2;
	// ��ת,1:ˮƽ��ת��0:��ֱ��ת,����:ˮƽ�ʹ�ֱ����ת
	flip(img, result1, 1);
	namedWindow("lena");
	imshow("lena", result1);
	imwrite("../lena.bmp", result1);
	// ��ͼ��������
	result1.copyTo(result2);
	waitKey(0);
}

