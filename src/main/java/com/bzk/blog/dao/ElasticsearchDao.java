package com.bzk.blog.dao;

import com.bzk.blog.dto.ArticleSearchDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


/**
 * elasticsearch
 */
@Repository
public interface ElasticsearchDao extends ElasticsearchRepository<ArticleSearchDTO,Integer> {
}
