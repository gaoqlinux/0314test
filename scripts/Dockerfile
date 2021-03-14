FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD file-server.jar file-server.jar
RUN echo "Asia/Shanghai" > /etc/timezone
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/file-server.jar"]
