package lifeplanner.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        // Check if the endpoint exists
        if (isEndpointExists(request)) {
            response.sendRedirect("/access-denied");
        } else {
            response.sendRedirect("/not-found");
        }
    }

    private boolean isEndpointExists(HttpServletRequest request) {
        try {
            return request.getRequestDispatcher(request.getRequestURI()) != null;
        } catch (Exception e) {
            return false;
        }
    }
}