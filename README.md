# Vert.x Starter

A simple Java webapp using [Eclipse Vert.x](http://vertx.io)

## Prerequisites

* [Java 1.8](https://www.java.com/download/)
* [Docker](https://docs.docker.com/engine/installation/) (Optional)

## Download and Build

1. Download code `git clone https://github.com/amdelamar/vertx-starter`
1. `cd vertx-starter`
1. Run build `./gradlew clean build`
1. Test server `java -jar build/libs/vertx-starter-0.1.0.jar`
1. Visit [https://localhost:8443/](https://localhost:8443/) to see the app running.

## Run with Redeploy

App can be run in redeploy mode, so any changes to files are recompiled quickly. Which is useful for development.

1. Run redeploy `./gradlew run`
1. Visit [https://localhost:8443/](https://localhost:8443/) to see the app running.

This last command launches the application and redeploys as soon as you change something in `src/`.

## Run in Docker

1. Build image `docker build -t vertx-starter .`
1. Run container `docker run -p 8443:8443 vertx-starter`
1. Visit [https://localhost:8443/](https://localhost:8443/) to see the app running.

## License

[MIT](/LICENSE)
