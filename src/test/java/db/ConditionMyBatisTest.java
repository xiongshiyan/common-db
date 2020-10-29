package db;

import org.junit.Test;
import top.jfunc.common.db.condition.Conditions;
import top.jfunc.common.db.condition.Disjunction;
import top.jfunc.common.db.condition.Junction;
import top.jfunc.common.db.condition.Restrictions;

/**
 * @author xiongshiyan at 2020/10/29 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ConditionMyBatisTest {
    @Test
    public void testSimpleExpression() {
        Conditions conditions = new Conditions();
        conditions.add(Restrictions.eq("count", 2));
        conditions.add(Restrictions.eq("name", "名字"));
        conditions.add(Restrictions.ge("age", 18));
        conditions.add(Restrictions.lt("age", 30));
        conditions.add(Restrictions.ge("num",8));
        conditions.add(Restrictions.le("num", 130));
        conditions.add(Restrictions.ne("address", "这个地址"));
        System.out.println(conditions.toMybatisSql());

        Conditions conditions1 = new Conditions();
        conditions1.eq("name", "名字")
                .ge("age", 18)
                .lt("age", 30)
                .ne("address", "这个地址");
        System.out.println(conditions1.toMybatisSql());
    }

    @Test
    public void testBetweenExpression() {
        Conditions conditions = new Conditions();
        conditions.add(Restrictions.eq("count", 2));
        conditions.add(Restrictions.between("age",20,30));
        System.out.println(conditions.toMybatisSql());
    }

    @Test
    public void testConjunction() {
        Conditions conditions = new Conditions();
        conditions.add(Restrictions.and(Restrictions.eq("age",18),Restrictions.eq("name","李四")));
        conditions.add(Restrictions.or(Restrictions.eq("count",18),Restrictions.eq("count",29)));
        System.out.println(conditions.toMybatisSql());
    }
    @Test
    public void testDisjunction() {
        Conditions c1 = new Conditions();
        c1.add(Restrictions.and(Restrictions.eq("age",18),Restrictions.eq("name","李四")));

        Conditions c2 = new Conditions();
        c2.add(Restrictions.or(Restrictions.eq("count",18),Restrictions.eq("count",29)));

        Junction conjunction = Restrictions.conjunction(c1, c2);
        Junction disjunction = Restrictions.disjunction(c1, c2);

        System.out.println(conjunction.toMybatisSql());
        System.out.println(disjunction.toMybatisSql());
    }

    @Test
    public void testOneConjunction() {
        Conditions conditions = new Conditions();
        conditions.add(Restrictions.or(Restrictions.eq("count", 18), Restrictions.eq("count", 29)));
        System.out.println(conditions.toMybatisSql());
    }

    @Test
    public void testSqlRestriction() {
        Conditions conditions = new Conditions();

        conditions.add(Restrictions.conjunction(Restrictions.eq("xxx",1), Restrictions.eq("yyy", 2)));
        System.out.println(conditions.toMybatisSql());
    }
}
