package com.xlinclass.elk.aop;


import com.alibaba.fastjson.JSONObject;
import com.xlinclass.elk.kafka.KfkaSender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Aspect
@Component
public class AopLogAspect {

    @Autowired
    private KfkaSender kfkaSender;

    //声明一个切点
    @Pointcut("execution(* com.xlinclass.api.service.impl.*.*(..))")
    private void serviceAspect(){

    }

    //请求method前打印日志
    @Before(value = "serviceAspect()")
    public void methodBefore(JoinPoint joinPoint){
        ServletRequestAttributes requestAttributes =(ServletRequestAttributes) RequestContextHolder.
                getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObject.put("request_time",df.format(new Date()));
        jsonObject.put("request_url",request.getRequestURL().toString());
        //请求方法
        jsonObject.put("request_method",request.getMethod());
        //请求类方法
        jsonObject.put("signature",joinPoint.getSignature());
        jsonObject.put("request_args", Arrays.asList(joinPoint.getArgs()));
        JSONObject requestJsonObject = new JSONObject();
        requestJsonObject.put("request",jsonObject);
        kfkaSender.send(requestJsonObject);
    }

    @AfterReturning(returning = "o",pointcut = "serviceAspect()")
    public void methodAfterReturing(Object o){
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObject.put("response_time",df.format(new Date()));
        jsonObject.put("response_content",JSONObject.toJSONString(o));
        JSONObject responseJsonObject = new JSONObject();
        responseJsonObject.put("response",jsonObject);
        kfkaSender.send(responseJsonObject);
    }
}
