package com.bzk.blog.service.impl;

import com.bzk.blog.entity.ArticleTag;
import com.bzk.blog.dao.ArticleTagDao;
import com.bzk.blog.service.ArticleTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 文章标签服务
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagDao, ArticleTag> implements ArticleTagService {

}
