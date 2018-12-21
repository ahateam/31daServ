nohup java -jar zero-0.0.1-fat.jar &  

ps -aux | grep java
kill -9 xxxxx


java -Xms512m -Xmx1024m -jar D:/jar/helloworld-jar-with-dependencies.jar



shell脚本用不了
是脚本中包含了\r造成的
unix的换行是\n，dos等系统换行是\r\n
所以要注意shell脚本的格式问题。
此外也可以使用dos2unix命令转换shell文件格式

安装dos2unix
yum install dos2unix -y

使用
dos2unix *.sh