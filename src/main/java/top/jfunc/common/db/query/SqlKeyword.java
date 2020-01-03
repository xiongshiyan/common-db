package top.jfunc.common.db.query;

/**
 * SQL关键字
 * @author xiongshiyan at 2019/12/11 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public enum SqlKeyword {
    SELECT,
    FROM,
    LEFT_JOIN{
        @Override
        public String getKeyword() {
            return "LEFT JOIN";
        }
    },RIGHT_JOIN{
        @Override
        public String getKeyword() {
            return "RIGHT JOIN";
        }
    },INNER_JOIN{
        @Override
        public String getKeyword() {
            return "INNER JOIN";
        }
    },
    ON,
    WHERE,
    AND,
    OR,
    IN,
    NOT_IN{
        @Override
        public String getKeyword() {
            return "NOT IN";
        }
    },
    EXISTS,
    NOT_EXISTS{
        @Override
        public String getKeyword() {
            return "NOT EXISTS";
        }
    },
    GROUP_BY{
        @Override
        public String getKeyword() {
            return "GROUP BY";
        }
    },
    HAVING,
    ORDER_BY{
        @Override
        public String getKeyword() {
            return "ORDER BY";
        }
    },
    ASC,
    DESC,
    LIMIT,
    UNION,
    UNION_ALL{
        @Override
        public String getKeyword() {
            return "UNION ALL";
        }
    };

    public String getKeyword(){
        return name();
    }
}
