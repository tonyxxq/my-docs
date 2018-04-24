这篇文章进行了很好的归纳总结：https://www.cnblogs.com/my--sunshine/p/7093412.html

克隆git目录  git clone https://github.com/udacity/asteroids.git
git log 查看提交日志
git config --global color.ui auto 获得彩色输出

从命令行复制并粘贴
为了完成本测试题，需要复制并粘贴一些提交的 ID。

Windows
要在 Git Bash 中复制并粘贴，设置为快速编辑模式。

Mac
要在 Mac 上的终端中复制并粘贴，请使用 Cmd+C 和 Cmd+V。

Ubuntu
要在 Ubuntu 上的终端中复制并粘贴，请使用 Ctrl+Shift+C 和 Ctrl+Shift+V。

You are in 'detached HEAD' state（你处于“分离的 HEAD”状态）

Git 将你目前所在的提交称为 HEAD。

HEAD保存的就是分支的名称

可通过切换到前一个提交来“分离”HEAD(危险)。


git checkout 4035769377cce96a88d5c1167079e12f30492391 获取之前版本的文件，恢复文件

git init 
git status
git add filename
git commit filename


避免每次提交到github都输入用户名密码:
设置credential.helper为store
git config --global credential.helper store
查看用户配置
git config --list
push时再次提交提示输入用户名密码，这次会把用户名密码记录在.git-credential文件中
下次就不同输入用户名密码了



提交代码到github:
git add remote origin https://github.com/TonyXXQ/reflections
git push origin master

github页面上有一个fork按钮表示把别人的仓库clone到自己的github上的自己的仓库
然后再把该仓库clone到本地 就可以在自己的仓库上修改了

git commit 只是提交到本地 
如果要远程仓库可见必须使用git push


说明Fork、克隆和分支之间的不同。


github多人合作：
在settings中的Collaborators中邀请合作者，合作者就可以提交代码合作开发


pull的时候git会自动进行merge
git pull = git fetch + git merge


git clone https://github.com/TonyXXQ/reflections
git remote -v
origin  https://github.com/TonyXXQ/recipes (fetch)
origin  https://github.com/TonyXXQ/recipes (push)
origin/master
origin表示远程指向的代码库,master表示默认常见的一个分支
origin表示的只是一个名字而已，如果想加一个远程指向代码库，可以如下
git remote add upstream https://github.com/user1/repository.git
提交的时候orgin/master表示提交到orgin远程库的master目录
          upstream/master表示提交到upstream远程库的master目录


git fetch只是获取不会merge
git pull会进行merge
git fetch比较安全


fast-forward-merge:子孙和祖先的关系
no-fast-forward-merge：



Git HEAD detached from XXX (git HEAD 游离) 解决办法
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
















