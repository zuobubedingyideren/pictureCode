package com.px.pictureend.aop;

import com.px.pictureend.annotation.AuthCheck;
import com.px.pictureend.exception.BusinessException;
import com.px.pictureend.exception.ErrorCode;
import com.px.pictureend.model.entity.User;
import com.px.pictureend.model.enums.UserRoleEnum;
import com.px.pictureend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * packageName: com.px.pictureend.aop
 *
 * @author: idpeng
 * @version: 1.0
 * @className: AuthInterceptor
 * @date: 2025/12/4 16:37
 * @description: 权限校验拦截器
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 权限校验拦截器方法
     * 该方法使用AOP环绕通知，在目标方法执行前后进行权限校验
     *
     * @param joinPoint 连接点对象，包含被拦截方法的信息
     * @param authCheck 权限校验注解对象，包含必须的角色信息
     * @return 目标方法的执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取必须的角色要求
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);

        // 如果没有设置必须角色，则直接放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }

        // 校验用户角色是否有效
        UserRoleEnum loginUserRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (loginUserRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 管理员权限校验：如果需要管理员权限但用户不是管理员，则拒绝访问
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(loginUserRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 执行目标方法
        return joinPoint.proceed();

    }

}
