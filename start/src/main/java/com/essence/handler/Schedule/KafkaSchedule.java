package com.essence.handler.Schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.essence.business.xqh.api.Third.ThirdWaringService;
import com.essence.business.xqh.common.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 学习中心定时器
 *
 * @author wxb
 * @create 2018-09-21 8:21
 **/
@Component
@Configurable
@EnableScheduling
public class KafkaSchedule {


    @Autowired
    ThirdWaringService thirdWaringService;
    @PostConstruct//容器加入对象时执行
    public void handlePre() {
        //AccessToken accessToken = AccessToken.getToken();
        /*if(accessToken == null){
            WeixinUtils.
        }*/
        System.out.println("获取accessToken");
    }

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Scheduled(cron = "0 0/1 * * * ?") //每1分钟,推送
    public void sendWarnInfo() {

        try {

            List<Map<String,Object>> rainWarning = (List<Map<String, Object>>) thirdWaringService.getRainWarning();
            List<Map<String,Object>> waterWarning = (List<Map<String, Object>>) thirdWaringService.getWaterWarning();
            if (CollectionUtils.isEmpty(rainWarning)&&CollectionUtils.isEmpty(waterWarning)){
                return;
            }
            Date time = DateUtil.getCurrentTime();

            for (Map rainMap:rainWarning){
                rainMap.put("id",null);
                rainMap.put("alarm_type",2);
                rainMap.put("threhold",null);
                rainMap.put("alarm_time", DateUtil.dateToStringNormal3(time));
                rainMap.put("monitor_data", null);
            }
            for (Map waterMap:waterWarning){
                waterMap.put("id",null);
                waterMap.put("alarm_type",3);
                waterMap.put("threhold",null);
                waterMap.put("alarm_time", DateUtil.dateToStringNormal3(time));
                waterMap.put("monitor_data", null);
                //waterMap.put("alarm_content","超警戒水位");
            }
            waterWarning.addAll(rainWarning);

            String message = JSON.toJSONString(waterWarning, SerializerFeature.WriteMapNullValue);

            kafkaTemplate.send("alarmTopic", message).addCallback(success -> {
                // 消息发送到的topic
                String topic = ((SendResult)success).getRecordMetadata().topic();
                // 消息发送到的分区
                int partition = ((SendResult)success).getRecordMetadata().partition();
                // 消息在分区内的offset
                long offset = ((SendResult)success).getRecordMetadata().offset();
                System.out.println("发送消息成功:" + topic + "-" + partition + "-" + offset);
            }, failure -> {
                System.out.println("发送消息失败:" + failure.getMessage());
            });


        } catch (Exception e) {
            System.out.println(" 定时任务处理失败 >>>> sendWarnInfo >>>> " + e.getMessage());
        }

    }



    }






