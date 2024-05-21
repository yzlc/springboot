package com.yzlc.common.aop;

import com.yzlc.common.model.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public Response exceptionHandler(HttpServletRequest req, Exception e){
        log.error("",e);
        return Response.internalServerError();
    }
}