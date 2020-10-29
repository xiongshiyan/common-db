package db;

import org.junit.Test;
import top.jfunc.common.db.condition.*;

/**
 * @author xiongshiyan at 2020/10/29 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ConditionJdbcTest {
    @Test
    public void testSimpleExpression1() {
        SimpleExpression eq = Restrictions.eq("count", 2);
        System.out.println(eq.toJdbcSql());
        System.out.println(eq.getParameters());
    }

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
        System.out.println(conditions.toJdbcSql());
        System.out.println(conditions.getParameters());

        Conditions conditions1 = new Conditions();
        conditions1.eq("name", "名字")
                .ge("age", 18)
                .lt("age", 30)
                .ne("address", "这个地址");
        System.out.println(conditions1.toJdbcSql());
        System.out.println(conditions1.getParameters());
    }
    @Test
    public void testBetweenExpression() {
        Conditions conditions = new Conditions();
        conditions.add(Restrictions.eq("count", 2));
        conditions.add(Restrictions.between("age",20,30));
        System.out.println(conditions.toJdbcSql());
        System.out.println(conditions.getParameters());
    }

    @Test
    public void testConjunction() {
        Conditions conditions = new Conditions();
        conditions.add(Restrictions.and(Restrictions.eq("age",18),Restrictions.eq("name","李四")));
        conditions.add(Restrictions.or(Restrictions.eq("count",18),Restrictions.eq("count",29)));
        System.out.println(conditions.toJdbcSql());
        System.out.println(conditions.getParameters());
    }
    @Test
    public void testDisjunction() {
        Conditions c1 = new Conditions();
        c1.add(Restrictions.and(Restrictions.eq("age",18),Restrictions.eq("name","李四")));

        Conditions c2 = new Conditions();
        c2.add(Restrictions.or(Restrictions.eq("count",18),Restrictions.eq("count",29)));

        Junction conjunction = Restrictions.conjunction(c1, c2);
        Junction disjunction = Restrictions.disjunction(c1, c2);

        System.out.println(conjunction.toJdbcSql());
        System.out.println(disjunction.toJdbcSql());
    }
    @Test
    public void testLogic() {
        Conditions c1 = new Conditions();
        c1.add(Restrictions.and(Restrictions.eq("age",18),Restrictions.eq("name","李四")));

        Conditions c2 = new Conditions();
        c2.add(Restrictions.or(Restrictions.eq("count",18),Restrictions.eq("count",29)));

        LogicalExpression and = new LogicalExpression(c1, c2, "AND");
        LogicalExpression or = new LogicalExpression(c1, c2, "OR");

        System.out.println(and.toJdbcSql());
        System.out.println(or.toJdbcSql());
    }
    @Test
    public void testOneConjunction() {
        Conditions conditions = new Conditions();
        conditions.add(Restrictions.or(Restrictions.eq("count", 18), Restrictions.eq("count", 29)));
        System.out.println(conditions.toJdbcSql());
        System.out.println(conditions.getParameters());
    }

    @Test
    public void testSqlRestriction() {
        Conditions conditions = new Conditions();

        conditions.add(Restrictions.conjunction(Restrictions.eq("xxx",1), Restrictions.eq("yyy", 2)));
        System.out.println(conditions.toJdbcSql());
        System.out.println(conditions.getParameters());
    }
}
