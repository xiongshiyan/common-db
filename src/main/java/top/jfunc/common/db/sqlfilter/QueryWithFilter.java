package top.jfunc.common.db.sqlfilter;

import top.jfunc.common.db.AppendMore;
import top.jfunc.common.db.bean.Page;
import top.jfunc.common.db.utils.List2Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiongshiyan at 2018/1/7
 * 查询表数据，复杂的过滤在Java应用层做，SQL中只写很容易过滤的条件，基于一个事实：数据库过滤复杂条件效率不高
 */
public class QueryWithFilter {

    public static final int BATCH_PULL_DATA_SIZE = 10000;
    public static final int PAGE_TOTAL_DATA_SIZE = 20000;

    /**
     * 查询数据，传入过滤器，复杂的过滤写在过滤器中，SQL只写容易过滤的条件
     * @param pageNumber 页数
     * @param pageSize 每页数量
     * @param batchPullDataSize 每次从数据库拉取数量，此值有待测试优化，每次拉取多就可以少拉取几次，每次拉取少就多拉取几次，需要找到这个平衡点
     * @param pageTotalDataSize 页面可以放的总记录数量（基于一个事实，页面记录数量太多，也没得价值），此值如果设置很大，就退化为正常的查询。此值不宜过大，否则可能造成内存溢出
     * @param appendMore 具体查询的实现
     * @param filter Java应用层过滤器，比较复杂的过滤放在Java层做，如果传进来null，则不过滤
     * @return Page<T>
     */
    public static <T> Page<T> query(int pageNumber, int pageSize, int batchPullDataSize, int pageTotalDataSize, AppendMore<T> appendMore , Filter<T> filter){
        List<T> resultList = doQueryList(batchPullDataSize, pageTotalDataSize, appendMore , filter);

        return List2Page.toPage(resultList , pageNumber , pageSize);
        /*//计算总行数、总页数
        int totalRow = resultList.size();
        int totalPage = totalRow / pageSize;
        if(totalRow % pageSize != 0){
            ++totalPage;
        }
        //计算当页开始和结束条数
        int thisPage = (pageNumber - 1) * pageSize;
        int toIndex = thisPage + pageSize;
        //一般是末页不够的情况
        if(toIndex > resultList.size()){
            toIndex = resultList.size();
        }
        //获取当前页数据
        resultList = resultList.subList(thisPage, toIndex);
        return new Page<>(resultList, pageNumber, pageSize, totalPage, totalRow);*/
    }
    public static <T> Page<T> query(int pageNumber, int pageSize, int pageTotalDataSize, AppendMore<T> appendMore , Filter<T> filter){
        List<T> resultList = doQueryList(BATCH_PULL_DATA_SIZE, pageTotalDataSize, appendMore , filter);
        return List2Page.toPage(resultList , pageNumber , pageSize);
    }
    public static <T> Page<T> query(int pageNumber, int pageSize, AppendMore<T> appendMore , Filter<T> filter){
        List<T> resultList = doQueryList(BATCH_PULL_DATA_SIZE, PAGE_TOTAL_DATA_SIZE, appendMore , filter);
        return List2Page.toPage(resultList , pageNumber , pageSize);
    }

    /**
     * 多次查询直到找到pageTotalDataSize条记录或者不够
     * @param batchPullDataSize 每次拉取数量
     * @param pageTotalDataSize 页面最多显示条数
     * @param filter 过滤器
     */
    public static <T> List<T> doQueryList(int batchPullDataSize, int pageTotalDataSize, AppendMore<T> appendMore, Filter<T> filter) {
        List<T> resultList =  new ArrayList<>();
        //实际每次拉取的数量，当只差几条数据的时候就没必要拉取很多了
        int actPullSize = batchPullDataSize;
        int i=0;
        while(true){
            int pageNumber = (i++) + 1;
            List<T> dataList = appendMore.getList(pageNumber , actPullSize);
            int dbCountNumber = dataList.size();

            if(filter != null){
                dataList = filter.filter(dataList);
            }
            resultList.addAll(dataList);

            //1.已经满足了 2.拉取的不够了
            if(resultList.size() >= pageTotalDataSize
                    || dbCountNumber < actPullSize){
                break;
            }
            //实际拉取的数量就是这之间的小者：batchPullDataSize , pageTotalDataSize-resultList.size()
            actPullSize = Math.min(batchPullDataSize , pageTotalDataSize-resultList.size());
        }

        //为了达到pageTotalDataSize，resultList的size可能大于它
        if(resultList.size() > pageTotalDataSize){
            resultList = resultList.subList(0,pageTotalDataSize);
        }
        return resultList;
    }
}
