package db;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.db.bean.Page;
import top.jfunc.common.db.utils.List2Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiongshiyan at 2018/5/29
 */
public class List2PageTest {
    //测试一个list的分页
    @Test(expected = IllegalArgumentException.class)
    public void testSubList(){
        List<Integer> ss = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24));
        int pageNumber = 2;
        int pageSize = 10;
        Page<Integer> page = top.jfunc.common.db.utils.List2Page.toPage(ss , pageNumber , pageSize);
        Assert.assertEquals(page.getList() , Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20));
        Assert.assertEquals(page.getTotalRow() , 24);
        Assert.assertEquals(page.getTotalPage() , 3);
        Assert.assertEquals(page.getPageNumber() , 2);
        Assert.assertEquals(page.getPageSize() , 10);
        System.out.println("1===========================");

        pageNumber = 5;
        pageSize = 10;
        Page<Integer> page2 = top.jfunc.common.db.utils.List2Page.toPage(ss , pageNumber , pageSize);
        Assert.assertEquals(page2.getList() , new ArrayList<>(0));
        Assert.assertEquals(page2.getTotalRow() , 24);
        Assert.assertEquals(page2.getTotalPage() , 3);
        Assert.assertEquals(page2.getPageNumber() , 5);
        Assert.assertEquals(page2.getPageSize() , 10);
        System.out.println("2===========================");

        pageNumber = 0;
        List2Page.toPage(ss , pageNumber , pageSize);
    }
}
