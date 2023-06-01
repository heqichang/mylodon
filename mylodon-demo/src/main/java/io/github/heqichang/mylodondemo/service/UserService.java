package io.github.heqichang.mylodondemo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.heqichang.mylodondemo.entity.User;
import io.github.heqichang.mylodondemo.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @author heqichang
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
}
