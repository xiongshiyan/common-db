package top.jfunc.common.db.page;

/**
 * 不同数据库处理分页
 * @author xiongshiyan at 2020/9/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface PageBuilder {
    /**
     * 处理分页
     * @param selectClause select子句
     * @param sqlExceptSelect from后子句
     * @param pageNumber 页码
     * @param pageSize 每页数
     * @return 合并的
     */
    String sqlWithPage(String selectClause, String sqlExceptSelect, int pageNumber, int pageSize);
}
