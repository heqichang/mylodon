package com.github.heqichang.mylodon.core.loader.cache;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.heqichang.mylodon.core.loader.ILoadEntityProvider;
import com.github.heqichang.mylodon.core.loader.annotation.LoadEntity;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author heqichang
 */
public class LoadEntityCache {

    @SuppressWarnings({"rawtypes"})
    private final static Map<Class<?>, List<LoadEntityInfo>> entityMap = new ConcurrentHashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<LoadEntityInfo> get(Class<?> tClass) {

        return entityMap.computeIfAbsent(tClass, v -> {

            Field[] fields = ReflectUtil.getFields(tClass);

            List<LoadEntityInfo> infoList = new ArrayList<>();
            for (Field field : fields) {
                LoadEntity loadEntity = field.getAnnotation(LoadEntity.class);
                if (null == loadEntity) {
                    continue;
                }

                LoadEntityInfo info = new LoadEntityInfo();
                info.setLoadFieldName(field.getName());
                info.setThisFieldColumnName(loadEntity.thisColumn());
                info.setEntityFieldColumnName(loadEntity.entityColumn());

                // 如果加载类型是集合，需要取出集合中实际的类型
                Class<?> loadClass = field.getType();
                if (Collection.class.isAssignableFrom(field.getType())) {
                    Type genericType = field.getGenericType();
                    if(genericType instanceof ParameterizedType){
                        ParameterizedType pt = (ParameterizedType) genericType;
                        loadClass = (Class<?>)pt.getActualTypeArguments()[0];
                    }
                    info.setOneToMany(true);
                }

                info.setLoadClass(loadClass);

                if (!ClassUtil.isInterface(loadEntity.provider())) {
                    try {
                        ILoadEntityProvider provider = loadEntity.provider().getDeclaredConstructor().newInstance();
                        info.setProvider(provider);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                // 找到关联的 service
                Map<String, IService> beansOfType = SpringUtil.getBeansOfType(IService.class);
                for (Map.Entry<String, IService> entry : beansOfType.entrySet()) {
                    if (entry.getValue().getEntityClass().isAssignableFrom(loadClass)) {
                        info.setEntityRawClass(entry.getValue().getEntityClass());
                        info.setService(entry.getValue());
                        break;
                    }
                }

                info.setDeepLoad(loadEntity.deepLoad());

                infoList.add(info);
            }

            return infoList;
        });

    }
}
