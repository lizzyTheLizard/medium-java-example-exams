FROM maven AS build  
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/main/java /build/src/main/java
COPY src/main/resources /build/src/main/resources
COPY src/test/java /build/src/test/java
COPY src/test/resources /build/src/test/resources
RUN mvn package

FROM openjdk
COPY --from=build /build/target/medium-java-security-exams.jar /medium-java-security-exams.jar
EXPOSE 8080  
ENTRYPOINT ["java","-jar","/medium-java-security-exams.jar"]  
