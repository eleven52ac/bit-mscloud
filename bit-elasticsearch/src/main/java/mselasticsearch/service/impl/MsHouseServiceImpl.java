package mselasticsearch.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import mscommon.common.ApiResponse;
import mscommon.common.ApiUtils;
import mselasticsearch.Constant;
import mselasticsearch.domain.MsHouseInfo;
import mselasticsearch.mapper.MsHouseInfoMapper;
import mselasticsearch.service.DocumentStrategy;
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
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Datetime: 2025年03月03日16:18
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mselasticsearch.service.impl
 * @Project: camellia-mscloud
 * @Description:
 */
@Service("msHouseService")
public  class MsHouseServiceImpl extends ServiceImpl<MsHouseInfoMapper, MsHouseInfo> implements DocumentStrategy {

    @Autowired
    private MsHouseInfoMapper msHouseInfoMapper;

    /**
     * 添加文档
     * @param hotelId
     * @param oldElasticsearchClient
     * @return
     */
    @Override
    public ApiResponse addDocumentByOldClient(Long hotelId, RestHighLevelClient oldElasticsearchClient) {
        try{
            MsHouseInfo msHouseInfo = msHouseInfoMapper.selectById(hotelId);
            if (msHouseInfo == null || msHouseInfo.getHouseId() == null) return ApiUtils.error("房屋不存在");
            String houseJson = JSON.toJSONString(msHouseInfo);
            IndexRequest request = new IndexRequest(Constant.INDEX_HOUSE_NAME)
                    .id(msHouseInfo.getHouseId().toString())
                    .source(houseJson, XContentType.JSON);
            IndexResponse index = oldElasticsearchClient.index(request, RequestOptions.DEFAULT);
            if (index.getResult() == DocWriteResponse.Result.CREATED) {
                return ApiUtils.success(index, "添加文档成功");
            }else if (index.getResult().equals(DocWriteResponse.Result.UPDATED)) {
                return ApiUtils.success(index, "文档已更新");
            } else {
                return ApiUtils.error("添加文档失败", "未知错误");
            }
        } catch (IOException e) {
            return ApiUtils.error("添加文档失败", "网络或服务器错误: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse addDocumentByNewClient(Long hotelId, RestClient newElasticsearchClient) {
        return null;
    }

    @Override
    public ApiResponse addDocumentByNewClientAsync(Long hotelId, RestClient newElasticsearchClient) {
        return null;
    }

    /**
     * 查询文档
     * @param id
     * @param oldElasticsearchClient
     * @return
     */
    @Override
    public ApiResponse queryDocumentByOldClient(Long id, RestHighLevelClient oldElasticsearchClient) {
        try{
            GetRequest request = new GetRequest(Constant.INDEX_HOUSE_NAME, id.toString());
            GetResponse response = oldElasticsearchClient.get(request, RequestOptions.DEFAULT);
            if(response.getSourceAsString() == null){
                return ApiUtils.success("查询"+id+"文档失败", "文档不存在");
            }else {
                MsHouseInfo msHouseInfo = JSON.parseObject(response.getSourceAsString(), MsHouseInfo.class);
                return ApiUtils.success(msHouseInfo, "查询"+id+"文档成功");
            }
        } catch (IOException e) {
            return ApiUtils.error("查询文档失败", e.getMessage());
        }
    }


    @Override
    public ApiResponse queryDocumentByNewClient(Long id, RestClient newElasticsearchClient) {
        return null;
    }

    @Override
    public ApiResponse updateDocumentByOldClient(Long id, RestHighLevelClient oldElasticsearchClient) {
        try{
            UpdateRequest request = new UpdateRequest(Constant.INDEX_HOUSE_NAME, id.toString());
            Map<String, Object> fieldsToUpdate = new HashMap<>();
            fieldsToUpdate.put("address", "测试更新文档");
            try {
                request.doc(fieldsToUpdate);
                UpdateResponse response = oldElasticsearchClient.update(request, RequestOptions.DEFAULT);
                if (response.getResult() == DocWriteResponse.Result.UPDATED) {
                    return ApiUtils.success(response, "更新文档 " + id + " 成功");
                } else {
                    return ApiUtils.success(response, "更新文档 " + id + " 失败，原因: " + response.getResult());
                }
            } catch (IOException e) {
                return ApiUtils.error("更新文档失败", "网络或服务器错误: " + e.getMessage());
            }
        }catch (Exception e){
            return ApiUtils.error("更新文档失败", e.getMessage());
        }
    }

    @Override
    public ApiResponse updateDocumentByNewClient(Long id, RestClient oldElasticsearchClient) {
        return null;
    }

    @Override
    public ApiResponse deleteDocumentByOldClient(Long id, RestHighLevelClient oldElasticsearchClient) {
        try{
            DeleteRequest request = new DeleteRequest(Constant.INDEX_HOUSE_NAME, id.toString());
            DeleteResponse response = oldElasticsearchClient.delete(request, RequestOptions.DEFAULT);
            if (response.getResult() == DocWriteResponse.Result.DELETED) {
                return ApiUtils.success(response, "删除文档 " + id + " 成功");
            } else {
                return ApiUtils.success(response, "删除文档 " + id + " 失败，原因: "+ response.getResult());
            }
        } catch (IOException e) {
            return ApiUtils.error("删除文档失败", "网络或服务器错误: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse deleteDocumentByNewClient(Long id, RestClient newElasticsearchClient) {
        return null;
    }

    @Override
    @DS("house")
    public ApiResponse batchAddDocumentByOldClient(RestHighLevelClient oldElasticsearchClient) {
        try{
            QueryWrapper<MsHouseInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNotNull("house_id");
            List<MsHouseInfo> list = this.list((queryWrapper));
            if (list.isEmpty()) {
                return ApiUtils.success("没有需要添加的文档", "没有找到房屋数据");
            }
            BulkRequest request = new BulkRequest();
            list.forEach(msHouseInfo -> {
                String houseJson = JSON.toJSONString(msHouseInfo);
                IndexRequest indexRequest = new IndexRequest(Constant.INDEX_HOUSE_NAME)
                        .id(msHouseInfo.getHouseId().toString())
                        .source(houseJson, XContentType.JSON);
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
        }catch (Exception e){
            return ApiUtils.error("批量添加文档失败", e.getMessage());
        }
    }

    @Override
    public ApiResponse batchAddDocumentByNewClient(RestClient newElasticsearchClient) {
        return null;
    }
}
