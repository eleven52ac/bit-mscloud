package mselasticsearch.controller;

import mscommon.common.ApiResponse;
import mscommon.common.ApiUtils;
import mselasticsearch.Constant;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Datetime: 2025年01月17日15:43
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mselasticsearch.controller
 * @Project: camellia-mscloud
 * @Description:
 */
@RequestMapping("/index")
@RestController
public class IndexController {

    @Autowired
    @Qualifier("oldElasticsearchClient")
    private RestHighLevelClient oldEsClient;

    @Autowired
    @Qualifier("newElasticsearchClient")
    private RestClient newEsClient;


    /**
     * 使用老版客户端创建索引库
     *
     * 该方法使用 Elasticsearch 7.x 的 RestHighLevelClient 来创建索引库。通过 CreateIndexRequest 设置索引名称和映射模板，调用 Elasticsearch 的索引创建 API。
     * 返回响应结果，判断是否成功创建索引。
     *
     * @return ApiResponse 返回 API 响应，包含操作结果（成功或失败）
     */
    @GetMapping("/create/old")
    public ApiResponse createIndexByOldClient(@RequestParam("indexName") String indexName,
                                              @RequestParam("mappingTemplate") String mappingTemplate){
        try {
            // 创建索引请求对象，传入索引名称。
            CreateIndexRequest request = new CreateIndexRequest(Constant.INDEX_HOTEL_NAME);
            // 设置索引的映射模板，事先定义的模板（Constant.MAPPING_TEMPLATE）
            request.source(Constant.HOUSE_MAPPING_TEMPLATE, XContentType.JSON);
            // 发送创建索引请求
            CreateIndexResponse createIndexResponse = oldEsClient.indices().create(request, RequestOptions.DEFAULT);
            // 判断索引创建是否被确认。
            if(createIndexResponse.isAcknowledged()){
                return ApiUtils.success(null,"索引创建成功");
            } else {
                return ApiUtils.error("索引创建失败");
            }
        } catch (Exception e) {
            return ApiUtils.error("索引创建失败", e.getMessage());
        }
    }

    /**
     * 使用新版客户端创建索引库
     * @return
     */
    @GetMapping("/create/new")
    public ApiResponse createIndexByNewClient(){
        try {
            // 创建 PUT 请求，指定索引名称为 /hotel
            Request request = new Request("PUT", "/hotel");
            // 设置请求体
            request.setJsonEntity(Constant.MAPPING_TEMPLATE);
            // 使用 newEsClient 发送请求
            Response response = newEsClient.performRequest(request);
            if(response.getStatusLine().getStatusCode() == 200){
                return ApiUtils.success("索引创建成功", response.getStatusLine().toString());
            }else{
                return ApiUtils.error("索引创建失败", response.getStatusLine().toString());
            }
        } catch (Exception e) {
            return ApiUtils.error("索引创建失败", e.getMessage());
        }
    }

    /**
     * 使用老版删除索引库
     * @return
     */
    @GetMapping("/delete/old")
    public ApiResponse deleteIndexByOldClient(){
        try {
            // 创建删除索引请求，传入索引名称
            DeleteIndexRequest request = new DeleteIndexRequest(Constant.INDEX_HOTEL_NAME);
            // 发送删除索引请求
            AcknowledgedResponse response = oldEsClient.indices().delete(request, RequestOptions.DEFAULT);
            // 判断索引删除是否被确认
            if(response.isAcknowledged()){
                return ApiUtils.success(null,"索引删除成功");
            } else {
                return ApiUtils.error("索引删除失败");
            }
        } catch (Exception e) {
            return ApiUtils.error("索引删除失败", e.getMessage());
        }
    }

    /**
     * 使用新版删除索引库
     * @return
     */
    @GetMapping("/delete/new")
    public ApiResponse deleteIndexByNewClient(){
        try {
            // 创建 DELETE 请求，指定要删除的索引名称 "/hotel"
            Request request = new Request("DELETE", "/hotel");
            // 使用新版 RestClient 发送请求
            Response response = newEsClient.performRequest(request);
            // 检查响应状态码，如果为 200，表示删除成功
            if (response.getStatusLine().getStatusCode() == 200) {
                return ApiUtils.success("索引删除成功", response.getStatusLine().toString());
            } else {
                return ApiUtils.error("索引删除失败", response.getStatusLine().toString());
            }
        } catch (Exception e) {
            return ApiUtils.error("索引删除失败", e.getMessage());
        }
    }

    @GetMapping("/exist/old")
    public ApiResponse existIndexByOldClient(){
        try {
            // 创建获取索引请求，传入索引名称
            GetIndexRequest request = new GetIndexRequest(Constant.INDEX_HOTEL_NAME);
            // 发送获取索引请求
            boolean exists = oldEsClient.indices().exists(request, RequestOptions.DEFAULT);
            // 判断索引是否存在
            if(exists){
                return ApiUtils.success(null,"索引存在");
            } else {
                return ApiUtils.error("索引不存在");
            }
        } catch (Exception e) {
            return ApiUtils.error("索引查询失败", e.getMessage());
        }
    }

    @GetMapping("/exist/new")
    public ApiResponse existIndexByNewClient(){
        try {
            // 创建 HEAD 请求，指定要查询的索引名称 "/hotel"
            Request request = new Request("HEAD", "/hotel");
            // 使用新版 RestClient 发送请求
            Response response = newEsClient.performRequest(request);
            // 检查响应状态码，如果为 200，表示索引存在
            if (response.getStatusLine().getStatusCode() == 200) {
                return ApiUtils.success("索引存在", response.getStatusLine().toString());
            }else {
                return ApiUtils.error("索引不存在", response.getStatusLine().toString());
            }
        } catch (Exception e) {
            return ApiUtils.error("索引查询失败", e.getMessage());
        }
    }


}
