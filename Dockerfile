FROM java:8

RUN mkdir -p /opt
RUN mkdir -p /data

ADD target/scala-2.11/ilpartdemain-assembly-*.jar /opt/app.jar

CMD ["java", "-jar", "/opt/app.jar"]
