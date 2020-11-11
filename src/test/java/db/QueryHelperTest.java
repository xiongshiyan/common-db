package db;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.db.QueryHelper;
import top.jfunc.common.db.condition.Conditions;
import top.jfunc.common.db.condition.MappedExpression;
import top.jfunc.common.db.condition.Op;
import top.jfunc.common.db.condition.Restrictions;
import top.jfunc.common.db.query.*;

/**
 * @author xiongshiyan at 2018/5/10
 * QueryHelper的简单测试，也是介绍其用法
 */
public class QueryHelperTest {
    /**
     * addCondition、addGroupProperty、addOrderProperty、addLimit
     */
    @Test
    public void testNormal(){
        QueryHelper helper = new QueryHelper("  tsu.name AS moshaoName,tsu.user_name AS moshaoUserName,COUNT(*) AS useCard ,tsu.remain_blank_card AS sellingCard,(COUNT(*)+tsu.remain_blank_card) AS cardCount , p.name AS managerName,p.user_name AS managerUserName,pp.name AS agentName,pp.user_name AS agentUserName",
                "tcm_cmcc_order_extend tcoe LEFT JOIN tcm_spreader_user tsu ON tsu.id=tcoe.user_id LEFT JOIN tcm_spreader_user p ON p.id=tsu.parent_user_id LEFT JOIN tcm_spreader_user pp ON pp.id=p.parent_user_id");
        //helper.addCondition("tcoe.tcm_state=0");
        helper.addCondition("tsu.name like ?" , "sdas and ' d And Or");
        helper.addGroupProperty("tcoe.user_id");
        helper.addOrderProperty("tcoe.user_id" ,true);
        helper.paging(1,10);
        Assert.assertEquals("SELECT   tsu.name AS moshaoName,tsu.user_name AS moshaoUserName,COUNT(*) AS useCard ,tsu.remain_blank_card AS sellingCard,(COUNT(*)+tsu.remain_blank_card) AS cardCount , p.name AS managerName,p.user_name AS managerUserName,pp.name AS agentName,pp.user_name AS agentUserName FROM tcm_cmcc_order_extend tcoe LEFT JOIN tcm_spreader_user tsu ON tsu.id=tcoe.user_id LEFT JOIN tcm_spreader_user p ON p.id=tsu.parent_user_id LEFT JOIN tcm_spreader_user pp ON pp.id=p.parent_user_id WHERE tsu.name like 'sdas dOr' GROUP BY tcoe.user_id ORDER BY tcoe.user_id ASC  LIMIT 0 , 10" , helper.getSql());


        QueryHelper helper2 = new QueryHelper("SELECT *", "activity" , "a").addCondition("a.id=1").addCondition("a.name='信息'");
        Assert.assertEquals("SELECT * FROM activity a WHERE a.id=1 AND a.name='信息'" , helper2.getSql());
    }

    /**
     * QueryHelper帮助拼装参数，能处理?
     */
    @Test
    public void testPositionParam(){
        QueryHelper helper = new QueryHelper("SELECT tsu.name AS moshaoName,tsu.user_name AS moshaoUserName,COUNT(*) AS useCard ,tsu.remain_blank_card AS sellingCard,(COUNT(*)+tsu.remain_blank_card) AS cardCount , p.name AS managerName,p.user_name AS managerUserName,pp.name AS agentName,pp.user_name AS agentUserName",
                "tcm_cmcc_order_extend tcoe LEFT JOIN tcm_spreader_user tsu ON tsu.id=tcoe.user_id LEFT JOIN tcm_spreader_user p ON p.id=tsu.parent_user_id LEFT JOIN tcm_spreader_user pp ON pp.id=p.parent_user_id");
        helper.addCondition("tcoe.tcm_state=?" , 0);
        helper.addCondition(3>2 ,"tcoe.user_id=?" , 12445);
        Assert.assertEquals("SELECT tsu.name AS moshaoName,tsu.user_name AS moshaoUserName,COUNT(*) AS useCard ,tsu.remain_blank_card AS sellingCard,(COUNT(*)+tsu.remain_blank_card) AS cardCount , p.name AS managerName,p.user_name AS managerUserName,pp.name AS agentName,pp.user_name AS agentUserName FROM tcm_cmcc_order_extend tcoe LEFT JOIN tcm_spreader_user tsu ON tsu.id=tcoe.user_id LEFT JOIN tcm_spreader_user p ON p.id=tsu.parent_user_id LEFT JOIN tcm_spreader_user pp ON pp.id=p.parent_user_id WHERE tcoe.tcm_state=0 AND tcoe.user_id=12445" , helper.getSql());
        Assert.assertEquals(0 , helper.getListParameters().get(0));
        Assert.assertEquals(12445 , helper.getListParameters().get(1));
    }

