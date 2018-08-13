/*
 * FrameProcessor.h
 *
 *  Created on: 2018Äê2ÔÂ28ÈÕ
 *      Author: Administrator
 */

#ifndef CH10_VIDEO_FRAMEPROCESSOR_H_
#define CH10_VIDEO_FRAMEPROCESSOR_H_

#include <core/core.hpp>

using namespace cv;
class FrameProcessor {
public:
	FrameProcessor();
	virtual void process(Mat &frame, Mat &output)=0;
	virtual ~FrameProcessor();
};

#endif /* CH10_VIDEO_FRAMEPROCESSOR_H_ */
