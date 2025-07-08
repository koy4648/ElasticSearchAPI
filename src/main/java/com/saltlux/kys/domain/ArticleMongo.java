package com.saltlux.kys.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "article")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class ArticleMongo {

    @Id
    @Field("news_id")
    String newsId;
    @Field("published_at")
    String publishedAt;
    String[] category;
    String title;
    String content;
    String byline;
    @Field("provider_code")
    String providerCode;
}
