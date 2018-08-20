package top.jfunc.common.db.utils;

import top.jfunc.common.db.bean.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiongshiyan at 2018/5/29
 */
public class List2Page {
    /**
     * 对list分页处理，给定 {@link List list} 和 page相关参数，
     * 返回一个 {@link Page page}
     * @see List
     * @see Page
     * @param list 给定list
     * @return page
     */
    public static <T> Page<T> toPage(List<T> list , int pageNumber , int pageSize){
        if(null == list){
            throw new IllegalArgumentException("list must not be null");
        }
        if (pageNumber < 1 || pageSize < 1) {
            throw new IllegalArgumentException("pageNumber and pageSize must more than 0");
        }
        int size = list.size();
        if(0 == size){
            return new Page<>(new ArrayList<T>(0), pageNumber, pageSize, 0, 0);
        }

        int totalPage = size / pageSize;
        if (size % pageSize != 0) {
            totalPage++;
        }

        if (pageNumber > totalPage) {
            return new Page<>(new ArrayList<T>(0), pageNumber, pageSize, totalPage, size);
        }

        int first = (pageNumber-1) * pageSize;
        int last = (pageNumber) * pageSize;

        List<T> subList = list.subList(Math.min(size , first), Math.min(size , last));

        return new Page<>(subList , pageNumber , pageSize , totalPage , size);
    }
}
