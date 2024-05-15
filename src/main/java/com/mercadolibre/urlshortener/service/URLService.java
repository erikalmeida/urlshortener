package com.mercadolibre.urlshortener.service;

import com.mercadolibre.urlshortener.dto.ShortURLDto;

public interface URLService {

    /**
     * Create a shortUrl object.
     *
     * @param shortURLDto
     * @return the shortened URL
     */
    ShortURLDto shortenUrl(ShortURLDto shortURLDto);

    /**
     * Updates a shortUrl object.
     *
     * @param updateDto
     * @return the shortened URL
     */
    ShortURLDto updateShortUrl(String shortCode, ShortURLDto updateDto);

    /**
     * get the original url
     *
     * @param urlId
     * @return redirect to original url
     */
    String getOriginalUrl(String urlId);

    /**
     * get the short url object data
     *
     * @param urlId
     * @return  shortUrl Data
     */
    ShortURLDto getShortUrl(String urlId);


}
