FROM openjdk:14-alpine
EXPOSE 8080
RUN apk add --no-cache tzdata
RUN cp /usr/share/zoneinfo/Asia/Bangkok /etc/localtime
COPY ./target /usr/src
WORKDIR /usr/src
CMD ["java", "-jar", "camunda-viriyah.jar"]
