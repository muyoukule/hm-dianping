package com.hmdp.service;

import com.hmdp.result.Result;
import com.hmdp.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IBlogService extends IService<Blog> {

    /**
     * 保存探店笔记
     *
     * @param blog
     * @return 博客笔记id
     */
    Result saveBlog(Blog blog);

    /**
     * 点赞博客
     * @param id
     * @return 无
     */
    Result likeBlog(Long id);

    /**
     * 博客热门排行榜
     * @param current
     * @return 热门博客信息
     */
    Result queryHotBlog(Integer current);

    /**
     * 查询博客详情
     * @param id
     * @return 博客详情
     */
    Result queryBlogById(Long id);

    /**
     * 博客点赞用户
     *
     * @param id
     * @return userDTOS
     */
    Result queryBlogLikes(Long id);

    /**
     * 分页查询收邮箱
     *
     * @param max
     * @param offset
     * @return 邮箱信息
     */
    Result queryBlogOfFollow(Long max, Integer offset);
}
