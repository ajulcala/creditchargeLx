FROM openjdk:11
VOLUME /tmp
EXPOSE 8016
ADD ./target/creditcharge-0.0.1-SNAPSHOT.jar creditcharge.jar
ENTRYPOINT ["java","-jar","/creditcharge.jar"]