- nginx�İ�װ��ж��

  ```
  # ��װ
  sudo apt-get install nginx

  # ж��
  sudo apt-get purge nginx
  ```

- ����nginx

  ```
  sudo /etc/init.d/nginx start

  sudo service nginx start
  ```

- ֹͣnginx

  ```
  sudo /etc/init.d/nginx stop

  sudo service nginx stop
  ```

- ����nginx

  ```
  sudo /etc/init.d/nginx restart

  sudo service nginx restart

  ```

- ���������ļ�

  ```
  sudo nginx -t 

  # ���������ļ�
  sudo vim /etc/nginx/sites-avaiable/default 
  ```

- �鿴����״̬

  ```
  sudo service nginx status
  ```


- ��������������������

  ������� ��һ��λ�ڿͻ��˺�ԭʼ������(origin server)֮��ķ�������Ϊ�˴�ԭʼ������ȡ�����ݣ��ͻ����������һ������ָ��Ŀ��(ԭʼ������)��Ȼ�������ԭʼ������ת�����󲢽���õ����ݷ��ظ��ͻ��ˡ��ͻ��˱���Ҫ����һЩ�ر�����ò���ʹ���������
   �������Reverse Proxy��ʵ�����з�ʽ��ָ�Դ��������������internet�ϵ���������Ȼ������ת�����ڲ������ϵķ������������ӷ������ϵõ��Ľ�����ظ�internet���������ӵĿͻ��ˣ���ʱ�������������ͱ���Ϊһ����������

 

 

