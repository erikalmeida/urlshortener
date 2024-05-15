package com.mercadolibre.urlshortener.repository;

import com.mercadolibre.urlshortener.domain.ShortURL;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShortURLRepository extends MongoRepository<ShortURL, ObjectId> {

    Optional<ShortURL> findByShortCode(String shortCode);
}
