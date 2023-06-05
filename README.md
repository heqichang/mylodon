### 简介
在 mybatis-plus 基础上，基于注解的方式，提供关联模型的加载。

适用于需要将多个 join 的查询，拆分成多个单表查询。即使是列表的加载，也是找出对应的键值使用 IN 查询一次性调用。
并且支持嵌套调用（通过 deepLoad = true 触发）

### 新手起步

```xml
<dependency>
    <groupId>io.github.heqichang</groupId>
    <artifactId>mylodon-core</artifactId>
    <version>0.0.1</version>
</dependency>
```


```java

public class Resume {

    private Long id;

    private Long userId;

}

```

```java

public class User {

    private Long id;

    private String name;

    // 这个是关联 resume 模型，运行时会抽出本模型中的 id 值，拼装好 list 后给 queryWrapper.in("user_id", list) 
    @LoadEntity(thisColumn = "id", entityColumn = "user_id")
    @TableField(exist = false)
    private Resume resume;

}
```

```java

List<User> userList = userService.lambdaQuery().last("LIMIT 100").list();
Loader.loadList(userList);

```



