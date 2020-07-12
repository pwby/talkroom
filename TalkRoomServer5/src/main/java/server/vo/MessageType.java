package server.vo;

/**
 * @description:
 * @author: pwby
 * @create: 2020-03-31 08:54
 **/

public enum MessageType {

    /*
    * 动作类型
    * */
    LOGIN("申请登录",0),
    LOGINSUCCED("登录成功",1),
    LOGINFAILED("登录失败",2),
    REG("注册",3),
    REGSUCCED("注册成功",4),
    REGFAILED("注册失败",5),
    PRIVATE_CHAT("私聊",9),
    GROUP_CHAT("群聊",10),
    HOME("主页面",6),
    FLUSH("刷新",12),
    EXIT("退出系统",15),
    LOADOFFLINEMESSAGE("加载离线消息",16),
    LOADGROUPMSG("加载群信息",18),
    ADMINISTRATOR("管理员上线",19),
    OFFLINE("踢掉在线用户",20),
    CHANGEPASSWD("更改用户密码",21),

    /*
    * 发送内容类型
    * */
    TEXT("普通文本",7),
    FILE("文件",8),
    Express("发送表情",13),
    HISTORY("历史记录",11);
    private String type;
    private int index;

    MessageType(String type, int index) {
        this.type = type;
        this.index = index;
    }
    public static String get(int i){
        return get(i);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
