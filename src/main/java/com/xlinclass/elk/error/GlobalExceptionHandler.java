package com.xlinclass.elk.error;


import com.alibaba.fastjson.JSONObject;
import com.xlinclass.elk.kafka.KfkaSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    private KfkaSender kfkaSender;

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public JSONObject exceptionHandler(Exception e){
        log.info("全局捕获异常，error:{}",e);
        JSONObject errorObject = new JSONObject();
        JSONObject logJson = new JSONObject();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logJson.put("request_time",df.format(new Date()));
        logJson.put("error_info",e);
        errorObject.put("request_error",logJson);
        kfkaSender.send(logJson);

        JSONObject resultObject = new JSONObject();
        resultObject.put("code",500);
        resultObject.put("msg","系统错误");
        return resultObject;
    }
}
