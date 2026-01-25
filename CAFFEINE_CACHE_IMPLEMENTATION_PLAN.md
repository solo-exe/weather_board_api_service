# Caffeine Cache Implementation Plan for Map Tiles

## Overview
This plan implements in-memory caching for OpenWeather map tiles using Caffeine to reduce API calls by ~90%+.

---

## Implementation Steps

### **Step 1: Add Dependencies to `pom.xml`**

Add Spring Cache and Caffeine dependencies after line 80 (after the actuator dependency):

```xml
<!-- Spring Cache Support -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<!-- Caffeine Cache Implementation -->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

**What this does:**
- `spring-boot-starter-cache`: Enables Spring's caching abstraction
- `caffeine`: High-performance in-memory cache implementation

---

### **Step 2: Configure Cache Settings in `application.yaml`**

Add cache configuration after the `logging` section:

```yaml
# Cache Configuration
spring:
  cache:
    type: caffeine
    cache-names: mapTiles
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=7d,recordStats
```

**Configuration explained:**
- `maximumSize=1000`: Store up to 1000 tiles (~50-100MB depending on tile size)
- `expireAfterWrite=7d`: Tiles expire after 7 days (they rarely change)
- `recordStats`: Enable cache statistics for monitoring

**Adjust if needed:**
- For more memory, increase `maximumSize` to 2000-5000
- For frequently updating layers, reduce `expireAfterWrite` to 1d or 12h

---

### **Step 3: Enable Caching in Application Class**

Update `WeatherBoardApiServiceApplication.java`:

```java
package com.sollo_script.weather_board_api_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching  // Add this annotation
public class WeatherBoardApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherBoardApiServiceApplication.class, args);
    }
}
```

**What this does:**
- `@EnableCaching`: Activates Spring's annotation-driven cache management

---

### **Step 4: Add @Cacheable to Map Tile Method**

Update `WeatherController.java` - modify the `getMapLayer` method:

```java
@GetMapping(value = "/map_layer/{mapType}/{z}/{x}/{y}", produces = "image/png")
@ResponseBody
@Cacheable(value = "mapTiles", key = "#mapType + '_' + #z + '_' + #x + '_' + #y")
public ResponseEntity<byte[]> getMapLayer(
        @PathVariable String mapType,
        @PathVariable int z,
        @PathVariable int x,
        @PathVariable int y,
        @RequestParam Optional<String> apiKey) {

    byte[] imageBytes = weatherService.getMapLayerImage(mapType, z, x, y, apiKey);

    System.out.println("WeatherController.getMapLayer() - Tile: " + mapType + "/" + z + "/" + x + "/" + y);

    return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS)) // Add HTTP cache headers
            .contentLength(imageBytes.length)
            .body(imageBytes);
}
```

**Add this import at the top:**
```java
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import java.util.concurrent.TimeUnit;
```

**What this does:**
- `@Cacheable`: Caches the response using the cache key
- Cache key format: `clouds_new_5_15_20` (unique per tile)
- `CacheControl.maxAge(7, TimeUnit.DAYS)`: Tells browsers to cache tiles for 7 days

---

### **Step 5: (Optional) Create Cache Configuration Class**

For more control, create a dedicated cache configuration class:

**File:** `src/main/java/com/sollo_script/weather_board_api_service/config/CacheConfig.java`

```java
package com.sollo_script.weather_board_api_service.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("mapTiles");
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(1000)                        // Max 1000 tiles
                .expireAfterWrite(7, TimeUnit.DAYS)       // Expire after 7 days
                .recordStats();                            // Enable statistics
    }
}
```

**Note:** If you create this class, you can remove the cache configuration from `application.yaml` and remove `@EnableCaching` from the main application class (it's now in `CacheConfig`).

**Choose either:**
- ✅ **Option A**: YAML configuration (simpler, recommended for now)
- ✅ **Option B**: Java configuration class (more control, better for complex setups)

---

### **Step 6: (Optional) Frontend Optimization**

Update `Map.tsx` to help browser caching:

```typescript
<TileLayer
    opacity={0.7}
    keepBuffer={8}  // Keep extra tiles in memory (default is 2)
    updateWhenZooming={false}  // Don't request tiles while actively zooming
    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    url={`http://localhost:7002/api/v1/openweather/map_layer/${mapType}/{z}/{x}/{y}${apiKey ? `?apiKey=${apiKey}` : ""}`}
