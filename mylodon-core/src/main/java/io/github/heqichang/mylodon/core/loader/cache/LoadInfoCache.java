package com.ruoyi.common.experiment.loader.cache;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.heqichang.mylodon.core.loader.ILoadCountProvider;
import io.github.heqichang.mylodon.core.loader.ILoadEntityProvider;
import io.github.heqichang.mylodon.core.loader.annotation.LoadCount;
import io.github.heqichang.mylodon.core.loader.annotation.LoadEntity;
import io.github.heqichang.mylodon.core.loader.cache.LoadCountInfo;
import io.github.heqichang.mylodon.core.loader.cache.LoadEntityInfo;
import io.github.heqichang.mylodon.core.loader.cache.LoadInfo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author heqichang
 */
public class LoadInfoCache {

    @SuppressWarnings({"rawtypes"})
    private final static Map<Class<?>, List<LoadInfo>> entityMap = new ConcurrentHashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<LoadInfo> get(Class<?> tClass) {

        return entityMap.computeIfAbsent(tClass, v -> {

            Field[] fields = ReflectUtil.getFields(tClass);

            List<LoadInfo> infoList = new ArrayList<>();
            for (Field field : fields) {

                LoadEntity loadEntity = field.getAnnotation(LoadEntity.class);
                if (null != loadEntity) {
                    LoadEntityInfo info = buildEntityInfo(loadEntity, field);
                    infoList.add(info);
                    continue;
                }

                LoadCount loadCount = field.getAnnotation(LoadCount.class);
                if (null != loadCount) {
                    LoadCountInfo info = buildCountInfo(loadCount, field);
                    infoList.add(info);
                }
            }

            return infoList;
        });

    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    private static LoadEntityInfo buildEntityInfo(LoadEntity loadEntity, Field field) {

        LoadEntityInfo info = new LoadEntityInfo();
        info.setLoadFieldName(field.getName());

        info.setThisFieldColumnNames(Arrays.asList(loadEntity.thisColumns()));
        info.setEntityFieldColumnNames(Arrays.asList(loadEntity.entityColumns()));

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

        return info;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static LoadCountInfo buildCountInfo(LoadCount loadCount, Field field) {

        LoadCountInfo info = new LoadCountInfo();
        info.setLoadFieldName(field.getName());

        info.setThisFieldColumnNames(Arrays.asList(loadCount.thisColumns()));
        info.setEntityFieldColumnNames(Arrays.asList(loadCount.entityColumns()));

        if (!ClassUtil.isInterface(loadCount.provider())) {
            try {
                ILoadCountProvider provider = loadCount.provider().getDeclaredConstructor().newInstance();
                info.setProvider(provider);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 找到关联的 service
        Map<String, IService> beansOfType = SpringUtil.getBeansOfType(IService.class);
        for (Map.Entry<String, IService> entry : beansOfType.entrySet()) {
            if (entry.getValue().getEntityClass().isAssignableFrom(loadCount.entity())) {
                info.setService(entry.getValue());
                break;
            }
        }

        return info;
    }
}
