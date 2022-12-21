package com.bzk.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bzk.blog.entity.ChatRecord;
import org.springframework.stereotype.Repository;

/**
 * 聊天记录
 */
@Repository
public interface ChatRecordDao extends BaseMapper<ChatRecord> {
}
