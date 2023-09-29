ARG APP_HOME=/app

FROM gradle:jdk11-alpine as build-image
ARG APP_HOME
WORKDIR $APP_HOME
COPY --chown=gradle:gradle . .
#RUN ./gradlew build
RUN gradle build


FROM amazoncorretto:11-alpine3.15-jdk
ARG APP_HOME
COPY --from=build-image $APP_HOME/build/classes/java/main .