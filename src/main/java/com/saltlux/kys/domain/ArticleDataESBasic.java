package com.saltlux.kys.domain;

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
public class ArticleDataESBasic {

    @Id
    String id;

    @Field(type= FieldType.Date)
    String publishedAt;

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
