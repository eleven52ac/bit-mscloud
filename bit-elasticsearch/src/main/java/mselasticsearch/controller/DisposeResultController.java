package mselasticsearch.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import mscommon.common.ApiResponse;
import mscommon.common.ApiUtils;
import mselasticsearch.service.DisposeStrategy;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Datetime: 2025年03月02日21:23
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mselasticsearch.controller
 * @Project: camellia-mscloud
 * @Description:
 */
@RequestMapping("/dispose")
@RestController
@Slf4j
public class DisposeResultController {

    @Autowired
    private RestHighLevelClient oldClient;

    private final Map<String, DisposeStrategy> disposeStrategyMap = new HashMap<>();

    @Autowired
    DisposeResultController(@Qualifier("hotelDispose") DisposeStrategy hotelDispose,
                            @Qualifier("houseDispose") DisposeStrategy houseDispose){
        disposeStrategyMap.put("hotel", hotelDispose);
        disposeStrategyMap.put("house", houseDispose);
    }

    @GetMapping("/pagination")
    public ApiResponse<Map<String, Object>> disposeResult(@RequestParam("database") String database,
                                                          @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                          @RequestParam(name = "size", required = false, defaultValue = "15") Integer size,
                                                          @RequestParam(name = "sortField", required = false) String sortField,
                                                          @RequestParam(name = "sortOrder", required = false, defaultValue = "desc") String sortOrder) {
        try{
            if(disposeStrategyMap.containsKey(database)){
                return disposeStrategyMap.get(database).matchAll(oldClient,database,page,size,sortField,sortOrder);
            }else return ApiUtils.error("该索引不存在");
        }catch (Exception e){
            Map <String, Object> errorMap = new HashMap<>();
            errorMap.put("查询失败", e.getMessage());
            return ApiUtils.error(errorMap, e.getMessage());
        }
    }

    @GetMapping("/highlighter/old")
    public ApiResponse<Map<String, Object>> disposeResultByHighlighter(
            @RequestParam("database") String database,
            @RequestParam("field") String field,
            @RequestParam("condition") String condition,
            @RequestParam("highLighterField") String highLighterField) {
        try {
            // 1. 创建 SearchRequest 对象，指定查询的索引
            SearchRequest request = new SearchRequest(database);
            request.source().query(QueryBuilders.matchQuery(field, condition));
            request.source().highlighter(new HighlightBuilder()
                    .field(highLighterField)
                    .requireFieldMatch(false));
            // 2. 执行查询
            SearchResponse response = oldClient.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            // 3. 使用 Map<String, Object> 便于存放 List<String> 和其他数据（如文章总数）
            Map<String, Object> resultMap = new HashMap<>();
            // 4. 遍历所有查询结果，提取高亮信息
            for (SearchHit hit : hits) {
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields != null && !highlightFields.isEmpty()) {
                    for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
                        String fieldName = entry.getKey();
                        HighlightField highlightField = entry.getValue();
                        List<String> fragments = new ArrayList<>();
                        for (Text fragment : highlightField.getFragments()) {
                            fragments.add(fragment.string());
                        }
                        // 如果同一字段在多个文档中出现，将高亮片段合并
                        if (resultMap.containsKey(fieldName)) {
                            @SuppressWarnings("unchecked")
                            List<String> existingFragments = (List<String>) resultMap.get(fieldName);
                            existingFragments.addAll(fragments);
                        } else {
                            resultMap.put(fieldName, fragments);
                        }
                    }
                }
            }
            // 5. 添加查询总数
            resultMap.put("文章总数", hits.getTotalHits().value);
            return ApiUtils.success(resultMap, "查询成功");
        } catch (IOException e) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return ApiUtils.error(errorMap, "查询失败");
        }
    }
}
