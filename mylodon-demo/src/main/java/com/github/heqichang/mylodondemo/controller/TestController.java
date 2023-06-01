package com.github.heqichang.mylodondemo.controller;

import com.github.heqichang.mylodon.core.loader.Loader;
import com.github.heqichang.mylodondemo.entity.User;
import com.github.heqichang.mylodondemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author heqichang
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String test() {

        List<User> userList = userService.lambdaQuery().last("LIMIT 100").list();
        Loader.loadList(userList);
        return "test";
    }

}
