package com.denemeler.webrest;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestResponseLoggingFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        //System.out.printf("Filter Logging Request %s : %s %s\n", req.getMethod(),
        //        req.getRequestURI(), Thread.currentThread().getName());
        chain.doFilter(request, response);
        //System.out.printf(
        //        "Filter Logging Response : %s %s\n",
        //        res.getContentType(), Thread.currentThread().getName());
    }

    // other methods
}