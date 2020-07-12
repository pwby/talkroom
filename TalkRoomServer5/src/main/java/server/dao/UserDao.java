package server.dao;
import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;
import org.apache.log4j.Logger;
import server.ServerStart;
import server.entry.User;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;




public class UserDao extends BasedDao {

    /*
    *用户注册 insert
    */
    public void userReg(User user){
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = getConnection();
            String sql = "insert into user(user_name,password,brief,qq_image) values (?,?,?,?)";
            statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            System.out.println("数据库操作:用户注册:");
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getPassword()); //加密
            statement.setString(3, user.getBrief());
            statement.setString(4,user.getQqImage());
            statement.execute();
        }catch(SQLException e){
            logger.error("用户注册失败",e);
        }finally {
            closeResources(connection,statement);
        }

    }

    /*
     * @Date 18:23 2020-04-01  18:23:16
     * @Description 获取数据库内所有成员
     * @Param
     * @return
     **/
    public Set<User> getAllUser(){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try{
            Set<User> allUser = new HashSet<>();
            connection = getConnection();
            String sql = "select * from user ";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
           while(resultSet.next()){
                User user = new User();
                String userName = resultSet.getString("user_name");
                String password  = resultSet.getString("password");
                String brief = resultSet.getString("brief");
                String qqImage = resultSet.getString("qq_image");

                user.setUserName(userName);
                user.setPassword(password);
                user.setBrief(brief);
                user.setQqImage(qqImage);
                allUser.add(user);
            }
            return allUser;
        }catch (SQLException e){
           logger.error("获取所有用户失败",e);
        }finally {
            closeResources(connection,statement,resultSet);
        }
       return null;
    }



    public void modifyPassword(User user){
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = getConnection();
            String sql = "update user set password = ? where user_name = ?";
            statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,user.getPassword());
            statement.setString(2,user.getUserName());
            statement.execute();
        }catch(SQLException e){
            logger.error("用户修改失败",e);
        }finally {
            closeResources(connection,statement);
        }
    }
}
