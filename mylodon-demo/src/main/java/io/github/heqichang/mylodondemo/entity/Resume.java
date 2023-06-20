package io.github.heqichang.mylodondemo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.github.heqichang.mylodon.core.loader.annotation.LoadCount;
import lombok.Getter;
import lombok.Setter;

/**
 * @author heqichang
 */
@Getter
@Setter
public class Resume {

    private Long id;

    private Long userId;

    @LoadCount(thisColumn = "id", entityColumn = "resume_id", entity = Application.class)
    @TableField(exist = false)
    private Long applicationCount;

}
