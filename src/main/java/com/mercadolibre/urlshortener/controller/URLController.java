package com.mercadolibre.urlshortener.controller;

import com.mercadolibre.urlshortener.dto.ShortURLDto;
import com.mercadolibre.urlshortener.metrics.UrlShortenerMetrics;
import com.mercadolibre.urlshortener.service.URLService;
import com.mercadolibre.urlshortener.util.URLUtil;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.mercadolibre.urlshortener.constants.URLShortenerConstants.*;

@RestController
@RequestMapping
public class URLController {

    @Autowired
    private URLService urlService;

    @GetMapping("/{shortCode}")
    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    public void redirectToOriginalUrl(@PathVariable String shortCode, HttpServletResponse response) throws IOException {

        Timer timer = Metrics.timer(URL_REDIRECT_TIME);
        long startTime = System.currentTimeMillis();

        String originalUrl = urlService.getOriginalUrl(shortCode);
        if (StringUtils.isNotBlank(originalUrl)) {
            response.setHeader("Location", originalUrl);
            long duration = System.currentTimeMillis() - startTime;
            timer.record(duration, TimeUnit.MILLISECONDS);
            return;
        }

        // If the original URL is not found, return not found error
        response.sendError(HttpStatus.NOT_FOUND.value(), "URL not found or not valid");
    }

    @PostMapping
    public ResponseEntity<ShortURLDto> shortenURL(@RequestBody ShortURLDto request) {
        // Shorten the URL and return the shortened URL
        // Validate request
        if (URLUtil.isValidRequestInsert(request)) {
            return ResponseEntity.badRequest().build();
        }

        long startTime = System.currentTimeMillis();

        ShortURLDto shortenedURL = urlService.shortenUrl(request);
        // Increment the counter for method invocations
        UrlShortenerMetrics.counter(URL_SHORTEN_COUNTER).increment();

        long duration = System.currentTimeMillis() - startTime;
        UrlShortenerMetrics.timer(URL_SHORTEN_TIME).record(duration, TimeUnit.MILLISECONDS);

        return ResponseEntity.ok(shortenedURL);
    }

    @PutMapping("/{shortCode}")
    public ResponseEntity<ShortURLDto> updateShortURL(@PathVariable String shortCode, @RequestBody ShortURLDto updateDTO) {

        if (!URLUtil.isValidRequestUpdate(updateDTO) || StringUtils.isBlank(shortCode)) {
            return ResponseEntity.badRequest().build();
        }

        ShortURLDto updatedDTO = urlService.updateShortUrl(shortCode, updateDTO);

        if (updatedDTO != null) {
            return ResponseEntity.ok(updatedDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{shortCode}/data")
    public ResponseEntity<ShortURLDto> getShortUrl(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        ShortURLDto shortURLDto = urlService.getShortUrl(shortCode);
        if (Objects.nonNull(shortURLDto)) {
            return ResponseEntity.ok(shortURLDto);
        }
        return ResponseEntity.notFound().build();
    }
}
