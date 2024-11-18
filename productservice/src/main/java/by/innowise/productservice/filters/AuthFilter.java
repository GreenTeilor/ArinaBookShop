package by.innowise.productservice.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        ContextHolder.getContext().setAuthToken(request.getHeader(Context.AUTHORIZATION));
        String authHeader = request.getHeader(Context.AUTHORIZATION);
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader(Context.AUTHORIZATION, authHeader);
        filterChain.doFilter(request, response);
    }
}
