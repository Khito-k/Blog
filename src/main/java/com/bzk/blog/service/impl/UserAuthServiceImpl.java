package com.bzk.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.bzk.blog.constant.CommonConst;
import com.bzk.blog.dao.UserInfoDao;
import com.bzk.blog.dao.UserRoleDao;
import com.bzk.blog.dto.EmailDTO;
import com.bzk.blog.dto.UserAreaDTO;
import com.bzk.blog.dto.UserBackDTO;
import com.bzk.blog.dto.UserInfoDTO;
import com.bzk.blog.vo.*;

import com.bzk.blog.entity.UserInfo;
import com.bzk.blog.entity.UserAuth;
import com.bzk.blog.dao.UserAuthDao;
import com.bzk.blog.entity.UserRole;
import com.bzk.blog.enums.LoginTypeEnum;
import com.bzk.blog.enums.RoleEnum;
import com.bzk.blog.exception.BizException;
import com.bzk.blog.service.BlogInfoService;
import com.bzk.blog.service.RedisService;
import com.bzk.blog.service.UserAuthService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bzk.blog.strategy.context.SocialLoginStrategyContext;
import com.bzk.blog.util.PageUtils;
import com.bzk.blog.util.UserUtils;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.bzk.blog.constant.CommonConst.*;
import static com.bzk.blog.constant.MQPrefixConst.EMAIL_EXCHANGE;
import static com.bzk.blog.constant.RedisPrefixConst.*;
import static com.bzk.blog.enums.UserAreaTypeEnum.getUserAreaType;
import static com.bzk.blog.util.CommonUtils.checkEmail;
import static com.bzk.blog.util.CommonUtils.getRandomCode;


