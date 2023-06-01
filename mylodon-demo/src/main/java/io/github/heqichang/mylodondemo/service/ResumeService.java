package io.github.heqichang.mylodondemo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.heqichang.mylodondemo.entity.Resume;
import io.github.heqichang.mylodondemo.mapper.ResumeMapper;
import org.springframework.stereotype.Service;

/**
 * @author heqichang
 */
@Service
public class ResumeService extends ServiceImpl<ResumeMapper, Resume> {
}
