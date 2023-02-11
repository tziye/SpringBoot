FROM openjdk:11

WORKDIR /app

COPY target/*.jar app.jar
COPY deploy/run.sh run.sh
COPY deploy/jmxremote.password jmxremote.password
COPY deploy/arthas-3.5.0 arthas

RUN chmod 777 run.sh
RUN chmod 0400 jmxremote.password

RUN mkdir -p log/gc

# locale -a查看支持的UTF-8 
ENV LANG C.UTF-8  
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone

EXPOSE 9999
EXPOSE 19090
EXPOSE 28088

CMD ["sh", "run.sh"]

# 打包：mvn clean package -Dmaven.test.skip
# 构建镜像：docker build -f deploy/Dockerfile -t tziye/springboot:v1.0 .
# 运行：docker run -p 9999:9999 -p 19090:19090 -p 28088:28088 -e SERVER_HOST=localhost --link mysql:db -d tziye/springboot:v1.0