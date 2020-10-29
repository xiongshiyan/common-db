package top.jfunc.common.db.condition;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author chenzhaoju
 * @author yangjian
 * @author xiongshiyan
 */
public interface Criterion extends Serializable {

    /**
     * 获取sql 片段
     * 对应的参数使用{@link Criterion#getParameterMap()}
     * @return 返回 SQL 字符串片段
     *
     */
    String toMybatisSql();

    /**
     * 获取 参数列表
     * @return 返回参数列表
     */
    Map<String,Object> getParameterMap();


    /**
     * 获取sql 片段，参数处使用?代替或者具名参数
     * ?对应的参数使用{@link Criterion#getParameters()}，
     * :具名参数使用{@link Criterion#getParameterMap()}
     * @return 返回 SQL 字符串片段
     *
     */
    String toJdbcSql();
    /**
     * 获取输入的条件，必须保证顺序
     */
    List<Object> getParameters();

}
