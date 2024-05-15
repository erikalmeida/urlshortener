package com.mercadolibre.urlshortener.controller;

import com.mercadolibre.urlshortener.dto.ShortURLDto;
import com.mercadolibre.urlshortener.service.URLService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class URLControllerTest {

    @Mock
    private URLService urlService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private URLController urlController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static Stream<Arguments> provideRedirectTestData() {
        return Stream.of(
                //1
                Arguments.of("abc123", "https://www.original.com", HttpStatus.MOVED_PERMANENTLY.value()),
                Arguments.of("invalidCode", null, HttpStatus.NOT_FOUND.value()),
                Arguments.of("", null, HttpStatus.NOT_FOUND.value()),
                Arguments.of("abc123", "https://www.example.com", HttpStatus.MOVED_PERMANENTLY.value()),
                //5
                Arguments.of("abc123", "", HttpStatus.NOT_FOUND.value()),
                Arguments.of("abc123", null, HttpStatus.NOT_FOUND.value()),
                Arguments.of("invalid$code", null, HttpStatus.NOT_FOUND.value())
        );
    }

    @ParameterizedTest
    @MethodSource("provideRedirectTestData")
    void testRedirectToOriginalUrl(String shortCode, String originalUrl, int expectedHttpStatus) throws IOException {
        when(urlService.getOriginalUrl(shortCode)).thenReturn(originalUrl);

        urlController.redirectToOriginalUrl(shortCode, response);

        if (expectedHttpStatus == HttpStatus.MOVED_PERMANENTLY.value()) {
            verify(response).setHeader("Location", originalUrl);
        } else {
            verify(response).sendError(expectedHttpStatus, "URL not found or not valid");
        }
        verifyNoMoreInteractions(response);
    }

    private static Stream<Arguments> provideShortenTestData() {
        return Stream.of(
                Arguments.of( ShortURLDto.builder().originalUrl("https://www.original.com").shortenedUrl("abc123").build(),
                        true, HttpStatus.OK),
                Arguments.of(ShortURLDto.builder().originalUrl(null).shortenedUrl(null).build(),
                        false, HttpStatus.BAD_REQUEST),
                Arguments.of(ShortURLDto.builder().originalUrl("invalid-url").shortenedUrl("abc123").build(),
                        false, HttpStatus.BAD_REQUEST)

        );
    }

    @ParameterizedTest
    @MethodSource("provideShortenTestData")
    void testShortenURL(ShortURLDto request, boolean isValidRequest, HttpStatus expectedHttpStatus) {
        if (!isValidRequest) {
            ResponseEntity<ShortURLDto> responseEntity = urlController.shortenURL(request);
            assertEquals(expectedHttpStatus, responseEntity.getStatusCode());
        }
    }

    private static Stream<Arguments> provideUpdateTestData() {
        return Stream.of(
                Arguments.of("abc123", false, HttpStatus.NOT_FOUND),
                Arguments.of("abc123", true, HttpStatus.NOT_FOUND),
                Arguments.of("invalidCode", null, HttpStatus.NOT_FOUND),
                Arguments.of("", true, HttpStatus.BAD_REQUEST)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUpdateTestData")
    void testUpdateShortURL(String shortCode, Boolean isValidRequest, HttpStatus expectedHttpStatus) {
        // Mocking ShortURLDto based on isValidRequest
        ShortURLDto updateDTO = mock(ShortURLDto.class);
        when(updateDTO.getValidUrl()).thenReturn(isValidRequest);
        when(updateDTO.getOriginalUrl()).thenReturn("updatedurl.com");

        ResponseEntity<ShortURLDto> responseEntity = urlController.updateShortURL(shortCode, updateDTO);

        assertEquals(expectedHttpStatus, responseEntity.getStatusCode());
    }

    private static Stream<Arguments> provideShortCodeTestData() {
        ShortURLDto validShortURLDto = ShortURLDto.builder().originalUrl("https://www.example.com").shortenedUrl("abc123").build();
        return Stream.of(
                // Test case 1: Valid short code
                Arguments.of("abc123", validShortURLDto, HttpStatus.OK),
                // Test case 2: Null short code
                Arguments.of(null, null, HttpStatus.NOT_FOUND),
                // Test case 3: Empty short code
                Arguments.of("", null, HttpStatus.NOT_FOUND),
                // Test case 4: Invalid short code
                Arguments.of("invalidCode", null, HttpStatus.NOT_FOUND)
        );
    }

    @ParameterizedTest
    @MethodSource("provideShortCodeTestData")
    void testGetShortUrl(String shortCode, ShortURLDto shortURLDto, HttpStatus expectedHttpStatus) throws IOException {
        when(urlService.getShortUrl(shortCode)).thenReturn(shortURLDto);

        ResponseEntity<ShortURLDto> responseEntity = urlController.getShortUrl(shortCode, response);

        assertEquals(expectedHttpStatus, responseEntity.getStatusCode());
        if (expectedHttpStatus == HttpStatus.OK) {
            assertEquals(shortURLDto, responseEntity.getBody());
        }
        verifyNoMoreInteractions(response);
    }

}