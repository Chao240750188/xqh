package com.essence.business.xqh.service.floodScheduling;

import com.essence.business.xqh.api.floodScheduling.dto.SchedulingPlanDocPageListParamDto;
import com.essence.business.xqh.api.floodScheduling.dto.SchedulingPlanDocumentViewDto;
import com.essence.business.xqh.api.floodScheduling.dto.SkddSchedulingPlanDocumentDto;
import com.essence.business.xqh.api.floodScheduling.service.SchedulingPlanDocumentService;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddObjResDao;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddSchedulingPlanDocumentDao;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddObjRes;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddSchedulingPlanDocument;
import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import com.essence.framework.util.FileUtil;
import com.essence.framework.util.StrUtil;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 调度方案文档信息表服务实现
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/03/30
 */
@Transactional
@Service
public class SchedulingPlanDocumentServiceImpl implements SchedulingPlanDocumentService {

    @Autowired
    SkddSchedulingPlanDocumentDao schedulingPlanDocumentDao; //调度方案文档数据访问
    @Autowired
    SkddObjResDao objResDao; //水库数据访问

    /**
     * 分页查询调度方案文档列表
     * @param param
     * @return
     */
    @Override
    public Paginator<SkddSchedulingPlanDocumentDto> getSchedulingPlanDocListPage(SchedulingPlanDocPageListParamDto param){
        Paginator<SkddSchedulingPlanDocumentDto> paginator = new Paginator<SkddSchedulingPlanDocumentDto>(param.getCurrentPage(),param.getPageSize());
        List<SkddSchedulingPlanDocumentDto> skddSchedulingPlanDocumentDtoList = new ArrayList<>();
        //排序
        List<Criterion> orders = new ArrayList<>();
        Criterion criterion = new Criterion();
        criterion.setFieldName("createTime");
        criterion.setOperator(Criterion.DESC);
        orders.add(criterion);
        //分页参数实体实例
        PaginatorParam paginatorParam = new PaginatorParam();
        paginatorParam.setCurrentPage(param.getCurrentPage());
        paginatorParam.setPageSize(param.getPageSize());
        paginatorParam.setOrders(orders);
        //查询条件
        List<Criterion> conditions = new ArrayList<>();
        if (param.getResCode() != null && !param.getResCode().equals("")){
            Criterion criterion1 = new Criterion();
            criterion1.setFieldName("resCode");
            criterion1.setOperator(Criterion.EQ);
            criterion1.setValue(param.getResCode());
            conditions.add(criterion1);
            paginatorParam.setConditions(conditions);
        }
        //查询并返回数据
        Paginator<SkddSchedulingPlanDocument> all = schedulingPlanDocumentDao.findAll(paginatorParam);
        BeanUtils.copyProperties(all,paginator);
        List<SkddSchedulingPlanDocument> items = all.getItems();
        if (items.size()>0){
            for (int i=0;i<items.size();i++){
                SkddSchedulingPlanDocument schedulingPlanDocument = items.get(i);
                SkddSchedulingPlanDocumentDto skddSchedulingPlanDocumentDto = new SkddSchedulingPlanDocumentDto();
                BeanUtils.copyProperties(schedulingPlanDocument,skddSchedulingPlanDocumentDto);
                skddSchedulingPlanDocumentDtoList.add(skddSchedulingPlanDocumentDto);
            }
        }
        paginator.setItems(skddSchedulingPlanDocumentDtoList);

        return paginator;
    }

    /**
     * 添加一个调度方案信息
     * @param schedulingPlanDocumentDto
     * @return
     */
    @Override
    public SkddSchedulingPlanDocumentDto addSchedulingPlan(SkddSchedulingPlanDocumentDto schedulingPlanDocumentDto){
        SkddSchedulingPlanDocument schedulingPlanDocument = new SkddSchedulingPlanDocument();
        BeanUtils.copyProperties(schedulingPlanDocumentDto,schedulingPlanDocument);
        schedulingPlanDocument.setId(StrUtil.getUUID());
        schedulingPlanDocument.setCreateTime(LocalDateTime.now());
        SkddSchedulingPlanDocument dbSPD = schedulingPlanDocumentDao.save(schedulingPlanDocument);
        BeanUtils.copyProperties(dbSPD,schedulingPlanDocumentDto);
        return schedulingPlanDocumentDto;
    }

