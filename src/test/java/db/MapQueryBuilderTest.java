package db;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.db.QueryHelper;
import top.jfunc.common.db.condition.Conditions;
import top.jfunc.common.db.condition.Op;
import top.jfunc.common.db.condition.Restrictions;
import top.jfunc.common.db.query.NamedQueryBuilder;

/**
 * @author xiongshiyan at 2018/5/10
 * QueryHelper的简单测试，也是介绍其用法
 */
public class MapQueryBuilderTest {
    /**
     * QueryHelper简单拼装 :参数名
     */
    @Test
    public void testNamedParamMap(){
        NamedQueryBuilder helper = new NamedQueryBuilder("SELECT *", "tcm_cmcc_order_extend" , "tcoe");
        helper.addCondition("tcoe.tcm_state=:state" , "state" , 0);
        helper.addCondition("tcoe.user_id=:userId" , "userId" , "123");

        Assert.assertEquals("SELECT * FROM tcm_cmcc_order_extend tcoe WHERE tcoe.tcm_state=:state AND tcoe.user_id=:userId",helper.getSqlWithoutPadding());
        Assert.assertEquals(2 , helper.getMapParameters().size());
        Assert.assertEquals("SELECT * FROM tcm_cmcc_order_extend tcoe WHERE tcoe.tcm_state=0 AND tcoe.user_id='123'",helper.getSql());
    }

    @Test
    public void testHaving(){
        NamedQueryBuilder helper = new NamedQueryBuilder("SELECT count(*)", "tcm_cmcc_order_extend" , "tcoe");
        helper.addGroupProperty("tcoe.id");
        helper.addHaving("count(*)>:ss" , "ss" , 1);
        Assert.assertEquals("SELECT count(*) FROM tcm_cmcc_order_extend tcoe GROUP BY tcoe.id HAVING count(*)>1" , helper.getSql());
    }

    @Test
    public void testOrMap(){
        NamedQueryBuilder helper = new NamedQueryBuilder("SELECT *", "activity" , "a");
        helper.addCondition("a.name=:name" , "name","xx");
        helper.addCondition("a.age=:age" , "age",1);
        helper.or("a.id=:id" , "id",2);
        Assert.assertEquals("SELECT * FROM activity a WHERE a.name=:name AND a.age=:age OR a.id=:id" , helper.getSqlWithoutPadding());
    }
    @Test
    public void testOrNewMap(){
        NamedQueryBuilder helper = new NamedQueryBuilder("SELECT *", "activity" , "a");
        helper.addCondition("a.name=:name" , "name","xx");
        helper.addCondition("a.age=:age" , "age",1);
        helper.orNew("a.id=:id" , "id",2);
        Assert.assertEquals("SELECT * FROM activity a WHERE (a.name=:name AND a.age=:age) OR (a.id=:id)" , helper.getSqlWithoutPadding());
    }
    @Test
    public void testCriterionMapParameter(){
        NamedQueryBuilder helper = new NamedQueryBuilder("SELECT *", "activity" , "a");
        Conditions conditions = new Conditions();

        conditions.add(Restrictions.mapped("a.name", Op.EQ, "name","熊诗言"));
        conditions.add(Restrictions.mapped("a.age", Op.GE, "age",12));

        helper.addCondition(conditions);

        Assert.assertEquals("SELECT * FROM activity a WHERE (a.name = '熊诗言' AND a.age >= 12)", helper.getSql());
        Assert.assertEquals("SELECT * FROM activity a WHERE (a.name = :name AND a.age >= :age)", helper.getSqlWithoutPadding());

        System.out.println(helper.getMapParameters());

    }
}

