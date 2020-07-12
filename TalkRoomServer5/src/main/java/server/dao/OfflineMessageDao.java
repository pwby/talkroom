package server.dao;

import server.vo.MessageVO;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: pwby
 * @create: 2020-03-28 19:01
 **/
public class OfflineMessageDao extends BasedDao {
    /*
     * @Date 20:44 2020-03-28  20:44:43
     * @Description 存储离线消息
     * @Param
     * @return
     **/
     public void saveOfflineMessage(MessageVO offlineMessage) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            String sql = "insert into  offlinemessage( sender, receiver, msg_type,msg_content_type,msg_content,file_name, send_time ) values (?,?,?,?,?,?,?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, offlineMessage.getSender());
                statement.setString(2, offlineMessage.getReceiver());
                statement.setInt(3, offlineMessage.getType());
                statement.setInt(4, offlineMessage.getContentType());
                statement.setString(5, offlineMessage.getContent());
                statement.setString(6, offlineMessage.getFileName());
                statement.setString(7, offlineMessage.getTime());

             statement.execute();
        } catch (SQLException e) {
            logger.error("离线消息存储失败",e);
        } finally {
            closeResources(connection, statement);
        }

    }

    /*
     * @Date 20:45 2020-03-28  20:45:39
     * @Description 加载离线消息
     * @Param
     * @return
     **/
    public Set<MessageVO> loadPrivateOfflineMessage(String user) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Set<MessageVO> offlineMessagesList = new HashSet<MessageVO>();
        try {
            connection = getConnection();
            String sql = "select * from offlinemessage where  receiver = ?  ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, user);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                MessageVO offlineMessage = new MessageVO();
                offlineMessage.setSender(resultSet.getString("sender"));
                offlineMessage.setReceiver(resultSet.getString("receiver"));
                offlineMessage.setType(resultSet.getInt("msg_type"));
                offlineMessage.setContentType(resultSet.getInt("msg_content_type"));
                offlineMessage.setContent(resultSet.getString("msg_content"));
                offlineMessage.setFileName(resultSet.getString("file_name"));
                offlineMessage.setTime((resultSet.getString("send_time")));
                offlineMessagesList.add(offlineMessage);

                 delPrivateOfflineMessage(user);
            }
        } catch (SQLException e) {
           logger.error("离线消息查看失败",e);
        } finally {
            closeResources(connection, statement, resultSet);
        }
        return offlineMessagesList;
    }
/*
* 删除离线消息
* */
    public void  delPrivateOfflineMessage(String user) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            String sql = "delete from offlinemessage where  receiver = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, user);
            statement.execute();

            statement.execute();
        } catch (SQLException e) {
            logger.error("离线消息删除失败",e);
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }
}