    /**
     * 根据ID查询一个调度方案文档信息
     * @param id
     * @return
     */
    @Override
    public SchedulingPlanDocumentViewDto querySchedulingPlanById(String id){
        if (id == null || id.equals("")){
            return null;
        }
        SkddSchedulingPlanDocument dbSPD = schedulingPlanDocumentDao.queryById(id);
        if (dbSPD == null){
            return null;
        }
        SchedulingPlanDocumentViewDto dto = new SchedulingPlanDocumentViewDto();
        dto.setId(dbSPD.getId());
        dto.setCreateTime(dbSPD.getCreateTime());
        dto.setDistricts(dbSPD.getDistricts());
        dto.setOrganizationUnit(dbSPD.getOrganizationUnit());
        dto.setPlanName(dbSPD.getPlanName());
        dto.setPreplanDate(dbSPD.getPreplanDate());
        dto.setResCode(dbSPD.getResCode());
        if (dbSPD.getAttachfilePath() != null && !dbSPD.getAttachfilePath().equals("")){
            String fileName = dbSPD.getAttachfilePath().substring(dbSPD.getAttachfilePath().lastIndexOf("\\") + 1);
            String fileSuff = dbSPD.getAttachfilePath().substring(dbSPD.getAttachfilePath().lastIndexOf(".") + 1);
            dto.setAttachfileName(fileName);
            dto.setAttachfileSuff(fileSuff);
        }
        SkddObjRes objRes = objResDao.queryByResCode(dbSPD.getResCode());
        if (objRes != null){
            dto.setResName(objRes.getResName());
        }
        return dto;
    }

    /**
     * 编辑一个调度方案信息
     * @param schedulingPlanDocumentDto
     * @return
     */
    @Override
    public SkddSchedulingPlanDocumentDto editSchedulingPlan(SkddSchedulingPlanDocumentDto schedulingPlanDocumentDto){
        if (schedulingPlanDocumentDto.getId() == null || schedulingPlanDocumentDto.getId().equals("")){
            return null;
        }
        SkddSchedulingPlanDocument dbSPD = schedulingPlanDocumentDao.queryById(schedulingPlanDocumentDto.getId());
        if (dbSPD == null){
            return null;
        }
        SkddSchedulingPlanDocument schedulingPlanDocument = new SkddSchedulingPlanDocument();
        BeanUtils.copyProperties(schedulingPlanDocumentDto,schedulingPlanDocument);
        schedulingPlanDocument.setAttachfilePath(dbSPD.getAttachfilePath());
        schedulingPlanDocument.setCreateTime(LocalDateTime.now());
        dbSPD = schedulingPlanDocumentDao.save(schedulingPlanDocument);
        BeanUtils.copyProperties(dbSPD,schedulingPlanDocumentDto);
        return schedulingPlanDocumentDto;
    }

