package net.arville.easybill.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.arville.easybill.helper.PerRequestHelper;
import org.springframework.boot.logging.LogLevel;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestLoggerFilter extends OncePerRequestFilter {
    private final PerRequestHelper requestHelper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        var wrappedRequest = request instanceof ContentCachingRequestWrapper
                ? (ContentCachingRequestWrapper) request
                : new ContentCachingRequestWrapper(request);
        var wrappedResponse = response instanceof ContentCachingResponseWrapper
                ? (ContentCachingResponseWrapper) response
                : new ContentCachingResponseWrapper(response);

        try {
            this.beforeRequest(wrappedRequest, wrappedResponse);
            filterChain.doFilter(request, response);
        } finally {
            this.afterRequest(wrappedRequest, wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        }
    }

    protected void beforeRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        requestHelper.log(LogLevel.INFO, "== Processing {} {} ==", request.getMethod(), request.getRequestURI());
    }

    protected void afterRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        requestHelper.log(LogLevel.INFO, "Execution time: {} ms", requestHelper.getCalculatedExecutionTime());
        requestHelper.log(LogLevel.INFO, "== Finish {} {} ==", request.getMethod(), request.getRequestURI());
    }
}
