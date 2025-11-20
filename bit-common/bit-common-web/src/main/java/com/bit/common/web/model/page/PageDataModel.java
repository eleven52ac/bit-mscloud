package com.bit.common.web.model.page;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Datetime: 2025年11月20日14:56
 * @Author: Eleven52AC
 * @Description: 分页数据模型
 */
@Data
public class PageDataModel<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -4251712784309006455L;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 每页数量
     */
    private Long pageSize;

    /**
     * 当前页码
     */
    private Long pageNum;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 当前页的数据列表
     */
    private List<T> records;

    /**
     * 通用转换方法：从 MyBatis Plus Page<T> 转成 PageDataModel<T>
     * @Author: Eleven52AC
     * @Description:
     * @param page
     * @return
     * @param <T>
     */
    public static <T> PageDataModel<T> from(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        PageDataModel<T> model = new PageDataModel<>();
        model.setTotal(page.getTotal());
        model.setPageSize(page.getSize());
        model.setPageNum(page.getCurrent());
        model.setPages(page.getPages());
        model.setRecords(page.getRecords());
        return model;
    }
}