/>
```

---

## Testing the Implementation

### **1. Build and Run**

```bash
cd weather-board-api-service
mvn clean install
mvn spring-boot:run
```

### **2. Verify Cache is Working**

Watch the console logs when loading the map:

**First load (cache MISS):**
```
Calling Tile URI: http://tile.openweathermap.org/map/clouds_new/5/15/10.png?apiKey=xxx
WeatherController.getMapLayer() - Tile: clouds_new/5/15/10
```

**Second load (cache HIT - no external API call):**
```
WeatherController.getMapLayer() - Tile: clouds_new/5/15/10
```

Notice: The "Calling Tile URI" log from `WeatherServiceImpl` should **NOT** appear on cache hits.

### **3. Monitor Cache Statistics**

Add this endpoint to see cache stats:

```java
// Add to WeatherController.java
@GetMapping("/cache/stats")
@ResponseBody
public ResponseEntity<String> getCacheStats() {
    CacheManager cacheManager = this.cacheManager; // Inject CacheManager
    Cache cache = cacheManager.getCache("mapTiles");
    if (cache instanceof CaffeineCache) {
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = 
            (com.github.benmanes.caffeine.cache.Cache<Object, Object>) 
            ((CaffeineCache) cache).getNativeCache();
        return ResponseEntity.ok(nativeCache.stats().toString());
    }
    return ResponseEntity.ok("Cache stats not available");
}
```

Then visit: `http://localhost:7002/api/v1/openweather/cache/stats`

---

## Expected Results

### **Performance Metrics**

| Scenario | Before Caching | After Caching | Improvement |
|----------|---------------|---------------|-------------|
| Initial map load (50 tiles) | 50 API calls | 50 API calls | 0% (first load) |
| Reload same location | 50 API calls | **0 API calls** | **100%** ✅ |
| Pan to nearby area (20 new tiles) | 20 API calls | **5-10 API calls** | **50-75%** ✅ |
| Zoom in/out (30 new tiles) | 30 API calls | **10-15 API calls** | **50-67%** ✅ |

### **Cost Savings**
- **Daily API usage reduction**: ~85-95%
- **Example**: 10,000 tile requests → ~1,500 actual API calls
- **Memory usage**: ~50-150MB for 1000 tiles

---

## Troubleshooting

### **Cache not working?**

1. **Check logs** - Ensure you see "Calling Tile URI" only on first load
2. **Verify `@EnableCaching`** - Must be present on application class or config class
3. **Check dependencies** - Run `mvn dependency:tree` to verify Caffeine is included
4. **Clear cache** - Restart the server to clear the cache

### **Memory issues?**

Reduce `maximumSize` in configuration:
```yaml
caffeine:
  spec: maximumSize=500,expireAfterWrite=7d,recordStats
```

### **Tiles not updating?**

If OpenWeather updates tiles frequently, reduce TTL:
```yaml
caffeine:
  spec: maximumSize=1000,expireAfterWrite=1d,recordStats
```

Or manually clear cache:
```java
@Autowired
private CacheManager cacheManager;

public void clearMapTilesCache() {
    cacheManager.getCache("mapTiles").clear();
}
```

---

## Next Steps (Future Enhancements)

1. **Add cache eviction endpoint** (for manual clearing)
2. **Implement Redis** (for multi-instance deployments)
3. **Add cache warming** (pre-load popular tiles)
4. **Monitor cache hit rates** (Actuator metrics)
5. **Cache other endpoints** (weather data with shorter TTL)

---

## Summary Checklist

- [ ] Add dependencies to `pom.xml`
- [ ] Add cache config to `application.yaml`
- [ ] Add `@EnableCaching` to main application class
- [ ] Add `@Cacheable` to `getMapLayer` method
- [ ] Add import statements for `Cacheable`, `CacheControl`, `TimeUnit`
- [ ] Add HTTP cache headers to response
- [ ] Run `mvn clean install`
- [ ] Test with frontend - verify reduced API calls
- [ ] Monitor logs to confirm cache hits

**Estimated implementation time:** 15-30 minutes

---

## Questions?

- **When will cache populate?** - On first request for each unique tile
- **What if I change map type?** - Each map type has separate cache entries
- **Will cache survive restart?** - No, Caffeine is in-memory only (use Redis for persistence)
- **Can I cache other endpoints?** - Yes! Add `@Cacheable` to any method

Good luck with implementation! 🚀
