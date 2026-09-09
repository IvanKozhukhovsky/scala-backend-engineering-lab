# desk local startup

Version: 0.1.0
Image: desk.jar as USER 1000 with HEALTHCHECK GET http://127.0.0.1:8080/health

## API examples

- GET /health → 200
- GET /orders/GBP → 200
- GET /orders/JPY → 404
- POST /orders {"code":"GBP","qty":10} → 200

This repository remains a learning lab. desk 0.1.0 is initial development, not a hosted production service.
