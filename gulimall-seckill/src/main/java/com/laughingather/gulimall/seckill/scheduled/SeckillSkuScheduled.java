package com.laughingather.gulimall.seckill.scheduled;

import com.laughingather.gulimall.common.constant.SeckillConstants;
import com.laughingather.gulimall.seckill.service.SeckillSkuService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品定时任务
 *
 * @author：laughingather
 * @create：2021-11-12 2021/11/12
 */
@Slf4j
@Component
public class SeckillSkuScheduled {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private SeckillSkuService seckillSkuService;

    /**
     * 秒杀商品定时上架
     * 需要保证幂等性处理
     * <p>
     * 近三天内 00:00:00 - 23:59:59 区间内的待上架商品
     */
    @Async
    @Scheduled(cron = "*/10 * * * * ?")
    public void uploadSeckillSkuLatest3Days() {
        log.info("上架秒杀产品");
        RLock lock = redissonClient.getLock(SeckillConstants.UPLOAD_LOCK);
        lock.lock(SeckillConstants.UPLOAD_LOCK_TIME, TimeUnit.SECONDS);
        try {
            seckillSkuService.uploadSeckillSkuLatest3Days();
        } finally {
            lock.unlock();
        }
    }


    /**
     * 秒杀商品定时下架清理
     * 需要保证幂等性处理
     * <p>
     * 秒杀时间结束的
     * <p>
     * TODO:秒杀时间结束的商品从缓存中清理
     */
    @Async
    @Scheduled(cron = "*/10 * * * * ?")
    public void shelvesSeckillSku() {
        log.info("下架秒杀产品");

    }


}

