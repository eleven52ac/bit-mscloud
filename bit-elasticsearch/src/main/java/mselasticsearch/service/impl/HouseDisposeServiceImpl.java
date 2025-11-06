package mselasticsearch.service.impl;

import common.common.ApiResponse;
import mselasticsearch.service.DisposeStrategy;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Datetime: 2025年03月03日21:32
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mselasticsearch.service.impl
 * @Project: camellia-mscloud
 * @Description:
 */
@Service("houseDispose")
public class HouseDisposeServiceImpl implements DisposeStrategy {
    @Override
    public ApiResponse<Map<String, Object>> matchAll(RestHighLevelClient oldClient, String database, Integer page, Integer size, String sortField, String sortOrder) {
        // 显式调用接口中的 default 方法
        return DisposeStrategy.super.matchAll(oldClient, database, page, size, sortField, sortOrder);
    }
}
