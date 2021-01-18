package com.essence.business.xqh.common.returnFormat;

import java.util.Collection;
import java.util.Map;

public class SystemSecurityMessage {
    public static final String CODE_OK = "ok";
    public static final String CODE_ERROR = "error";

    private String code;
    private Object info;
    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    public SystemSecurityMessage() {

    }

    public SystemSecurityMessage(String code, Object info, Object result) {
        this.code = code;
        this.info = info;
        this.result = result;
    }

    public SystemSecurityMessage(String code, Object info) {
        this.code = code;
        this.info = info;
    }

    public static SystemSecurityMessage getSuccessMsg(Object info, Object result) {
        if (null == result || ((result instanceof Collection) && ((Collection) result).size() == 0) || ((result instanceof Map) && ((Map) result).size() == 0) || "".equals(result.toString())) {
            result = "";
        }
        return new SystemSecurityMessage(CODE_OK, info, result);
    }

    public static SystemSecurityMessage getSuccessMsg(Object info) {
        return new SystemSecurityMessage(CODE_OK, info);
    }

    public static SystemSecurityMessage getFailMsg(Object info, Object result) {
        return new SystemSecurityMessage(CODE_ERROR, info, result);
    }

    public static SystemSecurityMessage getFailMsg(Object info) {
        return new SystemSecurityMessage(CODE_ERROR, info);
    }

    public static SystemSecurityMessage BuildSystemSecurityMessage(SystemSecurityEx controllerAction) {
        try {
            return new SystemSecurityMessage(CODE_OK, "成功", controllerAction.controllerAction());
        } catch (Exception ex) {
            ex.printStackTrace();
            return new SystemSecurityMessage(CODE_ERROR, "失败", null);
        }
    }


}

