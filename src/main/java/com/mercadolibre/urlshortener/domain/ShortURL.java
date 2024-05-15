package com.mercadolibre.urlshortener.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Document(collection = "short_url")
public class ShortURL {

    @Id
    private String id; // map objectId to String
    private String originalUrl;
    private String shortCode; //generated code
    private String createDate;
    private String updateDate;
    private boolean validUrl;
    private Integer clicks;

}
