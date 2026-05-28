# BillSplit — Backend

Spring Boot REST API for the BillSplit receipt scanner. Accepts a receipt image, sends it to the Gemini Vision API, and returns structured JSON with items, prices, store, date, and time.

## Requirements

- Java 17
- A [Google Gemini API key](https://aistudio.google.com/app/apikey)

## Setup

Create a `.env` file in the project root:

```
GEMINI_API_KEY=your_key_here
```

## Running

```bash
chmod +x gradlew
./gradlew bootRun
```

The server starts on `http://localhost:8080`.

## API

### `POST /api/receipt/scan`

Upload a receipt image and get back structured data.

**Request:** `multipart/form-data` with a `file` field (JPEG or PNG, max 10 MB)

**Response:**

```json
{
	"store": "Bauhaus",
	"date": "14/10-18",
	"time": "15:50",
	"items": [
		{ "name": "Vedklyv HL 650", "price": "2995.00", "isDiscount": false },
		{ "name": "DEKORT", "price": "-495.00", "isDiscount": true }
	],
	"total": "1800.00",
	"moms": "360.00"
}
```

Missing fields are returned as empty strings. Discounts are negative prices with `isDiscount: true`.

## Project structure

```
src/main/java/com/example/demo/
├── controller/   ReceiptController.java   — REST endpoint
├── service/      GeminiService.java       — Gemini API integration
├── dto/          ReceiptData.java         — response shape
└── config/       CorsConfig.java          — allows requests from localhost:5173
```
