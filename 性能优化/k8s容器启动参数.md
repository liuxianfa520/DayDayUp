jekins打包：

```
+ mvn clean package -DskipTests -pl wbs-web -am -U
这里省略mvn打包日志。。。。

+ cp -rf wbs-web/target/wbs-web.jar /data/docker/build/wbs-web/apps/wbs-web.jar
+ cd /data/docker/build/wbs-web
+ /bin/sh docker_build.sh yes
Sending build context to Docker daemon  102.5MB

Step 1/7 : FROM hub.newbanker.cn/newbanker/jdk:2018120701
 ---> 6e9e122ad620
Step 2/7 : MAINTAINER  Duqiu  qiudu@newbanker.cn
 ---> Using cache
 ---> 206aebfd5bc6
Step 3/7 : USER root
 ---> Using cache
 ---> f765c6ac1b1d
Step 4/7 : ADD apps /apps/
 ---> 6fa9c7b7b853
Step 5/7 : EXPOSE 8080
 ---> Running in 9a7ed3a66a0e
Removing intermediate container 9a7ed3a66a0e
 ---> 026a0e090739
Step 6/7 : ENV JAVA_OPTS="-server -Xms2048m -Xmx2048m -Xmn1024m -XX:MetaspaceSize=256M -XX:MaxTenuringThreshold=10 -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=50 -XX:ParallelGCThreads=4 -verbosegc -XX:+PrintGCDetails  -XX:+PrintGCDateStamps -Xloggc:/apps/logs/gc_tststs.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/apps/logs"
 ---> Running in 14820c8cb455
Removing intermediate container 14820c8cb455
 ---> a21512d72fd2
Step 7/7 : ENTRYPOINT /apps/start.sh "$JAVA_OPTS" && tail -f /apps/logs/application.log
 ---> Running in b59e68cce546
Removing intermediate container b59e68cce546
 ---> 5b907d652aad
Successfully built 5b907d652aad
Successfully tagged hub.newbanker.cn/newbanker/wbs-web:202108171347
```







```sh
sh-4.2# cat start.sh
#!/bin/sh
JAVA_OPTS=""
if [ "$1" != "" ]; then
     JAVA_OPTS="$1"
fi

ti=`date +%Y%m%d%H%M%S`

JAVA_OPTS=${JAVA_OPTS/tststs/$ti}

java $JAVA_OPTS -jar -Dserver.port=8080 -Dfile.encoding=UTF8 -Djava.io.tmpdir=/apps/logs /apps/sop-web-api.jar --spring.config.location=/apps/application.properties
```



容器中java启动命令为：

```sh
java -server 
-Xms2048m 
-Xmx2048m 
-Xmn1024m 
-XX:MetaspaceSize=256M 
-XX:MaxTenuringThreshold=10 
-XX:+UseG1GC 
-XX:MaxGCPauseMillis=200 
-XX:InitiatingHeapOccupancyPercent=50 
-XX:ParallelGCThreads=4 
-verbosegc 
-XX:+PrintGCDetails  
-XX:+PrintGCDateStamps 
-Xloggc:/apps/logs/gc_tststs.log 
-XX:+HeapDumpOnOutOfMemoryError 
-XX:HeapDumpPath=/apps/logs 
-Dserver.port=8080 
-Dfile.encoding=UTF8 
-Djava.io.tmpdir=/apps/logs 
-jar /apps/xxx-web.jar 
--spring.config.location=/apps/application.properties
```

