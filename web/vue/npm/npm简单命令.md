npm的一下简单命令：

```
# 安装包，如果node_modules有包不需要更新，即使远程仓库有新包
$ npm install <packageName>

# 强制重新安装
$ npm install <packageName> -f 

# 更新安装包，远程仓库有新包，则更新
$ npm update <packageName>

# 查看包的相关信息
$ npm view <packageName>

# 获取缓存目录位置（Linux 或 Mac 默认是用户主目录下的.npm，在 Windows 默认是%AppData%/npm-cache）
$ npm config get cache

# 清空缓存
npm cache clean

# 从缓存中获取，离线安装
npm-cache install <packageName>

# 使用淘宝镜像，或者使用cnpm
npm config set registry https://registry.npm.taobao.org

# 判断npm淘宝镜像配置收成功
npm config get registry
```

