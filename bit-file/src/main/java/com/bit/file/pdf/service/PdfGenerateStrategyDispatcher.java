package com.bit.file.pdf.service;


import commons.enums.PdfTypeEnum;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PdfGenerateStrategyDispatcher {

    private final Map<PdfTypeEnum, PdfGenerateService> strategyMap = new HashMap<>();

    public PdfGenerateStrategyDispatcher(List<PdfGenerateService> services) {
        services.forEach(s -> strategyMap.put(s.supportType(), s));
    }

    public void generate(String businessId, String businessTypeCode, String pdfPath) {
        PdfTypeEnum typeEnum = PdfTypeEnum.fromCode(businessTypeCode);
        PdfGenerateService strategy = strategyMap.get(typeEnum);
        if (strategy == null) {
            throw new IllegalArgumentException("未找到对应PDF策略: " + businessTypeCode);
        }
        strategy.generatePdf(businessId, typeEnum, pdfPath);
    }

}

