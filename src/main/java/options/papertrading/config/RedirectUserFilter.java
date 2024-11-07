package options.papertrading.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RedirectUserFilter extends OncePerRequestFilter {
    private static final List<String> REDIRECT_URLS =
            Arrays.asList("/auth/login", "/auth/", "/", "/auth/hello", "/auth/hello?");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && shouldRedirect(requestURI)) {
            String redirectUrl = request.getHeader("Referer");
            if (redirectUrl == null || redirectUrl.contains("/auth/login")) {
                redirectUrl = "/portfolio";
            }
            response.sendRedirect(redirectUrl);
            return;
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            if (requestURI.equals("/auth/") || requestURI.equals("/auth/profile")) {
                response.sendRedirect("/auth/login");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean shouldRedirect(String requestURI) {
        return REDIRECT_URLS.contains(requestURI);
    }
}