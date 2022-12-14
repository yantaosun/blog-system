package com.foru.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.foru.blog.constant.CommonConst;
import com.foru.blog.constant.CommonConst.EmailOperation;
import com.foru.blog.constant.MQPrefixConst;
import com.foru.blog.constant.RedisPrefixConst;
import com.foru.blog.dao.UserAuthDao;
import com.foru.blog.dao.UserInfoDao;
import com.foru.blog.dao.UserRoleDao;
import com.foru.blog.dto.blog.EmailDTO;
import com.foru.blog.dto.blog.UserAreaDTO;
import com.foru.blog.dto.blog.UserBackDTO;
import com.foru.blog.dto.blog.UserInfoDTO;
import com.foru.blog.entity.UserAuth;
import com.foru.blog.entity.UserInfo;
import com.foru.blog.entity.UserRole;
import com.foru.blog.enums.LoginTypeEnum;
import com.foru.blog.enums.RoleEnum;
import com.foru.blog.enums.UserAreaTypeEnum;
import com.foru.blog.exception.BizException;
import com.foru.blog.service.BlogInfoService;
import com.foru.blog.service.RedisService;
import com.foru.blog.service.UserAuthService;
import com.foru.blog.strategy.context.SocialLoginStrategyContext;
import com.foru.blog.util.CommonUtils;
import com.foru.blog.util.PageUtils;
import com.foru.blog.util.UserUtils;
import com.foru.blog.vo.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.foru.blog.vo.gitee.GiteeLoginVO;
import com.foru.blog.vo.qq.QQLoginVO;
import com.foru.blog.vo.weibo.WeiboLoginVO;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * ??????????????????
 *
 * @author 9527
 * @date 2021/08/10
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
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private SocialLoginStrategyContext socialLoginStrategyContext;

    @Override
    public void sendCode(String toEmail, Integer operation) {
        // ????????????????????????
        if (!CommonUtils.checkEmail(toEmail)) {
            throw new BizException("?????????????????????");
        }
        // ?????????????????????????????????
        String name = toEmail;
        if(!(UserUtils.getLoginUser() == null))
            name = UserUtils.getLoginUser().getNickname();
        String code = CommonUtils.getRandomCode();
        // ???????????????
        EmailDTO emailDTO = EmailDTO.builder()
                .userName(name)
                .operation(EmailOperation.ofType(operation))
                .email(toEmail)
                .subject(CommonConst.SUBJECT)
                .content(code)
                .build();
        rocketMQTemplate.convertAndSend(MQPrefixConst.EMAIL_TOPIC + ":" + MQPrefixConst.EMAIL_TOPIC_TAG_OF_USER,emailDTO);
        // ??????????????????redis????????????????????????15??????
        redisService.set(RedisPrefixConst.USER_CODE_KEY + toEmail, code, RedisPrefixConst.CODE_EXPIRE_TIME);
    }

    @Override
    public List<UserAreaDTO> listUserAreas(ConditionVO conditionVO) {
        List<UserAreaDTO> userAreaDTOList = new ArrayList<>();
        switch (Objects.requireNonNull(UserAreaTypeEnum.getUserAreaType(conditionVO.getType()))) {
            case USER:
                // ??????????????????????????????
                Object userArea = redisService.get(RedisPrefixConst.USER_AREA);
                if (Objects.nonNull(userArea)) {
                    userAreaDTOList = JSON.parseObject(userArea.toString(), List.class);
                }
                return userAreaDTOList;
            case VISITOR:
                // ????????????????????????
                Map<String, Object> visitorArea = redisService.hGetAll(RedisPrefixConst.VISITOR_AREA);
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
                .uId(user.getUsername())
                .password(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
                .loginType(LoginTypeEnum.EMAIL.getType())
                .build();
        userAuthDao.insert(userAuth);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePassword(UserVO user) {
        // ????????????????????????
        if (!checkUser(user)) {
            throw new BizException("?????????????????????");
        }
        // ???????????????????????????
        userAuthDao.update(new UserAuth(), new LambdaUpdateWrapper<UserAuth>()
                .set(UserAuth::getPassword, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
                .eq(UserAuth::getUId, user.getUsername()));
    }

    @Transactional(rollbackFor = Exception.class)
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

    @Transactional
    @Override
    public UserInfoDTO giteeLogin(GiteeLoginVO giteeLoginVO) {
        return socialLoginStrategyContext.executeLoginStrategy(JSON.toJSONString(giteeLoginVO), LoginTypeEnum.GITEE);
    }

    /**
     * GitHub??????
     *
     * @param giteeLoginVO Gitee????????????
     * @return ??????????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoDTO githubLogin(GiteeLoginVO giteeLoginVO) {
        return socialLoginStrategyContext.executeLoginStrategy(JSON.toJSONString(giteeLoginVO), LoginTypeEnum.GITHUB);
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
     *
     * @param user ????????????
     * @return ??????
     */
    private Boolean checkUser(UserVO user) {
        if (!user.getCode().equals(redisService.get(RedisPrefixConst.USER_CODE_KEY + user.getUsername()))) {
            throw new BizException("??????????????????");
        }
        //???????????????????????????
        UserAuth userAuth = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getUId)
                .eq(UserAuth::getUId, user.getUsername()));
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
                                .replaceAll(CommonConst.PROVINCE, "")
                                .replaceAll(CommonConst.CITY, "");
                    }
                    return CommonConst.UNKNOWN;
                })
                .collect(Collectors.groupingBy(item -> item, Collectors.counting()));
        // ????????????
        List<UserAreaDTO> userAreaList = userAreaMap.entrySet().stream()
                .map(item -> UserAreaDTO.builder()
                        .name(item.getKey())
                        .value(item.getValue())
                        .build())
                .collect(Collectors.toList());
        redisService.set(RedisPrefixConst.USER_AREA, JSON.toJSONString(userAreaList));
    }

}
