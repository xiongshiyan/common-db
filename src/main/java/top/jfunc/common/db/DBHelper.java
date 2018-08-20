package top.jfunc.common.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * 用于获取数据库连接的帮助类，主要用于导出大数据量
 * @author 熊诗言
 */
public class DBHelper {
    private static final Logger logger = LoggerFactory.getLogger(DBHelper.class);
    private Connection conn = null;

    public DBHelper(String url , String username ,String password , String driverClass){
        try {
            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public DBHelper(String url , String username ,String password){
        this(url,username,password,"com.mysql.jdbc.Driver");
    }
    /**
     * @param sql:查询SQL语句
     * @throws SQLException SQL异常
     * 获取JDBC PrepareStatement(采用的是流数据接收方式，每次只从服务器接收部份数据，直到所有数据处理完毕，不会发生JVM OOM)
     */
    public PreparedStatement getPrepareStatement(String sql) throws SQLException {
        PreparedStatement pst;
        pst = this.conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        pst.setFetchSize(Integer.MIN_VALUE);
        pst.setFetchDirection(ResultSet.FETCH_REVERSE);
        return pst;
    }
    public void close() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            logger.error(e.getMessage() , e);
        }
    }
}