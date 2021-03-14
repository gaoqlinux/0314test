package com.data4truth.mos.exception;

import com.zjdex.framework.bean.BaseResponse;
import com.zjdex.framework.exception.CodeException;
import com.zjdex.framework.util.ResponseUtil;
import com.zjdex.framework.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 * 路径： GlobalExceptionHandler
 * 创建人： LCKJ
 * 项目： file
 * 时间： 2019/8/22 14:48
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BaseResponse handleUnKnownException(Exception e) {
        log.error("Returning HTTP 500 Internal Server Error", e);
        return ResponseUtil.error(ResultCode.Codes.BUSINESS_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BaseResponse handleMethodException(HttpServletRequest request, HttpRequestMethodNotSupportedException exception) {
        log.error("Unsupport Method " + request.getRequestURI(), exception);

        return ResponseUtil.error(ResultCode.Codes.NOT_ALLOWED);
    }

    @ExceptionHandler(value = {ServletRequestBindingException.class, MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class, BindException.class, ConversionException.class, HttpMessageConversionException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BaseResponse handleMissingServletRequestParameterException(Exception exception) {
        log.error("Bad ParameterException exception : {}", exception.getMessage());
        return ResponseUtil.error(ResultCode.Codes.PARRAMS_ERROR.getCode(), "参数错误");
    }

    @ExceptionHandler(value = CodeException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BaseResponse handleCodeException(CodeException codeException) {
        log.error("code exception : {}", codeException);
        return ResponseUtil.error(codeException.getCode(), codeException.getMessage());
    }
}