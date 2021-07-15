package com.essence.business.xqh.web.thirdLogin;

import com.essence.business.xqh.api.Third.ThirdWaringService;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warning")
public class ThirdWaringController {


    @Autowired
    ThirdWaringService thirdWaringService;
    /**
     * 降雨预警信息接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/rainWarning")
    public SystemSecurityMessage getRainWarning() {
        try {
            return new SystemSecurityMessage("ok", "查询降雨预警信息接口成功", thirdWaringService.getRainWarning());
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "查询降雨预警信息接口失败");
        }
    }

    /**
     *河道洪水告警信息接口
     * @return
     */
    @GetMapping(value = "/waterWarning")
    public SystemSecurityMessage getWaterWarning() {
        try {
            return new SystemSecurityMessage("ok", "查询河道洪水告警信息接口成功", thirdWaringService.getWaterWarning());
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "查询河道洪水告警信息接口失败");
        }
    }
    @Autowired
    KafkaTemplate kafkaTemplate;
   /* @Autowired
    KafkaTemplate<String,String> kafkaTemplate;*/

    // 发送消息,简单的发送信息，默认的主题创造分区个数以及副本个数实在server.properties里面配置
    // 或者是通过代码来配置分区个数以及副本的个数。
    @GetMapping("/kafka/normal/{message}")
    public void sendMessage1(@PathVariable("message") String normalMessage) {
        /**
         * 分区没有代码会自动创建
         */
        ListenableFuture topic1 = kafkaTemplate.send("topic1", normalMessage);
    }

    /**
     * 带回调的生产者，kafkaTemplate提供了一个回调方法addCallback，
     * 我们可以在回调方法中监控消息是否发送成功 或 失败时做补偿处理，有两种写法，
     * @param callbackMessage
     */
    @GetMapping("/kafka/callbackOne/{message}")
    public void sendMessage2(@PathVariable("message") String callbackMessage) {
        //ListenableFuture<Send> future = kafkaTemplate.send("topic1", callbackMessage);
        /**
         * public void sendMessage3(@PathVariable("message") String callbackMessage) {
         *     kafkaTemplate.send("topic1", callbackMessage).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
         *         @Override
         *         public void onFailure(Throwable ex) {
         *             System.out.println("发送消息失败："+ex.getMessage());
         *         }
         *
         *         @Override
         *         public void onSuccess(SendResult<String, Object> result) {
         *             System.out.println("发送消息成功：" + result.getRecordMetadata().topic() + "-"
         *                     + result.getRecordMetadata().partition() + "-" + result.getRecordMetadata().offset());
         *         }
         *     });
         * }
         */
        //这个addCallback方法是接口的实现类调用的，但是参数传的是一个接口，所以就需要匿名内部类
         kafkaTemplate.send("topic2", callbackMessage).addCallback(success -> {
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
    }
    // TODO: 2021/7/14 会发现如果是多个分区的情况，如果不指定生产者生产到哪个分区，这个默认使用的是轮询规则。
    //todo:消费者消费哪些分区的数据也是有规则的。当新增分区的时候，都是会重新排序的。

}
