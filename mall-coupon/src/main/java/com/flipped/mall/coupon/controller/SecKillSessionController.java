package com.flipped.mall.coupon.controller;

import com.flipped.mall.common.entity.api.MyPage;
import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.coupon.entity.SecKillSessionEntity;
import com.flipped.mall.coupon.entity.query.SecKillSessionQuery;
import com.flipped.mall.coupon.service.SecKillSessionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;


/**
 * 秒杀活动场次模块
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@RestController
@RequestMapping("/coupon/sec-kill-session")
public class SecKillSessionController {
    @Resource
    private SecKillSessionService secKillSessionService;

    @GetMapping("/page")
    public MyResult<MyPage<SecKillSessionEntity>> pageSecKillSession(@ModelAttribute SecKillSessionQuery secKillSessionQuery) {
        MyPage<SecKillSessionEntity> secKillSessionPage = secKillSessionService.pageSecKillSession(secKillSessionQuery);
        return MyResult.success(secKillSessionPage);
    }

    @GetMapping("/{id}")
    public MyResult<SecKillSessionEntity> getSecKillSessionById(@PathVariable Long id) {
        SecKillSessionEntity secKillSession = secKillSessionService.getById(id);
        return MyResult.success(secKillSession);
    }

    @PostMapping
    public MyResult<Void> saveSecKillSession(@RequestBody SecKillSessionEntity secKillSession) {
        secKillSession.setCreateTime(LocalDateTime.now());
        secKillSessionService.save(secKillSession);
        return MyResult.success();
    }

    @PutMapping
    public MyResult<Void> updateSecKillSessionById(@RequestBody SecKillSessionEntity secKillSession) {
        secKillSessionService.updateById(secKillSession);
        return MyResult.success();
    }


}
