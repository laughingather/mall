package com.flipped.mall.search.service.impl;

import com.flipped.mall.common.constant.SearchConstants;
import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.common.util.JsonUtil;
import com.flipped.mall.search.config.MyElasticSearchConfig;
import com.flipped.mall.search.entity.EsSku;
import com.flipped.mall.search.entity.query.SearchQuery;
import com.flipped.mall.search.entity.vo.SearchVO;
import com.flipped.mall.search.feign.entity.AttrDTO;
import com.flipped.mall.search.feign.entity.BrandDTO;
import com.flipped.mall.search.feign.service.ProductFeignService;
import com.flipped.mall.search.service.ProductSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Slf4j
@Service
public class ProductSearchServiceImpl implements ProductSearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private ProductFeignService productFeignService;

    @Override
    public SearchVO search(SearchQuery searchQuery) {
        SearchVO searchVO = null;

        // ??????????????????
        SearchRequest searchRequest = buildSearchRequest(searchQuery);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, MyElasticSearchConfig.COMMON_OPTIONS);
            // ??????????????????
            searchVO = buildSearchResult(searchResponse, searchQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchVO;
    }

    @Override
    public SearchVO searchByEasyEs(SearchQuery searchQuery) {
        return null;
    }


    /**
     * ??????????????????
     * ???????????????????????????????????????????????????????????????
     *
     * @param searchQuery
     * @return
     */
    private SearchRequest buildSearchRequest(SearchQuery searchQuery) {

        // ??????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // ??????????????????
        buildQuery(searchQuery, searchSourceBuilder);

        // ????????????
        buildOrder(searchQuery, searchSourceBuilder);

        // ????????????
        buildPage(searchQuery, searchSourceBuilder);

        // ??????????????????
        buildHighlight(searchQuery, searchSourceBuilder);

        // ????????????
        buildAggregation(searchSourceBuilder);

        String DSL = searchSourceBuilder.toString();
        log.info("?????????DSL??????{}", DSL);

        return new SearchRequest(new String[]{SearchConstants.PRODUCT_INDEX}, searchSourceBuilder);
    }


    /**
     * ??????????????????
     *
     * @param searchResponse
     * @param searchQuery
     * @return
     */
    private SearchVO buildSearchResult(SearchResponse searchResponse, SearchQuery searchQuery) {
        log.info("ES?????????????????????{}", searchResponse);

        SearchVO searchVO = new SearchVO();

        // ????????????
        buildSearchVOProducts(searchQuery, searchResponse, searchVO);

        // ????????????
        buildSearchVOCategory(searchResponse, searchVO);

        // ????????????
        buildSearchVOBrands(searchResponse, searchVO);

        // ????????????
        buildSearchVOAttrs(searchResponse, searchVO);

        // ????????????
        buildSearchVOPageInfo(searchQuery, searchResponse, searchVO);

        // ???????????????
        buildSearchVONavs(searchQuery, searchResponse, searchVO);

        log.info("?????????????????????{}", searchVO);

        return searchVO;
    }


    private void buildSearchVOProducts(SearchQuery searchQuery, SearchResponse searchResponse, SearchVO searchVO) {
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<EsSku> skuESModels = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(hits)) {
            for (SearchHit hit : hits) {
                String esSkuJSON = hit.getSourceAsString();
                EsSku esSku = JsonUtil.json2Bean(esSkuJSON, EsSku.class);

                // ??????????????????
                if (StringUtils.isNotBlank(searchQuery.getKeyword())) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    esSku.setSkuTitle(skuTitle.getFragments()[0].string());
                }

                skuESModels.add(esSku);
            }
        }
        searchVO.setProducts(skuESModels);
    }

    private void buildSearchVOCategory(SearchResponse searchResponse, SearchVO searchVO) {
        List<SearchVO.CategoryVO> catalogVOs = new ArrayList<>();
        ParsedLongTerms categoryAgg = searchResponse.getAggregations().get("category_agg");
        List<? extends Terms.Bucket> categoryAggBuckets = categoryAgg.getBuckets();
        if (CollectionUtils.isNotEmpty(categoryAggBuckets)) {
            for (Terms.Bucket categoryAggBucket : categoryAggBuckets) {
                SearchVO.CategoryVO categoryVO = new SearchVO.CategoryVO();
                // ????????????id
                categoryVO.setCategoryId(Long.parseLong(categoryAggBucket.getKeyAsString()));
                // ??????????????????
                ParsedStringTerms categoryNameAgg = categoryAggBucket.getAggregations().get("category_name_agg");
                List<? extends Terms.Bucket> categoryNameAggBuckets = categoryNameAgg.getBuckets();
                categoryVO.setCategoryName(categoryNameAggBuckets.get(0).getKeyAsString());

                catalogVOs.add(categoryVO);
            }
        }
        searchVO.setCategories(catalogVOs);
    }

    private void buildSearchVOBrands(SearchResponse searchResponse, SearchVO searchVO) {
        List<SearchVO.BrandVO> brandVOs = new ArrayList<>();
        ParsedLongTerms brandAgg = searchResponse.getAggregations().get("brand_agg");
        List<? extends Terms.Bucket> brandAggBuckets = brandAgg.getBuckets();
        if (CollectionUtils.isNotEmpty(brandAggBuckets)) {
            for (Terms.Bucket brandAggBucket : brandAggBuckets) {

                SearchVO.BrandVO brandVO = new SearchVO.BrandVO();
                // ????????????id
                brandVO.setBrandId(brandAggBucket.getKeyAsNumber().longValue());
                // ??????????????????
                ParsedStringTerms brandNameAgg = brandAggBucket.getAggregations().get("brand_name_agg");
                List<? extends Terms.Bucket> brandNameAggBuckets = brandNameAgg.getBuckets();
                brandVO.setBrandName(brandNameAggBuckets.get(0).getKeyAsString());

                ParsedStringTerms brandImgAgg = brandAggBucket.getAggregations().get("brand_img_agg");
                List<? extends Terms.Bucket> brandImgAggBuckets = brandImgAgg.getBuckets();
                brandVO.setBrandImg(brandImgAggBuckets.get(0).getKeyAsString());

                brandVOs.add(brandVO);
            }
        }
        searchVO.setBrands(brandVOs);
    }

    private void buildSearchVOAttrs(SearchResponse searchResponse, SearchVO searchVO) {
        List<SearchVO.AttrVO> attrVOs = new ArrayList<>();
        ParsedNested attrAgg = searchResponse.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        List<? extends Terms.Bucket> attrIdAggBuckets = attrIdAgg.getBuckets();
        if (CollectionUtils.isNotEmpty(attrIdAggBuckets)) {
            for (Terms.Bucket attrIdAggBucket : attrIdAggBuckets) {
                SearchVO.AttrVO attrVO = new SearchVO.AttrVO();
                // ??????id
                attrVO.setAttrId(attrIdAggBucket.getKeyAsNumber().longValue());
                // ????????????
                ParsedStringTerms attrNameAgg = attrIdAggBucket.getAggregations().get("attr_name_agg");
                attrVO.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());
                // ?????????
                ParsedStringTerms attrValueAgg = attrIdAggBucket.getAggregations().get("attr_value_agg");
                List<? extends Terms.Bucket> attrValueAggBuckets = attrValueAgg.getBuckets();
                List<String> attrVOValues = attrValueAggBuckets.stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
                attrVO.setAttrValues(attrVOValues);

                attrVOs.add(attrVO);
            }
        }
        searchVO.setAttrs(attrVOs);
    }

    private void buildSearchVOPageInfo(SearchQuery searchQuery, SearchResponse searchResponse, SearchVO searchVO) {
        // ?????????
        long total = searchResponse.getHits().getTotalHits().value;
        searchVO.setTotal(total);
        // ?????????
        long totalPage = total % SearchConstants.PAGE_TOTAL == 0 ? total / SearchConstants.PAGE_TOTAL : (total / SearchConstants.PAGE_TOTAL) + 1;
        searchVO.setTotalPage(totalPage);
        // ????????????
        List<Long> pageNavs = new ArrayList<>();
        for (long i = 1; i <= totalPage; i++) {
            pageNavs.add(i);
        }
        searchVO.setPageNavs(pageNavs);
        // ????????????
        searchVO.setPageNum(searchQuery.getPageNum());
    }

    /**
     * ?????????????????????
     *
     * @param searchQuery
     * @param searchResponse
     * @param searchVO
     */
    private void buildSearchVONavs(SearchQuery searchQuery, SearchResponse searchResponse, SearchVO searchVO) {
        if (CollectionUtils.isNotEmpty(searchQuery.getAttrs())) {
            List<SearchVO.NavVO> navVOList = searchQuery.getAttrs().stream().map(attr -> {
                // ????????????attr???????????????
                SearchVO.NavVO navVO = new SearchVO.NavVO();

                String[] split = attr.split(SearchConstants.ATTR_SPLIT);
                navVO.setNavValue(split[1]);
                MyResult<AttrDTO> attrDTOResult = productFeignService.getAttrById(Long.parseLong(split[0]));
                if (attrDTOResult.getSuccess()) {
                    navVO.setNavName(attrDTOResult.getData().getAttrName());
                } else {
                    navVO.setNavName(split[0]);
                }

                // ??????????????????????????????????????????????????????????????????????????????url?????????????????????
                String replace = replaceQueryString(searchQuery, "attrs", attr);
                navVO.setLink(SearchConstants.URL + replace);

                return navVO;
            }).collect(Collectors.toList());

            searchVO.setNavs(navVOList);
        }


        if (CollectionUtils.isNotEmpty(searchQuery.getBrandId())) {
            List<SearchVO.NavVO> navs = searchVO.getNavs();
            SearchVO.NavVO navVO = new SearchVO.NavVO();

            navVO.setNavName("??????");
            List<Long> brandId = searchQuery.getBrandId();
            MyResult<List<BrandDTO>> brandsResult = productFeignService.listBrandsByIds(brandId);
            if (brandsResult.getSuccess()) {
                List<BrandDTO> brands = brandsResult.getData();
                StringBuffer buffer = new StringBuffer();
                String replace = "";
                for (BrandDTO brand : brands) {
                    buffer.append(brand.getBrandName()).append(";");
                    replace = replaceQueryString(searchQuery, "brandId", brand.getBrandId().toString());
                }
                navVO.setNavValue(buffer.toString());
                navVO.setLink(SearchConstants.URL + replace);
            }

            navs.add(navVO);
        }

    }


    private String replaceQueryString(SearchQuery searchQuery, String key, String value) {
        String encode = "";
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            // ???????????????????????????Java?????????
            encode.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String replace = searchQuery.get_queryUrl().replace("&" + key + "=" + encode, "");
        return replace;
    }


    private void buildQuery(SearchQuery searchQuery, SearchSourceBuilder searchSourceBuilder) {
        // ????????????
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(searchQuery.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", searchQuery.getKeyword()));
        }

        // ????????????
        if (null != searchQuery.getCategoryId()) {
            boolQuery.filter(QueryBuilders.termQuery("categoryId", searchQuery.getCategoryId()));
        }
        if (CollectionUtils.isNotEmpty(searchQuery.getBrandId())) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", searchQuery.getBrandId()));
        }
        if (null != searchQuery.getHasStock()) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock", searchQuery.getHasStock().equals(1) ? true : false));
        }
        // ???????????????????????? 0-500/-500/0- ?????????
        if (StringUtils.isNotBlank(searchQuery.getSkuPrice()) && searchQuery.getSkuPrice().contains(SearchConstants.PRICE_SPLIT)) {
            String[] price = searchQuery.getSkuPrice().split(SearchConstants.PRICE_SPLIT);
            // ????????????????????????
            if (searchQuery.getSkuPrice().startsWith(SearchConstants.PRICE_SPLIT)) {
                boolQuery.filter(QueryBuilders.rangeQuery("skuPrice").lte(price[0]));
            }
            // ????????????????????????
            else if (searchQuery.getSkuPrice().endsWith(SearchConstants.PRICE_SPLIT)) {
                boolQuery.filter(QueryBuilders.rangeQuery("skuPrice").gte(price[0]));
            }
            // ??????????????????
            else {
                boolQuery.filter(QueryBuilders.rangeQuery("skuPrice").gte(price[0]).lte(price[1]));
            }
        }
        if (CollectionUtils.isNotEmpty(searchQuery.getAttrs())) {
            for (String attr : searchQuery.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] split = attr.split(SearchConstants.ATTR_SPLIT);
                String attrId = split[0];
                String[] attrValues = split[1].split(":");
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrName", attrValues));

                // ????????????????????????????????? nested ??????
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }

        }

        searchSourceBuilder.query(boolQuery);
    }


    private void buildOrder(SearchQuery searchQuery, SearchSourceBuilder searchSourceBuilder) {
        if (StringUtils.isNotBlank(searchQuery.getSort())) {
            // ??????????????????   ?????????_????????????
            String[] sort = searchQuery.getSort().split(SearchConstants.ATTR_SPLIT);
            SortOrder sortOrder = "ASC".equalsIgnoreCase(sort[1]) ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(sort[0], sortOrder);
        }
    }


    private void buildPage(SearchQuery searchQuery, SearchSourceBuilder searchSourceBuilder) {
        if (searchQuery.getPageNum() == null) {
            searchQuery.setPageNum(1);
        }
        searchSourceBuilder.from((searchQuery.getPageNum() - 1) * SearchConstants.PAGE_TOTAL);
        searchSourceBuilder.size(SearchConstants.PAGE_TOTAL);
    }


    private void buildHighlight(SearchQuery searchQuery, SearchSourceBuilder searchSourceBuilder) {
        if (StringUtils.isNotBlank(searchQuery.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags(SearchConstants.PRE_TAGS);
            highlightBuilder.postTags(SearchConstants.POST_TAGS);

            searchSourceBuilder.highlighter(highlightBuilder);
        }
    }


    private void buildAggregation(SearchSourceBuilder searchSourceBuilder) {
        // ????????????
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);
        // ????????????????????????
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brandAgg);

        // ????????????
        TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("category_agg").field("categoryId").size(50);
        // ????????????????????????
        categoryAgg.subAggregation(AggregationBuilders.terms("category_name_agg").field("categoryName").size(1));
        searchSourceBuilder.aggregation(categoryAgg);

        // ????????????
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
        // ????????????????????????
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attrAgg.subAggregation(attrIdAgg);
        searchSourceBuilder.aggregation(attrAgg);
    }
}
