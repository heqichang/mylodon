package io.github.heqichang.mylodondemo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.heqichang.mylodondemo.entity.Application;
import io.github.heqichang.mylodondemo.mapper.ApplicationMapper;
import org.springframework.stereotype.Service;

/**
 * @author heqichang
 */
@Service
public class ApplicationService extends ServiceImpl<ApplicationMapper, Application> {
}
