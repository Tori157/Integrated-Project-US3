FROM maven:3.8.3-openjdk-17
COPY . /backendAPI
WORKDIR /backendAPI
RUN mvn clean package -DskipTests
ENTRYPOINT ["java","-jar","./target/backend-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080
