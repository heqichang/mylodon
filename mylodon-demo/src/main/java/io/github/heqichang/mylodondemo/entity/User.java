package io.github.heqichang.mylodondemo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.github.heqichang.mylodon.core.loader.annotation.LoadEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author heqichang
 */
@Getter
@Setter
public class User {

    private Long id;

    private String name;

    @LoadEntity(thisColumn = "id", entityColumn = "user_id", deepLoad = true)
    @TableField(exist = false)
    private Resume resume;

}
