package lizzy.medium.example.exams.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WebLogFilter extends OncePerRequestFilter {
    private final static String beforeMessagePrefix = "Before request [";
    private final static String beforeMessageSuffix = "]";
    private final static String afterMessagePrefix = "After request [";
    private final static String afterMessageSuffix = "]";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean isFirstRequest = !this.isAsyncDispatch(request);
        boolean shouldLog = logger.isInfoEnabled();
        if (shouldLog && isFirstRequest) {
            logBefore(request);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (shouldLog && !this.isAsyncStarted(request)) {
                logAfter(request, response);
            }
        }
    }

    private void logBefore(HttpServletRequest request) {
        StringBuilder msg = new StringBuilder();
        msg.append(beforeMessagePrefix);
        msg.append(request.getMethod()).append(" ");
        msg.append(request.getRequestURI());
        String payload;
        payload = request.getQueryString();
        if (payload != null) {
            msg.append('?').append(payload);
        }
        msg.append(", id=").append(request.hashCode());
        payload = request.getRemoteAddr();
        if (StringUtils.hasLength(payload)) {
            msg.append(", client=").append(payload);
        }
        if (request.getUserPrincipal() != null) {
            payload = request.getUserPrincipal().getName();
            if (payload != null) {
                msg.append(", user=").append(payload);
            }
        }
        HttpHeaders headers = (new ServletServerHttpRequest(request)).getHeaders();
        msg.append(", headers=").append(formatHeaders(headers));
        msg.append(beforeMessageSuffix);
        log.info(msg.toString());
    }

    private void logAfter(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder msg = new StringBuilder();
        msg.append(afterMessagePrefix);
        msg.append(request.getMethod()).append(" ");
        msg.append(request.getRequestURI());
        String payload;
        payload = request.getQueryString();
        if (payload != null) {
            msg.append('?').append(payload);
        }
        msg.append(", id=").append(request.hashCode());
        payload = Integer.toString(response.getStatus());
        if (StringUtils.hasLength(payload)) {
            msg.append(", responseCode=").append(payload);
        }
        HttpHeaders headers = (new ServletServerHttpResponse(response)).getHeaders();
        msg.append(", headers=").append(formatHeaders(headers));
        msg.append(afterMessageSuffix);
        log.info(msg.toString());
    }

    private String formatHeaders(MultiValueMap<String, String> headers) {
        return headers.entrySet().stream()
                .filter(entry -> !entry.getKey().equalsIgnoreCase(HttpHeaders.AUTHORIZATION))
                .map(entry -> entry.getKey() + ":" + entry.getValue().stream()
                        .map((s) -> "\"" + s + "\"")
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
