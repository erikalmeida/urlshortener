package com.mercadolibre.urlshortener.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class URLShortenerConstants {

    public static final int SHORT_URL_CHAR_SIZE = 7;
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Value("${mercado.libre.base.url}")
    public static String DEFAULT_BASE_URL;

    public static final String BASE_URL = System.getenv("BASE_URL_ML") != null ? System.getenv("BASE_URL_ML") : DEFAULT_BASE_URL;

    //metrics
    public static final String URL_REDIRECT_TIME = "mercadolibre.urlshortener.redirect.timer";
    public static final String URL_SHORTEN_TIME = "mercadolibre.urlshortener.shorten.timer";
    public static final String URL_SHORTEN_COUNTER = "mercadolibre.urlshortener.shorten.counter";
}
