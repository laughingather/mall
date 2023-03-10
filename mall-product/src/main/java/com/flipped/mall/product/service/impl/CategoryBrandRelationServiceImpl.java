package com.flipped.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flipped.mall.product.dao.BrandDao;
import com.flipped.mall.product.dao.CategoryBrandRelationDao;
import com.flipped.mall.product.dao.CategoryDao;
import com.flipped.mall.product.entity.BrandEntity;
import com.flipped.mall.product.entity.CategoryBrandRelationEntity;
import com.flipped.mall.product.entity.CategoryEntity;
import com.flipped.mall.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 品牌分类关联关系管理逻辑实现
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Resource
    private CategoryBrandRelationDao categoryBrandRelationDao;
    @Resource
    private BrandDao brandDao;
    @Resource
    private CategoryDao categoryDao;

    @Override
    public List<CategoryEntity> listCategoryByBrandId(Long brandId) {
        return categoryBrandRelationDao.listCategoryByBrandId(brandId);
    }

    @Override
    public List<BrandEntity> listBrandsByCategoryId(Long categoryId) {
        return categoryBrandRelationDao.listBrandsByCategoryId(categoryId);
    }

    @Override
    public void saveCategoryBrandRelation(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long categoryId = categoryBrandRelation.getCategoryId();

        // 查询品牌名称和分类名称
        String brandName = brandDao.getBrandNameById(brandId);
        String categoryName = categoryDao.getCategoryNameById(categoryId);
        categoryBrandRelation.setBrandName(brandName);
        categoryBrandRelation.setCategoryName(categoryName);

        categoryBrandRelationDao.insert(categoryBrandRelation);
    }
}