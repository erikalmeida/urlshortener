package com.mercadolibre.urlshortener.util;

import com.mercadolibre.urlshortener.dto.ShortURLDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class URLUtilTest {

    private static Stream<Arguments> provideIsValidRequestInsertTestData() {
        return Stream.of(
                //1
                Arguments.of(null, true), // Null request
                Arguments.of(ShortURLDto.builder().originalUrl("").build(), true),
                Arguments.of(ShortURLDto.builder().build(), true),
                Arguments.of(ShortURLDto.builder().originalUrl("invalidurl").build(), true),
                Arguments.of(ShortURLDto.builder().originalUrl("https://www.example.com").build(), false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideIsValidRequestInsertTestData")
    void testIsValidRequestInsert(ShortURLDto request, boolean expected) {
        assertEquals(expected, URLUtil.isValidRequestInsert(request));
    }

    private static Stream<Arguments> provideIsValidRequestUpdateTestData() {
        return Stream.of(
                Arguments.of(null, false),
                Arguments.of(ShortURLDto.builder().build(), false),
                Arguments.of(ShortURLDto.builder().validUrl(true).build(), true),
                Arguments.of(ShortURLDto.builder().originalUrl("test").build(), true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideIsValidRequestUpdateTestData")
    void testIsValidRequestUpdate(ShortURLDto request, boolean expected) {
        assertEquals(expected, URLUtil.isValidRequestUpdate(request));
    }


    private static Stream<Arguments> provideIsWellFormedUrlTestData() {
        return Stream.of(
                Arguments.of(null, false), 
                Arguments.of("", false), 
                Arguments.of("invalidurl", false), 
                Arguments.of("https://www.example.com", true),
                Arguments.of("  https://www.example.com  ", true), 
                Arguments.of("HTTPs://www.example.com", true), 
                Arguments.of("https://www.example.com?key=value", true), 
                Arguments.of("https://www.example.com#fragment", true), 
                Arguments.of("https://www.üniçøde.com", false)

        );
    }

    @ParameterizedTest
    @MethodSource("provideIsWellFormedUrlTestData")
    void testIsWellFormedUrl(String urlString, boolean expected) {
        assertEquals(expected, URLUtil.isWellFormedUrl(urlString));
    }

}