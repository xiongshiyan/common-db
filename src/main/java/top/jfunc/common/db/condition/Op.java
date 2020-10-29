package top.jfunc.common.db.condition;

/**
 * SQL操作符
 */
public interface Op {
    /**
     * 获取分割符
     */
    String getSeperator();

    Op EQ   = ()->"=";
    Op NE   = ()->"<>";
    Op LIKE = ()->"LIKE";
    Op GT   = ()->">";
    Op LT   = ()->"<";
    Op GE   = ()->">=";
    Op LE   = ()->"<=";
    Op BLANK= ()->"";
}
