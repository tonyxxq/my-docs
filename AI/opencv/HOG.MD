归一化的思想是把scale给移除了，所有像素点位置的像素值统一都很小，从而去掉了光照产生的影响。 



1. 提取物体的特征，包括两方面，颜色和形状

2. 模板匹配

   官方例子：[点这里](http://opencv-python-tutroals.readthedocs.io/en/latest/py_tutorials/py_imgproc/py_template_matching/py_template_matching.html)

3. 颜色直方图

   ```python
   import numpy as np
   import cv2
   import matplotlib.pyplot as plt
   import matplotlib.image as mpimg

   image = mpimg.imread('cutout1.jpg')

   # Define a function to compute color histogram features  
   def color_hist(img, nbins=32, bins_range=(0, 256)):
       image = mpimg.imread('cutout1.jpg')
       # Compute the histogram of the RGB channels separately
       rhist = np.histogram(image[:,:,0], bins=32, range=(0, 256))
       ghist = np.histogram(image[:,:,1], bins=32, range=(0, 256))
       bhist = np.histogram(image[:,:,2], bins=32, range=(0, 256))
       # Generating bin centers
       bin_edges = rhist[1]
       bin_centers = (bin_edges[1:]  + bin_edges[0:len(bin_edges)-1])/2 
       # Concatenate the histograms into a single feature vector
       hist_features = np.concatenate((rhist[0], ghist[0], bhist[0]))    
       # Return the individual histograms, bin_centers and feature vector
       return rhist, ghist, bhist, bin_centers, hist_features
       
   rh, gh, bh, bincen, feature_vec = color_hist(image, nbins=32, bins_range=(0, 256))

   # Plot a figure with all three bar charts
   if rh is not None:
       fig = plt.figure(figsize=(12,3))
       plt.subplot(131)
       plt.bar(bincen, rh[0])
       plt.xlim(0, 256)
       plt.title('R Histogram')
       plt.subplot(132)
       plt.bar(bincen, gh[0])
       plt.xlim(0, 256)
       plt.title('G Histogram')
       plt.subplot(133)
       plt.bar(bincen, bh[0])
       plt.xlim(0, 256)
       plt.title('B Histogram')
       fig.tight_layout()
   else:
       print('Your function is returning None for at least one variable...')
   ```

4. 颜色空间

   ```python
   import cv2
   import numpy as np
   import matplotlib.pyplot as plt
   from mpl_toolkits.mplot3d import Axes3D

   def plot3d(pixels, colors_rgb,
           axis_labels=list("RGB"), axis_limits=((0, 255), (0, 255), (0, 255))):
       """Plot pixels in 3D."""

       # Create figure and 3D axes
       fig = plt.figure(figsize=(8, 8))
       ax = Axes3D(fig)

       # Set axis limits
       ax.set_xlim(*axis_limits[0])
       ax.set_ylim(*axis_limits[1])
       ax.set_zlim(*axis_limits[2])

       # Set axis labels and sizes
       ax.tick_params(axis='both', which='major', labelsize=14, pad=8)
       ax.set_xlabel(axis_labels[0], fontsize=16, labelpad=16)
       ax.set_ylabel(axis_labels[1], fontsize=16, labelpad=16)
       ax.set_zlabel(axis_labels[2], fontsize=16, labelpad=16)

       # Plot pixel values with colors given in colors_rgb
       ax.scatter(
           pixels[:, :, 0].ravel(),
           pixels[:, :, 1].ravel(),
           pixels[:, :, 2].ravel(),
           c=colors_rgb.reshape((-1, 3)), edgecolors='none')

       return ax  # return Axes3D object for further manipulation


   # Read a color image
   img = cv2.imread("000275.png")

   # Select a small fraction of pixels to plot by subsampling it
   scale = max(img.shape[0], img.shape[1], 64) / 64  # at most 64 rows and columns
   img_small = cv2.resize(img, (np.int(img.shape[1] / scale), np.int(img.shape[0] / scale)), interpolation=cv2.INTER_NEAREST)

   # Convert subsampled image to desired color space(s)
   img_small_RGB = cv2.cvtColor(img_small, cv2.COLOR_BGR2RGB)  # OpenCV uses BGR, matplotlib likes RGB
   img_small_HSV = cv2.cvtColor(img_small, cv2.COLOR_BGR2HSV)
   img_small_rgb = img_small_RGB / 255.  # scaled to [0, 1], only for plotting

   # Plot and show
   plot3d(img_small_RGB, img_small_rgb)
   plt.show()

   plot3d(img_small_HSV, img_small_rgb, axis_labels=list("HSV"))
   plt.show()
   ```

5. 减少特征的数量

   ```python
   import numpy as np
   import cv2
   import matplotlib.pyplot as plt
   import matplotlib.image as mpimg

   image = mpimg.imread('cutout1.jpg')

   def bin_spatial(img, color_space='RGB', size=(32, 32)):
       # Convert image to new color space (if specified)
       if color_space != 'RGB':
           if color_space == 'HSV':
               feature_image = cv2.cvtColor(img, cv2.COLOR_RGB2HSV)
           elif color_space == 'LUV':
               feature_image = cv2.cvtColor(img, cv2.COLOR_RGB2LUV)
           elif color_space == 'HLS':
               feature_image = cv2.cvtColor(img, cv2.COLOR_RGB2HLS)
           elif color_space == 'YUV':
               feature_image = cv2.cvtColor(img, cv2.COLOR_RGB2YUV)
           elif color_space == 'YCrCb':
               feature_image = cv2.cvtColor(img, cv2.COLOR_RGB2YCrCb)
       else: feature_image = np.copy(img)             
       # Use cv2.resize().ravel() to create the feature vector
       # ravel把图像flattern，变为1维向量
       features = cv2.resize(feature_image, size).ravel() 
       # Return the feature vector
       return features
       
   feature_vec = bin_spatial(image, color_space='RGB', size=(32, 32))

   # Plot features
   plt.plot(feature_vec)
   plt.title('Spatially Binned Features')
   ```

6. HOG特征探索

   官方教程地址：[点这里](http://scikit-image.org/docs/dev/api/skimage.feature.html?highlight=feature%20hog#skimage.feature.hog)

   ```python
   import matplotlib.image as mpimg
   import matplotlib.pyplot as plt
   import numpy as np
   import cv2
   import glob
   from skimage.feature import hog

   # Read in our vehicles
   car_images = glob.glob('*.jpeg')

   def get_hog_features(img, orient, pix_per_cell, cell_per_block, vis=True, 
                        feature_vec=True):
       return_list = hog(img, orientations=orient, pixels_per_cell=(pix_per_cell,pix_per_cell),
                                     cells_per_block=(cell_per_block, cell_per_block),
                                     block_norm= 'L2-Hys', transform_sqrt=False, 
                                     visualise= vis, feature_vector= feature_vec)
       # name returns explicitly
       hog_features = return_list[0]
       if vis:
           hog_image = return_list[1]
           return hog_features, hog_image
       else:
           return hog_features

   # Generate a random index to look at a car image
   ind = np.random.randint(0, len(car_images))
   # Read in the image
   image = mpimg.imread(car_images[ind])
   gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)

   # Call our function with vis=True to see an image output
   features, hog_image = get_hog_features(gray, orient = 9, 
                           pix_per_cell = 8, cell_per_block = 2, 
                           vis = True, feature_vec = False)

   # Plot the examples
   fig = plt.figure()
   plt.subplot(121)
   plt.imshow(image, cmap='gray')
   plt.title('Example Car Image')
   plt.subplot(122)
   plt.imshow(hog_image, cmap='gray')
   plt.title('HOG Visualization')
   ```

7. 特征合并

   把之前的颜色特征向量和HOG特征向量归一化之后进行合并。

   ```python
   import numpy as np
   import cv2
   import matplotlib.pyplot as plt
   import matplotlib.image as mpimg
   from sklearn.preprocessing import StandardScaler
   import glob

   # Define a function to compute binned color features  
   def bin_spatial(img, size=(32, 32)):
       # Use cv2.resize().ravel() to create the feature vector
       features = cv2.resize(img, size).ravel() 
       # Return the feature vector
       return features

   # Define a function to compute color histogram features  
   def color_hist(img, nbins=32, bins_range=(0, 256)):
       # Compute the histogram of the color channels separately
       channel1_hist = np.histogram(img[:,:,0], bins=nbins, range=bins_range)
       channel2_hist = np.histogram(img[:,:,1], bins=nbins, range=bins_range)
       channel3_hist = np.histogram(img[:,:,2], bins=nbins, range=bins_range)
       # Concatenate the histograms into a single feature vector
       hist_features = np.concatenate((channel1_hist[0], channel2_hist[0], channel3_hist[0]))
       # Return the individual histograms, bin_centers and feature vector
       return hist_features

   # Define a function to extract features from a list of images
   # Have this function call bin_spatial() and color_hist()
   # Define a function to extract features from a list of images
   # Have this function call bin_spatial() and color_hist()
   def extract_features(imgs, cspace='RGB', spatial_size=(32, 32),
                           hist_bins=32, hist_range=(0, 256)):
       # Create a list to append feature vectors to
       features = []
       # Iterate through the list of images
       for file in imgs:
           # Read in each one by one
           image = mpimg.imread(file)
           # apply color conversion if other than 'RGB'
           if cspace != 'RGB':
               if cspace == 'HSV':
                   feature_image = cv2.cvtColor(image, cv2.COLOR_RGB2HSV)
               elif cspace == 'LUV':
                   feature_image = cv2.cvtColor(image, cv2.COLOR_RGB2LUV)
               elif cspace == 'HLS':
                   feature_image = cv2.cvtColor(image, cv2.COLOR_RGB2HLS)
               elif cspace == 'YUV':
                   feature_image = cv2.cvtColor(image, cv2.COLOR_RGB2YUV)
           else: feature_image = np.copy(image)      
           # Apply bin_spatial() to get spatial color features
           spatial_features = bin_spatial(feature_image, size=spatial_size)
           # Apply color_hist() also with a color space option now
           hist_features = color_hist(feature_image, nbins=hist_bins, bins_range=hist_range)
           # Append the new feature vector to the features list
           features.append(np.concatenate((spatial_features, hist_features)))
       # Return list of feature vectors
       return features

   images = glob.glob('*.jpeg')
   cars = []
   notcars = []
   for image in images:
       if 'image' in image or 'extra' in image:
           notcars.append(image)
       else:
           cars.append(image)
           
   car_features = extract_features(cars, cspace='RGB', spatial_size=(32, 32),
                           hist_bins=32, hist_range=(0, 256))
   notcar_features = extract_features(notcars, cspace='RGB', spatial_size=(32, 32),
                           hist_bins=32, hist_range=(0, 256))

   if len(car_features) > 0:
       # Create an array stack of feature vectors
       X = np.vstack((car_features, notcar_features)).astype(np.float64)                       
       # Fit a per-column scaler
       X_scaler = StandardScaler().fit(X)
       # Apply the scaler to X
       scaled_X = X_scaler.transform(X)
       car_ind = np.random.randint(0, len(cars))
       # Plot an example of raw and scaled features
       fig = plt.figure(figsize=(12,4))
       plt.subplot(131)
       plt.imshow(mpimg.imread(cars[car_ind]))
       plt.title('Original Image')
       plt.subplot(132)
       plt.plot(X[car_ind])
       plt.title('Raw Features')
       plt.subplot(133)
       plt.plot(scaled_X[car_ind])
       plt.title('Normalized Features')
       fig.tight_layout()
   else: 
       print('Your function only returns empty feature vectors...')
   ```

8. 构建分类器

   ```

   ```

9. 滑动窗口

   因为不知道物体在图像中的大小，为了更精确的检测到物体的位置，需要进行多尺度搜索。但这样搜索窗口的数量会变得更多，为了减少计算量，可以值检测目标对象存在的区域。

   ```python
   import numpy as np
   import cv2
   import matplotlib.pyplot as plt
   import matplotlib.image as mpimg

   image = mpimg.imread('bbox-example-image.jpg')

   # Here is your draw_boxes function from the previous exercise
   def draw_boxes(img, bboxes, color=(0, 0, 255), thick=6):
       # Make a copy of the image
       imcopy = np.copy(img)
       # Iterate through the bounding boxes
       for bbox in bboxes:
           # Draw a rectangle given bbox coordinates
           cv2.rectangle(imcopy, bbox[0], bbox[1], color, thick)
       # Return the image copy with boxes drawn
       return imcopy
       
       
   # Define a function that takes an image,
   # start and stop positions in both x and y, 
   # window size (x and y dimensions),  
   # and overlap fraction (for both x and y)
   def slide_window(img, x_start_stop=[None, None], y_start_stop=[None, None], 
                       xy_window=(64, 64), xy_overlap=(0.5, 0.5)):
       # If x and/or y start/stop positions not defined, set to image size
       # Compute the span of the region to be searched    
       # Compute the number of pixels per step in x/y
       # Compute the number of windows in x/y
       # Initialize a list to append window positions to
       window_list = []
       for i in range(0, img.shape[0], int(xy_overlap[0]*xy_window[0])):
           for j in range(0, img.shape[1], int(xy_overlap[1]*xy_window[1])):
               window_list.append([(i, j), (i + xy_window[0], j + xy_window[1])])
       # Loop through finding x and y window positions
       #     Note: you could vectorize this step, but in practice
       #     you'll be considering windows one by one with your
       #     classifier, so looping makes sense
           # Calculate each window position
           # Append window position to list
       # Return the list of windows
       return window_list

   windows = slide_window(image, x_start_stop=[None, None], y_start_stop=[None, None], 
                       xy_window=(128, 128), xy_overlap=(0.5, 0.5))

   window_img = draw_boxes(image, windows, color=(0, 0, 255), thick=6)                    
   plt.imshow(window_img)
   ```

10. 建立多维度的窗口

    ​