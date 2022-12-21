package com.bzk.blog.controller;


import com.bzk.blog.annotation.OptLog;
import com.bzk.blog.dto.TagBackDTO;
import com.bzk.blog.dto.TagDTO;
import com.bzk.blog.service.TagService;
import com.bzk.blog.vo.ConditionVO;
import com.bzk.blog.vo.PageResult;
import com.bzk.blog.vo.Result;
import com.bzk.blog.vo.TagVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.bzk.blog.constant.OptTypeConst.REMOVE;
import static com.bzk.blog.constant.OptTypeConst.SAVE_OR_UPDATE;


/**
 * 标签控制器
 */
@Api(tags = "标签模块")
@CrossOrigin //跨域解决
@RestController
public class TagController {
	@Autowired
	private TagService tagService;
	
	/**
	 * 查询标签列表
	 * @return {@link Result < TagDTO >} 标签列表
	 */
	@ApiOperation(value = "查询标签列表")
	@GetMapping("/tags")
	public Result<PageResult<TagDTO>> listTags() {
		return Result.ok(tagService.listTags());
	}
	
	/**
	 * 查询后台标签列表
	 * @param condition 条件
	 * @return {@link Result< TagBackDTO >} 标签列表
	 */
	@ApiOperation(value = "查询后台标签列表")
	@GetMapping("/admin/tags")
	public Result<PageResult<TagBackDTO>> listTagBackDTO(ConditionVO condition) {
		return Result.ok(tagService.listTagBackDTO(condition));
	}
	
	/**
	 * 搜索文章标签
	 * @param condition 条件
	 * @return {@link Result<String>} 标签列表
	 */
	@ApiOperation(value = "搜索文章标签")
	@GetMapping("/admin/tags/search")
	public Result<List<TagDTO>> listTagsBySearch(ConditionVO condition) {
		return Result.ok(tagService.listTagsBySearch(condition));
	}
	
	/**
	 * 添加或修改标签
	 * @param tagVO 标签信息
	 * @return {@link Result<>}
	 */
	@OptLog(optType = SAVE_OR_UPDATE)
	@ApiOperation(value = "添加或修改标签")
	@PostMapping("/admin/tags")
	public Result<?> saveOrUpdateTag(@Valid @RequestBody TagVO tagVO) {
		tagService.saveOrUpdateTag(tagVO);
		return Result.ok();
	}
	
	/**
	 * 删除标签
	 * @param tagIdList 标签id列表
	 * @return {@link Result<>}
	 */
	@OptLog(optType = REMOVE)
	@ApiOperation(value = "删除标签")
	@DeleteMapping("/admin/tags")
	public Result<?> deleteTag(@RequestBody List<Integer> tagIdList) {
		tagService.deleteTag(tagIdList);
		return Result.ok();
	}
	
}

