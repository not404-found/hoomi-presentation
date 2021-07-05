FROM openjdk:11-jre-slim

WORKDIR /

COPY hoomi-firebase-firebase-adminsdk.json hoomi-firebase-firebase-adminsdk.json
ENV FIREBASE_KEY_PATH="hoomi-firebase-firebase-adminsdk.json"
COPY hoomi-management*.jar hoomi-management.jar


EXPOSE 8060
CMD ["java", "-jar", "hoomi-management.jar"]
