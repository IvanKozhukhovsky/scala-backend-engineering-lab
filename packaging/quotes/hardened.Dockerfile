FROM eclipse-temurin:21-jre-alpine-3.24
WORKDIR /app
COPY quotes.jar quotes.jar
ENV QUOTES_CODE=EUR
RUN adduser -D -H -u 1000 app
USER 1000
CMD ["java", "-jar", "quotes.jar"]
