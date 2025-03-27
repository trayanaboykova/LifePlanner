package lifeplanner.validation;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.access.AccessDeniedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomAccessDeniedHandlerUTest {

    @InjectMocks
    private CustomAccessDeniedHandler handler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Test
    void handle_endpointExists_redirectsToAccessDenied() throws Exception {
        String uri = "/existing-endpoint";
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getRequestDispatcher(uri)).thenReturn(dispatcher);

        handler.handle(request, response, new AccessDeniedException("Denied"));

        verify(response).sendRedirect("/access-denied");
    }

    @Test
    void handle_endpointDoesNotExist_redirectsToNotFound() throws Exception {
        String uri = "/non-existent-endpoint";
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getRequestDispatcher(uri)).thenReturn(null);

        handler.handle(request, response, new AccessDeniedException("Denied"));

        verify(response).sendRedirect("/not-found");
    }

    @Test
    void handle_whenExceptionInIsEndpointExists_redirectsToNotFound() throws Exception {
        String uri = "/error";
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getRequestDispatcher(uri)).thenThrow(new RuntimeException("Error"));

        handler.handle(request, response, new AccessDeniedException("Denied"));

        verify(response).sendRedirect("/not-found");
    }
}