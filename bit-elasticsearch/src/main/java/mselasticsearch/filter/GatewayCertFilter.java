//package mselasticsearch.filter;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.security.cert.X509Certificate;
//
//@Component
//public class GatewayCertFilter implements Filter {
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        if (request.isSecure()) {
//            X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
//
//            if (certs == null || certs.length == 0) {
//                ((HttpServletResponse) response).sendError(403, "Forbidden: No client certificate");
//                return;
//            }
//
//            X509Certificate cert = certs[0];
//            String dn = cert.getSubjectX500Principal().getName();
//
//            if (!dn.contains("OU=Gateway")) {
//                ((HttpServletResponse) response).sendError(403, "Forbidden: not from gateway");
//                return;
//            }
//        }
//
//        chain.doFilter(request, response);
//    }
//}
