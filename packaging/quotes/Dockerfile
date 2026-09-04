FROM eclipse-temurin:24-jre-alpine-3.22
WORKDIR /app
COPY quotes.jar quotes.jar
ENV QUOTES_CODE=EUR
CMD ["java", "-jar", "quotes.jar"]
