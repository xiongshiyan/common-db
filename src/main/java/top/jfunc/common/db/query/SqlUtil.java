package top.jfunc.common.db.query;

import top.jfunc.common.utils.CollectionUtil;
import top.jfunc.common.utils.MapUtil;

import java.util.List;
import java.util.Map;

/**
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SqlUtil {
    public static final String BLANK           = " ";
    public static final String COMMA           = leftRightBlank(",");
    public static final String QUOTE           = "'";
    public static final String LEFT_BRAKET     = leftRightBlank("(");
    public static final String RIGHT_BRAKET    = leftRightBlank(")");
    /**
     *  替换掉SQL注入的那些字符 ['|;|--| and | or ]
     */
    public static String SQL_INJECT_CHARS = "([';]+|(--)+|(\\s+([aA][nN][dD])\\s+)+|(\\s+([oO][rR])\\s+)+)";
    /**
     * 在左边添加空格
     */
    public static String leftBlank(String word){
        return BLANK + word;
    }
    /**
     * 在右边添加空格
     */
    public static String rightBlank(String word){
        return word + BLANK;
    }
    /**
     * 在左右量边添加空格
     */
    public static String leftRightBlank(String word){
        return BLANK + word + BLANK;
    }

    /**
     * 判断一个字符串是否以某个关键词开头，不区分大小写
     * @param src 原字符串
     * @param keyWord 关键词
     * @return 是否以之开头
     */
    public static boolean startsWith(String src , String keyWord){
        String trim = src.trim();
        int len = keyWord.length();
        if(trim.length() < len){
            return false;
        }
        return trim.substring(0 , len).toUpperCase().startsWith(keyWord.toUpperCase());
    }

    /**
     * 替换SQL中的?
     * @param sql sql可能包含?
     * @param params 参数
     * @return 替换后的sql
     */
    public static String paddingParam(String sql , List<Object> params) {
        if(CollectionUtil.isEmpty(params)){
            return sql;
        }
        // 填充参数
        // 填充参数
        for(int i = 0 , size = params.size(); i < size; i++){
            // 1.巧妙利用替换一次之后，后面的?就自动往前移动一位，那么replaceFirst每次替换的就是下一个?
            // 2.去掉某些特殊符号，防注入
            String param = (params.get(i) instanceof Number) ? params.get(i) + "" :
                    "'" + params.get(i).toString().replaceAll(SQL_INJECT_CHARS, "")
                            + "'";
            sql = sql.replaceFirst("\\?", param);
        }
        return sql;
    }
    /**
     * 替换SQL中的 :name
     * @param sql sql可能包含 :name
     * @param params 参数
     * @return 替换后的sql
     */
    public static String paddingParam(String sql , Map<String , Object> params) {
        if(MapUtil.isEmpty(params)){
            return sql;
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            String param = (v instanceof Number) ? v + "" :
                    "'" + v.toString().replaceAll(SQL_INJECT_CHARS, "")
                            + "'";
            sql = sql.replace(":"+k , param);
        }

        return sql;
    }
}
