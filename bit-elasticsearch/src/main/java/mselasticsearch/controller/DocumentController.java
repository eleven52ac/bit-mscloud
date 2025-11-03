package mselasticsearch.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import mscommon.common.ApiResponse;
import mscommon.common.ApiUtils;
import mselasticsearch.Constant;
import mselasticsearch.domain.HotelDoc;
import mselasticsearch.domain.MsHotel;
import mselasticsearch.service.DocumentStrategy;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Datetime: 2025年01月20日10:33
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mselasticsearch.controller
 * @Project: camellia-mscloud
 * @Description:
 */
@RequestMapping("/doc")
@RestController
@Slf4j
public class DocumentController {

    @Autowired
    private RestHighLevelClient oldElasticsearchClient;

    @Autowired
    private RestClient newElasticsearchClient;

    private final Map<String, DocumentStrategy> strategyMap = new HashMap<>();

    /**
     * 策略模式
     * @param msHotelService
     * @param msHouseService
     */
    @Autowired
    DocumentController(@Qualifier("msHotelService") DocumentStrategy msHotelService,
                       @Qualifier("msHouseService") DocumentStrategy msHouseService){
        strategyMap.put("hotel", msHotelService);
        strategyMap.put("house", msHouseService);
    }

    /**
     * 使用RestHighLevelClient客户端添加文档
     * @param hotelId
     * @return
     */
    @PostMapping("/add/old")
    public ApiResponse addDocumentByOldClient(@RequestParam("hotelId") Long hotelId,
                                              @RequestParam("strategy") String strategy) {
        try {
            if (strategyMap.containsKey(strategy)){
                return strategyMap.get(strategy).addDocumentByOldClient(hotelId, oldElasticsearchClient);
            }else return ApiUtils.error("改索引不存在");
        }catch (Exception e){
            return ApiUtils.error("添加文档失败", e.getMessage());
        }
    }

    /**
     * 使用RestClient客户端添加文档
     * @param hotelId
     * @return
     */
    @PostMapping("/add/new")
    public ApiResponse addDocumentByNewClient(@RequestParam("hotelId") Long hotelId,
                                              @RequestParam("strategy") String strategy) {
        try{
            if(strategyMap.containsKey(strategy)){
                return strategyMap.get(strategy).addDocumentByNewClient(hotelId, newElasticsearchClient);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            return ApiUtils.error("添加文档失败", e.getMessage());
        }
    }

    /**
     * 使用RestClient客户端更新文档（异步）
     */
    @PostMapping("/update/new/async")
    public ApiResponse addDocumentByNewClientAsync(@RequestParam("hotelId") Long hotelId,
                                                   @RequestParam("strategy") String strategy) {
        try{
            if(strategyMap.containsKey(strategy)){
                return strategyMap.get(strategy).addDocumentByNewClientAsync(hotelId, newElasticsearchClient);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            return ApiUtils.error("添加文档失败", e.getMessage());
        }
    }

    /**
     * 使用RestHighLevelClient客户端查询文档
     * @param id
     * @return
     */
    @PostMapping("/query/old")
    public ApiResponse queryDocumentByOldClient(@RequestParam("id") Long id,
                                                @RequestParam("strategy") String strategy) {
        try{
            if(strategyMap.containsKey(strategy)){
                return strategyMap.get(strategy).queryDocumentByOldClient(id, oldElasticsearchClient);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            return ApiUtils.error("查询文档失败", e.getMessage());
        }
    }

    /**
     * 使用RestClient客户端查询文档
     * @param id
     * @return
     */
    @PostMapping("/query/new")
    public ApiResponse queryDocumentByNewClient(@RequestParam("id") Long id,
                                                @RequestParam("strategy") String strategy) {
        try{
            if(strategyMap.containsKey(strategy)){
                return strategyMap.get(strategy).queryDocumentByNewClient(id, newElasticsearchClient);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            return ApiUtils.error("查询文档失败", e.getMessage());
        }
    }


    /**
     * 使用RestHighLevelClient客户端更新文档
     * @param id
     * @return
     */
    @PostMapping("/update/old")
    public ApiResponse updateDocumentByOldClient(@RequestParam("id") Long id,
                                                 @RequestParam("strategy") String strategy) {
        try{
            if(strategyMap.containsKey(strategy)){
                return strategyMap.get(strategy).updateDocumentByOldClient(id, oldElasticsearchClient);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            return ApiUtils.error("更新文档失败", e.getMessage());
        }
    }


    /**
     * 使用RestClient客户端更新文档
     * @param id
     * @return
     */
    @PostMapping("/update/new")
    public ApiResponse updateDocumentByNewClient(@RequestParam("id") Long id,
                                                 @RequestParam("strategy") String strategy) {
        try{
            if(strategyMap.containsKey(strategy)){
                return strategyMap.get(strategy).updateDocumentByNewClient(id, newElasticsearchClient);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            return ApiUtils.error("更新文档失败", e.getMessage());
        }
    }


    /**
     * 使用RestHighLevelClient客户端删除文档
     * @param id
     * @return
     */
    @PostMapping("/delete/old")
    public ApiResponse deleteDocumentByOldClient(@RequestParam("id") Long id,
                                                 @RequestParam("strategy") String strategy) {
        try{
            if(strategyMap.containsKey(strategy)){
                return strategyMap.get(strategy).deleteDocumentByOldClient(id, oldElasticsearchClient);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            return ApiUtils.error("删除文档失败", e.getMessage());
        }
    }


    /**
     * 使用RestClient客户端删除文档
     * @param id
     * @return
     */
    @PostMapping("/delete/new")
    public ApiResponse deleteDocumentByNewClient(@RequestParam("id") Long id,
                                                 @RequestParam("strategy") String strategy) {
        try{
            if(strategyMap.containsKey(strategy)){
                return strategyMap.get(strategy).deleteDocumentByNewClient(id, newElasticsearchClient);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            return ApiUtils.error("删除文档失败", e.getMessage());
        }
    }


    /**
     * 使用RestHighLevelClient客户端批量添加文档
     * @return
     */
    @GetMapping("/batch/add/old")
    public ApiResponse batchAddDocumentByOldClient(@RequestParam("strategy") String strategy) {
        try {
            if(strategyMap.containsKey(strategy)){
                return strategyMap.get(strategy).batchAddDocumentByOldClient(oldElasticsearchClient);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            return ApiUtils.error("批量添加文档失败", e.getMessage());
        }
    }


    /**
     * 使用RestClient客户端批量添加文档
     * @return
     */
    @GetMapping("/batch/add/new")
    public ApiResponse batchAddDocumentByNewClient(@RequestParam("strategy") String strategy) {
        try{
            if(strategyMap.containsKey(strategy)){
                return strategyMap.get(strategy).batchAddDocumentByNewClient(newElasticsearchClient);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            return ApiUtils.error("批量添加文档失败", e.getMessage());
        }
    }

    
}
