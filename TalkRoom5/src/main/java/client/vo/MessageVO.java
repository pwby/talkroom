package client.vo;

import lombok.Data;

/**
 * @description:
 * @author: pwby
 * @create: 2020-03-31 08:53
 **/
@Data
public class MessageVO {

    private  int type;
    private  String sender;
    private  String receiver;
    private  String content;
    private  String time;

    private  int contentType;
    private  String fileName;
}
