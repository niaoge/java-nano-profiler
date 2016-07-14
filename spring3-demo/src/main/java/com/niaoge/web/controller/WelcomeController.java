package com.niaoge.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.niaoge.service.HelloWorldService;

@Controller
public class WelcomeController {

    @Resource
    HelloWorldService helloWorldService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printWelcome(ModelMap model) throws InterruptedException {
        model.addAttribute("message", "Spring 3 MVC");

        helloWorldService.sayHello();
        return "hello";
    }

}