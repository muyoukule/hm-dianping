//package com.hmdp.service.impl;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.hmdp.result.Result;
//import com.hmdp.entity.VoucherOrder;
//import com.hmdp.mapper.VoucherOrderMapper;
//import com.hmdp.service.ISeckillVoucherService;
//import com.hmdp.service.IVoucherOrderService;
//import com.hmdp.utils.RedisIdWorker;
//import com.hmdp.utils.UserHolder;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.aop.framework.AopContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.script.DefaultRedisScript;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.PostConstruct;
//import java.util.Collections;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * 基于阻塞队列实现秒杀优化
// */
//@Service
//public class VoucherOrderServiceImpl02 extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
//
//    @Autowired
//    private ISeckillVoucherService seckillVoucherService;
//
//    @Autowired
//    private RedisIdWorker redisIdWorker;
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Autowired
//    private RedissonClient redissonClient;
//
//    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
//
//    static {
//        SECKILL_SCRIPT = new DefaultRedisScript<>();
//        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
//        SECKILL_SCRIPT.setResultType(Long.class);
//    }
//
//    /**
//     * 阻塞队列
//     */
//    private BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024 * 1024);
//
//    /**
//     * 异步处理线程池
//     */
//    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();
//
//    /**
//     * 在类初始化之后执行，因为当这个类初始化好了之后，随时都是有可能要执行的
//     */
//    @PostConstruct
//    private void init() {
//        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
//    }
//
//    /**
//     * 用于线程池处理的任务，当初始化完毕后，就会去从对列中去拿信息
//     */
//    private class VoucherOrderHandler implements Runnable {
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    // 1.获取队列中的订单信息
//                    VoucherOrder voucherOrder = orderTasks.take();
//                    // 2.创建订单
//                    handleVoucherOrder(voucherOrder);
//                } catch (Exception e) {
//                    log.error("处理订单异常", e);
//                }
//            }
//        }
//
//        /**
//         * 创建订单
//         *
//         * @param voucherOrder
//         */
//        private void handleVoucherOrder(VoucherOrder voucherOrder) {
//            //1.获取用户
//            Long userId = voucherOrder.getUserId();
//            // 2.创建锁对象
//            RLock redisLock = redissonClient.getLock("lock:order:" + userId);
//            // 3.尝试获取锁
//            boolean isLock = redisLock.tryLock();
//            // 4.判断是否获得锁成功
//            if (!isLock) {
//                // 获取锁失败，直接返回失败或者重试
//                log.error("不允许重复下单！");
//                return;
//            }
//            try {
//                //注意：由于是spring的事务是放在ThreadLocal中，此时的是多线程，事务会失效
//                proxy.createVoucherOrder(voucherOrder);
//            } finally {
//                // 释放锁
//                redisLock.unlock();
//            }
//        }
//
//    }
//
//    /**
//     * 代理对象
//     */
//    private IVoucherOrderService proxy;
//
//    /**
//     * 优惠券秒杀下单
//     *
//     * @param voucherId
//     * @return
//     */
//    @Override
//    public Result seckillVoucher(Long voucherId) {
//        Long userId = UserHolder.getUser().getId();
//        // 1.执行Lua脚本
//        Long result = stringRedisTemplate.execute(
//                SECKILL_SCRIPT,
//                Collections.emptyList(),
//                voucherId.toString(), userId.toString()
//        );
//        int r = result.intValue();
//        // 2.判断结果是否为0
//        if (r != 0) {
//            // 2.1.不为0 ，代表没有购买资格
//            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
//        }
//        // 2.2.为0，有购买资格，把下单信息保存到阻塞队列
//        VoucherOrder voucherOrder = new VoucherOrder();
//        // 2.3.订单id
//        long orderId = redisIdWorker.nextId("order");
//        voucherOrder.setId(orderId);
//        // 2.4.用户id
//        voucherOrder.setUserId(userId);
//        // 2.5.代金券id
//        voucherOrder.setVoucherId(voucherId);
//        // 2.6.放入阻塞队列
//        orderTasks.add(voucherOrder);
//        //3.获取代理对象
//        proxy = (IVoucherOrderService) AopContext.currentProxy();
//        //4.返回订单id
//        return Result.ok(orderId);
//    }
//
//    @Override
//    @Transactional
//    public void createVoucherOrder(VoucherOrder voucherOrder) {
//        Long userId = voucherOrder.getUserId();
//        // 5.1.查询订单
//        int count = query().eq("user_id", userId).eq("voucher_id", voucherOrder.getVoucherId()).count();
//        // 5.2.判断是否存在
//        if (count > 0) {
//            // 用户已经购买过了
//            log.error("用户已经购买过了");
//            return;
//        }
//        // 6.扣减库存
//        boolean success = seckillVoucherService.update()
//                .setSql("stock = stock - 1") // set stock = stock - 1
//                .eq("voucher_id", voucherOrder.getVoucherId()).gt("stock", 0) // where id = ? and stock > 0
//                .update();
//        if (!success) {
//            // 扣减失败
//            log.error("库存不足");
//            return;
//        }
//        save(voucherOrder);
//    }
//
//}