    /**
     * QueryHelper简单拼装 :参数名
     */
    @Test
    public void testNamedParamMap(){
        QueryHelper helper = new QueryHelper("SELECT *", "tcm_cmcc_order_extend" , "tcoe");
        helper.addMapCondition("tcoe.tcm_state=:state" , "state" , 0);
        helper.addMapCondition("tcoe.user_id=:userId" , "userId" , "123");

        Assert.assertEquals("SELECT * FROM tcm_cmcc_order_extend tcoe WHERE tcoe.tcm_state=:state AND tcoe.user_id=:userId",helper.getSqlWithoutPadding());
        Assert.assertEquals(2 , helper.getMapParameters().size());
        Assert.assertEquals("SELECT * FROM tcm_cmcc_order_extend tcoe WHERE tcoe.tcm_state=0 AND tcoe.user_id='123'",helper.getSql());
    }

    @Test
    public void testHaving(){
        QueryHelper helper = new QueryHelper("SELECT count(*)", "tcm_cmcc_order_extend" , "tcoe");
        helper.addGroupProperty("tcoe.id");
        helper.addMapHaving("count(*)>:ss" , "ss" , 1);
        Assert.assertEquals("SELECT count(*) FROM tcm_cmcc_order_extend tcoe GROUP BY tcoe.id HAVING count(*)>1" , helper.getSql());
    }

    @Test
    public void testLeftJoin(){
        QueryHelper helper = new QueryHelper("SELECT *", "tcm_cmcc_order_extend" , "tcoe");
        helper.leftJoin("cmcc co", "co.id=tcoe.cmcc_id");
        helper.leftJoin("member_org o").on("o.id=tcoe.user_id");
        Assert.assertEquals("SELECT * FROM tcm_cmcc_order_extend tcoe LEFT JOIN cmcc co ON co.id=tcoe.cmcc_id LEFT JOIN member_org o ON o.id=tcoe.user_id" , helper.getSql());
    }
    @Test
    public void testAddIn(){
        QueryHelper helper = new QueryHelper("SELECT *", "tcm_cmcc_order_extend" , "tcoe");
        helper.addIn("tcoe.id" , 1,2,3,4);
        helper.addIn("tcoe.phone" , "15208384257");
        Assert.assertEquals("SELECT * FROM tcm_cmcc_order_extend tcoe WHERE tcoe.id IN (1,2,3,4) AND tcoe.phone IN ('15208384257')" , helper.getSql());
    }
    @Test
    public void testIn(){
        QueryHelper helper = new QueryHelper("SELECT *", "activity" , "a");
        helper.addCondition("a.name=?" , "xx");
        helper.in("a.id" , "SELECT code FROM base_table");
        Assert.assertEquals("SELECT * FROM activity a WHERE a.name='xx' AND a.id IN (SELECT code FROM base_table)" , helper.getSql());
    }
    @Test
    public void testOr(){
        QueryHelper helper = new QueryHelper("SELECT *", "activity" , "a");
        helper.addCondition("a.name=?" , "xx");
        helper.addCondition("a.age=?" , 1);
        helper.or("a.id=?" , 2);
        Assert.assertEquals("SELECT * FROM activity a WHERE a.name='xx' AND a.age=1 OR a.id=2" , helper.getSql());
    }
    @Test
    public void testOrMap(){
        QueryHelper helper = new QueryHelper("SELECT *", "activity" , "a");
        helper.addMapCondition("a.name=:name" , "name","xx");
        helper.addMapCondition("a.age=:age" , "age",1);
        helper.orMapCondition("a.id=:id" , "id",2);
        Assert.assertEquals("SELECT * FROM activity a WHERE a.name=:name AND a.age=:age OR a.id=:id" , helper.getSqlWithoutPadding());
    }
    @Test(expected = RuntimeException.class)
    public void testOrEx(){
        QueryHelper helper = new QueryHelper("SELECT *", "activity" , "a");
        helper.or("a.id=?" , 2);
    }
    @Test
    public void testOrNew(){
        QueryHelper helper = new QueryHelper("SELECT *", "activity" , "a");
        helper.addCondition("a.name=?" , "xx");
        helper.addCondition("a.age=?" , 1);
        helper.orNew("a.id=?" , 2);
        Assert.assertEquals("SELECT * FROM activity a WHERE (a.name='xx' AND a.age=1) OR (a.id=2)" , helper.getSql());
    }
    @Test
    public void testOrNewMap(){
        QueryHelper helper = new QueryHelper("SELECT *", "activity" , "a");
        helper.addMapCondition("a.name=:name" , "name","xx");
        helper.addMapCondition("a.age=:age" , "age",1);
        helper.orNewMapCondition("a.id=:id" , "id",2);
        Assert.assertEquals("SELECT * FROM activity a WHERE (a.name=:name AND a.age=:age) OR (a.id=:id)" , helper.getSqlWithoutPadding());
    }
    @Test(expected = RuntimeException.class)
    public void testOrNewEx(){
        QueryHelper helper = new QueryHelper("SELECT *", "activity" , "a");
        helper.orNew("a.id=?" , 2);
    }
    @Test
    public void testAddParams(){
        QueryHelper helper = new QueryHelper("SELECT *", "tcm_cmcc_order_extend tcoe" , "cmcc co" , "member_org mo");
        helper.addCondition("tcoe.cmcc_id=co.id");
        helper.addCondition("tcoe.user_id=mo.id");
        Assert.assertEquals("SELECT * FROM tcm_cmcc_order_extend tcoe , cmcc co , member_org mo WHERE tcoe.cmcc_id=co.id AND tcoe.user_id=mo.id" , helper.getSql());
    }
    @Test
    public void testAll(){
        QueryHelper helper = new QueryHelper("SELECT tcoe.user_id", "tcm_cmcc_order_extend tcoe" , "cmcc co" , "member_org mo");
        helper.leftJoin("organization o" , "o.id=mo.org_id");
        helper.leftJoin("organization_class oc").on("oc.id=o.WebsiteId");
        helper.addCondition("tcoe.cmcc_id=co.id");
        helper.addCondition("tcoe.user_id=mo.id");
        helper.addCondition(1>0 , "tcoe.id>?" , 12);
        helper.addGroupProperty("tcoe.user_id");
        helper.addHaving("SUM(co.order_id) > 10");
        helper.addDescOrderProperty("SUM(co.order_id)");
        helper.paging(1,10);
        Assert.assertEquals("SELECT tcoe.user_id FROM tcm_cmcc_order_extend tcoe , cmcc co , member_org mo LEFT JOIN organization o ON o.id=mo.org_id LEFT JOIN organization_class oc ON oc.id=o.WebsiteId WHERE tcoe.cmcc_id=co.id AND tcoe.user_id=mo.id AND tcoe.id>? GROUP BY tcoe.user_id HAVING SUM(co.order_id) > 10 ORDER BY SUM(co.order_id) DESC  LIMIT 0 , 10" , helper.getSqlWithoutPadding());
        Assert.assertEquals("SELECT tcoe.user_id FROM tcm_cmcc_order_extend tcoe , cmcc co , member_org mo LEFT JOIN organization o ON o.id=mo.org_id LEFT JOIN organization_class oc ON oc.id=o.WebsiteId WHERE tcoe.cmcc_id=co.id AND tcoe.user_id=mo.id AND tcoe.id>12 GROUP BY tcoe.user_id HAVING SUM(co.order_id) > 10 ORDER BY SUM(co.order_id) DESC  LIMIT 0 , 10" , helper.getSql());
    }

