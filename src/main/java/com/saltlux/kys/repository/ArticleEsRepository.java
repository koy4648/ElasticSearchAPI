package com.saltlux.kys.repository;

import com.saltlux.kys.domain.ArticleDataESBasic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleEsRepository extends ElasticsearchRepository<ArticleDataESBasic,String> {

}
