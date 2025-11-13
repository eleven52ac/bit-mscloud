package common.dto.reuqest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
public class ClientMetaInfo {
    private String ip;
    private String os;
    private String device;
    private String region;
    private String network;
    private String internalToken;

    public static ClientMetaInfo from(HttpServletRequest req) {
        ClientMetaInfo info = new ClientMetaInfo();
        info.setIp(req.getHeader("X-Client-IP"));
        info.setOs(req.getHeader("X-Client-OS"));
        info.setDevice(req.getHeader("X-Client-Device"));
        String regionEncoded = req.getHeader("X-Client-Region");
        String region = new String(Base64.getDecoder().decode(regionEncoded), StandardCharsets.UTF_8);
        info.setRegion(region);
        info.setNetwork(req.getHeader("X-Client-Network"));
        info.setInternalToken(req.getHeader("X-Internal-Token"));
        return info;
    }
}
