package com.niaoge.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service()
public class HelloWorldService {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldService.class);

    @Resource
    HellowWorldDao              hellowWorldDao;

    public void sayHello() throws InterruptedException {
        Thread.sleep(30);
        hellowWorldDao.sayHello();
    }

}