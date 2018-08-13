使用了opencv+ocr技术对银行卡号进行了识别

集成opencv到开发环境

1.在项目下添加opencv的jar包（C:\opencv\opencv\build\java\opencv-2411.jar）
2.把opencv_java2411.dll放到eclipse的安装目录的两个文件夹下
F:\eclipse-jee-mars-2-win32-x86_64\eclipse\bin
F:\eclipse-jee-mars-2-win32-x86_64\eclipse\jre\bin
opencv_java2411.dll所在的地址为：C:\opencv\opencv\build\java\x64\把opencv_java2411.dll

集成tessocr到开发环境
下载tessocr的java开发包Tess4J-3.4.3-src.zip
把下面所有jar包放到开发环境中，就能执行开发了
如果支持中文需要加入chi_sim.traineddata到tessdata目录下

另外也可以安装tessocr软件进行字符识别，但是jar包的方式更准确

