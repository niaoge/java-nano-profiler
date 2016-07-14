package com.niaoge.service;

import org.springframework.stereotype.Component;

@Component
public class HellowWorldDao {

    public void sayHello() throws InterruptedException {
        Thread.sleep(100);
    }

}
