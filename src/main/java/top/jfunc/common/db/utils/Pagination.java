package top.jfunc.common.db.utils;

import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import top.jfunc.common.db.bean.Page;
import top.jfunc.common.db.bean.Record;
import top.jfunc.common.db.query.AbstractQueryBuilder;
import top.jfunc.common.utils.Map2Bean;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author xiongshiyan at 2018/5/9
 * @see top.jfunc.common.db.query.AbstractQueryBuilder
 * @see top.jfunc.common.db.query.JdbcQueryBuilder
 * @see top.jfunc.common.db.query.NamedQueryBuilder
 * @see top.jfunc.common.db.bean.Record
 * SQL语句用{@link AbstractQueryBuilder}封装。结果集用JavaBean或者{@link Record}来封装。JavaBean需要保证别名就是属性。
 * 可以使用{@link AbstractQueryBuilder}完全处理参数，也可以不处理，支持?和:的方式
 */
public class Pagination {
    private EntityManager entityManager;

    public Pagination(EntityManager entityManager){
        this.entityManager = entityManager;
    }
    public Pagination(){}

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * 返回查询的一个Record，没有则为null
     */
    public Record findFirst(String sql , Object... params){
        return findFirst(sql , Record.class , params);
    }
    public Record findFirst(String sql , Map<String , Object> searchMap){
        return findFirst(sql , Record.class , searchMap);
    }

    /**
     * 返回查询的一个实体，没有则为null
     */
    public <T> T findFirst(String sql , Class<T> clazz , Object... params){
        List<T> ts = find(sql, clazz, params);
        return (ts == null || ts.size() == 0) ? null : ts.get(0);
    }
    public <T> T findFirst(String sql , Class<T> clazz ,Map<String , Object> searchMap){
        List<T> ts = find(sql, clazz, searchMap);
        return (ts == null || ts.size() == 0) ? null : ts.get(0);
    }


    public List<Record> find(String sql , Object... params){
        return find(sql, Record.class , params);
    }
    public List<Record> find(String sql , Map<String , Object> searchMap){
        return find(sql, Record.class , searchMap);
    }
    public List<Record> find(String sql){
        return find(sql, Record.class , (Map<String , Object>)null);
    }

