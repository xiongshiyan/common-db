package top.jfunc.common.db.condition;


/**
 * @author xiongshiyan
 */
public class LogicalExpression extends AbstractCriterion {
	private final Criterion lhs;
	private final Criterion rhs;
	private final String op;

	public LogicalExpression(Criterion lhs, Criterion rhs, String op) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.op = op;
	}

	@Override
	public String toMybatisSql() {
		return "(" + lhs.toMybatisSql() + ' ' + getOp() + ' ' + rhs.toMybatisSql() +")";
	}

	@Override
	public String toJdbcSql() {
		return "(" + lhs.toJdbcSql() + ' ' + getOp() + ' ' + rhs.toJdbcSql() +")";
	}


	public String getOp() {
		return op;
	}

	@Override
	public String toString() {
		return lhs.toString() + ' ' + getOp() + ' ' + rhs.toString();
	}
}
