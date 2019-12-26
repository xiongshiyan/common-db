package db;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.db.query.SqlUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SqlUtilTest {
    @Test
    public void testPaddingParam1(){
        List<Object> list = new ArrayList<>(2);
        list.add("gg");
        list.add(5);
        String sql = "SELECT * FROM table t where t.t1=? and t.t2=?";
        String s = SqlUtil.paddingParam(sql, list);
        Assert.assertEquals("SELECT * FROM table t where t.t1='gg' and t.t2=5" , s);

    }
    @Test
    public void testPaddingParam2(){
        Map<String , Object> map = new HashMap<>(2);
        map.put("t1" , "gg");
        map.put("t2" , 5);
        String sql = "SELECT * FROM table t where t.t1=:t1 and t.t2=:t2";
        String s = SqlUtil.paddingParam(sql, map);
        Assert.assertEquals("SELECT * FROM table t where t.t1='gg' and t.t2=5" , s);

    }
    @Test(expected = IllegalArgumentException.class)
    public void testPaddingParam3(){
        Map<String , Object> map = new HashMap<>(2);
        map.put("t1" , "gg");
        String sql = "SELECT * FROM table t where t.t1=:t1 and t.t2=:t2";
        SqlUtil.paddingParam(sql, map);
    }
}
