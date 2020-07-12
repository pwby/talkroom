package server.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.apache.log4j.Logger;
import server.util.CommUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class BasedDao {
    private static DruidDataSource dataSource;
    public static Logger logger = Logger.getLogger(BasedDao.class);

   /*
   *  保证在类加载执行，保证只加载一次
   * */
    static {
       /*
        *  首先获取一个数据配置文件
        *  创建数据源
        **/
        Properties properties = CommUtils.loadProperties("datasource.properties");
        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            logger.error("数据库连接创建失败",e);
        }
    }

    /*
    *获取连接
    */
    protected DruidPooledConnection getConnection(){
        try {
            return (DruidPooledConnection) dataSource.getPooledConnection();
        } catch (SQLException e) {
            logger.error("获取数据库连接失败",e);
        }
        return null;
    }

    /*
    *关闭资源，使用两个重载
    */
    protected void closeResources(Connection connection, Statement statement){
        if(connection!= null){
            try {
                connection.close();
            } catch (SQLException e) {
               logger.error("关闭数据库失败",e);
            }
        }
        if(statement!= null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void closeResources(Connection connection, Statement statement, ResultSet resultSet){
        closeResources(connection,statement);
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
               logger.error(e);
            }
        }
    }
}
