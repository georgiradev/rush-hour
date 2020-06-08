FROM openjdk:11
COPY target/rush-hour-1.0.0-RELEASE.jar rush-hour-1.0.0-RELEASE.jar
ENTRYPOINT ["java","-jar","rush-hour-1.0.0-RELEASE.jar"]