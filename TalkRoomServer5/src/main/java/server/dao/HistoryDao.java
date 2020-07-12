package server.dao;

import server.vo.MessageVO;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: pwby
 * @create: 2020-03-28 19:00
 **/
public class HistoryDao extends BasedDao{

    /*
     * @Date 19:02 2020-03-28  19:02:52
     * @Description 查看历史记录
     * @Param
     * @return
     **/
    public Set<MessageVO> loadHistory(){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Set<MessageVO> historyList=new HashSet<>();
        try{
            connection = getConnection();
            String sql = "select * from history group by time";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while(resultSet.next()){
                MessageVO history=new MessageVO();
                history.setSender(resultSet.getString("sender"));
                history.setType(resultSet.getInt("msg_type"));
                history.setContentType(resultSet.getInt("msg_content_type"));
                history.setFileName(resultSet.getString("file_name"));
                history.setContent(resultSet.getString("msg_content"));
                history.setTime(resultSet.getString("time"));
                historyList.add(history);

            }
return historyList;



        }catch (SQLException e){
           logger.error("群消息:加载历史记录失败",e);
        }finally {
            closeResources(connection,statement,resultSet);
        }
        return null;

    }


    /*
     * @Date 19:06 2020-03-28  19:06:50
     * @Description 存储聊天记录
     * @Param
     * @return
     **/
    public void saveHistory(MessageVO msg){
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = getConnection();
            String sql = "insert into  history(sender,msg_type,msg_content_type,file_name,msg_content,time) values (?,?,?,?,?,?)";
            statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);

                statement.setString(1, msg.getSender());
                statement.setInt(2, msg.getType()); //加密
                statement.setInt(3, msg.getContentType());
                statement.setString(4, msg.getFileName());
                statement.setString(5,msg.getContent());
                statement.setString(6, msg.getTime());

                statement.execute();
        }catch(SQLException e){
            logger.error("保存历史记录失败",e);
        }finally {
            closeResources(connection,statement);
        }

    }


}
