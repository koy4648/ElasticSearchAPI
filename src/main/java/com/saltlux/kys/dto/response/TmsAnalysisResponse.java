package com.saltlux.kys.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class TmsAnalysisResponse {

    @JsonProperty("tms_sentiment_negative_text")
    private String[] tmsSentimentNegativeText;

    @JsonProperty("tms_sentiment_positive_text")
    private String[] tmsSentimentPositiveText;

    @JsonProperty("tms_bigram")
    private String tmsBigram;

    @JsonProperty("tms_feature")
    private String tmsFeature;

    @JsonProperty("tms_entity_name_location")
    private String[] tmsEntityNameLocation;

    @JsonProperty("tms_sentiment_polarity_score")
    private int tmsSentimentPolarityScore;

    @JsonProperty("tms_raw_stream_index")
    private String tmsRawStreamIndex;

    @JsonProperty("tms_raw_stream_store")
    private String tmsRawStreamStore;

    @JsonProperty("tms_entity_name_person")
    private String[] tmsEntityNamePerson;

    @JsonProperty("tms_morph")
    private String tmsMorph;

    @JsonProperty("tms_entity_name_organization")
    private String[] tmsEntityNameOrganization;
}