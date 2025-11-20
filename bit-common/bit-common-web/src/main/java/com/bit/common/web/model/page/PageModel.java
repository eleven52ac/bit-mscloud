package com.bit.common.web.model.page;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

/**
 * @Datetime: 2025年11月20日14:25
 * @Author: Eleven52AC
 * @Description: 分页数据模型
 */
@Data
public class PageModel implements Serializable {

    @Serial
    private static final long serialVersionUID = -6507752560553288269L;

    /** 当前页码 */
    private Long pageNum = 1L;

    /** 每页大小 */
    private Long pageSize = 10L;

    /** 排序字段 */
    private String orderByColumn;

    /** 排序方式 asc / desc */
    private String isAsc = "asc";

    /** 分页参数合理化 */
    private Boolean reasonable = true;

    /**
     * 生成 SQL 排序语句（防注入 + 下划线转换）
     */
    public String getOrderBy() {
        if (orderByColumn == null || orderByColumn.trim().isEmpty()) {
            return null;
        }
        // 转下划线
        String column = toUnderScoreCase(orderByColumn);
        // 防止 SQL 注入
        column = column.replaceAll("[A-Za-z0-9_]+", "$0");
        return column + " " + isAsc;
    }

    /**
     * 兼容 ElementUI、Ant Design "ascending or descending"
     * @Author: Eleven52AC
     * @Description:
     * @param isAsc
     */
    public void setIsAsc(String isAsc) {
        if ("ascending".equalsIgnoreCase(isAsc)) {
            this.isAsc = "asc";
        } else if ("descending".equalsIgnoreCase(isAsc)) {
            this.isAsc = "desc";
        } else {
            this.isAsc = isAsc;
        }
    }

    /**
     * 驼峰转下划线
     * @Author: Eleven52AC
     * @Description:
     * @param str
     * @return
     */
    private String toUnderScoreCase(String str) {
        return str.replaceAll("([A-Z])", "_$1").toLowerCase();
    }

    public Long getSafePageNum() {
        return pageNum == null || pageNum < 1 ? 1L : pageNum;
    }

    public Long getSafePageSize() {
        return pageSize == null || pageSize < 1 ? 10L : pageSize;
    }
}
