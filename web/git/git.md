#### git常用功能总结

[这篇文章](https://www.cnblogs.com/my--sunshine/p/7093412.html)进行了很好的归纳总结。

- 常用的命令

  ```
  # 从git上克隆  
  git clone https://github.com/udacity/asteroids.git
  # 查看日志
  git log 
  # 命令行设置彩色的输出
  git config --global color.ui auto
  # 避免每次提交都填写用户名密码，可以设置credential.helper为store，当然也可以使用该方式设置其他的配置
  git config --global credential.helper store
  # 查看用户配置
  git config --list
  # git初始化，标志位git仓库
  git init 
  # 查看当前git的状态
  git status
  # 添加文件到待提交
  git add filename
  # 提交到本地
  git commit filename
  # 添加远程仓库
  git add remote origin https://github.com/TonyXXQ/reflections
  # 查看当前配置了哪些远程仓库 
  git remote -v
  # 提交到远程,origin表示远程的地址别名，master表示远程的分支
  git push origin master
  # 重新获取提交的分支中的其中一次提交
  git checkout 4035769377cce96a88d5c1167079e12f30492391
  # 撤销提交，同时把本地的代码撤销到之前的状态
  git reset --hard commitid  
  # 测试提交，不撤销本地修改的代码
  git reset commitid
  ```

- github多人合作：

  在settings中的Collaborators中邀请合作者，合作者就可以提交代码合作开发。


- pull和fetch的区别： 

  pull的时候git会自动进行merge

  git pull = git fetch + git merge

  git fetch只是获取不会merge

  git pull会进行merge

  git fetch比较安全


- Git HEAD detached from XXX (git HEAD 游离) 解决办法

  当切换到最近的某一次提交（commit id）的时候会出现这种情况，这时项目不能提交。
  解决办法把当前项目新建一个分支，再合并，再提交。

  ```
  # 查看有哪些版本的分支
  git branch -v
  # 查看当前状态
  git status
  # 新建分支temp
  git branch temp
  # 切换到要合并到的分支
  git checkout qa
  # 合并
  git merge temp
  # 提交
  git push origin qa
  ```
















