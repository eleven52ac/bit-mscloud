package mselasticsearch.controller;

import lombok.extern.slf4j.Slf4j;
import common.common.ApiResponse;
import common.common.ApiUtils;
import mselasticsearch.service.QueryDocStrategy;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Datetime: 2025年03月01日15:52
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mselasticsearch.controller
 * @Project: camellia-mscloud
 * @Description:
 */
@RequestMapping("/doc")
@RestController
@Slf4j
public class QueryDocController {

    @Autowired
    private RestHighLevelClient oldElasticsearchClient;

    private final Map<String, QueryDocStrategy> queryDocStrategyMap = new HashMap<>();

    @Autowired
    QueryDocController(@Qualifier("hotelQueryService") QueryDocStrategy queryDocStrategy,
                       @Qualifier("houseQueryService") QueryDocStrategy houseQueryStrategy) {
        queryDocStrategyMap.put("hotel", queryDocStrategy);
        queryDocStrategyMap.put("house", houseQueryStrategy);
    }

    /**
     * 全文检索查询
     *
     * @param database
     * @return
     */
    @GetMapping("/query/old")
    public ApiResponse<Map<String, Object>> queryDocumentByMatchAll(@RequestParam("database") String database) {
        try {
            if (queryDocStrategyMap.containsKey(database)) {
                return queryDocStrategyMap.get(database).matchAll(oldElasticsearchClient, database);
            } else {
                return ApiUtils.error("该索引不存在");
            }
        } catch (Exception e) {
            Map<String, Object> map = new HashMap();
            map.put("查询失败", e.getMessage());
            return ApiUtils.error(map, e.getMessage());
        }
    }

    /**
     * 条件检索查询
     * @param database
     * @param fields
     * @param condition
     * @return
     */
    @GetMapping("/query/condition/new")
    public ApiResponse<Map<String, Object>> queryDocumentByMatch(@RequestParam("database") String database,
                                                                     @RequestParam("fields") String fields,
                                                                     @RequestParam("condition") String condition) {
        try{
            if(queryDocStrategyMap.containsKey(database)){
                return queryDocStrategyMap.get(database).matchByCondition(oldElasticsearchClient, database, fields, condition);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            Map<String, Object> map = new HashMap();
            map.put("查询失败", e.getMessage());
            return ApiUtils.error(map, e.getMessage());
        }
    }

    /**
     * 精确条件检索查询
     * @param database
     * @param field
     * @param value
     * @return
     */
    @GetMapping("/query/condition/term")
    public ApiResponse<Map<String, Object>> queryDocumentByTerm(@RequestParam("database") String database,
                                                                @RequestParam("field") String field,
                                                                @RequestParam("value") String value) {
        try{
            if(queryDocStrategyMap.containsKey(database)){
                return queryDocStrategyMap.get(database).matchByTerm(oldElasticsearchClient, database, field, value);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            Map<String, Object> map = new HashMap();
            map.put("查询失败", e.getMessage());
            return ApiUtils.error(map, e.getMessage());
        }
    }

    /**
     * 范围检索查询
     * @param database
     * @param field
     * @param gte
     * @param lte
     * @return
     */
    @GetMapping("/query/condition/range")
    public ApiResponse<Map<String, Object>> queryDocumentByRange(@RequestParam("database") String database,
                                                                 @RequestParam("field") String field,
                                                                 @RequestParam(value = "gte", required = false) String gte,
                                                                 @RequestParam(value = "lte", required = false) String lte) {
        try{
            if(queryDocStrategyMap.containsKey(database)){
                return queryDocStrategyMap.get(database).matchByRange(oldElasticsearchClient, database, field, gte, lte);
            }
            return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            Map<String, Object> map = new HashMap();
            map.put("查询失败", e.getMessage());
            return ApiUtils.error(map, e.getMessage());
        }
    }
}
