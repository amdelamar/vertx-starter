FROM openjdk:8-jre-alpine

# Copy app to new directory
RUN mkdir -p /usr/vertx-starter
COPY build/libs/vertx-starter-0.1.0-fat.jar /usr/vertx-starter

# Work out of the directory
WORKDIR /usr/vertx-starter

# Expose http ports
EXPOSE 8080 8443

# Start java application
CMD ["java","-jar","spark-starter-0.1.0-fat.jar"]
