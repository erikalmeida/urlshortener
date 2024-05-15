package com.mercadolibre.urlshortener.service.impl;

import com.mercadolibre.urlshortener.converter.ShortURLConverter;
import com.mercadolibre.urlshortener.domain.ShortURL;
import com.mercadolibre.urlshortener.dto.ShortURLDto;
import com.mercadolibre.urlshortener.repository.ShortURLRepository;
import com.mercadolibre.urlshortener.service.URLService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static com.mercadolibre.urlshortener.constants.URLShortenerConstants.ALPHABET;
import static com.mercadolibre.urlshortener.constants.URLShortenerConstants.SHORT_URL_CHAR_SIZE;

@Service
@Slf4j
@NoArgsConstructor
@Transactional
public class URLServiceImpl implements URLService {

    public static final String URL_NOT_FOUND = "URL not found";
    private ShortURLRepository shortURLRepository;
    private final Random random = new Random();

    @Autowired
    public URLServiceImpl(ShortURLRepository shortURLRepository) {
        this.shortURLRepository = shortURLRepository;
    }

    @Override
    public ShortURLDto shortenUrl(ShortURLDto shortURLDto) {
        log.info("Starting URL shortening");
        // Cast DTO to model
        ShortURL shortURL = ShortURLConverter.toEntity(shortURLDto, generateRandomShortUrl());
        // Save and  convert entity back to DTO and return
        return ShortURLConverter.toDto(shortURLRepository.save(shortURL));
    }

    @Override
    public ShortURLDto updateShortUrl(String shortCode, ShortURLDto updateDto) {
        // search for existing record
        log.info("Starting record update");
        Optional<ShortURL> savedUrl = shortURLRepository.findByShortCode(shortCode);
        if (savedUrl.isPresent()) {
            var existingUrl = savedUrl.get();
            if (Objects.nonNull(updateDto.getValidUrl())) {
                existingUrl.setValidUrl(updateDto.getValidUrl());
            }
            // Convert saved or updated entity back to DTO and return
            return ShortURLConverter.toDto(shortURLRepository.save(existingUrl));
        }
        // If data is invalid, return null
        log.info(URL_NOT_FOUND);
        return null;
    }

    @Override
    public String getOriginalUrl(String urlId) {
        // Retrieve ShortURL entity from repository
        log.info("Starting URL redirection");
        ShortURL shortURL = shortURLRepository.findByShortCode(urlId).orElse(null);
        if (shortURL != null && shortURL.isValidUrl()) {
            //increment clicks
            shortURL.setClicks(shortURL.getClicks() + 1);
            shortURLRepository.save(shortURL);
            // return the redirect URL
            return shortURL.getOriginalUrl();
        }
        // If ShortURL not found, return null or throw an exception
        log.info(URL_NOT_FOUND);
        return null;
    }

    @Override
    public ShortURLDto getShortUrl(String urlId) {
        log.info("Starting URL fetch");
        ShortURL shortURL = shortURLRepository.findByShortCode(urlId).orElse(null);
        if (shortURL != null) {
            // return the redirect URL
            return ShortURLConverter.toDto(shortURL);
        }
        // If ShortURL not found, return null or throw an exception
        log.info(URL_NOT_FOUND);
        return null;
    }

    public String generateRandomShortUrl() {
        int maxRetries = 1000; // Maximum number of retries
        for (int retry = 0; retry < maxRetries; retry++) {
            String shortCode = generateRandomShortCode();
            if (isShortCodeUnique(shortCode)) {
                return shortCode;
            }
        }
        throw new RuntimeException("Failed to generate a unique short URL after " + maxRetries + " retries.");
    }

    private String generateRandomShortCode() {
        char[] result = new char[SHORT_URL_CHAR_SIZE];
        for (int i = 0; i < SHORT_URL_CHAR_SIZE; i++) {
            int randomIndex = random.nextInt(ALPHABET.length());
            result[i] = ALPHABET.charAt(randomIndex);
        }
        return new String(result);
    }

    private boolean isShortCodeUnique(String shortLink) {
        return shortURLRepository.findByShortCode(shortLink).isEmpty();
    }

}
