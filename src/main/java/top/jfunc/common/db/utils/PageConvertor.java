package top.jfunc.common.db.utils;

import top.jfunc.common.db.bean.Page;

import java.util.List;

/**
 * @author xiongshiyan at 2018/4/28
 * org.springframework.data.domain.Page（数据查询）转换为JFinal的Page（前端展示）
 */
public class PageConvertor {
    /**
     * 将domain的Page转换为页面的Page
     */
    public static <T> Page<T> convert(org.springframework.data.domain.Page<T> domainPage){
        int totalPages = domainPage.getTotalPages();
        int totalRows = (int)domainPage.getTotalElements();
        List<T> content = domainPage.getContent();
        int pageNumber = domainPage.getNumber() + 1;
        int pageSize = domainPage.getSize();
        return new Page<>(content , pageNumber , pageSize , totalPages , totalRows);
    }
}
