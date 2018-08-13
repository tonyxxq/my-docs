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
	//文件句柄
	long hFile = 0;
	//文件信息，_finddata_t需要io.h头文件
	struct _finddata_t fileinfo;
	std::string p;
	if ((hFile = _findfirst(p.assign(path).append("\\*").c_str(), &fileinfo)) != -1) {
		do {
			//如果是目录,迭代之
			//如果不是,加入列表
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

	// 使用指针进行缩减
	/*uchar * data = img.data;
	for (int i = 0; i < img.rows; i++) {
		data += img.step;
	}*/
    // 使用迭代器
   /* Mat_<Vec3b>::iterator it= img.begin<Vec3b>();
    Mat_<Vec3b>::iterator end= img.end<Vec3b>();
	for (; it != end; it++) {
		(*it)[0] = (*it)[0] / 64 * 64 + 32;
		(*it)[1] = (*it)[1] / 64 * 64 + 32;
		(*it)[2] = (*it)[2] / 64 * 64 + 32;
	}*/
	// 获取时钟周期数，且转换为秒
	/*double duration;
	duration = static_cast<double>(getTickCount());
	duration = static_cast<double>(getTickCount() - duration);
	cout << duration / getTickFrequency();*/
	// 使用拉普拉斯算子进行锐化
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
	// 设置边为0
    result.row(0).setTo(0);
    result.row(img.rows - 1).setTo(0);
    result.col(0).setTo(0);
    result.col(img.cols - 1).setTo(0);
	imshow("lena", img);
	imshow("lena2", result);*/
	// 使用filter2D
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
	// 定义感兴趣点，在原图像的基础上进行，操作的是同一内存(先在小范围上操作，注意：最终显示的是大图)
	// Mat logo = imread("../logo.jpg",  0);
	// 这个只是对源图像的小部分的引用
	// Mat roi = img(Rect(250, 250, logo.cols, logo.rows));
	/*addWeighted(roi, 1.0, logo, 0.5, 0., roi);
	//logo.copyTo(roi, logo);
	imshow("lena2", img);*/
	// 颜色空间转换
	/*Mat result;
	result.create(img.size(),img.type());
	cvtColor(img, result, CV_BGR2HSV);
    imshow("lena2", result);*/
    // 直方图
	/*Histogram1D hist;
	MatND matND = hist.getHistogram(img);*/
	//imshow("lena2", matND);
    // 定义查找表，获取原图像的负片，原图像中的像素在查找表中找到key，替换为其value值
	/*Mat lookup(1, 256, CV_8U);
	for (int i = 0; i < 256; i++) {
		lookup.at<uchar>(i) = 255-i;
	}
	Mat result;
	LUT(img, lookup, result);*/
	// 直方图均衡化，使像素更加平均
	/*Mat result;
	equalizeHist(img, result);*/
	/*Histogram1D hist;
	MatND matND = hist.getHistogramImg(result);*/
    // 查找ROI
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
    // 设置图像的阈值
	/*Mat result;
	threshold(img, result, 150, 255.0, CV_THRESH_BINARY);
    imshow("lena2", result);*/
	// 腐蚀把当前像素位替换为集合最小的值，膨胀把当前像素位置替换为集合最大的值
	/*// 图像腐蚀
	Mat result;
	erode(img, result, Mat());
	imshow("lena2", result);
	// 图像膨胀
	Mat result2;
	dilate(img, result2, Mat());
	imshow("lena3", result2);*/
	// 使用形态学滤波进行开闭运算
	// 开运算：先腐蚀后膨胀
	// 闭运算：先膨胀后腐蚀
	/*Mat closed;
	Mat element5(5, 5, CV_8U, Scalar(1));
	morphologyEx(img, closed, MORPH_OPEN, element5);
	imshow("lena3", closed);*/
	//
	/*MorphoFeatures feature(50);
	Mat result = feature.getEdges(img);*/
	// 低通滤波blur,GaussianBlur
	// 低频：图像的强度变化缓慢
	// 高频：图像的强度变化快速
	// blur(img, result, Size(5, 5));
	// GaussianBlur(img, result, Size(5, 5), 1.5);
	// 图像缩小为原来的一半，且进行低通滤波 ，去除噪声
	// pyrDown(img,result);
	// 图像放大为原来的一倍
	// pyrUp(img,result);
	// resize(img, result, Size(img.cols / 5, img.rows / 5));
	// 中值滤波
	/*medianBlur(img, result, 5);*/
	// 高通滤波
	/*Sobel(img, result, CV_16S, 1, 0, 3);
	double min;
	double max;
	minMaxLoc(result, &min, &max);
	result.convertTo(result, CV_8U, 0.5, 128.0);
	threshold(result, result, 105, 255, CV_THRESH_BINARY);*/
	// Canny算子
	/*Mat result;
	Canny(img, result, 320, 350);
	// 对图像进行翻转
	threshold(result, result, 128, 255, CV_THRESH_BINARY_INV);
	imshow("lena3", result);*/
	// 检测fast特征
	/*vector<KeyPoint> keypoints;
	FastFeatureDetector detector(40);
	detector.detect(img, keypoints);
	cout << keypoints.size();*/
	// drawKeypoints(img, keypoints, img, Scalar(255, 255, 255), DrawMatchesFlags::DRAW_OVER_OUTIMG);
    // 检测尺寸不变的特征/描述SURF特征,两幅图像可以旋转，大小不一致都可以找到匹配的特征点
	// 定义SURF特征检测类 对象，阈值设置为250
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
	// 读取视频序列
	/*VideoCapture capture("../1.mp4");
	if (!capture.isOpened()) {
		cout << "视频不存在";
		return 0;
	}
	// 获取视频或者设置视频的一些属性信息
	capture.set(CV_CAP_PROP_POS_FRAMES, 10);
	int rate = capture.get(CV_CAP_PROP_FPS);
	// 指定获取每一帧的延迟时间
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
	// 获取视频中的特征点
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
	// 检测并绘制棋盘角点
	/*vector<Point2f> conners;
	Size bodersize(6, 4);
	bool find = findChessboardCorners(img, bodersize, conners);
	drawChessboardCorners(img, bodersize, conners, find);*/
	// 相机标定（通过对物体不同角度拍摄的图片，获取真实坐标到投影坐标的映射关系）
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
	// 三通道转换为灰度图
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

// 存取像素值,随机设置其中的一些像素点为255
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
		cout << "图片不存在";
	}
	cout << "宽度：" << img.size().width;
	cout << "高度：" << img.size().height;
	Mat result1, result2;
	// 翻转,1:水平翻转，0:垂直翻转,负数:水平和垂直都翻转
	flip(img, result1, 1);
	namedWindow("lena");
	imshow("lena", result1);
	imwrite("../lena.bmp", result1);
	// 对图像进行深复制
	result1.copyTo(result2);
	waitKey(0);
}

