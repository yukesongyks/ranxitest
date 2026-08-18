package com.example.myapp.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问候语 REST 控制器
 */
@RestController
@RequestMapping("/api/hello")
public class HelloWorldController {

    private static final Logger log = LoggerFactory.getLogger(HelloWorldController.class);

    private final HelloWorldService helloWorldService;

    public HelloWorldController(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    /**
     * 获取问候语
     *
     * @param name 可选参数，被问候人名称
     * @return 问候消息
     */
    @GetMapping
    public HelloWorldVO getHello(@RequestParam(required = false) String name) {
        String message = (name != null)
                ? helloWorldService.getGreeting(name)
                : helloWorldService.getGreeting();
        log.info("HelloWorld API called with name='{}', response='{}'", name, message);
        return new HelloWorldVO(message);
    }
}