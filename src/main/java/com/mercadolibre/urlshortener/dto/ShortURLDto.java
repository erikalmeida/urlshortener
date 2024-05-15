package com.mercadolibre.urlshortener.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ShortURLDto {

    private String originalUrl;
    private String shortenedUrl;
    private String shortCode;
    private Boolean validUrl;
    private Integer clicks;

}
