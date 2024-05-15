package com.mercadolibre.urlshortener.service.impl;

import com.mercadolibre.urlshortener.domain.ShortURL;
import com.mercadolibre.urlshortener.dto.ShortURLDto;
import com.mercadolibre.urlshortener.repository.ShortURLRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

class URLServiceImplTest {

    @Mock
    private ShortURLRepository shortURLRepository;

    @InjectMocks
    private URLServiceImpl urlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static Stream<Arguments> provideShortenUrlTestData() {
        ShortURLDto validInputDto = ShortURLDto.builder().originalUrl("https://www.example.com").shortCode(null).build();
        ShortURL savedEntity = ShortURL.builder().originalUrl("https://www.example.com").shortCode("abc123").build();

        return Stream.of(
                Arguments.of(validInputDto, savedEntity),
                Arguments.of(ShortURLDto.builder().originalUrl("https://www.example.com$%").shortCode(null).build(), savedEntity),
                Arguments.of(ShortURLDto.builder().originalUrl(generateLongUrl()).shortCode(null).build(), savedEntity),
                Arguments.of(ShortURLDto.builder().originalUrl(null).shortCode(null).build(), null),
                Arguments.of(ShortURLDto.builder().originalUrl("").shortCode(null).build(), null)
        );
    }

    // Helper method to generate a long URL for testing purposes
    private static String generateLongUrl() {
        return "https://www.example.com/".repeat(1000);
    }

    @ParameterizedTest
    @MethodSource("provideShortenUrlTestData")
    void testShortenUrl(ShortURLDto inputDto, ShortURL savedEntity) {
        Mockito.when(shortURLRepository.save(any(ShortURL.class))).thenReturn(savedEntity);

        ShortURLDto resultDto = urlService.shortenUrl(inputDto);

        if (Objects.nonNull(savedEntity)) {
            Assertions.assertEquals(savedEntity.getOriginalUrl(), resultDto.getOriginalUrl());
            Assertions.assertEquals(savedEntity.getShortCode(), resultDto.getShortCode());
        }
        Mockito.verify(shortURLRepository, Mockito.times(1)).save(any(ShortURL.class));
    }


    private static Stream<Arguments> provideUpdateShortUrlTestData() {
        ShortURLDto validUpdateDto = ShortURLDto.builder().validUrl(true).build();
        ShortURL existingUrl = ShortURL.builder().originalUrl("https://www.example.com").shortCode("abc123").validUrl(true).build();
        ShortURL updatedUrl = ShortURL.builder().originalUrl("https://www.Updatedexample.com").shortCode("abc123").validUrl(true).build();
        ShortURLDto expectedDto = ShortURLDto.builder().originalUrl("https://www.Updatedexample.com").shortCode("abc123").validUrl(true).build();

        return Stream.of(
                Arguments.of("abc123", validUpdateDto, existingUrl, updatedUrl, expectedDto),
                Arguments.of("abc123", ShortURLDto.builder().validUrl(false).build(), existingUrl, updatedUrl, expectedDto),
                Arguments.of("invalidCode", validUpdateDto, null, null, null),
                Arguments.of(null, validUpdateDto, null, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUpdateShortUrlTestData")
    void testUpdateShortUrl(String shortCode, ShortURLDto updateDto, ShortURL existingUrl, ShortURL updatedUrl, ShortURLDto expectedDto) {
        Mockito.when(shortURLRepository.findByShortCode(shortCode)).thenReturn(Optional.ofNullable(existingUrl));
        Mockito.when(shortURLRepository.save(any(ShortURL.class))).thenReturn(updatedUrl);

        ShortURLDto resultDto = urlService.updateShortUrl(shortCode, updateDto);

        if (Objects.isNull(expectedDto)) {
            Assertions.assertNull(resultDto);
        } else {
            Assertions.assertNotNull(resultDto);
            Assertions.assertEquals(expectedDto.getOriginalUrl(), resultDto.getOriginalUrl());
            Assertions.assertEquals(expectedDto.getShortCode(), resultDto.getShortCode());
            Assertions.assertEquals(expectedDto.getValidUrl(), resultDto.getValidUrl());
        }
    }

    private static Stream<Arguments> provideGetOriginalUrlTestData() {
        ShortURL existingUrl = ShortURL.builder().originalUrl("https://www.example.com")
                .shortCode("abc123").validUrl(true).clicks(0).build();
        return Stream.of(
                Arguments.of("abc123", existingUrl, "https://www.example.com"),
                Arguments.of("invalidCode", null, null),
                Arguments.of(null, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetOriginalUrlTestData")
    void testGetOriginalUrl(String shortCode, ShortURL shortUrl, String expectedUrl) {
        Mockito.when(shortURLRepository.findByShortCode(shortCode)).thenReturn(Optional.ofNullable(shortUrl));

        var originalClicks = shortUrl != null ? shortUrl.getClicks() : 0;
        String resultUrl = urlService.getOriginalUrl(shortCode);

        Assertions.assertEquals(expectedUrl, resultUrl);
        if (shortUrl != null) {
            Assertions.assertEquals(originalClicks + 1, shortUrl.getClicks()); // Verify clicks incremented
            Mockito.verify(shortURLRepository, Mockito.times(1)).save(shortUrl); // Verify save method called to update clicks
        }
    }

    private static Stream<Arguments> provideGetShortUrlTestData() {
        ShortURL existingUrl = ShortURL.builder().originalUrl("https://www.example.com").shortCode("abc123").validUrl(true).build();
        ShortURLDto expectedDto = ShortURLDto.builder().originalUrl("https://www.example.com").shortCode("abc123").validUrl(true).build();

        return Stream.of(
                Arguments.of("abc123", existingUrl, expectedDto),
                Arguments.of("invalidCode", null, null),
                Arguments.of(null, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetShortUrlTestData")
    void testGetShortUrl(String shortCode, ShortURL shortUrl, ShortURLDto expectedDto) {
        Mockito.when(shortURLRepository.findByShortCode(shortCode)).thenReturn(Optional.ofNullable(shortUrl));

        ShortURLDto resultDto = urlService.getShortUrl(shortCode);

        if (expectedDto == null) {
            Assertions.assertNull(resultDto);
        } else {
            Assertions.assertNotNull(resultDto);
            Assertions.assertEquals(expectedDto.getOriginalUrl(), resultDto.getOriginalUrl());
            Assertions.assertEquals(expectedDto.getShortCode(), resultDto.getShortCode());
            Assertions.assertEquals(expectedDto.getValidUrl(), resultDto.getValidUrl());
        }
    }
}