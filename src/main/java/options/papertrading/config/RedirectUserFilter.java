package options.papertrading.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RedirectUserFilter extends GenericFilterBean {

    private static final List<String> REDIRECT_URLS = Arrays.asList("/auth/login", "/auth/", "/", "/auth/hello", "/auth/hello?");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && shouldRedirect(requestURI)) {
            String redirectUrl = httpRequest.getHeader("Referer");
            if (redirectUrl == null || redirectUrl.contains("/auth/login")) {
                redirectUrl = "/portfolio";
            }
            httpResponse.sendRedirect(redirectUrl);
            return;
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            if (requestURI.equals("/auth/") || requestURI.equals("/auth/profile")) {
                httpResponse.sendRedirect("/auth/login");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean shouldRedirect(String requestURI) {
        return REDIRECT_URLS.contains(requestURI);
    }
}