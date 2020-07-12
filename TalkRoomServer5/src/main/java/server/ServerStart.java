package server;
import org.apache.log4j.Logger;
import server.dao.HistoryDao;
import server.dao.OfflineMessageDao;
import server.dao.UserDao;
import server.entry.User;
import server.util.CommUtils;
import server.vo.MessageType;
import server.vo.MessageVO;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * @description:
 * @author: pwby
 * @create: 2020-03-31 14:21
 **/
public class ServerStart {

    //用户表、历史记录表、离线消息表
    private static UserDao userDao = new UserDao();
    private static HistoryDao historyDao = new HistoryDao();
    private static OfflineMessageDao offlineMessageDao = new OfflineMessageDao();

    //服务器端口号、在线好友、用户表中所有用户
    private static final int PORT;
    private static Map<String, Socket> onLinefriends = new ConcurrentHashMap<>();
    private static Set<User> allUser = userDao.getAllUser();

    //加载socket文件，获取端口号
    static {
        Properties properties = CommUtils.loadProperties("socket.properties");
        PORT = Integer.parseInt(properties.getProperty("port"));
        Properties log4j = CommUtils.loadProperties("log4j.properties");
    }

    //日志记录
    private static Logger logger = Logger.getLogger(ServerStart.class);

    //处理客户端请求的子线程
    private static class ExecuteClient implements Runnable {

        private Socket client;         //客户端套接字
        private Scanner in;            //socket输入流
        private PrintStream out;       //输出流
        private String userName;       //该子线程所服务的客户端用户名
        private boolean flag = true;   //该线程是否从线程池中销毁的标志

        //初始化socket
        public ExecuteClient(Socket client) {
            this.client = client;
            try {
                this.in = new Scanner(this.client.getInputStream());
                this.out = new PrintStream((this.client.getOutputStream()), true, "UTF-8");
            } catch (IOException e) {
                logger.error("获取客户端输入输入流失败", e);
            }
        }

        //登录
        public void login(MessageVO msgFromClient) {
            User clientUser = CommUtils.json2Object(msgFromClient.getContent(), User.class);
            MessageVO msg2Client = new MessageVO();
            /*
            * 检测该用户是否存在于用户信息库中
            * */
            //allUser.
            if (containUser(clientUser)) {
                /*
                 * 检验是否多次登录同一个账号
                 * */
                if (!onLinefriends.containsKey(clientUser.getUserName())) {

                    /*
                     * 将该用户添加到在线列表中
                     * */
                    this.userName = clientUser.getUserName();
                    onLinefriends.put(this.userName, client);

                    //发送登录成功信息
                    msg2Client.setType(MessageType.LOGINSUCCED.getIndex());
                    out.println(CommUtils.object2Json(msg2Client));
                } else {
                    //重复登录，发送登录失败信息
                    msg2Client.setType(MessageType.LOGINFAILED.getIndex());
                    out.println(CommUtils.object2Json(msg2Client));
                }
            } else {
                //用户库中不包含该用户，发送登录失败信息
                msg2Client.setType(MessageType.LOGINFAILED.getIndex());
                out.println(CommUtils.object2Json(msg2Client));
            }
        }

        public void reg(MessageVO messageVO) {
            User user = CommUtils.json2Object(messageVO.getContent(), User.class);
            MessageVO msg2Client = new MessageVO();
            /*
             * 检测该用户是否被注册过
             * */
            if (allUser.contains(user)) {
                //用户已经被注册过，发送注册失败信息
                msg2Client.setType(MessageType.REGFAILED.getIndex());
                out.println(CommUtils.object2Json(msg2Client));
            } else {
                this.userName = user.getUserName();

                //将用户存储在用户表中，刷新allUser,将该用户添加到在线好友中
                userDao.userReg(user);
                allUser = userDao.getAllUser();
                onLinefriends.put(this.userName, client);

                //发送注册成功信息
                msg2Client.setType(MessageType.REGSUCCED.getIndex());
                out.println(CommUtils.object2Json(msg2Client));
            }
        }

        //遍历allUser
        public boolean containUser(User msg){
            Iterator<User> entry = allUser.iterator();
            while(entry.hasNext()){
                User user = entry.next();
                if(user.equals(msg) && user.getPassword().equals(msg.getPassword())){
                    return true;
                }
            }
            return false;
        }


        public void sendAllFriends(int type) {
            MessageVO msg2Client = new MessageVO();
            msg2Client.setType(type);
            msg2Client.setContent(CommUtils.object2Json(allUser));
            try {
                //遍历在线好友列表，将allUser发送给所有在线好友
                for (String user : onLinefriends.keySet()) {
                    PrintStream out = new PrintStream(onLinefriends.get(user).getOutputStream(), true, "UTF-8");
                    msg2Client.setReceiver(user);
                    out.println(CommUtils.object2Json(msg2Client));
                }
            } catch (IOException e) {
                logger.error("向客户端发送所有好友失败", e);
            }
        }

        //将allUser发送给管理员
        public void sendAllUser2Admin(MessageVO messageVO) {
            messageVO.setContent(CommUtils.object2Json(allUser));
            out.println(CommUtils.object2Json(messageVO));
        }

        //加载离线消息
        public void loadOffLineMsg() {
            //离线消息表中加载信息（信息加载完毕后在数据库中已删除）
            Set<MessageVO> offlineMessage = offlineMessageDao.loadPrivateOfflineMessage(this.userName);
            Iterator<MessageVO> allMsg = offlineMessage.iterator();
            //一条一条发送给用户
            while (allMsg.hasNext()) {
                MessageVO msg= allMsg.next();
                String msg2Client = CommUtils.object2Json(msg);
                out.println(msg2Client);
            }
        }

