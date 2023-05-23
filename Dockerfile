FROM openjdk:21-slim
EXPOSE 8080
RUN mkdir /home/app
WORKDIR /home/app
COPY target/apriori-0.0.1-SNAPSHOT.jar .

CMD ["java", "--add-opens", "java.base/jdk.internal.misc=ALL-UNNAMED", "-Dspring.profiles.active=prod", "-jar", "apriori-0.0.1-SNAPSHOT.jar"]
