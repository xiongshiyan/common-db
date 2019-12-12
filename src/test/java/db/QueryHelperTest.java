package db;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.db.QueryHelper;
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
        helper.addLimit(1,10);
        Assert.assertEquals("SELECT   tsu.name AS moshaoName,tsu.user_name AS moshaoUserName,COUNT(*) AS useCard ,tsu.remain_blank_card AS sellingCard,(COUNT(*)+tsu.remain_blank_card) AS cardCount , p.name AS managerName,p.user_name AS managerUserName,pp.name AS agentName,pp.user_name AS agentUserName FROM tcm_cmcc_order_extend tcoe LEFT JOIN tcm_spreader_user tsu ON tsu.id=tcoe.user_id LEFT JOIN tcm_spreader_user p ON p.id=tsu.parent_user_id LEFT JOIN tcm_spreader_user pp ON pp.id=p.parent_user_id WHERE tsu.name like 'sdas dOr' GROUP BY tcoe.user_id ORDER BY tcoe.user_id ASC  LIMIT 0 , 10" , helper.getSql());
    }

    /**
     * QueryHelper帮助拼装参数，能处理?
     */
    @Test
    public void testPositionParam(){
        QueryHelper helper = new QueryHelper("SELECT tsu.name AS moshaoName,tsu.user_name AS moshaoUserName,COUNT(*) AS useCard ,tsu.remain_blank_card AS sellingCard,(COUNT(*)+tsu.remain_blank_card) AS cardCount , p.name AS managerName,p.user_name AS managerUserName,pp.name AS agentName,pp.user_name AS agentUserName",
                "tcm_cmcc_order_extend tcoe LEFT JOIN tcm_spreader_user tsu ON tsu.id=tcoe.user_id LEFT JOIN tcm_spreader_user p ON p.id=tsu.parent_user_id LEFT JOIN tcm_spreader_user pp ON pp.id=p.parent_user_id");
        helper.keyWordLower().addCondition("tcoe.tcm_state=?" , 0);
        helper.addCondition(3>2 ,"tcoe.user_id=?" , 12445);
        Assert.assertEquals("SELECT tsu.name AS moshaoName,tsu.user_name AS moshaoUserName,COUNT(*) AS useCard ,tsu.remain_blank_card AS sellingCard,(COUNT(*)+tsu.remain_blank_card) AS cardCount , p.name AS managerName,p.user_name AS managerUserName,pp.name AS agentName,pp.user_name AS agentUserName FROM tcm_cmcc_order_extend tcoe LEFT JOIN tcm_spreader_user tsu ON tsu.id=tcoe.user_id LEFT JOIN tcm_spreader_user p ON p.id=tsu.parent_user_id LEFT JOIN tcm_spreader_user pp ON pp.id=p.parent_user_id where tcoe.tcm_state=0 and tcoe.user_id=12445" , helper.getSql());
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
        Assert.assertEquals("SELECT * FROM tcm_cmcc_order_extend tcoe WHERE tcoe.id IN (1 , 2 , 3 , 4) AND tcoe.phone IN ('15208384257')" , helper.getSql());
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
        helper.keyWordLower().leftJoin("organization_class oc").on("oc.id=o.WebsiteId");
        helper.addCondition("tcoe.cmcc_id=co.id");
        helper.keyWordUpper().addCondition("tcoe.user_id=mo.id");
        helper.addCondition(1>0 , "tcoe.id>?" , 12);
        helper.keyWordLower().addGroupProperty("tcoe.user_id");
        helper.addHaving("SUM(co.order_id) > 10");
        helper.keyWordUpper().addDescOrderProperty("SUM(co.order_id)");
        helper.addLimit(1,10);
        Assert.assertEquals("SELECT tcoe.user_id FROM tcm_cmcc_order_extend tcoe , cmcc co , member_org mo LEFT JOIN organization o ON o.id=mo.org_id left join organization_class oc on oc.id=o.WebsiteId where tcoe.cmcc_id=co.id AND tcoe.user_id=mo.id AND tcoe.id>? group by tcoe.user_id having SUM(co.order_id) > 10 ORDER BY SUM(co.order_id) DESC  LIMIT 0 , 10" , helper.getSqlWithoutPadding());
        Assert.assertEquals("SELECT tcoe.user_id FROM tcm_cmcc_order_extend tcoe , cmcc co , member_org mo LEFT JOIN organization o ON o.id=mo.org_id left join organization_class oc on oc.id=o.WebsiteId where tcoe.cmcc_id=co.id AND tcoe.user_id=mo.id AND tcoe.id>12 group by tcoe.user_id having SUM(co.order_id) > 10 ORDER BY SUM(co.order_id) DESC  LIMIT 0 , 10" , helper.getSql());
    }
}

