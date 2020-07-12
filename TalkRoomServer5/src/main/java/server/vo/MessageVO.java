package server.vo;
import lombok.Data;
/**
 * @description:
 * @author: pwby
 * @create: 2020-03-31 08:53
 **/
@Data
public class MessageVO {

    private  int type;               //消息类型
    private  String sender;          //发送者
    private  String receiver;        //接收者
    private  String content;         //消息内容
    private  String time;            //发送时间
    private  int contentType;        //消息内容类型
    private  String fileName;        //文件名
}
