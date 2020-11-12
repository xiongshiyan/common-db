package db;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.db.QueryHelper;
import top.jfunc.common.db.condition.Conditions;
import top.jfunc.common.db.condition.Restrictions;
import top.jfunc.common.db.page.*;

/**
 * @author xiongshiyan at 2018/5/10
 * QueryHelper的简单测试，也是介绍其用法
 */
public class PageTest {
    @Test
    public void testMysql() {
        QueryHelper helper = getQueryHelper();
        String s = new MySqlPageBuilder().sqlWithPage(helper.getSelect(), helper.getSqlExceptSelect(), 1, 10);
        Assert.assertEquals("SELECT * FROM table t LIMIT 0 , 10", s);
    }

    @Test
    public void testOracle() {
        QueryHelper helper = getQueryHelper();
        String s = new OraclePageBuilder().sqlWithPage(helper.getSelect(), helper.getSqlExceptSelect(), 1, 10);
        Assert.assertEquals("select * from ( select row_.*, rownum rownum_ from (  SELECT * FROM table t ) row_ where rownum <= 10) table_alias where table_alias.rownum_ > 0", s);
    }

    @Test
    public void testPostgreSql() {
        QueryHelper helper = getQueryHelper();
        String s = new PostgrePageBuilder().sqlWithPage(helper.getSelect(), helper.getSqlExceptSelect(), 1, 10);
        Assert.assertEquals("SELECT * FROM table t LIMIT 10 OFFSET 0", s);
    }

    @Test
    public void testSqlite3() {
        QueryHelper helper = getQueryHelper();
        String s = new Sqlite3PageBuilder().sqlWithPage(helper.getSelect(), helper.getSqlExceptSelect(), 1, 10);
        Assert.assertEquals("SELECT * FROM table t LIMIT 0 , 10", s);
    }

    @Test
    public void testSqlserver() {
        QueryHelper helper = getQueryHelper();
        String s = new SqlServerPageBuilder().sqlWithPage(helper.getSelect(), helper.getSqlExceptSelect(), 1, 10);
        Assert.assertEquals("SELECT * FROM ( SELECT row_number() over (order by tempcolumn) temprownumber, * FROM  ( SELECT TOP 10 tempcolumn=0, * FROM table t)vip)mvp where temprownumber>0", s);
    }

    private QueryHelper getQueryHelper() {
        return new QueryHelper("SELECT *", "table", "t");
    }
}