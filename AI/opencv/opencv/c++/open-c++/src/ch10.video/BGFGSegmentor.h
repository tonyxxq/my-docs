/*
 * BGFGSegmentor.h
 *
 *  Created on: 2018Äê2ÔÂ28ÈÕ
 *      Author: Administrator
 */

#ifndef CH10_VIDEO_BGFGSEGMENTOR_H_
#define CH10_VIDEO_BGFGSEGMENTOR_H_

#include "FrameProcessor.h"

using namespace cv;

class BGFGSegmentor : public FrameProcessor{
private:
	Mat gray;
	Mat foreground;
	Mat backimg;
	Mat background;
	double learningrate;
    int thres;
public:
	BGFGSegmentor();
	virtual ~BGFGSegmentor();
	void process(Mat & frame, Mat & output);
};

#endif /* CH10_VIDEO_BGFGSEGMENTOR_H_ */