/**
 * ??????????????????
 */
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthDao, UserAuth> implements UserAuthService {
	@Autowired
	private RedisService redisService;
	@Autowired
	private UserAuthDao userAuthDao;
	@Autowired
	private UserRoleDao userRoleDao;
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private BlogInfoService blogInfoService;
	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private SocialLoginStrategyContext socialLoginStrategyContext;
	
	@Override
	public void sendCode(String username) {
		// ????????????????????????
		if (!checkEmail(username)) {
			throw new BizException("?????????????????????");
		}
		// ?????????????????????????????????
		String code = getRandomCode();
		// ???????????????
		EmailDTO emailDTO = EmailDTO.builder()
				.email(username)
				.subject("?????????")
				.content("?????????????????? " + code + " ?????????15????????????????????????????????????")
				.build();
		rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
		// ??????????????????redis????????????????????????15??????
		redisService.set(USER_CODE_KEY + username, code, CODE_EXPIRE_TIME);
	}
	
	@Override
	public List<UserAreaDTO> listUserAreas(ConditionVO conditionVO) {
		List<UserAreaDTO> userAreaDTOList = new ArrayList<>();
		switch (Objects.requireNonNull(getUserAreaType(conditionVO.getType()))) {
			case USER:
				// ??????????????????????????????
				Object userArea = redisService.get(USER_AREA);
				if (Objects.nonNull(userArea)) {
					userAreaDTOList = JSON.parseObject(userArea.toString(), List.class);
				}
				return userAreaDTOList;
			case VISITOR:
				// ????????????????????????
				Map<String, Object> visitorArea = redisService.hGetAll(VISITOR_AREA);
				if (Objects.nonNull(visitorArea)) {
					userAreaDTOList = visitorArea.entrySet().stream()
							.map(item -> UserAreaDTO.builder()
									.name(item.getKey())
									.value(Long.valueOf(item.getValue().toString()))
									.build())
							.collect(Collectors.toList());
				}
				return userAreaDTOList;
			default:
				break;
		}
		return userAreaDTOList;
	}
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void register(UserVO user) {
		// ????????????????????????
		if (checkUser(user)) {
			throw new BizException("?????????????????????");
		}
		// ??????????????????
		UserInfo userInfo = UserInfo.builder()
				.email(user.getUsername())
				.nickname(CommonConst.DEFAULT_NICKNAME + IdWorker.getId())
				.avatar(blogInfoService.getWebsiteConfig().getUserAvatar())
				.build();
		userInfoDao.insert(userInfo);
		// ??????????????????
		UserRole userRole = UserRole.builder()
				.userId(userInfo.getId())
				.roleId(RoleEnum.USER.getRoleId())
				.build();
		userRoleDao.insert(userRole);
		// ??????????????????
		UserAuth userAuth = UserAuth.builder()
				.userInfoId(userInfo.getId())
				.username(user.getUsername())
				.password(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
				.loginType(LoginTypeEnum.EMAIL.getType())
				.build();
		userAuthDao.insert(userAuth);
	}
	
	@Override
	public void updatePassword(UserVO user) {
		// ????????????????????????
		if (!checkUser(user)) {
			throw new BizException("?????????????????????");
		}
		// ???????????????????????????
		userAuthDao.update(new UserAuth(), new LambdaUpdateWrapper<UserAuth>()
				.set(UserAuth::getPassword, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
				.eq(UserAuth::getUsername, user.getUsername()));
	}
	
	@Override
	public void updateAdminPassword(PasswordVO passwordVO) {
		// ???????????????????????????
		UserAuth user = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
				.eq(UserAuth::getId, UserUtils.getLoginUser().getId()));
		// ????????????????????????????????????????????????
		if (Objects.nonNull(user) && BCrypt.checkpw(passwordVO.getOldPassword(), user.getPassword())) {
			UserAuth userAuth = UserAuth.builder()
					.id(UserUtils.getLoginUser().getId())
					.password(BCrypt.hashpw(passwordVO.getNewPassword(), BCrypt.gensalt()))
					.build();
			userAuthDao.updateById(userAuth);
		} else {
			throw new BizException("??????????????????");
		}
	}
	
	@Override
	public PageResult<UserBackDTO> listUserBackDTO(ConditionVO condition) {
		// ????????????????????????
		Integer count = userAuthDao.countUser(condition);
		if (count == 0) {
			return new PageResult<>();
		}
		// ????????????????????????
		List<UserBackDTO> userBackDTOList = userAuthDao.listUsers(PageUtils.getLimitCurrent(), PageUtils.getSize(), condition);
		return new PageResult<>(userBackDTOList, count);
	}
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public UserInfoDTO qqLogin(QQLoginVO qqLoginVO) {
		return socialLoginStrategyContext.executeLoginStrategy(JSON.toJSONString(qqLoginVO), LoginTypeEnum.QQ);
	}
	
	@Transactional(rollbackFor = BizException.class)
	@Override
	public UserInfoDTO weiboLogin(WeiboLoginVO weiboLoginVO) {
		return socialLoginStrategyContext.executeLoginStrategy(JSON.toJSONString(weiboLoginVO), LoginTypeEnum.WEIBO);
	}
	
	/**
	 * ??????????????????????????????
	 * @param user ????????????
	 * @return ??????
	 */
	private Boolean checkUser(UserVO user) {
		if (!user.getCode().equals(redisService.get(USER_CODE_KEY + user.getUsername()))) {
			throw new BizException("??????????????????");
		}
		//???????????????????????????
		UserAuth userAuth = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
				.select(UserAuth::getUsername)
				.eq(UserAuth::getUsername, user.getUsername()));
		return Objects.nonNull(userAuth);
	}
	
	/**
	 * ??????????????????
	 */
	@Scheduled(cron = "0 0 * * * ?")
	public void statisticalUserArea() {
		// ????????????????????????
		Map<String, Long> userAreaMap = userAuthDao.selectList(new LambdaQueryWrapper<UserAuth>().select(UserAuth::getIpSource))
				.stream()
				.map(item -> {
					if (StringUtils.isNotBlank(item.getIpSource())) {
						return item.getIpSource().substring(0, 2)
								.replaceAll(PROVINCE, "")
								.replaceAll(CITY, "");
					}
					return UNKNOWN;
				})
				.collect(Collectors.groupingBy(item -> item, Collectors.counting()));
		// ????????????
		List<UserAreaDTO> userAreaList = userAreaMap.entrySet().stream()
				.map(item -> UserAreaDTO.builder()
						.name(item.getKey())
						.value(item.getValue())
						.build())
				.collect(Collectors.toList());
		redisService.set(USER_AREA, JSON.toJSONString(userAreaList));
	}
	
}
