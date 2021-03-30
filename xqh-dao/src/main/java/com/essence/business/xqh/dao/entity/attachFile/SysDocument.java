package com.essence.business.xqh.dao.entity.attachFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 资料库记录
 */
@Entity
@Table(name = "SYS_DOCUMENT", schema = "XQH", catalog = "")
public class SysDocument implements Serializable {
    private static final long serialVersionUID = -7326394760810350134L;

    /**
     * 编号
     */
    @Id
    @Column(name = "C_ID")
    private String id;

    /**
     * 文档名称
     */
    @Column(name = "C_FILE_NAME")
    private String fileName;

    /**
     * 文档描述
     */
    @Column(name = "C_INFO")
    private String info;

    /**
     * 创建用户
     */
    @Column(name = "C_USER_NAME")
    private String userName;

    /**
     * 创建人
     */
    @Column(name = "C_USER_ID")
    private String userId;

    /**
     * 创建时间
     */
    @Column(name = "D_CREATE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人名字
     */
    @Column(name = "C_ATTACH_ID")
    private String attachId;

    @Transient
	private List<AttachFile> attachFile;

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getInfo() {
        return info;
    }


    public void setInfo(String info) {
        this.info = info;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUserId() {
        return userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }


    public Date getCreateTime() {
        return createTime;
    }


    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAttachId() {
        return attachId;
    }

    public void setAttachId(String attachId) {
        this.attachId = attachId;
    }

    public List<AttachFile> getAttachFile() {
        return attachFile;
    }

    public void setAttachFile(List<AttachFile> attachFile) {
        this.attachFile = attachFile;
    }

    @Override
    public String toString() {
        return "SysDocument [id=" + id + ", fileName=" + fileName + ", info=" + info + ", userName=" + userName
                + ", userId=" + userId + ", createTime=" + createTime + ", attachId=" + attachId + "]";
    }

}
