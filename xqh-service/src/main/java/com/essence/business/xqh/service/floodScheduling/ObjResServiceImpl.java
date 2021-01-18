package com.essence.business.xqh.service.floodScheduling;


import com.essence.business.xqh.api.floodScheduling.dto.SkddObjResDto;
import com.essence.business.xqh.api.floodScheduling.service.ObjResService;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddObjResDao;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddObjRes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 水库信息表服务实现接
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/03/30
 */
@Service
public class ObjResServiceImpl implements ObjResService {

    @Autowired
    private SkddObjResDao objResDao; //水库信息数据访问

    /**
     * 根据水库ID查询一个水库的信息
     * LiuGt add at 2020-03-30
     * @param resCode
     * @return
     */
    @Override
    public SkddObjResDto queryByResCode(String resCode){
        SkddObjResDto skddObjResDto = null;
        SkddObjRes objRes = objResDao.queryByResCode(resCode);
        if (null!=objRes){
            skddObjResDto = new SkddObjResDto();
            BeanUtils.copyProperties(objRes,skddObjResDto);
        }
        return skddObjResDto;
    }

    /**
     * 查询展示的水库列表
     * @return
     */
    @Override
    public List<Map<String, Object>> queryByShow(){
        return objResDao.queryByShow();
    }
}
