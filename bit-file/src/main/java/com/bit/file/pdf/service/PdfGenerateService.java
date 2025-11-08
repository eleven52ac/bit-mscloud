package com.bit.file.pdf.service;

import commons.enums.PdfTypeEnum;
import org.springframework.http.ResponseEntity;

/**
 * @Datetime: 2025年10月22日11:36
 * @Author: Eleven52AC
 * @Description: 系统业务中PDF的生成策略
 */
public interface PdfGenerateService {

    /**
     *
     * @Author: Eleven52AC
     * @Description: 获取支持的业务类型
     * @return
     */
    PdfTypeEnum supportType();

    /**
     *
     * @Author: Eleven52AC
     * @Description: 生成PDF
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param pdfPath PDF保存路径，没有就采用系统默认的。
     */
    ResponseEntity<byte[]> generatePdf(String businessId, PdfTypeEnum businessType, String pdfPath);
}
