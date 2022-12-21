package com.bzk.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bzk.blog.dao.ChatRecordDao;
import com.bzk.blog.entity.ChatRecord;
import com.bzk.blog.service.ChatRecordService;
import org.springframework.stereotype.Service;

/**
 * 聊天记录服务
 */
@Service
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordDao, ChatRecord> implements ChatRecordService {


}
