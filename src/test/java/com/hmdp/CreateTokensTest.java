package com.hmdp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.entity.User;
import com.hmdp.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.hmdp.constant.RedisConstants.LOGIN_CODE_KEY;

/**
 * P69创建 tokens.txt文件
 */
@SpringBootTest
class CreateTokensTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void batchLogin() throws IOException, InterruptedException {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.ne("phone", "13686869696");
        List<User> users = userService.getBaseMapper().selectList(query);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("D:\\tokens.txt")));
        // 以下执行统计了时间
        long start = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(users.size());
        users.forEach(user -> {
            Thread t = new Thread(() -> {
                LoginFormDTO login = new LoginFormDTO();
                login.setPhone(user.getPhone());
                login.setPassword(user.getPassword());
                userService.sendCode(user.getPhone(), null);
                login.setCode(stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + user.getPhone()));
                userService.login(login, null);
                // 对login方法添加了一行代码 loginForm.setCode(token); 会将token值设置到code字段，方便这个批量登录方法得到token
                String token = login.getCode();
                try {
                    bufferedWriter.write(token + "\n");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                countDownLatch.countDown();
            });
            t.start();
        });

        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println("执行用时：" + (end - start) + "ms");
        bufferedWriter.close();
    }
}