# Vert.x Starter

A simple Java webapp using Eclipse Vert.x (http://vertx.io)

## Prerequisites

* [JDK 1.8](https://www.java.com/en/download/faq/develop.xml)
* [Eclipse](https://eclipse.org/downloads/) or [Spring Tools Suite](https://spring.io/tools) (Optional)
* [Docker](https://docs.docker.com/engine/installation/) (Optional)

## Run Manually

1. Download code `git clone https://github.com/amdelamar/vertx-starter`
1. `cd vertx-starter`
1. Run build `./gradlew clean build`
1. Start server `java -jar build/libs/vertx-starter-0.1.0-fat.jar`
1. Visit [http://localhost:8080/](http://localhost:8080/) to see the app running.

## Run with Redeploy

App can be run in redeploy mode, so any changes to files are recompiled quickly. Which is useful for development.

1. Run redeploy `./gradlew vertxRun`
1. Visit [http://localhost:8080/](http://localhost:8080/) to see the app running.

This last command launches the application and redeploys as soon as you change something in `src/`.

## Run in Docker

1. Run build `./gradlew clean build`
1. Build image `docker build -t vertx-starter .`
1. Run container `docker run -p 8080:8080 vertx-starter`
1. Visit [http://localhost:8080/](http://localhost:8080/) to see the app running.

## License

[MIT](/LICENSE)
