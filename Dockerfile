FROM maven AS build  
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src /build/src/
RUN mvn package

FROM openjdk
COPY --from=build /build/target/medium-java-security-exams.jar /medium-java-security-exams.jar
EXPOSE 8080  
ENTRYPOINT ["java","-jar","/medium-java-security-exams.jar"]  
