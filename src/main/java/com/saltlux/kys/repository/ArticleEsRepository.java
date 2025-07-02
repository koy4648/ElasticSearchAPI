package com.saltlux.kys.repository;

import com.saltlux.kys.domain.ArticleDataES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleEsRepository extends ElasticsearchRepository<ArticleDataES,String> {

}
