package com.foru.blog.config.aspect;

import com.alibaba.fastjson.JSON;
import com.foru.blog.annotation.LoginLog;
import com.foru.blog.constant.CommonConst;
import com.foru.blog.constant.MQPrefixConst;
import com.foru.blog.dao.OperationLogDao;
import com.foru.blog.dto.blog.UserDetailDTO;
import com.foru.blog.entity.OperationLog;
import com.foru.blog.util.IpUtils;
import com.foru.blog.util.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.elasticsearch.common.collect.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 操作记录注解切面
 *
 * @author Myles Yang
 */
@Component
@Aspect
@Slf4j
public class LoginRecordAspect {

	/**
	 * 方法返回值符号
	 */
	public static final String METHOD_RETURNING_SIGN = "ret";

	/**
	 * el 表达式 解析器
	 */
	private static final ExpressionParser SPEL_PARSER = new SpelExpressionParser();

	private OperationLogDao operationLogDao;
	private RocketMQTemplate rocketMQTemplate;

	@Autowired
	public void setOperationLogDao(OperationLogDao operationLogDao) {
		this.operationLogDao = operationLogDao;
	}

	@Autowired
	public void setRocketMQTemplate(RocketMQTemplate rocketMQTemplate) {
		this.rocketMQTemplate = rocketMQTemplate;
	}

	@Pointcut("@annotation(com.foru.blog.annotation.LoginLog)")
	public void pointCut() {
	}

	/**
	 * 方法正常返回的advice
	 *
	 * @param point 方法的连接点
	 * @param ret   函数返回值，void的返回值为null
	 */
	@AfterReturning(value = "pointCut()", returning = METHOD_RETURNING_SIGN, argNames = "point,ret")
	public void afterReturning(JoinPoint point, Object ret) {
		// 获取RequestAttributes
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		// 从获取RequestAttributes中获取HttpServletRequest的信息
		HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes).resolveReference(RequestAttributes.REFERENCE_REQUEST);
		// 从切面织入点处通过反射机制获取织入点处的方法
		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();
		LoginLog annotation = method.getAnnotation(LoginLog.class);
		Api api = (Api) signature.getDeclaringType().getAnnotation(Api.class);
		ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
		// 条件判断是否执行日志记录
		if (StringUtils.hasText(annotation.condition())) {
			try {
				Boolean condition = (Boolean) spell(annotation.condition(),
						new String[]{METHOD_RETURNING_SIGN}, new Object[]{ret});
				if (!(Objects.nonNull(condition) && condition)) {
					return;
				}
			} catch (Exception e) {
				log.error("条件EL表达式解析错误:{}", annotation.condition());
				e.printStackTrace();
				return;
			}
		}

		OperationLog operationLog = new OperationLog();

		// 测试时 @AliasFor 失效，原因未知
		String content = StringUtils.isEmpty(annotation.value())
				? annotation.content()
				: annotation.value();
		String cont = null;
		if (StringUtils.hasText(content)) {
			try {
				cont = (String) spell(content,
						signature.getParameterNames(), point.getArgs());
				operationLog.setOptDesc(cont);
			} catch (Exception e) {
				log.error("内容EL表达式解析错误:{}", content);
				e.printStackTrace();
				return;
			}
		}


		// 操作模块
		operationLog.setOptModule(api.tags()[0]);
		// 操作类型
		operationLog.setOptType(annotation.optType());
		// 操作描述
		operationLog.setOptDesc(new StringBuilder().append(apiOperation.value()).append(CommonConst.SUCCESS).toString());
		// 获取请求的类名
		String className = point.getTarget().getClass().getName();
		// 获取请求的方法名
		String methodName = method.getName();
		methodName = className + "." + methodName;
		// 请求方式
		operationLog.setRequestMethod(Objects.requireNonNull(request).getMethod());
		// 请求方法
		operationLog.setOptMethod(methodName);
		// 请求参数
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)point.getArgs()[2];
		UserDetailDTO dto = (UserDetailDTO)token.getPrincipal();
		operationLog.setRequestParam(JSON.toJSONString(Map.of(CommonConst.DEFAULT_NICKNAME,dto.getNickname(), CommonConst.ID_FIELD,dto.getId())));
		// 返回结果
		operationLog.setResponseData(CommonConst.SUCCESS);
		// 请求用户ID
		operationLog.setUserId(UserUtils.getLoginUser().getUserInfoId());
		// 请求用户
		operationLog.setNickname(UserUtils.getLoginUser().getNickname());
		// 请求IP
		String ipAddress = IpUtils.getIpAddress(request);
		operationLog.setIpAddress(ipAddress);
		operationLog.setIpSource(IpUtils.getIpSource(ipAddress));
		// 请求URL
		operationLog.setOptUrl(request.getRequestURI());
		//operationLogDao.insert(operationLog);
		rocketMQTemplate.convertAndSend(MQPrefixConst.LOG_TOPIC,operationLog);
	}

	/**
	 * el表达式解析
	 *
	 * @param el    表达式
	 * @param names 参数名称数组
	 * @param args  参数数组
	 */
	public Object spell(String el, String[] names, Object[] args) {
		EvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < args.length; i++) {
			context.setVariable(names[i], args[i]);
		}
		return SPEL_PARSER.parseExpression(el).getValue(context);
	}

}
