package com.bzk.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bzk.blog.dto.OperationLogDTO;
import com.bzk.blog.vo.PageResult;
import com.bzk.blog.entity.OperationLog;
import com.bzk.blog.vo.ConditionVO;

/**
 * 操作日志服务
 */
public interface OperationLogService extends IService<OperationLog> {
	
	/**
	 * 查询日志列表
	 * @param conditionVO 条件
	 * @return 日志列表
	 */
	PageResult<OperationLogDTO> listOperationLogs(ConditionVO conditionVO);
	
}
