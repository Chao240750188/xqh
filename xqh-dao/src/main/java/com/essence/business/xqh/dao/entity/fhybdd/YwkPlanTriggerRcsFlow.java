package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "YWK_PLAN_TRIGGER_RCS_FLOW", schema = "XQH", catalog = "")
public class YwkPlanTriggerRcsFlow {

        @Id
        @Column(name = "ID")
        private String id;
        @Column(name = "TRIGGER_RCS_ID")
        private String triggerRcsId;
        @Column(name = "ABSOLUTE_TIME")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date absoluteTime;
        @Column(name = "RELATIVE_TIME")
        private Long relativeTime;
        @Column(name = "FLOW")
        private Double flow;
        @Column(name = "CREATE_TIME")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTriggerRcsId() {
            return triggerRcsId;
        }

        public void setTriggerRcsId(String triggerRcsId) {
            this.triggerRcsId = triggerRcsId;
        }

        public Date getAbsoluteTime() {
            return absoluteTime;
        }

        public void setAbsoluteTime(Date absoluteTime) {
            this.absoluteTime = absoluteTime;
        }

        public Long getRelativeTime() {
            return relativeTime;
        }

        public void setRelativeTime(Long relativeTime) {
            this.relativeTime = relativeTime;
        }

        public Double getFlow() {
            return flow;
        }

        public void setFlow(Double flow) {
            this.flow = flow;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }
}
