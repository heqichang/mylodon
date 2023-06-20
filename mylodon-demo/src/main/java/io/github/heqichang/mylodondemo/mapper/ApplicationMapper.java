package io.github.heqichang.mylodondemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.heqichang.mylodondemo.entity.Application;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author heqichang
 */
@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
}
