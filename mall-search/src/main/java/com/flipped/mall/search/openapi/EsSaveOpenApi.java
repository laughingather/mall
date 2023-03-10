package com.flipped.mall.search.openapi;

import com.flipped.mall.common.entity.api.ErrorCodeEnum;
import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.search.entity.EsSku;
import com.flipped.mall.search.service.ProductSaveService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@RestController
@RequestMapping("/openapi/search")
public class EsSaveOpenApi {

    @Resource
    private ProductSaveService productSaveService;

    /**
     * 商品上架
     *
     * @param skuESModels
     * @return
     */
    @PostMapping("/product")
    public MyResult<Void> productUp(@RequestBody List<EsSku> skuESModels) {
        try {
            // 保存到es是否发生了错误
            boolean hasFailures = productSaveService.productUp(skuESModels);
            return !hasFailures ? MyResult.success() :
                    MyResult.failed(ErrorCodeEnum.PRODUCT_UP_EXCEPTION);
        } catch (IOException e) {
            e.printStackTrace();
            return MyResult.failed(ErrorCodeEnum.PRODUCT_UP_EXCEPTION);
        }
    }

}
