FROM eclipse-temurin:21-jre-alpine-3.24
WORKDIR /app
COPY quotes.jar quotes.jar
ENV QUOTES_CODE=EUR
RUN adduser -D -H -u 1000 app
USER 1000
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD ["wget", "-qO-", "http://127.0.0.1:8080/health"]
CMD ["java", "-jar", "quotes.jar"]
