#### 设置局域网中的服务器免密登陆

1. 每台系统设置固定ip地址

2. 安装 openssh-server

   ```
   sudo apt-get autoremove openssh-client 
   sudo apt-get install openssh-server openssh-client 
   ```

3. 设置每台服务器的域名

   ```
   192.168.10.154 s0 
   192.168.10.138 s1
   ```

4. 在各个服务器中生成秘钥和私钥

   ```
   ssh-keygen -t rsa
   ```

5. 把各自的公钥文件复制到每台服务器

   ```
   # 复制出的服务器执行的命令
   cat     .ssh/id_rsa.pub  >> .ssh/authorized_keys
   chmod    600 authorized_keys
   scp      authorized_keys zhaolei@n2:~/
   
   # 接收的服务器执行的命令
   mv  authorized_keys    .ssh/
   chmod   600 authorized_keys
   ```

6. 验证是否成功

   ```
   ssh s0
   ```