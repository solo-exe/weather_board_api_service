# 🌤️ Weather Board API Gateway

A Spring Boot API gateway service that securely proxies OpenWeatherMap API requests, allowing frontend applications to access weather data without exposing API keys.

[![Live Frontend](https://img.shields.io/badge/Live%20Demo-ultra--weather--board-blue?style=for-the-badge&logo=vercel)](https://ultra-weather-board.vercel.app/)
[![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-green?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)

## 🎯 Purpose

This API gateway was created to support the [Ultra Weather Board](https://github.com/solo-exe/ultra-weather-board) frontend application, which was built following [Austin Davies Tech's](https://www.youtube.com/@AustinDavisTech) YouTube tutorial. The gateway enables learners to:

- **🔒 Keep API keys secure** - Your OpenWeatherMap API key stays on the server
- **🌐 Deploy frontends anywhere** - Host on Vercel, Netlify, GitHub Pages without exposing secrets
- **📊 Add rate limiting** - Protect against abuse with built-in request throttling
- **⚡ Improve performance** - Cache map tiles for faster subsequent loads

## 🚀 Quick Start

### Prerequisites

- Java 25 or higher
- Maven 3.x
- OpenWeatherMap API key ([Get one free](https://openweathermap.org/api))

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/solo-exe/weather_board_api_service.git
   cd weather_board_api_service
   ```

2. **Create your `.env` file** (copy from sample)
   ```bash
   cp .env.sample .env
   ```

3. **Edit `.env` with your API key**
   ```properties
   PORT=7002
   OPENWEATHERMAP_API_KEY=your_api_key_here
   OPENWEATHERMAP_BASE_URL=https://api.openweathermap.org
   OPENWEATHERMAP_TILE_BASE_URL=https://tile.openweathermap.org/map
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Test the API**
   ```bash
   curl "http://localhost:7002/api/v1/openweather/weather?lat=51.5&lon=-0.12"
   ```

### Docker Deployment

```bash
docker build -t weather-board-api .
docker run -p 7002:7002 --env-file .env weather-board-api
```

## 📡 API Endpoints

All endpoints are prefixed with `/api/v1/openweather`

| Endpoint | Method | Description | Parameters |
|----------|--------|-------------|------------|
| `/weather` | GET | Current weather & forecast | `lat`, `lon`, `apiKey?` |
| `/geocode` | GET | Location search | `location`, `limit`, `apiKey?` |
| `/air_pollution` | GET | Air quality data | `lat`, `lon`, `apiKey?` |
| `/map_layer/{type}/{z}/{x}/{y}` | GET | Weather map tiles | `type`: clouds, precipitation, temp, etc. |

### Example Requests

```bash
# Weather data
curl "https://weather-board-api-service.onrender.com/api/v1/openweather/weather?lat=6.5&lon=3.4"

# Location search
curl "https://weather-board-api-service.onrender.com/api/v1/openweather/geocode?location=London&limit=1"

# Air pollution
curl "https://weather-board-api-service.onrender.com/api/v1/openweather/air_pollution?lat=51.5&lon=-0.12"

# Map tiles (returns PNG)
curl "https://weather-board-api-service.onrender.com/api/v1/openweather/map_layer/clouds_new/5/16/10"
```

## 🏗️ Architecture

```
┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
│   Frontend App      │────▶│   API Gateway       │────▶│   OpenWeatherMap    │
│   (React/Vite)      │     │   (Spring Boot)     │     │   External API      │
│                     │     │                     │     │                     │
│   - No API keys     │     │   - Rate limiting   │     │   - Weather data    │
│   - Public hosting  │     │   - Caching         │     │   - Map tiles       │
│                     │     │   - API key storage │     │   - Geocoding       │
└─────────────────────┘     └─────────────────────┘     └─────────────────────┘
```

## ✨ Features

- **🪣 Rate Limiting** - Bucket4j-based per-IP rate limiting (2 requests/second burst, 5/second refill)
- **☕ Caffeine Caching** - In-memory caching for map tiles (7-day TTL, max 1000 entries)
- **📝 Request Logging** - JSON file-based daily usage tracking (auto-cleanup after 30 days)
- **🌐 CORS Enabled** - All origins allowed for flexible frontend integration
- **💾 Daily Limits** - 900 calls/day cap to prevent API quota exhaustion

## 📁 Project Structure

```
weather-board-api-service/
├── src/main/java/com/sollo_script/weather_board_api_service/
│   ├── controller/
│   │   └── WeatherController.java    # REST endpoints
│   ├── service/
│   │   ├── WeatherService.java       # Service interface
│   │   ├── RateLimitService.java     # Rate limiting
│   │   └── impl/
│   │       └── WeatherServiceImpl.java
│   ├── repository/
│   │   └── JsonDailyLogRepository.java  # File-based storage
│   ├── config/
│   │   ├── ApiClientConfig.java      # RestClient beans
│   │   ├── CacheConfig.java          # Caffeine setup
│   │   └── CorsConfig.java           # CORS configuration
│   ├── dto/                          # Response models
│   ├── entity/
│   │   └── DailyLog.java             # Usage tracking entity
│   └── exception/                    # Error handling
├── data/
│   └── daily_logs.json               # Usage data (auto-generated)
├── Dockerfile
├── pom.xml
└── .env.sample
```

## 🔗 Related Projects

- **Frontend**: [Ultra Weather Board](https://github.com/solo-exe/ultra-weather-board)
- **Live Demo**: [ultra-weather-board.vercel.app](https://ultra-weather-board.vercel.app/)
- **Tutorial Source**: [Austin Davies Tech](https://www.youtube.com/@AustinDavisTech)

## 📄 License

MIT License - feel free to use this for your own projects!

---

**Built with ❤️ by [solo-exe](https://github.com/solo-exe)** | *Inspired by Austin Davies Tech tutorials*
