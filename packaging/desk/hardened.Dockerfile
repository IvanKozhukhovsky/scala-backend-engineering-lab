FROM eclipse-temurin:21-jre-alpine-3.24
WORKDIR /app
COPY desk.jar desk.jar
ENV DESK_CODE=GBP
RUN adduser -D -H -u 1000 app
USER 1000
CMD ["java", "-jar", "desk.jar"]
