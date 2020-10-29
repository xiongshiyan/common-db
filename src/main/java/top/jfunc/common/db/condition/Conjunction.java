package top.jfunc.common.db.condition;

import java.util.Collection;

/**
 * SQL 条件连接符，默认使用 AND
 * @author chenzhaoju
 * @author yangjian
 */
public class Conjunction extends Junction {

    public Conjunction() {
        super( Nature.AND );
    }

    protected Conjunction(Criterion... criterion) {
        super( Nature.AND, criterion );
    }

    public Conjunction(Collection<Criterion> criterions) {
        super(Nature.AND, criterions);
    }

}
