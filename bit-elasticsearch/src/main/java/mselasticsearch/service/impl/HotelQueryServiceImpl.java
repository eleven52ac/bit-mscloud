package mselasticsearch.service.impl;

import common.common.ApiResponse;
import common.common.ApiUtils;
import mselasticsearch.service.QueryDocStrategy;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Datetime: 2025年03月04日16:51
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mselasticsearch.service.impl
 * @Project: camellia-mscloud
 * @Description:
 */
@Service("hotelQueryService")
public class HotelQueryServiceImpl implements QueryDocStrategy {

    /**
     * 查询所有文档
     * @param oldElasticsearchClient
     * @param database
     * @return
     */
    @Override
    public ApiResponse<Map<String, Object>> matchAll(RestHighLevelClient oldElasticsearchClient, String database) {
        try{
            // 1. 准备Request对象
            SearchRequest request = new SearchRequest(database);
            // 2. 准备DSL
            request.source().query(QueryBuilders.matchAllQuery());
            // 3. 发送请求
            SearchResponse response = oldElasticsearchClient.search(request, RequestOptions.DEFAULT);
            // 4. 处理响应
            SearchHits hits = response.getHits();
            Map<String,Object> map = new HashMap();
            map.put("文档总数", hits.getTotalHits().value);
            for (SearchHit hit : hits) {
                map.put(hit.getId(), hit.getSourceAsMap());
            }
            return ApiUtils.success(map, "查询成功");
        } catch (IOException e) {
            Map<String, Object> map = new HashMap();
            map.put("error", e.getMessage());
            return ApiUtils.error(map,"查询失败");
        }
    }

    /**
     * 根据条件查询文档
     * @param oldElasticsearchClient
     * @param database
     * @param fields
     * @param condition
     * @return
     */
    @Override
    public ApiResponse<Map<String, Object>> matchByCondition(RestHighLevelClient oldElasticsearchClient, String database, String fields, String condition) {
        try{
            // 1. 准备Request对象
            SearchRequest request = new SearchRequest(database);
            // 2. 准备DSL
            request.source().query(QueryBuilders.matchQuery(fields,condition));
            // 3. 发送请求
            SearchResponse response = oldElasticsearchClient.search(request, RequestOptions.DEFAULT);
            // 4. 处理响应
            SearchHits hits = response.getHits();
            Map<String,Object> map = new HashMap();
            map.put("文档总数", hits.getTotalHits().value);
            for (SearchHit hit : hits) {
                map.put(hit.getId(), hit.getSourceAsMap());
            }
            return ApiUtils.success(map, "查询成功");
        } catch (IOException e) {
            Map<String, Object> map = new HashMap();
            map.put("error", e.getMessage());
            return ApiUtils.error(map,"查询失败");
        }
    }

    @Override
    public ApiResponse<Map<String, Object>> matchByTerm(RestHighLevelClient oldElasticsearchClient, String database, String field, String value) {
        try{
            // 1. 准备Request对象
            SearchRequest request = new SearchRequest(database);
            // 2. 准备DSL
            request.source().query(QueryBuilders.termQuery(field,value));
            // 3. 发送请求
            SearchResponse response = oldElasticsearchClient.search(request, RequestOptions.DEFAULT);
            // 4. 处理响应
            SearchHits hits = response.getHits();
            Map<String,Object> map = new HashMap();
            for (SearchHit hit : hits) {
                map.put(hit.getId(), hit.getSourceAsMap());
            }
            map.put("文档总数", hits.getTotalHits().value);
            return ApiUtils.success(map, "查询成功");
        } catch (IOException e) {
            Map<String, Object> map = new HashMap();
            map.put("查询失败", e.getMessage());
            return ApiUtils.error(map, e.getMessage());
        }
    }

    @Override
    public ApiResponse<Map<String, Object>> matchByRange(RestHighLevelClient oldElasticsearchClient, String database, String field, String gte, String lte) {
        try{
            // 1. 准备Request对象
            SearchRequest request = new SearchRequest(database);
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(field);
            if (gte != null && !gte.isEmpty()) rangeQueryBuilder.gte(gte);
            if (lte != null && !lte.isEmpty()) rangeQueryBuilder.lte(lte);
            request.source().query(rangeQueryBuilder);
            // 2. 准备DSL
            SearchResponse response = oldElasticsearchClient.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            Map<String,Object> map = new HashMap();
            for (SearchHit hit : hits) {
                map.put(hit.getId(), hit.getSourceAsMap());
            }
            map.put("文档总数", hits.getTotalHits().value);
            return ApiUtils.success(map, "查询成功");
        }catch (IOException e){
            Map<String, Object> map = new HashMap();
            map.put("查询失败", e.getMessage());
            return ApiUtils.error(map, e.getMessage());
        }
    }

}
