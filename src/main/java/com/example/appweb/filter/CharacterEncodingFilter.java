package com.example.appweb.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CharacterEncodingFilter implements Filter {
    
    private String encoding = "UTF-8";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String encodingParam = filterConfig.getInitParameter("encoding");
        if (encodingParam != null && !encodingParam.trim().isEmpty()) {
            this.encoding = encodingParam;
        }
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Obtener el path de la petición
        String requestPath = httpRequest.getRequestURI();
        
        // No aplicar el filtro a recursos estáticos (CSS, JS, imágenes, etc.)
        if (requestPath.endsWith(".css") || requestPath.endsWith(".js") || requestPath.endsWith(".png") 
                || requestPath.endsWith(".jpg") || requestPath.endsWith(".jpeg") || requestPath.endsWith(".gif")
                || requestPath.endsWith(".ico") || requestPath.endsWith(".svg") || requestPath.endsWith(".woff")
                || requestPath.endsWith(".woff2") || requestPath.endsWith(".ttf") || requestPath.endsWith(".eot")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Configurar codificación para requests
        if (httpRequest.getCharacterEncoding() == null) {
            httpRequest.setCharacterEncoding(encoding);
        }
        
        // Configurar codificación para responses
        httpResponse.setCharacterEncoding(encoding);
        httpResponse.setContentType("text/html;charset=" + encoding);
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Cleanup si es necesario
    }
}
