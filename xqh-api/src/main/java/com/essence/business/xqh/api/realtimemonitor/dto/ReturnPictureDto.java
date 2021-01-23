package com.essence.business.xqh.api.realtimemonitor.dto;

import com.essence.business.xqh.common.util.QixiangImageDto;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName WeatherDto
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2020/8/24 18:29
 * @Version 1.0
 **/
public class ReturnPictureDto implements Serializable {
    private String code;
    private String time;
    private String type;
    private String info;
    private List<QixiangImageDto> result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<QixiangImageDto> getResult() {
        return result;
    }

    public void setResult(List<QixiangImageDto> result) {
        this.result = result;
    }
}
