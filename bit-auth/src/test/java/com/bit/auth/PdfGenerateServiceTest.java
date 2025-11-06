//package com.bit.auth;
//
//import commons.enums.PdfTypeEnum;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
///**
// * @Datetime: 2025年10月22日17:05
// * @Author: Eleven52AC
// * @Description:
// */
//@ExtendWith(MockitoExtension.class)
//class PdfGenerateServiceTest {
//
//    @Mock
//    private PdfGenerateService pdfGenerateService;
//
//    private PdfGenerateStrategyDispatcher dispatcher;
//
//    @BeforeEach
//    void setUp() {
//        when(pdfGenerateService.supportType()).thenReturn(PdfTypeEnum.BZDX_FAMILY_QUALIFICATION_RETRIAL);
//        dispatcher = new PdfGenerateStrategyDispatcher(List.of(pdfGenerateService));
//    }
//
//    @Test
//    void testGeneratePdf() {
//        dispatcher.generate("1", PdfTypeEnum.BZDX_FAMILY_QUALIFICATION_RETRIAL.getCode(), "testPath");
//        verify(pdfGenerateService).generatePdf("1", PdfTypeEnum.BZDX_FAMILY_QUALIFICATION_RETRIAL, "testPath");
//    }
//}
//
