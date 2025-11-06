package mselasticsearch.service;

import common.common.ApiResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @Datetime: 2025年03月03日16:11
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mselasticsearch.service
 * @Project: camellia-mscloud
 * @Description:
 */
public interface DocumentStrategy {
    ApiResponse addDocumentByOldClient(Long hotelId, RestHighLevelClient oldElasticsearchClient);

    ApiResponse addDocumentByNewClient(Long hotelId, RestClient newElasticsearchClient);

    ApiResponse addDocumentByNewClientAsync(Long hotelId, RestClient newElasticsearchClient);

    ApiResponse queryDocumentByOldClient(Long id, RestHighLevelClient oldElasticsearchClient);

    ApiResponse queryDocumentByNewClient(Long id, RestClient newElasticsearchClient);

    ApiResponse updateDocumentByOldClient(Long id, RestHighLevelClient oldElasticsearchClient);

    ApiResponse updateDocumentByNewClient(Long id, RestClient newElasticsearchClient);

    ApiResponse deleteDocumentByOldClient(Long id, RestHighLevelClient oldElasticsearchClient);

    ApiResponse deleteDocumentByNewClient(Long id, RestClient newElasticsearchClient);

    ApiResponse batchAddDocumentByOldClient(RestHighLevelClient oldElasticsearchClient);

    ApiResponse batchAddDocumentByNewClient(RestClient newElasticsearchClient);
}
