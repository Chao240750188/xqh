package com.essence.business.xqh.web.thirdLogin;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class KafkaConsumer {

    // 消费监听,默认的分组是配置文件里的分组,isGroupID: id是否为GroupId  id: 线程的id，当GroupId没有被配置的时候，默认id为GroupId，也是线程id
    @KafkaListener(groupId = "ttttt",topics = {"topic1"})//clientIdPrefix: 消费者Id前缀
    public void onMessage1(ConsumerRecord<?, ?> record){
        // 消费的哪个topic、partition的消息,打印出消息内容
        System.out.println("简单消费："+record.topic()+"-"+record.partition()+"-"+record.value());
    }

    /**
     * @Title 指定topic、partition、offset消费
     * @Description 同时监听topic1和topic2，监听topic1的0号分区、topic2的 "0号和1号" 分区，指向1号分区的offset初始值为8,topic里面俩个分区
     * @Author xc
     * @Date 2021/7/14 13:38
     * @Param [record]
     * @return void
     **/
     /*  @KafkaListener(groupId = "felix-group",topicPartitions = {  // TODO: 2021/7/14 这个地方 topic主题2不存在就会报错，error
            @TopicPartition(topic = "topic1", partitions = { "0" }),//必须指定offset,At least one 'partition' or 'partitionOffset' required in @TopicPartition for topic 'topic2'
            @TopicPartition(topic = "topic2", partitions = {"0","1"} *//*,partitionOffsets = @PartitionOffset(partition = "1", initialOffset = "8")*//*)
    })
    public void onMessage2(ConsumerRecord<?, ?> record) {
        System.out.println("test1+topic:"+record.topic()+"|partition:"+record.partition()+"|offset:"+record.offset()+"|value:"+record.value());
    }*/
     /*@KafkaListener(id = "test1",groupId = "felix-group",topics = {"topic1","topic2"})
    public void onMessage2(ConsumerRecord<?, ?> record) {
        System.out.println("test1+topic:"+record.topic()+"|partition:"+record.partition()+"|offset:"+record.offset()+"|value:"+record.value());
    }*/

     @KafkaListener(id = "alarm1",groupId = "alarm-group",topics = {"alarmTopic"})
    public void onMessage2(ConsumerRecord<?, ?> record) {
        System.out.println("alarm1+topic:"+record.topic()+"|partition:"+record.partition()+"|offset:"+record.offset()+"|value:"+record.value());
         String message = (String) record.value();
         Map map1 = JSON.parseObject(message, Map.class);
         System.out.println("map:"+map1.toString());
         /*List<Map> result = JSON.parseArray(message, Map.class);
         for (Map map :result){
             System.out.println("map:"+map.toString());
         }*/
     }


    /**
     * 这个地方对比上个方法同样监听了topic2里面的0，1俩个分区。
     * @param record
     */
   /* @KafkaListener(groupId = "felix-group",topicPartitions = {  // TODO: 2021/7/14 这个地方 topic主题2不存在就会报错，error
            @TopicPartition(topic = "topic1", partitions = { "0" }),//todo 如果不指定分区，会按照分区规则来，如果指定了分区就不行了
                   //todo 俩个消费者都指定了同样的分区，就会消息被执行两遍 = = =这个不是我们希望的，所以不指定分区吧
            @TopicPartition(topic = "topic2", partitions = {"0","1"}*//*, partitionOffsets = @PartitionOffset(partition = "1", initialOffset = "8")*//*)
    })
    public void onMessage3(ConsumerRecord<?, ?> record) {
        System.out.println("test2+topic:"+record.topic()+"|partition:"+record.partition()+"|offset:"+record.offset()+"|value:"+record.value());
    }*/

 /*   @KafkaListener(id = "test2",groupId = "felix-group",topics = {"topic1","topic2"})//todo 如果不指定分区，会按照分区规则来，如果指定了分区就不行了
    public void onMessage3(ConsumerRecord<?, ?> record) {
        System.out.println("test2+topic:"+record.topic()+"|partition:"+record.partition()+"|offset:"+record.offset()+"|value:"+record.value());
    }*/

    //todo kafka是如何保证消息不被重复消费的

    //todo 如果kafka消费者组需要增加消费者,最多增加到和partition数量一致,超过的消费者只会占用资源,不会起作用
    //todo kafka消费者组需要增加消费者,最多增加到和partition数量一致,超过的消费者只会占用资源,不会起作用
    /**
     * todo 当一个分组下多个消费者的时候，
     * Range 范围分区(默认的)
     *
     * 假如有10个分区，3个消费者，把分区按照序号排列0，1，2，3，4，5，6，7，8，9；消费者为C1,C2,C3，那么用分区数除以消费者数来决定每个Consumer消费几个Partition，除不尽的前面几个消费者将会多消费一个
     * 最后分配结果如下
     *
     * C1：0，1，2，3
     * C2：4，5，6
     * C3：7，8，9
     *
     * 如果有11个分区将会是：
     *
     * C1：0，1，2，3
     * C2：4，5，6，7
     * C3：8，9，10
     *
     * 假如我们有两个主题T1,T2，分别有10个分区，最后的分配结果将会是这样：
     *
     * C1：T1（0，1，2，3） T2（0，1，2，3）
     * C2：T1（4，5，6） T2（4，5，6）
     * C3：T1（7，8，9） T2（7，8，9）
     *
     * 在这种情况下，C1多消费了两个分区
     *
     */

    // TODO: 2021/7/14 什么时候触发分区分配策略：
    //1.同一个Consumer Group内新增或减少Consumer
    //2.Topic分区发生变化




}
