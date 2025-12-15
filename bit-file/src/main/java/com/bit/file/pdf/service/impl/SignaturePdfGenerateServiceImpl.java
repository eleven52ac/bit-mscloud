package com.bit.file.pdf.service.impl;

import com.bit.common.core.enums.biz.PdfTypeEnum;
import com.bit.file.pdf.service.PdfGenerateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;


/**
 * @Datetime: 2025年10月22日14:50
 * @Author: Eleven52AC
 * @Description: 公廉租房家庭资格再审PDF
 */
@Service
public class SignaturePdfGenerateServiceImpl implements PdfGenerateService {


    //@Autowired
    //private SpringTemplateEngine templateEngine;

    @Override
    public PdfTypeEnum supportType() {
        return PdfTypeEnum.BZDX_FAMILY_QUALIFICATION_RETRIAL;
    }

    @Override
    public ResponseEntity<byte[]> generatePdf(String businessId, PdfTypeEnum businessType, String pdfPath) {
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        System.out.println("生成PDF");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rendered.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.toByteArray());
    }
}
