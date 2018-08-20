package db;

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
        System.out.println(helper.getSql());
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
        System.out.println(helper.getSql());
        System.out.println(helper.getListParameters());

        printArray(helper.getArrayParameters());
    }

    private void printArray(Object... objects){
        for (Object object : objects) {
            System.out.println(object);
        }
    }

    /**
     * QueryHelper简单拼装 :参数名
     */
    @Test
    public void testNamedParamMap(){
        QueryHelper helper = new QueryHelper("SELECT *", "tcm_cmcc_order_extend" , "tcoe");
        helper.addMapCondition("tcoe.tcm_state=:state" , "state" , 0);
        helper.addMapCondition("tcoe.user_id=:userId" , "userId" , "123");
        System.out.println(helper);
        System.out.println(helper.getMapParameters());
    }

    @Test
    public void testHaving(){
        QueryHelper helper = new QueryHelper("SELECT count(*)", "tcm_cmcc_order_extend" , "tcoe");
        helper.addGroupProperty("tcoe.id");
        helper.addMapHaving("count(*)>:ss" , "ss" , 1);
        System.out.println(helper.getSql());
        System.out.println(helper.getMapParameters());
    }

    @Test
    public void testLeftJoin(){
        QueryHelper helper = new QueryHelper("SELECT *", "tcm_cmcc_order_extend" , "tcoe");
        helper.leftJoin("cmcc co", "co.id=tcoe.cmcc_id");
        helper.leftJoin("member_org o").on("o.id=tcoe.user_id");
        System.out.println(helper.getSql());
    }
    @Test
    public void testAddIn(){
        QueryHelper helper = new QueryHelper("SELECT *", "tcm_cmcc_order_extend" , "tcoe");
        helper.addIn("tcoe.id" , 1,2,3,4);
        helper.addIn("tcoe.phone" , "15208384257");
        System.out.println(helper.getSql());
    }
    @Test
    public void testAddParams(){
        QueryHelper helper = new QueryHelper("SELECT *", "tcm_cmcc_order_extend tcoe" , "cmcc co" , "member_org mo");
        helper.addCondition("tcoe.cmcc_id=co.id");
        helper.addCondition("tcoe.user_id=mo.id");
        System.out.println(helper.getSql());
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
        System.out.println(helper);
    }
    @Test
    public void testUnion(){
        QueryHelper helper = new QueryHelper("SELECT *", "FROM tcm_cmcc_order_extend tcoe" , "cmcc co" , "member_org mo");
        helper.addCondition("tcoe.cmcc_id=co.id");
        helper.addCondition("tcoe.user_id=mo.id");
        QueryHelper helper2 = new QueryHelper("SELECT *", "tcm_cmcc_order_extend tcoe" , "cmcc co" , "member_org mo");
        helper2.addCondition("tcoe.cmcc_id=co.id");
        helper2.addCondition("tcoe.user_id=mo.id");

        String union = helper.union(helper2);
        System.out.println(union);
        String unionAll = helper.unionAll(helper2);
        System.out.println(unionAll);

        String s = helper.union(QueryHelper.KW_UNION, "select id,name from ss", "select id,name from aa", "select id,name from dd");
        System.out.println(s);
    }
}

