package top.jfunc.common.db.sqlfilter;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author 熊诗言 2017/01/07
 * Record过滤器
 */
@FunctionalInterface
public interface Filter<T> extends Predicate<T>{
    /**
     * 看一条记录是否满足条件
     * @param record 一条记录
     * @return 是否满足
     */
    @Override
    boolean test(T record);
    /**
     * 过滤记录
     * @param records 源记录，同时也是过滤后的
     * @return 满足条件的记录条数
     */
    default List<T> filter(List<T> records){
        Objects.requireNonNull(records);
        return records.stream().filter(this).collect(Collectors.toList());

        /*Iterator<Record> iterator = records.iterator();
        while (iterator.hasNext()) {
            Record record = iterator.next();
            boolean acc = test(record);
            if(!acc){
                //不满足的删除
                iterator.remove();
            }
        }*/
    }
}