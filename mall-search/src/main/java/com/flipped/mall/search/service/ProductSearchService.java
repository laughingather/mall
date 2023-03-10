package com.flipped.mall.search.service;

import com.flipped.mall.search.entity.query.SearchQuery;
import com.flipped.mall.search.entity.vo.SearchVO;

/**
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
public interface ProductSearchService {

    /**
     * 根据条件返回检索结果集
     *
     * @param searchQuery
     * @return
     */
    SearchVO search(SearchQuery searchQuery);

    /**
     * 使用easy-es查询
     *
     * @param searchQuery
     * @return
     */
    SearchVO searchByEasyEs(SearchQuery searchQuery);


}
