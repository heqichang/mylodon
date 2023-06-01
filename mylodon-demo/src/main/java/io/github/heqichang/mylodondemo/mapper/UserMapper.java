package io.github.heqichang.mylodondemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.heqichang.mylodondemo.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author heqichang
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
