- 语义分割获取图像的每个有像素信息，而不是给识别的物体添加边框。

  ![](imgs/1.png)

- 一种场景理解的方法是训练多个解码器，比如一个用于分割，一个用于深度测量。

  ![](imgs/2.png)

- IOU

  > 通常用于测量模型在语义分割上的性能

  Let's walk through an example IOU calculation.

  ## Steps

  - count true positives (TP)
  - count false positives (FP)
  - count false negatives (TN)
  - Intersection = TP
  - Union = TP + FP + FN
  - IOU = Intersection/Union

  [![img](https://s3.cn-north-1.amazonaws.com.cn/u-img/fa1e90e7-4106-4855-93ee-1e5ce6e192ef)](https://classroom.udacity.com/nanodegrees/nd013/parts/6047fe34-d93c-4f50-8336-b70ef10cb4b2/modules/595f35e6-b940-400f-afb2-2015319aa640/lessons/69fe4a9c-656e-46c8-bc32-aee9e60b8984/concepts/a3ecbb2e-afb9-4e90-a1ea-5bea5f96b595#)

  [![img](https://s3.cn-north-1.amazonaws.com.cn/u-img/8e7682d5-16de-4c5d-9605-3dc4e4551e78)](https://classroom.udacity.com/nanodegrees/nd013/parts/6047fe34-d93c-4f50-8336-b70ef10cb4b2/modules/595f35e6-b940-400f-afb2-2015319aa640/lessons/69fe4a9c-656e-46c8-bc32-aee9e60b8984/concepts/a3ecbb2e-afb9-4e90-a1ea-5bea5f96b595#)

  **Mean IOU = [(3/7) + (2/6) + (3/4) + (1/6) ]/4 = 0.420**

- ​