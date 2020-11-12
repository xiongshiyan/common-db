package db;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.db.QueryHelper;
import top.jfunc.common.db.condition.Conditions;
import top.jfunc.common.db.condition.Restrictions;

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
        Assert.assertEquals("SELECT   tsu.name AS moshaoName,tsu.user_name AS moshaoUserName,COUNT(*) AS useCard ,tsu.remain_blank_card AS sellingCard,(COUNT(*)+tsu.remain_blank_card) AS cardCount , p.name AS managerName,p.user_name AS managerUserName,pp.name AS agentName,pp.user_name AS agentUserName FROM tcm_cmcc_order_extend tcoe LEFT JOIN tcm_spreader_user tsu ON tsu.id=tcoe.user_id LEFT JOIN tcm_spreader_user p ON p.id=tsu.parent_user_id LEFT JOIN tcm_spreader_user pp ON pp.id=p.parent_user_id WHERE tsu.name like 'sdas dOr' GROUP BY tcoe.user_id ORDER BY tcoe.user_id ASC" , helper.getSql());


        QueryHelper helper2 = new QueryHelper("SELECT *", "activity" , "a");
        helper2.addCondition("a.id=1").addCondition("a.name='信息'");
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
        Assert.assertEquals("SELECT tcoe.user_id FROM tcm_cmcc_order_extend tcoe , cmcc co , member_org mo LEFT JOIN organization o ON o.id=mo.org_id LEFT JOIN organization_class oc ON oc.id=o.WebsiteId WHERE tcoe.cmcc_id=co.id AND tcoe.user_id=mo.id AND tcoe.id>? GROUP BY tcoe.user_id HAVING SUM(co.order_id) > 10 ORDER BY SUM(co.order_id) DESC" , helper.getSqlWithoutPadding());
        Assert.assertEquals("SELECT tcoe.user_id FROM tcm_cmcc_order_extend tcoe , cmcc co , member_org mo LEFT JOIN organization o ON o.id=mo.org_id LEFT JOIN organization_class oc ON oc.id=o.WebsiteId WHERE tcoe.cmcc_id=co.id AND tcoe.user_id=mo.id AND tcoe.id>12 GROUP BY tcoe.user_id HAVING SUM(co.order_id) > 10 ORDER BY SUM(co.order_id) DESC" , helper.getSql());
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
}

