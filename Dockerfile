FROM node:21 AS ng-builder

RUN npm i -g @angular/cli

WORKDIR /ngapp

COPY miniproject2frontend/package*.json .
COPY miniproject2frontend/angular.json .
COPY miniproject2frontend/tsconfig.* .
COPY miniproject2frontend/src src

RUN npm ci && ng build


# Starting with this Linux server
FROM maven:3-eclipse-temurin-21 AS sb-builder

## Build the application
# Create a directory call /sbapp
# go into the directory cd /app
WORKDIR /sbapp

# everything after this is in /sbapp
COPY miniproject2/mvnw .
COPY miniproject2/mvnw.cmd .
COPY miniproject2/pom.xml .
COPY miniproject2/.mvn .mvn
COPY miniproject2/src src
COPY --from=ng-builder /ngapp/dist/miniproject2frontend/browser src/main/resources/static

# Build the application
RUN mvn package -Dmaven.test.skip=true

FROM openjdk:21-jdk-bullseye

WORKDIR /app 

COPY --from=sb-builder /sbapp/target/miniproject2-0.0.1-SNAPSHOT.jar app.jar

## Run the application
# Define environment variable 
ENV PORT=8080 

# Expose the port
EXPOSE ${PORT}

# Run the program
ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar