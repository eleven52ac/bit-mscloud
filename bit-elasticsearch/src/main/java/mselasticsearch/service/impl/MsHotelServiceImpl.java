package mselasticsearch.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import common.common.ApiResponse;
import common.common.ApiUtils;
import mselasticsearch.Constant;
import mselasticsearch.domain.HotelDoc;
import mselasticsearch.domain.MsHotel;
import mselasticsearch.service.DocumentStrategy;
import mselasticsearch.mapper.MsHotelMapper;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author camellia
* @description 针对表【tb_hotel】的数据库操作Service实现
* @createDate 2025-01-20 11:23:05
*/
@Service("msHotelService")
public class MsHotelServiceImpl extends ServiceImpl<MsHotelMapper, MsHotel> implements DocumentStrategy {

    @Autowired
    private MsHotelMapper msHotelMapper;

    /**
     * 添加文档
     * @param hotelId
     * @param oldElasticsearchClient
     * @return
     */
    @Override
    public ApiResponse addDocumentByOldClient(Long hotelId, RestHighLevelClient oldElasticsearchClient) {
        try {
            MsHotel hotel = msHotelMapper.selectById(hotelId);
            if (hotel == null) return ApiUtils.error("酒店不存在");
            HotelDoc hotelDoc = new HotelDoc(hotel);
            String jsonHotelDoc = JSON.toJSONString(hotelDoc);
            IndexRequest request = new IndexRequest(Constant.INDEX_HOTEL_NAME);
            request.id(hotelDoc.getId().toString());
            request.source(jsonHotelDoc, XContentType.JSON);
            IndexResponse index = oldElasticsearchClient.index(request, RequestOptions.DEFAULT);
            if (index.getResult().equals(DocWriteResponse.Result.CREATED)) {
                return ApiUtils.success(index, "添加文档成功");
            } else if (index.getResult().equals(DocWriteResponse.Result.UPDATED)) {
                return ApiUtils.success(index, "文档已更新");
            } else {
                return ApiUtils.error("添加文档失败，操作未成功");
            }
        } catch (Exception e) {
            return ApiUtils.error("添加文档失败", e.getMessage());
        }
    }

    /**
     * 添加文档
     * @param hotelId
     * @param newElasticsearchClient
     * @return
     */
    @Override
    public ApiResponse addDocumentByNewClient(Long hotelId, RestClient newElasticsearchClient) {
        try {
            MsHotel hotel = msHotelMapper.selectById(hotelId);
            if (hotel == null) return ApiUtils.error("酒店不存在");
            HotelDoc hotelDoc = new HotelDoc(hotel);
            String jsonHotelDoc = JSON.toJSONString(hotelDoc);
            Request request = new Request("PUT", "/" + Constant.INDEX_HOTEL_NAME + "/_doc/" + hotelDoc.getId());
            request.setJsonEntity(jsonHotelDoc);
            Response response = newElasticsearchClient.performRequest(request);
            if(response.getStatusLine().getStatusCode() == 200){
                JSONObject jsonObject = JSON.parseObject(response.getEntity().getContent());
                return ApiUtils.success(jsonObject, "添加文档成功");
            }else{
                return ApiUtils.error("添加文档失败", response.getStatusLine().toString());
            }
        } catch (Exception e) {
            return ApiUtils.error("添加文档失败", e.getMessage());
        }
    }

