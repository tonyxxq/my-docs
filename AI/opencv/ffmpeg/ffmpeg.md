java代码实现 需要引入javave包

####ffmpeg常用命令####

1. 分离视频音频流

   ```
   // 分离视频流
   ffmpeg -i input.mp4 -an -vcodec copy separt_video.mp4　　

   // 分离音频流
   ffmpeg -i input.mp4 -vn -acodec mp3 separt_audio.mp3　　
   ```

2. 查看支持的格式

   ```
   // -vcodec可选的参数
   ffmpeg -codecs 

   // -f后面可选的参数
   ffmpeg -formats

   // -vf后面可选的参数
   ffmpeg -filters
   ```

3. 视频转码

   ```
   // 转码为码流原始文件
   ffmpeg –i input.mp4 –vcodec h264 –s 352*278 –an –f m4v transform_code.264    
   ```

4. 视频封装(视频/图片和音频合成)

   ```
   // 视频和音频合成
   ffmpeg -i separt_video.mp4 -i separt_audio.mp3 -vcodec copy -acodec copy merge_video_audio.mp4

   // 图片和音频合成（-r指定合成的图片的速率，每秒播放几张图片）
   ffmpeg -i separt_audio.mp3 -r 1  -i example/example.image%d.jpg -f mp4 merge_img_audio.mp4   
   ```

5. 视频剪切

   ```
   注意：如果没有指定-f  则使用-s不会生效

   // 剪切视频，-ss 开始时间，-t 持续时间
   ffmpeg -i input.mp4 -ss 00:00:01 -t 00:00:04 -vcodec copy -acodec copy cut_video.mp4    

   // 剪切视频，并且设置输出的分辨率
   ffmpeg -i input.mp4 -ss 00:00:01 -t 00:00:04 -f mp4 -acodec copy -s 960*960 cut_video_2.mp4

   // 分离成图片流（其中-r表示帧率，每秒获取几张图片）
   ffmpeg -i input.mp4 -r 1 -f image2 example/example.image%d.jpg 
   ```

6. 视频录制(还没有测试。。。。。。。)

   ```
   ffmpeg –i rtsp://192.168.3.205:5555/test –vcodec copy out.avi
   ```

7. 过滤器的使用

   ```
   // 如果把scale的后一个参数改为-1，则按比例缩放
   ffmpeg -i input.mp4 -vf scale=960:540 video_rescale.mp4 

   // 给视频添加logo, 左上角：默认
   ffmpeg -i input.mp4 -i logo.jpg -filter_complex overlay video_add_logo.mp4

   右上角：overlay=W-w
   左下角：overlay=0:H-h
   右下角：overlay=W-w:H-h

   ffmpeg -i video_add_logo.mp4 -vf delogo=0:0:220:90[:4[:1]] del_logo.mp4  // 去掉logo(还未测试完成。。。。area报错)

   delogo=x:y:w:h[:t[:show]] 
   x:y 离左上角的坐标 
   w:h logo的宽和高 
   t: 矩形边缘的厚度默认值4 
   show：若设置为1有一个绿色的矩形，默认值0。                                                
   ```


8. 主要参数

   ```
       -i 设定输入流
   	-f 设定输出格式
   	-ss 开始时间

   视频参数：
   	-b 设定视频流量，默认为200Kbit/s
   	-r 设定帧速率，默认为25
   	-s 设定画面的宽与高
   	-aspect 设定画面的比例
   	-vn 不处理视频
   	-vcodec 设定视频编解码器，未设定时则使用与输入流相同的编解码器

   音频参数：
   	-ar 设定采样率
   	-ac 设定声音的Channel数
   	-acodec 设定声音编解码器，未设定时则使用与输入流相同的编解码器
   	-an 不处理音频
   ```

   ​



