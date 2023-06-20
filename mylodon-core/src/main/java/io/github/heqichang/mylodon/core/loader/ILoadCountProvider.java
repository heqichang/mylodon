package io.github.heqichang.mylodon.core.loader;

import io.github.heqichang.mylodon.core.loader.parameter.ParameterGroup;
import java.util.List;
import java.util.Map;

/**
 * @author heqichang
 */
public interface ILoadCountProvider<T> {

    Map<String, Long> load(List<T> data, ParameterGroup parameters);

}
