package com.essence.xqh.rest;

import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.euauth.common.SysConstant;
import com.essence.euauth.entity.ValidResponse;
import com.essence.euauth.feign.SystemSubFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取子菜单
 *
 * @param
 * @Author zhichao.xing
 * @Description 获取子菜单
 * @Date 15:02 2019/9/4
 * @return
 **/
@Slf4j
@RestController
@RequestMapping("/portal/subsystem")
public class SystemSubController {
    @Autowired
    private SystemSubFeign systemSubFeign;

    /**
     * 获取子系统菜单正常
     *
     * @return
     */
    @GetMapping(value = "/myResourceTree/{projectId}")
    public SystemSecurityMessage selectMyResourceTree(@PathVariable(name = "projectId") String projectId, HttpServletRequest request) {
        String userId = String.valueOf(request.getSession().getAttribute(SysConstant.CURRENT_USER_ID));
        ValidResponse myResourceTree = systemSubFeign.getMyResourceTree(projectId,userId);
        log.info("projectId={}获取子系统菜单正常", projectId);
        return new SystemSecurityMessage("ok", "获取子系统菜单正常", myResourceTree.getResult());
    }
}