        /*
         * 管理员踢掉用户
         * */
        public void offline(MessageVO msgFromClient) {
            msgFromClient.setSender(msgFromClient.getContent());
            Socket socket = exit(msgFromClient);
            try {
                //用户在线
                if (socket != null) {
                    msgFromClient.setContent(msgFromClient.getSender() + "已被踢下线");
                    out.println(CommUtils.object2Json(msgFromClient));
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                } else {
                    //用户不在线
                    msgFromClient.setContent("该用户不在线，无法执行下线操作");
                    out.println(CommUtils.object2Json(msgFromClient));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        /*
         * 管理员重置密码
         * */
        public void changePassword(MessageVO messageVO) {
            User user = CommUtils.json2Object(messageVO.getContent(), User.class);
            //修改用户表中的用户信息，并刷新allUser
            userDao.modifyPassword(user);
            allUser = userDao.getAllUser();

            messageVO.setContent("修改成功");
            out.println(CommUtils.object2Json(messageVO));
        }

        //用户下线
        public Socket exit(MessageVO msgFromClient) {
            if (onLinefriends.containsKey(msgFromClient.getSender())) {
                Socket socket = onLinefriends.remove(msgFromClient.getSender());
                friendsAndState();
                sendAllFriends(msgFromClient.getType());
                return socket;
            }
            return null;
        }

        //向用户发送刷新后的所有用户状态
        public void autoFlush() {
            friendsAndState();
            MessageVO msg2Client = new MessageVO();
            msg2Client.setType(MessageType.FLUSH.getIndex());
            msg2Client.setContent(CommUtils.object2Json(allUser));
            msg2Client.setReceiver(this.userName);
            out.println(CommUtils.object2Json(msg2Client));
        }

        /*
         * 设置好友状态
         * */
        public void friendsAndState() {
            for (User user : allUser) {
                if (isOnline(user.getUserName())) {
                    user.setState(true);
                } else {
                    user.setState(false);
                }
            }
        }

        /*
         * @Date 21:03 2020-04-02  21:03:09
         * @Description 判断用户是否在线
         **/
        public boolean isOnline(String userName) {
            return onLinefriends.containsKey(userName);
        }

        /*
         * @Date 20:54 2020-04-02  20:54:26
         * @Description 私聊
         **/
        public void privateChat(MessageVO msgFromClient, String jsonMsg) {
            String receiver = msgFromClient.getReceiver();

            //在线发送给相应好友
            if (isOnline(receiver)) {
                PrintStream out;
                try {
                    out = new PrintStream(onLinefriends.get(receiver).getOutputStream(), true, "UTF-8");
                    out.println(jsonMsg);
                } catch (IOException e) {
                    logger.error("私聊消息发送失败", e);
                }
           //否则存储到离线消息库中
            } else {
                offlineMessageDao.saveOfflineMessage(msgFromClient);
            }
        }

        //群聊
        private void groupChat(MessageVO msgFromClient, String jsonMsg) {
            try {
                //先将消息存储在群聊历史表中
                historyDao.saveHistory(msgFromClient);
                //遍历在线好友（除了自己），发送消息
                for (Map.Entry<String, Socket> entry : onLinefriends.entrySet()) {
                    if (!msgFromClient.getSender().equals(entry.getKey())) {
                        msgFromClient.setReceiver(entry.getKey());
                        PrintStream out = new PrintStream(entry.getValue().getOutputStream(), true, "UTF-8");
                        out.println(jsonMsg);
                    }
                }
            } catch (IOException e) {
                logger.error("群聊消息发送失败", e);
            }
        }

        //加载群聊信息
        private void loadGroupMsg() {
            //历史表中加载群聊信息
            Set<MessageVO> msg = historyDao.loadHistory();
            MessageVO msg2Client = new MessageVO();
            msg2Client.setType(MessageType.LOADGROUPMSG.getIndex());
            msg2Client.setContent(CommUtils.object2Json(msg));
            out.println(CommUtils.object2Json(msg2Client));
        }

        @Override
        public void run() {
            while (flag) {
                if (in.hasNextLine()) {
                    String jsonStrFromClient = in.nextLine();
                    MessageVO msgFromClient = CommUtils.json2Object(jsonStrFromClient, MessageVO.class);
                    switch (msgFromClient.getType()) {
                        case 0:
                            login(msgFromClient);
                            break;
                        case 3:
                            reg(msgFromClient);
                            break;
                        case 6:
                            friendsAndState();
                            sendAllFriends(MessageType.HOME.getIndex());
                            break;
                        case 9:
                            privateChat(msgFromClient, jsonStrFromClient);
                            break;
                        case 10:
                            groupChat(msgFromClient, jsonStrFromClient);
                            break;
                        case 12:
                            autoFlush();
                            loadOffLineMsg();
                            break;
                        case 15:
                            exit(msgFromClient);
                            flag = false;
                            break;
                        case 18:
                            loadGroupMsg();
                            break;
                        case 19:
                            sendAllUser2Admin(msgFromClient);
                            break;
                        case 20:
                            offline(msgFromClient);
                            break;
                        case 21:
                            changePassword(msgFromClient);
                            break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        int maxSize = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(maxSize);
        while (true) {
            Socket client = serverSocket.accept();
            executorService.execute(new ExecuteClient(client));
        }
    }
}
