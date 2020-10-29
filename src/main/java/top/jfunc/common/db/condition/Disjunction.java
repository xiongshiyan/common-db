package top.jfunc.common.db.condition;

/**
 * @author chenzhaoju
 */
public class Disjunction extends Junction {

    protected Disjunction() {
        super( Nature.OR );
    }

    protected Disjunction(Criterion[] conditions) {
        super( Nature.OR, conditions );
    }
}