    /**
     * 查询列表
     * @param sql native sql语句，可以包含？
     * @param clazz 返回的类型，可以是JavaBean，可以是Record
     * @param params 参数列表
     * @param <T> 泛型
     * @return 查询列表结果
     */
    public <T> List<T> find(String sql , Class<T> clazz , Object... params){
        Session session = entityManager.unwrap(Session.class);
        org.hibernate.Query query = session.createSQLQuery(sql);

        //0-Based
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i , params[i]);
        }

        return (List<T>)getList(query, clazz);
    }
    /**
     * 查询列表
     * @param sql native sql语句，可以包含 :具名参数
     * @param clazz 返回的类型，可以是JavaBean，可以是Record
     * @param searchMap 具名参数列表
     * @param <T> 泛型
     * @return 查询列表结果
     */
    public <T> List<T> find(String sql , Class<T> clazz , Map<String , Object> searchMap){
        Session session = entityManager.unwrap(Session.class);
        org.hibernate.Query query = session.createSQLQuery(sql);

        if(null != searchMap) {
            searchMap.forEach(query::setParameter);
        }

        return (List<T>)getList(query, clazz);
    }



    /**----------------------------------------------record-positioned-parameter---------------------------------------------------*/
    public Page<Record> paginate( String nativeSQL,int pageNumber, int pageSize, Object... params){
        String nativeCountSQL = getCountSQL(nativeSQL);
        return paginate( null, nativeSQL, nativeCountSQL, Record.class,pageNumber, pageSize, params);
    }

    public Page<Record> paginate( String nativeSQL, Boolean isGroupBySql, int pageNumber, int pageSize, Object... params){
        String nativeCountSQL = getCountSQL(nativeSQL);
        return paginate( isGroupBySql, nativeSQL, nativeCountSQL, Record.class, pageNumber, pageSize,params);
    }
    public Page<Record> paginate( String nativeSQL, String nativeCountSQL, int pageNumber, int pageSize, Object... params){
        return paginate( null, nativeSQL, nativeCountSQL, Record.class, pageNumber, pageSize,params);
    }

    public Page<Record> paginate( Boolean isGroupBySql, String nativeSQL ,String nativeCountSQL ,int pageNumber, int pageSize, Object... params){
        return paginate( isGroupBySql, nativeSQL, nativeCountSQL, Record.class,pageNumber, pageSize, params);
    }

    /**----------------------------------------------record-maped-parameter---------------------------------------------------*/
    public Page<Record> paginate( String nativeSQL,int pageNumber, int pageSize, Map<String , Object> searchMap){
        String nativeCountSQL = getCountSQL(nativeSQL);
        return paginate( null, nativeSQL, nativeCountSQL, Record.class,pageNumber, pageSize, searchMap);
    }

    public Page<Record> paginate( String nativeSQL, Boolean isGroupBySql, int pageNumber, int pageSize, Map<String , Object> searchMap){
        String nativeCountSQL = getCountSQL(nativeSQL);
        return paginate( isGroupBySql, nativeSQL, nativeCountSQL, Record.class, pageNumber, pageSize,searchMap);
    }
    public Page<Record> paginate(String nativeSQL, String nativeCountSQL, int pageNumber, int pageSize, Map<String , Object> searchMap){
        return paginate( null, nativeSQL, nativeCountSQL, Record.class, pageNumber, pageSize,searchMap);
    }

    public Page<Record> paginate( Boolean isGroupBySql, String nativeSQL ,String nativeCountSQL ,int pageNumber, int pageSize, Map<String , Object> searchMap){
        return paginate( isGroupBySql, nativeSQL, nativeCountSQL, Record.class,pageNumber, pageSize, searchMap);
    }


    /**----------------------------------------------JavaBean-positioned-parameter---------------------------------------------------*/
    public <T> Page<T> paginate(Boolean isGroupBySql, String nativeSQL , Class<T> clazz, int pageNumber, int pageSize, Object... params){
        String nativeCountSQL = getCountSQL(nativeSQL);
        return paginate( isGroupBySql, nativeSQL, nativeCountSQL, clazz, pageNumber, pageSize,params);
    }
    public <T> Page<T> paginate( String nativeSQL ,String nativeCountSQL, Class<T> clazz,int pageNumber, int pageSize, Object... params){
        return paginate( null, nativeSQL, nativeCountSQL, clazz, pageNumber, pageSize, params);
    }

    public <T> Page<T> paginate(String nativeSQL , Class<T> clazz ,int pageNumber, int pageSize,  Object... params){
        String nativeCountSQL = getCountSQL(nativeSQL);
        return paginate( null, nativeSQL, nativeCountSQL ,clazz ,pageNumber, pageSize, params);
    }

    /**----------------------------------------------JavaBean-maped-parameter---------------------------------------------------*/
    public <T> Page<T> paginate( String nativeSQL , Class<T> clazz ,int pageNumber, int pageSize, Map<String , Object> searchMap){
        String nativeCountSQL = getCountSQL(nativeSQL);
        return paginate( null, nativeSQL, nativeCountSQL ,clazz ,pageNumber, pageSize, searchMap);
    }
    public <T> Page<T> paginate( Boolean isGroupBySql, String nativeSQL , Class<T> clazz,int pageNumber, int pageSize, Map<String , Object> searchMap){
        String nativeCountSQL = getCountSQL(nativeSQL);
        return paginate( isGroupBySql, nativeSQL, nativeCountSQL, clazz, pageNumber, pageSize,searchMap);
    }
    public <T> Page<T> paginate( String nativeSQL ,String nativeCountSQL, Class<T> clazz,int pageNumber, int pageSize, Map<String , Object> searchMap){
        return paginate( null, nativeSQL, nativeCountSQL, clazz, pageNumber, pageSize, searchMap);
    }

    /**
     * @param pageNumber pageNumber
     * @param pageSize pageSize
     * @param isGroupBySql 是否包含Group by语句，影响总行数
     * @param nativeSQL 原生SQL语句 {@link AbstractQueryBuilder}
     * @param nativeCountSQL 原生求总行数的SQL语句 {@link AbstractQueryBuilder}
     * @param clazz JavaBean风格的DTO或者Record，需要用别名跟JavaBean对应
     * @param <T> 返回JavaBean风格的DTO或者Record
     * @param params 按照顺序给条件
     */
    public <T> Page<T> paginate( Boolean isGroupBySql, String nativeSQL, String nativeCountSQL , Class<T> clazz , int pageNumber, int pageSize, Object... params){
        if (pageNumber < 1 || pageSize < 1) {
            throw new IllegalArgumentException("pageNumber and pageSize must more than 0");
        }
        Query countQuery = entityManager.createNativeQuery(nativeCountSQL);

        //坑死人，1-Based
        for (int i = 1; i <= params.length; i++) {
            countQuery.setParameter(i , params[i-1]);
        }

        List countQueryResultList = countQuery.getResultList();
        int size = countQueryResultList.size();
        if (isGroupBySql == null) {
            isGroupBySql = size > 1;
        }

        long totalRow;
        if (isGroupBySql) {
            totalRow = size;
        } else {
            totalRow = (size > 0) ? ((Number)countQueryResultList.get(0)).longValue() : 0;
        }
        if (totalRow == 0) {
            return new Page<>(new ArrayList<>(0), pageNumber, pageSize, 0, 0);
        }

        int totalPage = (int) (totalRow / pageSize);
        if (totalRow % pageSize != 0) {
            totalPage++;
        }

        if (pageNumber > totalPage) {
            return new Page<>(new ArrayList<>(0), pageNumber, pageSize, totalPage, (int)totalRow);
        }

        Session session = entityManager.unwrap(Session.class);
        int offset = pageSize * (pageNumber - 1);
        org.hibernate.Query query = session.createSQLQuery(nativeSQL).setFirstResult(offset).setMaxResults(pageSize);

        //坑死人，0-Based
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i , params[i]);
        }

        final List list = getList(query, clazz);


        return new Page<T>(list, pageNumber, pageSize, totalPage, (int)totalRow);
    }
    /**
     * @param pageNumber pageNumber
     * @param pageSize pageSize
     * @param isGroupBySql 是否包含Group by语句，影响总行数
     * @param nativeSQL 原生SQL语句 {@link AbstractQueryBuilder}
     * @param nativeCountSQL 原生求总行数的SQL语句 {@link AbstractQueryBuilder}
     * @param clazz JavaBean风格的DTO或者Record，需要用别名跟JavaBean对应
     * @param <T> 返回JavaBean风格的DTO或者Record
     * @param searchMap k-v条件
     */
    public <T> Page<T> paginate( Boolean isGroupBySql, String nativeSQL,String nativeCountSQL , Class<T> clazz ,int pageNumber, int pageSize, Map<String , Object> searchMap){
        if (pageNumber < 1 || pageSize < 1) {
            throw new IllegalArgumentException("pageNumber and pageSize must more than 0");
        }
        Query countQuery = entityManager.createNativeQuery(nativeCountSQL);

        if(null != searchMap) {
            searchMap.forEach(countQuery::setParameter);
        }

        List countQueryResultList = countQuery.getResultList();
        int size = countQueryResultList.size();
        if (isGroupBySql == null) {
            isGroupBySql = size > 1;
        }

        long totalRow;
        if (isGroupBySql) {
            totalRow = size;
        } else {
            totalRow = (size > 0) ? ((Number)countQueryResultList.get(0)).longValue() : 0;
        }
        if (totalRow == 0) {
            return new Page<>(new ArrayList<>(0), pageNumber, pageSize, 0, 0);
        }

        int totalPage = (int) (totalRow / pageSize);
        if (totalRow % pageSize != 0) {
            totalPage++;
        }

        if (pageNumber > totalPage) {
            return new Page<>(new ArrayList<>(0), pageNumber, pageSize, totalPage, (int)totalRow);
        }

        Session session = entityManager.unwrap(Session.class);
        int offset = pageSize * (pageNumber - 1);
        org.hibernate.Query query = session.createSQLQuery(nativeSQL).setFirstResult(offset).setMaxResults(pageSize);

        if(null != searchMap) {
            searchMap.forEach(query::setParameter);
        }

        final List list = getList(query, clazz);


        return new Page<T>(list, pageNumber, pageSize, totalPage, (int)totalRow);
    }

    private <T> List getList(org.hibernate.Query query, Class<T> clazz) {
        final List list;

        //Object[].class
        if(Object[].class == clazz){
            return query.list();
        }

        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List mapList = query.list();
        list = new ArrayList(mapList.size());
        mapList.forEach(map->{
            Map<String , Object> tmp = (Map<String , Object>) map;
            //Record.class
            if(Record.class == clazz){
                list.add(new Record(tmp));
                //Map及子类
            }else if(Map.class.isAssignableFrom(clazz)){
                list.add(tmp);
                //JavaBean风格
            }else {
                list.add(Map2Bean.convert(tmp , clazz));
            }
        });
        return list;
    }


    private String getCountSQL(String sql){
        String countSQL = "SELECT COUNT(*) AS totalRow " + sql.substring(sql.toUpperCase().indexOf("FROM"));
        return  replaceOrderBy(countSQL);
    }

    protected static class Holder {
        private static final Pattern ORDER_BY_PATTERN = Pattern.compile(
                "order\\s+by\\s+[^,\\s]+(\\s+asc|\\s+desc)?(\\s*,\\s*[^,\\s]+(\\s+asc|\\s+desc)?)*",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    }

    public String replaceOrderBy(String sql) {
        return Holder.ORDER_BY_PATTERN.matcher(sql).replaceAll("");
    }
}
