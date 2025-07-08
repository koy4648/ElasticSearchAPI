package com.saltlux.kys.repository;

import com.saltlux.kys.domain.ArticleBasicInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleEsRepository extends ElasticsearchRepository<ArticleBasicInfo,String> {

}