    /**
     * 删除一个调度方案信息
     * @param id
     */
    @Override
    public boolean deleteSchedulingPlanById(String id){
        SkddSchedulingPlanDocument dbSPD = schedulingPlanDocumentDao.queryById(id);
        if (dbSPD == null){
            return false;
        }
        try {
            String path = dbSPD.getAttachfilePath();
            FileUtil.deleteFile(path);
            schedulingPlanDocumentDao.delete(id);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 上传调度方案文档文件
     */
    @Override
    public List<String> uploadSchedulingPlanFile(String id, MultipartFile files){
        List<String> filePaths = new ArrayList<>();
        String fileBaseSavePath = PropertiesUtil.read("/prop/test/conf.properties").getProperty("FileSavePath") + File.separator;
        fileBaseSavePath += "SchedulingPlan"+ File.separator;
        File group=new File(fileBaseSavePath + id);
        if(!group.exists()){
            group.mkdirs();
        }
        //File[] finalFiles = new File[files.length];
        //for (int i=0;i<files.length;i++) {
        //   MultipartFile file=files[i];
            String filename = FileUtil.getNewFileName(fileBaseSavePath + id, files.getOriginalFilename());
            File newFile=null;
            try {
                newFile = FileUtil.getFile(files.getInputStream(), group.getAbsolutePath() + File.separator + filename);
                schedulingPlanDocumentDao.updateAttachfilePathById(group.getAbsolutePath() + File.separator + filename, id);
                filePaths.add(filename);
            } catch (IOException e) {
                newFile=null;
            }
        //    finalFiles[i]=newFile;
        //}
        //return uploadFile(groupid,finalFiles);
        return filePaths;
    }

    /**
     * 根据ID删除一个调度方案的附件文件
     * @param id
     * @return
     */
    @Override
    public boolean deleteSchedulingPlanDocById(String id){
        SkddSchedulingPlanDocument dbSPD = schedulingPlanDocumentDao.queryById(id);
        if (dbSPD == null){
            return false;
        }
        try {
            String path = dbSPD.getAttachfilePath();
            FileUtil.deleteFile(path);
            schedulingPlanDocumentDao.updateAttachfilePathById("", id);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 预览调度方案附件pdf
     * @param request
     * @param response
     * @param id
     */
    @Override
    public void previewSchedulingPlanDoc(HttpServletRequest request, HttpServletResponse response, String id) throws IOException{
        // 查询调度方案详情
        SkddSchedulingPlanDocument schedulingPlanDocument = schedulingPlanDocumentDao.queryById(id);
        if (schedulingPlanDocument != null) {
            String wordFile = schedulingPlanDocument.getAttachfilePath();
            String fileString = wordFile.substring(0, wordFile.lastIndexOf("."));
            String pdfFile = fileString + ".pdf";
            File target = new File(pdfFile);
            if (target.exists()) {
                // 如果文件已存在直接预览
                FileUtil.openFilebBreakpoint(request, response, target, target.getName());
            } else {
                ActiveXComponent app = null;
                Dispatch document = null;
                try{
                    // 打开word
                    app = new ActiveXComponent("Word.Application");
                    // app.setProperty("Visible", false);
                    // 获得word中所有打开的文档
                    Dispatch documents = app.getProperty("Documents").toDispatch();
                    // 打开文档
                    document = Dispatch.call(documents, "Open", wordFile, false, true).toDispatch();
                    // 另存为，将文档保存为pdf，其中word保存为pdf的格式宏的值是17
                    Dispatch.call(document, "SaveAs", pdfFile, 17);
                    // 关闭文档
                    Dispatch.call(document, "Close", false);
                    File file = new File(pdfFile);
                    if (file != null && file.exists()) {
                        int length = Integer.MAX_VALUE;
                        if (file.length() < length) {
                            length = (int) file.length();
                        }
                        response.setContentLength(length);
                        String fileName = file.getName();
                        FileUtil.openFilebBreakpoint(request, response, file, fileName);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    System.out.println("========Error:文档转换失败" + e.getMessage());
                }
                finally {
                    Dispatch.call(document, "Close", false);
                    if (app != null){
                        // 关闭office
                        app.invoke("Quit", 0);
                    }
                }
            }
        }
    }

    /**
     * 根据水库ID预览调度方案附件pdf
     * @param request
     * @param response
     * @param resCode
     */
    @Override
    public void previewSchedulingPlanDocByResCode(HttpServletRequest request, HttpServletResponse response, String resCode) throws IOException{
        // 根据水库查询最新的一个调度方案详情
        SkddSchedulingPlanDocument schedulingPlanDocument = schedulingPlanDocumentDao.queryLastOneByResCode(resCode);
        previewSchedulingPlanDoc(request, response, schedulingPlanDocument.getId());
    }
}
