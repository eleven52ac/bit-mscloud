package com.bit.common.utils.pdf;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


/**
 * @Datetime: 2024年12月29日10:42
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mscommon.utils
 * @Project: camellia-mscloud
 * @Description: PDF  工具类
 */
public class PDFUtils {

    /**
     * 获取PDF文件的页数
     * @param file
     * @return
     */
    public static int getPDFPageCount(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空！");
        }
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(file.getInputStream()))) {
            return pdfDoc.getNumberOfPages();
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // 返回 -1 表示读取失败
        }
    }

    // 初始化语言检测器
    static {
        try {
            // 加载语言模型，路径为 langdetect 的语言模型文件夹
            String profilePath = "/Volumes/camellia/language-detection/profiles";
            DetectorFactory.loadProfile(profilePath);
            System.out.println("Language profiles loaded successfully from: " + profilePath);

            // 验证加载的语言模型数量
            if (DetectorFactory.getLangList().isEmpty()) {
                throw new RuntimeException("No language profiles loaded. Please check the profile path.");
            }
            System.out.println("Loaded language profiles: " + DetectorFactory.getLangList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load language profiles. Please check the path and profile files.", e);
        }
    }

    /**
     * 检测 PDF 文件的语言
     *
     * @param file MultipartFile 对象
     * @return 检测出的语言代码 (如 "en", "zh", "fr")
     * @throws IOException 如果读取 PDF 文件或检测失败
     */
    public static String detectLanguageFromPDF(MultipartFile file) {
        StringBuilder textContent = new StringBuilder();
        // 提取 PDF 内容
        try (PdfReader pdfReader = new PdfReader(file.getInputStream());
             PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            int numberOfPages = pdfDocument.getNumberOfPages();
            for (int i = 1; i <= numberOfPages; i++) {
                String pageContent = PdfTextExtractor.getTextFromPage(
                        pdfDocument.getPage(i),
                        new LocationTextExtractionStrategy() // 提取文本位置的策略，避免乱码
                );
                textContent.append(pageContent);
            }
            //System.out.println("Extracted Text Content: " + textContent.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or parse the PDF file", e);
        }
        // 检测语言
        try {
            Detector detector = DetectorFactory.create();
            detector.append(textContent.toString());
            return detector.detect();
        } catch (LangDetectException e) {
            throw new RuntimeException("Failed to detect language from the PDF content", e);
        }
    }

    /**
     * 获取 PDF 文件的作者
     *
     * @param file MultipartFile 对象
     * @return PDF 文件的作者
     * @throws IOException 如果读取 PDF 文件失败
     */
    public static String getPdfAuthor(MultipartFile file) throws IOException {
        try (PdfReader pdfReader = new PdfReader(file.getInputStream());
             PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            // 获取 PDF 的文档信息
            PdfDocumentInfo pdfDocumentInfo = pdfDocument.getDocumentInfo();
            // 返回作者信息
            return pdfDocumentInfo.getAuthor();
        }
    }

}
