package com.mercadolibre.urlshortener.converter;

import com.mercadolibre.urlshortener.domain.ShortURL;
import com.mercadolibre.urlshortener.dto.ShortURLDto;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.mercadolibre.urlshortener.constants.URLShortenerConstants.BASE_URL;

public class ShortURLConverter {

    public static ShortURL toEntity(ShortURLDto shortURLDto, String newUrl) {
        if (shortURLDto == null) {
            return null;
        }
        return ShortURL.builder()
                .originalUrl(Strings.trimToNull(shortURLDto.getOriginalUrl()))
                .shortCode(newUrl)
                .validUrl(Objects.nonNull(shortURLDto.getValidUrl())? shortURLDto.getValidUrl() : true)
                .createDate(LocalDateTime.now().toString())
                .updateDate(null)
                .clicks(0)
                .build();
    }

    public static ShortURLDto toDto(ShortURL shortUrl) {
        if (shortUrl == null) {
            return null;
        }
        return ShortURLDto.builder()
                .originalUrl(shortUrl.getOriginalUrl())
                .validUrl(shortUrl.isValidUrl())
                .shortenedUrl(BASE_URL + shortUrl.getShortCode())
                .shortCode(shortUrl.getShortCode())
                .clicks(shortUrl.getClicks())
                .build();
    }

}
