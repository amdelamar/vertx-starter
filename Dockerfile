FROM openjdk:8-jre-alpine

ENV VERTICLE_FILE vertx-starter-0.1.0.jar
ENV VERTICLE_HOME /usr/verticles

# Copy App and resources
COPY build/libs/$VERTICLE_FILE $VERTICLE_HOME/
COPY webroot $VERTICLE_HOME/webroot/
COPY deploy $VERTICLE_HOME/deploy/

EXPOSE 8080 8443

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh","-c"]
CMD ["exec java -jar $VERTICLE_FILE"]