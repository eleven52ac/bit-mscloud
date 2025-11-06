package mselasticsearch.service;

import common.common.ApiResponse;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Map;

/**
 * @Datetime: 2025年03月04日16:50
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mselasticsearch.service.impl
 * @Project: camellia-mscloud
 * @Description:
 */
public interface QueryDocStrategy {
    ApiResponse<Map<String, Object>> matchAll(RestHighLevelClient oldElasticsearchClient, String database);

    ApiResponse<Map<String, Object>> matchByCondition(RestHighLevelClient oldElasticsearchClient, String database, String fields, String condition);

    ApiResponse<Map<String, Object>> matchByTerm(RestHighLevelClient oldElasticsearchClient, String database, String field, String value);

    ApiResponse<Map<String, Object>> matchByRange(RestHighLevelClient oldElasticsearchClient, String database, String field, String gte, String lte);
}
