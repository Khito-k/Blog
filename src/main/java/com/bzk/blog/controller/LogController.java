package com.bzk.blog.controller;

import com.bzk.blog.dto.OperationLogDTO;
import com.bzk.blog.service.OperationLogService;
import com.bzk.blog.vo.ConditionVO;
import com.bzk.blog.vo.PageResult;
import com.bzk.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日志控制器
 */
@Api(tags = "日志模块")
@CrossOrigin //跨域解决
@RestController
public class LogController {
	@Autowired
	private OperationLogService operationLogService;
	
	/**
	 * 查看操作日志
	 * @param conditionVO 条件
	 * @return {@link Result < OperationLogDTO >} 日志列表
	 */
	@ApiOperation(value = "查看操作日志")
	@GetMapping("/admin/operation/logs")
	public Result<PageResult<OperationLogDTO>> listOperationLogs(ConditionVO conditionVO) {
		return Result.ok(operationLogService.listOperationLogs(conditionVO));
	}
	
	/**
	 * 删除操作日志
	 * @param logIdList 日志id列表
	 * @return {@link Result<>}
	 */
	@ApiOperation(value = "删除操作日志")
	@DeleteMapping("/admin/operation/logs")
	public Result<?> deleteOperationLogs(@RequestBody List<Integer> logIdList) {
		operationLogService.removeByIds(logIdList);
		return Result.ok();
	}
	
}
