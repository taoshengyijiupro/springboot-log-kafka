package com.xlinclass.elk.kafka;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class KfkaSender<T> {

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    public void send(T obj){
        String jsonObj = JSON.toJSONString(obj);
        log.info("----------- message = {}",jsonObj);
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send("myLog", jsonObj);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.info("Produce:The message failed to be sent:" + throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                log.info("Produce:The message was sent successfully:");
                log.info("Produce result: {}",stringObjectSendResult.toString());
            }
        });
    }
}
