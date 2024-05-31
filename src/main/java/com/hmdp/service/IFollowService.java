package com.hmdp.service;

import com.hmdp.result.Result;
import com.hmdp.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IFollowService extends IService<Follow> {

    /**
     * 关注
     *
     * @param followUserId
     * @param isFollow
     * @return 无
     */
    Result follow(Long followUserId, Boolean isFollow);

    /**
     * 取消关注
     *
     * @param followUserId
     * @return boolean
     */
    Result isFollow(Long followUserId);

    /**
     * 共同关注
     *
     * @param followUserId
     * @return 用户
     */
    Result followCommons(Long followUserId);
}
