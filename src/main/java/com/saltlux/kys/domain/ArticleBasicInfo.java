package com.saltlux.kys.domain;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Document(indexName = "basic")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class ArticleBasicInfo {

    @Id
    String id;

    @Field(type = FieldType.Date)
    Instant publishedAt;

    @Field(type = FieldType.Keyword)
    String[] category;

    @Field(type= FieldType.Text)
    String title;

    @Field(type= FieldType.Text)
    String content;

    @Field(type= FieldType.Text)
    String byline;

    @Field(type= FieldType.Keyword)
    String providerCode;
}
