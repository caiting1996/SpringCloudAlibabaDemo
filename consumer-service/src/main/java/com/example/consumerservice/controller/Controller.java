package com.example.consumerservice.controller;

import com.example.service.ConsumerService;
import com.example.service.test;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("api")
public class Controller {

    @Reference
    private test test1;
    @Autowired
    private ConsumerService consumerService;
    @GetMapping("test")
    public String test() throws Exception {

        return test1.test1();
    }
    @GetMapping("test1")
    public String test1() throws Exception {

        return test1.degrade();
    }
    @GetMapping("test2")
    public String test2() throws Exception {
       consumerService.manage();
        
        return "success";
    }

    @GetMapping("test3")
    public String test3() throws Exception {
        String s=consumerService.kill();
        return s;
    }



}
