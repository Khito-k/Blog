package com.bzk.blog.service;

import com.bzk.blog.dto.MessageBackDTO;
import com.bzk.blog.vo.PageResult;
import com.bzk.blog.vo.ConditionVO;
import com.bzk.blog.vo.MessageVO;
import com.bzk.blog.dto.MessageDTO;
import com.bzk.blog.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bzk.blog.vo.ReviewVO;

import java.util.List;

/**
 * 留言服务
 */
public interface MessageService extends IService<Message> {
	
	/**
	 * 添加留言弹幕
	 * @param messageVO 留言对象
	 */
	void saveMessage(MessageVO messageVO);
	
	/**
	 * 查看留言弹幕
	 * @return 留言列表
	 */
	List<MessageDTO> listMessages();
	
	/**
	 * 审核留言
	 * @param reviewVO 审查签证官
	 */
	void updateMessagesReview(ReviewVO reviewVO);
	
	/**
	 * 查看后台留言
	 * @param condition 条件
	 * @return 留言列表
	 */
	PageResult<MessageBackDTO> listMessageBackDTO(ConditionVO condition);
	
}
