FROM eclipse-temurin:21-jre-alpine-3.24
WORKDIR /app
COPY quotes.jar quotes.jar
ENV QUOTES_CODE=EUR
CMD ["java", "-jar", "quotes.jar"]
