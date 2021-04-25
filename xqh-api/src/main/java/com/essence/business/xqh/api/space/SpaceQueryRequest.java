package com.essence.business.xqh.api.space;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName SpaceQueryRequest
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2021/4/23 14:03
 * @Version 1.0
 **/
@Data
public class SpaceQueryRequest implements Serializable {
    private String searchText;
    private String searchFields = "STNM";
    private String layers = "0,5,6,7,9,8,10,1,2,3,4";

    //-------------------
    private String geometry;
    private String sr = "4326";

}
