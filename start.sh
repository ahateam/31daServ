nohup java -jar zero-0.0.1-fat.jar &
echo $! > /var/run/zero-0.0.1-fat.pid
echo "server started"