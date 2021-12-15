package com.laughingather.gulimall.product.controller;

import com.laughingather.gulimall.product.service.SpuImagesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * spu图片路由
 *
 * @author laughingather
 * @email laughingather@gmail.com
 * @date 2021-04-11 15:12:49
 */
@RestController
@RequestMapping("product/spu-images")
public class SpuImagesController {

    @Resource
    private SpuImagesService spuImagesService;

}
