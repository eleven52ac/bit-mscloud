package com.bit.common.web.model.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.core.dto.response.ApiStatus;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;


/**
 * @Datetime: 2025年11月20日14:40
 * @Author: Eleven52AC
 * @Description: 分页信息
 */
@Data
public class PageDataInfo extends ApiResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -5922265843178214822L;

    /**
     * 处理分页结果的成功响应，返回分页数据及总记录数。
     *
     * @param data  返回的数据列表
     * @param total 数据总数，用于分页
     * @param <T> 数据类型
     * @return 包含数据、总数、状态码和消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> getPageDataInfo(Page<?> page) {
        PageDataModel data = PageDataModel.from(page);
        return new ApiResponse(ApiStatus.SUCCESS, data, "成功");
    }

}
