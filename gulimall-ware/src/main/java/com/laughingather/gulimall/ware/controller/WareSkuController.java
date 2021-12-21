package com.laughingather.gulimall.ware.controller;

import com.laughingather.gulimall.common.api.MyPage;
import com.laughingather.gulimall.common.api.MyResult;
import com.laughingather.gulimall.ware.entity.WareSkuEntity;
import com.laughingather.gulimall.ware.entity.query.WareSkuQuery;
import com.laughingather.gulimall.ware.service.WareSkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;


/**
 * 商品库存
 *
 * @author laughingather
 * @email laughingather@gmail.com
 * @date 2021-04-12 11:57:24
 */
@RestController
@RequestMapping("/ware/ware-sku")
@Api(tags = "商品库存模块")
public class WareSkuController {

    @Resource
    private WareSkuService wareSkuService;

    @GetMapping("/page")
    @ApiOperation(value = "分页查询商品库存列表")
    public MyResult<MyPage<WareSkuEntity>> listWareSkusWithPage(@ModelAttribute WareSkuQuery wareSkuQuery) {
        MyPage<WareSkuEntity> wareSkuMyPage = wareSkuService.listWareSkusWithPage(wareSkuQuery);
        return MyResult.success(wareSkuMyPage);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取商品库存详情")
    public MyResult<WareSkuEntity> getWareSkuById(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);
        return MyResult.success(wareSku);
    }

    @PostMapping
    @ApiOperation(value = "保存商品库存信息")
    public MyResult saveWareSku(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.saveWareSku(wareSku);
        return MyResult.success();
    }

    @DeleteMapping
    @ApiOperation(value = "批量删除商品库存信息")
    public MyResult deleteBatchWareSkuByIds(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));
        return MyResult.success();
    }

    @PutMapping
    @ApiOperation(value = "更新商品库存信息")
    public MyResult updateWareSkuById(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateWareSkuById(wareSku);
        return MyResult.success();
    }

}
