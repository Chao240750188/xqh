package com.essence.business.xqh.api.hsfxtk;

import com.essence.business.xqh.api.hsfxtk.dto.*;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 方案结果列表相关业务层
 */
public interface PlanInfoManageService {

    /**
     * 获取方案列表信息
     * @return
     */
    Paginator getPlanList(PaginatorParam paginatorParam);
}
