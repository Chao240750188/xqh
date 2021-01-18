package com.essence.handler;

import com.alibaba.fastjson.JSON;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 自定义异常
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public String handleServiceException(ServiceException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(e.getMessage()));
    }

    /**
     * 参数校验
     */
    //TODO 怎么只返回注解上的value
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(e.getMessage()));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseBody
    public String handleDuplicateKeyException(DuplicateKeyException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(e.getMessage()));
    }

    /**
     * 运行时异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public String runtimeExceptionHandler(RuntimeException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.RuntimeException));
    }

    /**
     * 空指针异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public String nullPointerExceptionHandler(NullPointerException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.NullPointerException));
    }

    /**
     * 类型转换异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ClassCastException.class)
    @ResponseBody
    public String classCastExceptionHandler(ClassCastException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.ClassCastException));
    }

    /**
     * IO异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(IOException.class)
    @ResponseBody
    public String iOExceptionHandler(IOException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.IoException));
    }

    /**
     * 未知方法异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(NoSuchMethodException.class)
    @ResponseBody
    public String noSuchMethodExceptionHandler(NoSuchMethodException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.NoSuchMethodException));
    }

    /**
     * 数组越界异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseBody
    public String indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.IndexOutOfBoundsException));
    }

    /**
     * 400错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public String requestNotReadable(HttpMessageNotReadableException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.RequestNotReadable));
    }

    /**
     * 400错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler({TypeMismatchException.class})
    @ResponseBody
    public String requestTypeMismatch(TypeMismatchException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.NoSuchMethodException));
    }

    /**
     * 400错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseBody
    public String requestMissingServletRequest(MissingServletRequestParameterException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.NoSuchMethodException));
    }

    /**
     * 405错误
     *
     * @return
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    public String request405() {
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.MethodNotAllowed));
    }

    /**
     * 406错误
     *
     * @return
     */
    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    @ResponseBody
    public String request406() {
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.NotAcceptable));
    }

    /**
     * 500错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
    @ResponseBody
    public String server500(RuntimeException e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.Error));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception e) {
        printLog(e);
        return JSON.toJSONString(SystemSecurityMessage.getFailMsg(Msg.Error));
    }

    /**
     * 异常信息打印日志
     *
     * @param e
     */
    private void printLog(Exception e) {
        logger.error("error >>> ", e);
    }
}
