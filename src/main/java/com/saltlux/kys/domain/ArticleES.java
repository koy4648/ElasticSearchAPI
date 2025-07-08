package com.saltlux.kys.domain;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Document(indexName = "article")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class ArticleES {

    @Id
    String id;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    Instant publishedAt;

    @Field(type = FieldType.Keyword)
    String[] category;

    @Field(type = FieldType.Text)
    String title;

    @Field(type = FieldType.Text)
    String content;

    @Field(type = FieldType.Keyword)
    String byline;

    @Field(type = FieldType.Keyword)
    String providerCode;

    /// tms api 결과값
    @Field(type = FieldType.Text)
    String[] tms_sentiment_negative_text;

    @Field(type = FieldType.Text)
    String[] tms_sentiment_positive_text;

    @Field(type = FieldType.Text)
    String tms_bigram;

    @Field(type = FieldType.Text)
    String tms_feature;

    @Field(type = FieldType.Keyword)
    String[] tms_entity_name_location;

    @Field(type = FieldType.Integer)
    int tms_sentiment_polarity_score;

    @Field(type = FieldType.Keyword)
    String tms_raw_stream_index;

    @Field(type = FieldType.Keyword)
    String tms_raw_stream_store;

    @Field(type = FieldType.Keyword)
    String[] tms_entity_name_person;

    @Field(type = FieldType.Text)
    String tms_morph;

    @Field(type = FieldType.Keyword)
    String[] tms_entity_name_organization;
}
