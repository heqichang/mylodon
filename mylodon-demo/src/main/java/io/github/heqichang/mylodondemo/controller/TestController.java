package io.github.heqichang.mylodondemo.controller;

import io.github.heqichang.mylodon.core.loader.Loader;
import io.github.heqichang.mylodondemo.entity.User;
import io.github.heqichang.mylodondemo.service.UserService;
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
        Loader.init(userList).load();
        return "test";
    }

}
