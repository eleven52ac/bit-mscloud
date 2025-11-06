package mselasticsearch.service.impl;

import common.common.ApiResponse;
import mselasticsearch.service.QueryDocStrategy;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Datetime: 2025年03月04日16:52
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mselasticsearch.service.impl
 * @Project: camellia-mscloud
 * @Description:
 */
@Service("houseQueryService")
public class HouseQueryServiceImpl implements QueryDocStrategy {
    @Override
    public ApiResponse<Map<String, Object>> matchAll(RestHighLevelClient oldElasticsearchClient, String database) {
        return null;
    }

    @Override
    public ApiResponse<Map<String, Object>> matchByCondition(RestHighLevelClient oldElasticsearchClient, String database, String fields, String condition) {
        return null;
    }

    @Override
    public ApiResponse<Map<String, Object>> matchByTerm(RestHighLevelClient oldElasticsearchClient, String database, String field, String value) {
        return null;
    }

    @Override
    public ApiResponse<Map<String, Object>> matchByRange(RestHighLevelClient oldElasticsearchClient, String database, String field, String gte, String lte) {
        return null;
    }
}
