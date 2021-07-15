package com.essence.handler.kafkaConfiguration;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaInitialConfiguration {
    // 创建一个名为testtopic的Topic并设置分区数为2，分区副本数为2，只有创建这个主题才会按照这个设置走，不然其他都是走默认的server.properties
    //
    @Bean
    public NewTopic initialTopic() {
        return new NewTopic("topic2",2, (short) 2 );
    }
    // 如果要修改分区数，只需修改配置值重启项目即可
    // 修改分区数并不会导致数据的丢失，但是分区数只能增大不能减小，改完重启下就行
    @Bean
    public NewTopic updateTopic() {
        return new NewTopic("topic2",2, (short) 2 );
    }
    @Bean
    public NewTopic initialTopic1() {
        return new NewTopic("topic1",2, (short) 2 );
    }
    // 如果要修改分区数，只需修改配置值重启项目即可
    // 修改分区数并不会导致数据的丢失，但是分区数只能增大不能减小，改完重启下就行
    @Bean
    public NewTopic updateTopic1() {
        return new NewTopic("topic1",2, (short) 2 );
    }
}
