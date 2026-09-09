# quotes local startup

Version: 0.1.0
Image: quotes.jar as USER 1000 with HEALTHCHECK GET http://127.0.0.1:8080/health

## API examples

- GET /health → 200
- GET /rates/EUR → 200
- GET /rates/JPY → 404
- POST /convert {"code":"EUR","qty":10} → 200

This repository remains a learning lab. quotes 0.1.0 is initial development, not a hosted production service.
