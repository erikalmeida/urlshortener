package com.mercadolibre.urlshortener.converter;

import com.mercadolibre.urlshortener.domain.ShortURL;
import com.mercadolibre.urlshortener.dto.ShortURLDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ShortURLConverterTest {

    private static Stream<Arguments> provideToEntityTestData() {
        return Stream.of(
                //1
                Arguments.of(
                        ShortURLDto.builder()
                                .originalUrl("https://www.example.com")
                                .build(),
                        "abc123",
                        ShortURL.builder()
                                .originalUrl("https://www.example.com")
                                .shortCode("abc123")
                                .updateDate(null)
                                .clicks(0)
                                .build()
                ),
                Arguments.of(
                        null,
                        "abc123",
                        null
                ),
                Arguments.of(
                        ShortURLDto.builder()
                                .originalUrl("https://www.example.com")
                                .build(),
                        "abc123$%", // Special characters in new URL
                        ShortURL.builder()
                                .originalUrl("https://www.example.com")
                                .shortCode("abc123$%")
                                .updateDate(null)
                                .clicks(0)
                                .build()
                ),
                Arguments.of(
                        ShortURLDto.builder()
                                .originalUrl(generateLongUrl()) // Very long original URL
                                .build(),
                        "abc123",
                        ShortURL.builder()
                                .originalUrl(generateLongUrl())
                                .shortCode("abc123")
                                .updateDate(null)
                                .clicks(0)
                                .build()
                ),
                Arguments.of(
                        ShortURLDto.builder()
                                .originalUrl("") // Empty original URL
                                .build(),
                        "abc123",
                        ShortURL.builder()
                                .originalUrl(null)
                                .shortCode("abc123")
                                .updateDate(null)
                                .clicks(0)
                                .build()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideToEntityTestData")
    void testToEntity(ShortURLDto shortURLDto, String newUrl, ShortURL expectedEntity) {
        ShortURL resultEntity = ShortURLConverter.toEntity(shortURLDto, newUrl);
        if (Objects.nonNull(shortURLDto) && Objects.nonNull(expectedEntity)) {
            assertEquals(expectedEntity.getOriginalUrl(), resultEntity.getOriginalUrl());
            assertEquals(expectedEntity.getShortCode(), resultEntity.getShortCode());
            assertEquals(expectedEntity.getUpdateDate(), resultEntity.getUpdateDate());
            assertEquals(expectedEntity.getClicks(), resultEntity.getClicks());
        } else {
            assertNull(resultEntity);
        }
    }

    private static Stream<Arguments> provideToDtoTestData() {
        return Stream.of(
                Arguments.of(
                        ShortURL.builder()
                                .originalUrl("https://www.example.com")
                                .shortCode("abc123")
                                .validUrl(true)
                                .clicks(0)
                                .build(),
                        ShortURLDto.builder()
                                .originalUrl("https://www.example.com")
                                .shortCode("abc123")
                                .validUrl(true)
                                .clicks(0)
                                .build()
                ),
                // Test case 2: Null ShortURL entity
                Arguments.of(
                        null,
                        null
                ),
                Arguments.of(
                        ShortURL.builder()
                                .originalUrl("https://www.example.com$%")
                                .shortCode("abc123")
                                .validUrl(true)
                                .clicks(2)
                                .build(),
                        ShortURLDto.builder()
                                .originalUrl("https://www.example.com$%")
                                .shortCode("abc123")
                                .validUrl(true)
                                .clicks(2)
                                .build()
                ),
                Arguments.of(
                        ShortURL.builder()
                                .originalUrl(generateLongUrl())
                                .shortCode("abc123")
                                .validUrl(true)
                                .clicks(0)
                                .build(),
                        ShortURLDto.builder()
                                .originalUrl(generateLongUrl())
                                .shortCode("abc123")
                                .validUrl(true)
                                .clicks(0)
                                .build()
                ),
                Arguments.of(
                        ShortURL.builder()
                                .originalUrl(null)
                                .shortCode("abc123")
                                .validUrl(true)
                                .build(),
                        null
                ),
                Arguments.of(
                        ShortURL.builder()
                                .originalUrl("")
                                .shortCode("abc123")
                                .validUrl(true)
                                .build(),
                        ShortURLDto.builder()
                                .originalUrl("")
                                .shortCode("abc123")
                                .validUrl(true)
                                .build()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideToDtoTestData")
    void testToDto(ShortURL shortUrl, ShortURLDto expectedDto) {
        ShortURLDto resultDto = ShortURLConverter.toDto(shortUrl);
        if (Objects.nonNull(shortUrl) && Objects.nonNull(expectedDto)) {
            assertEquals(expectedDto.getOriginalUrl(), resultDto.getOriginalUrl());
            assertEquals(expectedDto.getShortCode(), resultDto.getShortCode());
            assertEquals(expectedDto.getClicks(), resultDto.getClicks());
        }
    }

    // Helper method to generate a long URL for testing purposes
    private static String generateLongUrl() {
        return "https://www.example.com/".repeat(1000);
    }
}