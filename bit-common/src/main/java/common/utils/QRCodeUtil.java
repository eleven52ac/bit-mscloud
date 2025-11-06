package common.utils;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * QRCodeUtil 工具类
 * 用于生成二维码的二进制流
 */
public class QRCodeUtil {

    /**
     * 生成二维码的二进制流
     * 
     * @param data 二维码的内容
     * @return 二维码的二进制流
     * @throws IOException 如果生成二维码时发生错误
     */
    public static byte[] generateQRCodeBinary(String data) throws IOException {
        // 使用 ByteArrayOutputStream 存储二维码的二进制流
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // 使用 QRGen 生成二维码并写入到流中
        QRCode.from(data)
              .to(ImageType.PNG)
              .withSize(300, 300)
              .writeTo(stream);

        // 返回二维码的二进制流
        return stream.toByteArray();
    }
}
