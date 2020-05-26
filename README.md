# medium-java-exams
medium-java-exams is an example project to study secure software development. 
It contains a simple application to perform multiple-choice exams with multiple participations.
The application provides the following features:
- A teacher can create an exam by uploading an XML-File with the exam definition. 
- The teacher gets an exam key he can share with the students.
- Students can then perform the exams and get instant feedback if they responded the questions correctly.
- The teacher can check who has already successfully taken the exam.
- The teacher can stop the exam and get a final overview over the participations

Technically, the application contains a Spring Boot Backend using a PostgreSQL-DB and an 
Angular Frontend. Authentication is performed using OAUTH2.

## Installing / Getting started
To start this locally a Oauth2-Server and a postgreSQL-DB is needed. A docker compose file to start
these is provided:
```
docker-compose -f docker/docker-compose.yml up
```
Afterwards the actual application can be started using
```
cd medium-java-exams-service
../mvnw spring-boot:run
```

The application can then be reached at http://localhost:8080. A test user 'user' with password 'test' is already created.
New user can be created using self registration.

#Development
You can also start the UI individually using
```
cd medium-java-exams-ui
./npm.sh start
```