    @Test
    public void testMysql(){
        MysqlQueryBuilder builder = new MysqlQueryBuilder();
        builder.setSelectClause("SELECT *");
        builder.setFromClause("FROM table1 t1 LEFT JOIN table2 t2 ON t1.id=t2.id");
        builder.addCondition("t1.dep=3");
        builder.addAscOrderProperty("t1.create_time");

        builder.paging(3,25);

        System.out.println(builder.getSql());

        Assert.assertEquals("SELECT *FROM table1 t1 LEFT JOIN table2 t2 ON t1.id=t2.id WHERE t1.dep=3 ORDER BY t1.create_time ASC  LIMIT 50 , 25" , builder.getSql());

        //测试连缀
        MysqlQueryBuilder helper = new MysqlQueryBuilder("SELECT *", "activity" , "a").addCondition("a.id=1").addCondition("a.name='信息'");
        Assert.assertEquals("SELECT * FROM activity a WHERE a.id=1 AND a.name='信息'" , helper.getSql());
    }
    @Test
    public void testOracle(){
        OracleQueryBuilder builder = new OracleQueryBuilder();
        builder.setSelectClause("SELECT *");
        builder.setFromClause("FROM table1 t1 LEFT JOIN table2 t2 ON t1.id=t2.id");
        builder.addCondition("t1.dep=3");
        builder.addAscOrderProperty("t1.create_time");

        builder.paging(3,25);

        System.out.println(builder.getSql());

        Assert.assertEquals("select * from ( select row_.*, rownum rownum_ from (  SELECT * FROM table1 t1 LEFT JOIN table2 t2 ON t1.id=t2.id WHERE t1.dep=3 ORDER BY t1.create_time ASC  ) row_ where rownum <= 75) table_alias where table_alias.rownum_ > 50" , builder.getSql());
    }
    @Test
    public void testPostgreSql(){
        PostgreSqlQueryBuilder builder = new PostgreSqlQueryBuilder();
        builder.setSelectClause("SELECT *");
        builder.setFromClause("FROM table1 t1 LEFT JOIN table2 t2 ON t1.id=t2.id");
        builder.addCondition("t1.dep=3");
        builder.addAscOrderProperty("t1.create_time");

        builder.paging(3,25);

        System.out.println(builder.getSql());

        Assert.assertEquals("SELECT *FROM table1 t1 LEFT JOIN table2 t2 ON t1.id=t2.id WHERE t1.dep=3 ORDER BY t1.create_time ASC  LIMIT 25 offset 50" , builder.getSql());
    }
    @Test
    public void testSqlite3(){
        Sqlite3QueryBuilder builder = new Sqlite3QueryBuilder();
        builder.setSelectClause("SELECT *");
        builder.setFromClause("FROM table1 t1 LEFT JOIN table2 t2 ON t1.id=t2.id");
        builder.addCondition("t1.dep=3");
        builder.addAscOrderProperty("t1.create_time");

        builder.paging(3,25);

        System.out.println(builder.getSql());

        Assert.assertEquals("SELECT *FROM table1 t1 LEFT JOIN table2 t2 ON t1.id=t2.id WHERE t1.dep=3 ORDER BY t1.create_time ASC  LIMIT 50 , 25" , builder.getSql());
    }
    @Test
    public void testSqlserver(){
        SqlServerQueryBuilder builder = new SqlServerQueryBuilder();
        builder.setSelectClause("SELECT *");
        builder.setFromClause("FROM table1 t1 LEFT JOIN table2 t2 ON t1.id=t2.id");
        builder.addCondition("t1.dep=3");
        builder.addAscOrderProperty("t1.create_time");

        builder.paging(3,25);

        System.out.println(builder.getSql());

        Assert.assertEquals("SELECT * FROM ( SELECT row_number() over (order by tempcolumn) temprownumber, * FROM  ( SELECT TOP 75 tempcolumn=0, * FROM table1 t1 LEFT JOIN table2 t2 ON t1.id=t2.id WHERE t1.dep=3 ORDER BY t1.create_time ASC )vip)mvp where temprownumber>50" , builder.getSql());
    }
    @Test
    public void testNotIn(){
        QueryHelper helper = new QueryHelper("SELECT *", "tcm_cmcc_order_extend" , "tcoe");
        helper.addNotIn("tcoe.id" , 1,2,3,4);
        helper.addNotIn("tcoe.phone" , "15208384257");
        Assert.assertEquals("SELECT * FROM tcm_cmcc_order_extend tcoe WHERE tcoe.id NOT IN (1,2,3,4) AND tcoe.phone NOT IN ('15208384257')" , helper.getSql());
    }
    @Test
    public void testExist(){
        QueryHelper helper = new QueryHelper("SELECT *", "activity" , "a");
        helper.exists("select 'x' from tariff t where t.id=a.id");
        helper.notExists("select 'x' from tariff t where t.id=a.id");
        Assert.assertEquals("SELECT * FROM activity a WHERE EXISTS (select 'x' from tariff t where t.id=a.id) AND NOT EXISTS (select 'x' from tariff t where t.id=a.id)" , helper.getSql());
    }
    @Test
    public void testCriterion(){
        QueryHelper helper = new QueryHelper("SELECT *", "activity" , "a");
        helper.addCondition("a.xx=?",1);
        helper.addIn("a.yy",1,2,3);

        Conditions conditions = new Conditions();
        conditions.add(Restrictions.eq("a.count", 2));
        conditions.add(Restrictions.between("a.age",20,30));
        helper.addCondition(conditions);
        Assert.assertEquals("SELECT * FROM activity a WHERE a.xx=1 AND a.yy IN (1,2,3) AND (a.count = 2 AND a.age BETWEEN 20 AND 30)", helper.getSql());
        Assert.assertEquals("SELECT * FROM activity a WHERE a.xx=? AND a.yy IN (?,?,?) AND (a.count = ? AND a.age BETWEEN ? AND ?)", helper.getSqlWithoutPadding());
    }
    @Test
    public void testCriterionMapParameter(){
        QueryHelper helper = new QueryHelper("SELECT *", "activity" , "a");
        Conditions conditions = new Conditions();

        conditions.add(Restrictions.mapped("a.name", Op.EQ, "name","熊诗言"));
        conditions.add(Restrictions.mapped("a.age", Op.GE, "age",12));

        helper.addMapCondition(conditions);

        Assert.assertEquals("SELECT * FROM activity a WHERE (a.name = '熊诗言' AND a.age >= 12)", helper.getSql());
        Assert.assertEquals("SELECT * FROM activity a WHERE (a.name = :name AND a.age >= :age)", helper.getSqlWithoutPadding());

        System.out.println(helper.getMapParameters());

    }
}

