package mselasticsearch.service;

import common.common.ApiResponse;
import common.common.ApiUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Datetime: 2025年03月03日21:28
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mselasticsearch.service
 * @Project: camellia-mscloud
 * @Description:
 */
public interface DisposeStrategy {

    /**
     * 匹配所有,接口默认方法可以实现。
     *
     * @param oldClient
     * @param database
     * @param page
     * @param size
     * @param sortField
     * @param sortOrder
     * @return
     */
    default ApiResponse<Map<String, Object>> matchAll(RestHighLevelClient oldClient, String database, Integer page, Integer size, String sortField, String sortOrder){
        try{
            SearchRequest request = new SearchRequest(database);
            request.source().query(QueryBuilders.matchAllQuery());
            if(page > 0 && size > 0) request.source().from((page-1)*size).size(size);
            if(sortField != null && !sortField.isEmpty()) request.source().sort(sortField, SortOrder.valueOf(sortOrder.toUpperCase()));
            SearchResponse response = oldClient.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            Map<String, Object> map = new HashMap<>();
            for (SearchHit hit : hits) {
                map.put(hit.getId(), hit.getSourceAsMap());
            }
            if (hits.getTotalHits() != null) {
                map.put("文章总数", hits.getTotalHits().value);
            }
            return ApiUtils.success(map, "查询成功");
        }catch (IOException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMessage());
            return ApiUtils.error(map,"查询失败");
        }
    }

}