    /**
     * 添加文档异步
     * @param hotelId
     * @param newElasticsearchClient
     * @return
     */
    @Override
    public ApiResponse addDocumentByNewClientAsync(Long hotelId, RestClient newElasticsearchClient) {
        try {
            MsHotel hotel = msHotelMapper.selectById(hotelId);
            HotelDoc hotelDoc = new HotelDoc(hotel);
            String jsonHotelDoc = JSON.toJSONString(hotelDoc);
            Request request = new Request("PUT", "/" + Constant.INDEX_HOTEL_NAME + "/_doc/" + hotelDoc.getId());
            request.setJsonEntity(jsonHotelDoc);
            newElasticsearchClient.performRequestAsync(request, new ResponseListener() {
                @Override
                public void onSuccess(Response response) {
                    try {
                        // 异步请求成功，处理返回的数据
                        String responseBody = EntityUtils.toString(response.getEntity());  // 获取响应内容
                        JSONObject jsonObject = JSON.parseObject(responseBody);  // 解析 JSON
                        System.out.println("成功响应: " + jsonObject);
                    } catch (Exception e) {
                        // 处理读取响应时的异常
                        System.err.println("处理响应失败: " + e.getMessage());
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    // 异步请求失败时，处理异常
                    System.err.println("请求失败: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            return ApiUtils.success("请求已提交，处理中...");
        } catch (Exception e) {
            return ApiUtils.error("添加文档失败", e.getMessage());
        }
    }

    /**
     * 查询文档
     * @param id
     * @param oldElasticsearchClient
     * @return
     */
    @Override
    public ApiResponse queryDocumentByOldClient(Long id, RestHighLevelClient oldElasticsearchClient) {
        try {
            GetRequest request = new GetRequest(Constant.INDEX_HOTEL_NAME, id.toString());
            GetResponse response = oldElasticsearchClient.get(request, RequestOptions.DEFAULT);
            if(response.getSourceAsString() == null){
                return ApiUtils.success("查询"+id+"文档失败", "文档不存在");
            }else {
                HotelDoc hotelDoc = JSON.parseObject(response.getSourceAsString(), HotelDoc.class);
                return ApiUtils.success(hotelDoc, "查询"+id+"文档成功");
            }
        }catch (Exception e){
            return ApiUtils.error("查询文档失败", e.getMessage());
        }
    }

    @Override
    public ApiResponse queryDocumentByNewClient(Long id, RestClient newElasticsearchClient) {
        try{
            Request request = new Request("GET", "/" + Constant.INDEX_HOTEL_NAME + "/_doc/" + id);
            Response response = newElasticsearchClient.performRequest(request);
            if(response.getStatusLine().getStatusCode() == 200 && response.getEntity() != null){
                JSONObject jsonObject = JSON.parseObject(response.getEntity().getContent());
                return ApiUtils.success(jsonObject, "查询"+id+"文档成功");
            }
            else return ApiUtils.error("查询"+id+"文档失败", response.getStatusLine().toString());
        } catch (IOException e) {
            return ApiUtils.error("查询文档失败", e.getMessage());
        }
    }

    @Override
    public ApiResponse updateDocumentByOldClient(Long id, RestHighLevelClient oldElasticsearchClient) {
        try {
            UpdateRequest request = new UpdateRequest(Constant.INDEX_HOTEL_NAME, id.toString());
            Map<String, Object> fieldsToUpdate = new HashMap<>();
            fieldsToUpdate.put("address", "测试更新文档");
            fieldsToUpdate.put("price", 9999);
            try {
                request.doc(fieldsToUpdate);
                UpdateResponse response = oldElasticsearchClient.update(request, RequestOptions.DEFAULT);
                if (response.getResult() == DocWriteResponse.Result.UPDATED) {
                    return ApiUtils.success(response, "更新文档 " + id + " 成功");
                } else {
                    return ApiUtils.success(response, "更新文档 " + id + " 失败，原因: " + response.getResult());
                }
            } catch (IOException e) {
                return ApiUtils.error("更新文档失败，发生网络错误", e.getMessage());
            }
        } catch (Exception e) {
            return ApiUtils.error("更新文档失败", e.getMessage());
        }
    }

    @Override
    public ApiResponse updateDocumentByNewClient(Long id, RestClient newElasticsearchClient) {
        try {
            Map<String, Object> doc = new HashMap<>();
            doc.put("address", "测试更新文档");
            doc.put("price", 9999);
            Map<String, Object> requestMapping = new HashMap<>();
            requestMapping.put("doc", doc);
            Request request = new Request("POST", "/" + Constant.INDEX_HOTEL_NAME + "/_update/" + id);
            request.setJsonEntity(JSON.toJSONString(requestMapping));
            Response response = newElasticsearchClient.performRequest(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return ApiUtils.success(String.format("更新文档 %d 成功", id), response.getStatusLine().toString());
            } else {
                return ApiUtils.error(String.format("更新文档 %d 失败", id), response.getStatusLine().toString());
            }
        } catch (IOException e) {
            return ApiUtils.error("更新文档失败", "网络或服务器错误: " + e.getMessage());
        } catch (Exception e) {
            return ApiUtils.error("更新文档失败", "未知错误: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse deleteDocumentByOldClient(Long id, RestHighLevelClient oldElasticsearchClient) {
        try {
            DeleteRequest request = new DeleteRequest(Constant.INDEX_HOTEL_NAME, id.toString());
            DeleteResponse response = oldElasticsearchClient.delete(request, RequestOptions.DEFAULT);
            if (response.getResult() == DocWriteResponse.Result.DELETED) {
                return ApiUtils.success(response, "删除文档 " + id + " 成功");
            } else {
                return ApiUtils.success(response, "删除文档 " + id + " 失败，原因: " + response.getResult());
            }
        } catch (ElasticsearchException e) {
            return ApiUtils.error("删除文档失败", "Elasticsearch 错误: " + e.getMessage());
        } catch (IOException e) {
            return ApiUtils.error("删除文档失败", "IO 错误: " + e.getMessage());
        } catch (Exception e) {
            return ApiUtils.error("删除文档失败", "未知错误: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse deleteDocumentByNewClient(Long id, RestClient newElasticsearchClient) {
        try {
            Request request = new Request("DELETE", "/" + Constant.INDEX_HOTEL_NAME + "/_doc/" + id);
            Response response = newElasticsearchClient.performRequest(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return ApiUtils.success(String.format("删除文档 %d 成功", id), response.getStatusLine().toString());
            } else {
                return ApiUtils.error(String.format("删除文档 %d 失败", id), "状态码: " + statusCode + ", 原因: " + response.getStatusLine().toString());
            }
        } catch (ElasticsearchException e) {
            return ApiUtils.error(String.format("删除文档 %d 失败", id), "Elasticsearch 错误: " + e.getMessage());
        } catch (IOException e) {
            return ApiUtils.error(String.format("删除文档 %d 失败", id), "网络或服务器错误: " + e.getMessage());
        } catch (Exception e) {
            return ApiUtils.error(String.format("删除文档 %d 失败", id), "未知错误: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse batchAddDocumentByOldClient(RestHighLevelClient oldElasticsearchClient) {
        try {
            List<MsHotel> msHotelList = this.list();
            if (msHotelList.isEmpty()) {
                return ApiUtils.success("没有需要添加的文档", "没有找到酒店数据");
            }
            BulkRequest request = new BulkRequest();
            // 遍历所有酒店数据并添加到批量请求中
            msHotelList.forEach(msHotel -> {
                String hotelJson = JSON.toJSONString(new HotelDoc(msHotel));
                IndexRequest indexRequest = new IndexRequest(Constant.INDEX_HOTEL_NAME)
                        .id(msHotel.getId().toString())
                        .source(hotelJson, XContentType.JSON);
                request.add(indexRequest);
            });
            if (request.requests().isEmpty()) {
                return ApiUtils.error("批量添加文档失败", "没有有效的文档可以添加");
            }
            BulkResponse bulk = oldElasticsearchClient.bulk(request, RequestOptions.DEFAULT);
            if (bulk.hasFailures()) {
                return ApiUtils.error("批量添加文档失败", bulk.buildFailureMessage());
            } else {
                return ApiUtils.success(bulk, "批量添加文档成功");
            }
        } catch (Exception e) {
            return ApiUtils.error("批量添加文档失败", e.getMessage());
        }
    }

    @Override
    public ApiResponse batchAddDocumentByNewClient(RestClient newElasticsearchClient) {
        try {
            List<MsHotel> msHotelList = this.list();
            if (msHotelList.isEmpty()) {
                return ApiUtils.success("没有需要添加的文档", "没有找到酒店数据");
            }
            // 构建 BulkRequest 请求
            Request request = new Request("POST", "/" + Constant.INDEX_HOTEL_NAME + "/_bulk");
            StringBuilder bulkBody = new StringBuilder();
            // 遍历所有酒店数据并构建批量请求体
            for (MsHotel msHotel : msHotelList) {
                HotelDoc hotelDoc= new HotelDoc(msHotel);
                // 创建文档的索引操作
                String indexAction = String.format("{\"index\": {\"_id\": \"%s\"}}%n", hotelDoc.getId());
                String hotelJson = JSON.toJSONString(hotelDoc);
                String documentAction = String.format("%s%n", hotelJson);
                // 拼接索引操作和文档数据
                bulkBody.append(indexAction).append(documentAction);
            }
            // 将批量请求体设置到请求中
            request.setJsonEntity(bulkBody.toString());
            // 执行批量请求
            Response response = newElasticsearchClient.performRequest(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return ApiUtils.success("批量添加文档成功", response.getStatusLine().toString());
            } else {
                return ApiUtils.error("批量添加文档失败", response.getStatusLine().toString());
            }
        } catch (IOException e) {
            return ApiUtils.error("批量添加文档失败", "网络或服务器错误: " + e.getMessage());
        } catch (Exception e) {
            return ApiUtils.error("批量添加文档失败", "未知错误: " + e.getMessage());
        }
    }
}




