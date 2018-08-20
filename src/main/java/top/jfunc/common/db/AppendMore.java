package top.jfunc.common.db;

import java.util.List;

/**
 * @author xiongshiyan
 * @param <T>
 */
public interface AppendMore<T>{
    /**
     * 分页查询数据，导出到excel
     * @param pageNumber 从哪页开始
     * @param pageSize 每页数据
     * @return List
     */
    List<T> getList(int pageNumber, int pageSize);
}