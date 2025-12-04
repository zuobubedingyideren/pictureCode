package com.px.pictureend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.px.pictureend.model.dto.user.UserQueryRequest;
import com.px.pictureend.model.entity.User;
import com.px.pictureend.model.vo.user.LoginUserVO;
import com.px.pictureend.model.vo.user.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author idpeng
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-12-04 14:41:03
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request       请求
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 获取脱敏后的用户信息
     *
     * @param user 用户
     * @return 脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 是否注销成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 根据用户id获取用户信息
     *
      * @param user 用户
     * @return 脱敏用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 根据用户列表获取用户信息列表
     *
     * @param userList 用户列表
     * @return 脱敏用户信息列表
     */
    List<UserVO> getUserVOList(List<User> userList);


    /**
     * 获取查询条件
     *
     * @param userQueryRequest 用户查询条件
     * @return 查询条件
     */
    LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取默认密码
     *
     * @return 默认密码
     */
    String getEncryptedPassword(String defaultPassword);
}
