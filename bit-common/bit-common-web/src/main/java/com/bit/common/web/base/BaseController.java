package com.bit.common.web.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bit.common.web.context.ClientMetaInfo;
import com.bit.common.web.model.page.PageModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 所有 Controller 的父类，统一提供请求元数据能力
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public abstract class BaseController {

    @Autowired
    private HttpServletRequest request;

    /**
     * 获取当前请求的客户端设备信息（封装了 IP、系统、设备、地区、网络）
     */
    protected ClientMetaInfo getClientInfo() {
        return ClientMetaInfo.from(request);
    }

    /**
     * 获取当前请求 IP（便捷方法）
     */
    protected String getClientIp() {
        return getClientInfo().getIp();
    }

    protected PageModel getPageParam() {
        PageModel param = new PageModel();

        String pageNumStr = request.getParameter("pageNum");
        String pageSizeStr = request.getParameter("pageSize");

        if (pageNumStr != null) param.setPageNum(Long.valueOf(pageNumStr));
        if (pageSizeStr != null) param.setPageSize(Long.valueOf(pageSizeStr));

        return param;
    }

    /** 返回 MyBatis Plus 的 Page 对象 */
    protected <T> Page<T> getMybatisPage() {
        PageModel p = getPageParam();
        return new Page<>(p.getSafePageNum(), p.getSafePageSize());
    }
}
