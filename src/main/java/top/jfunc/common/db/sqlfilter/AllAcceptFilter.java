package top.jfunc.common.db.sqlfilter;
/**
 * @author 熊诗言 2017/01/07
 * 不过滤Record过滤器
 */
public class AllAcceptFilter<T> implements Filter<T> {
    @Override
    public boolean test(T record){
        return true;
    }
}