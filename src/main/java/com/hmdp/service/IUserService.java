package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.result.Result;
import com.hmdp.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface IUserService extends IService<User> {


    /**
     * 发送验证码
     *
     * @param phone
     * @param session
     */
    Result sendCode(String phone, HttpSession session);

    /**
     * 用户登录
     *
     * @param loginForm
     * @param session
     * @return
     */
    Result login(LoginFormDTO loginForm, HttpSession session);

    /**
     * 用户登出
     *
     * @param request
     * @return
     */
    Result logout(HttpServletRequest request);

    /**
     * 用户签到
     * @return
     */
    Result sign();

    /**
     * 签到统计
     *
     * @return
     */
    Result signCount();

}
