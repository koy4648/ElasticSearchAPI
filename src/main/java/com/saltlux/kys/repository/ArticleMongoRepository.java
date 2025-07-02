package com.saltlux.kys.repository;

import com.saltlux.kys.domain.ArticleDataMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMongoRepository extends MongoRepository<ArticleDataMongo, String> {

}
