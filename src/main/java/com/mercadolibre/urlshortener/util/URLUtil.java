package com.mercadolibre.urlshortener.util;

import com.mercadolibre.urlshortener.dto.ShortURLDto;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Objects;

public class URLUtil {

    private static final Logger log = LoggerFactory.getLogger(URLUtil.class);

    // Method to validate ShortURLDto data
    public static boolean isValidRequestInsert(ShortURLDto request) {
       return Objects.isNull(request)
               || StringUtils.isEmpty(request.getOriginalUrl())
               || !isWellFormedUrl(request.getOriginalUrl());
    }

    public static boolean isValidRequestUpdate(ShortURLDto request) {
        return Objects.nonNull(request) && Objects.nonNull(request.getValidUrl());
    }

    public static boolean isWellFormedUrl(String urlString) {
        try {
            URI uri = new URI(urlString.trim());
            // Check if URI has a scheme and host (protocol and domain)
            return uri.getScheme() != null && uri.getHost() != null;
        } catch (Exception e) {
            log.warn("URL provided not valid, check URL format.");
            return false;
        }
    }

}
