package com.xlinclass.api.service.impl;

import com.xlinclass.api.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Override
    public void test() {
        int i = 10;
        i = i/0;
        System.out.print(">>>>>>>>>>>>>>>>>>>>");
    }
}
