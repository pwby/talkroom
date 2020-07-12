package client.ui;

import client.entry.User;
import client.util.CommonUtil;
import client.vo.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: pwby
 * @create: 2020-03-23 18:48
 **/
public class Home  extends JFrame{

    //界面组件
    private JPanel Home;
    private JTabbedPane jPHomePage;
    private JPanel jPFriendList;
    private JPanel jPGroupList;
    private JPanel jPDynamic;
    private JButton dynamicBtn;
    private JButton friendBtn;
    private JButton groupListBtn;
    private JScrollPane jSPFriend;
    private JButton flushBtn;
    private JPanel jPExit;
    private JButton exitBtn;

    //Socket
    private Connect2Server connect2Server;
    private Scanner in;
    private PrintStream out;

    //qq图像的存储位置
    private String path = System.getProperty(("java.io.tmpdir"));

   /*
   * 该用户账号
   * */
    private String userName;

    /*
    * 该用户所有好友
    * */
    private  Set<User> users;

    /*
     * 缓存所有私聊界面
     * */
    private Map<String, PrivateChat> privateChatGUIList = new ConcurrentHashMap<>();

    /*
     * 群聊界面
     * */
    private GroupChat groupChatGUI;

    //日志
    private Logger logger = Logger.getLogger(Home.class);

    /*
    * 私聊点击事件
    * */
    private class PrivateLabelAction implements MouseListener {
        private  String friendName;
        private PrivateLabelAction(String friendName) {
            this.friendName=friendName;
        }

        /*
         * 鼠标点击事件
         * */
        @Override
        public void mouseClicked(MouseEvent e) {
            /*
             * 判断好友列表私聊界面缓存是否已经有指定的标签
             * */
            if (privateChatGUIList.containsKey(friendName)) {
                PrivateChat privateChat = privateChatGUIList.get(friendName);
                privateChat.getFrame().setVisible(true);
            } else {
                /*
                 * 第一次点击，创建私聊界面
                 * */
                MessageVO messageVO = new MessageVO();
                messageVO.setSender(friendName);
                messageVO.setReceiver(userName);
                PrivateChat privateChat = new PrivateChat(connect2Server,messageVO);
                privateChatGUIList.put(friendName, privateChat);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    /*
    * 发送用户此时所在页面状态
    * */
    private void sendState() {
            MessageVO msg2Server = new MessageVO();
            msg2Server.setType(MessageType.HOME.getIndex());
            out.println(CommonUtil.object2Json(msg2Server));
    }

    /*
     * 刚进入主页面时服务器发送的数据信息
     * */
    public void loadMessage() {
            if (in.hasNextLine()) {
                String jsonStrFromServer = in.nextLine();
                MessageVO messageVO = CommonUtil.json2Object(jsonStrFromServer, MessageVO.class);
                if(messageVO.getType() == MessageType.HOME.getIndex()) {
                    this.userName = messageVO.getReceiver();
                    this.setTitle(this.userName + "的主页");
                    this.users = CommonUtil.json2Set(messageVO.getContent(), User.class);
                    loadUsers();
                }
        }
    }

    //加载服务器发送的所有用户状态信息
    public void loadMessage(MessageVO messageVO){
        if(messageVO.getType() == MessageType.HOME.getIndex() || messageVO.getType() == MessageType.FLUSH.getIndex()
                || messageVO.getType() == MessageType.EXIT.getIndex() || messageVO.getType()==MessageType.OFFLINE.getIndex()) {
            this.users = CommonUtil.json2Set(messageVO.getContent(), User.class);
            loadUsers();
        }
    }

    public void loadUsers() {
        JLabel[] userLabels = new JLabel[users.size()];

        JPanel friends = new JPanel();
        friends.setLayout(new GridLayout(20,1,15,10));

        /*
        * 遍历我的所有好友
        * */
        Iterator<User> iterator = users.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (!user.getUserName().equals(this.userName) ) {
               ImageIcon imageIcon = loadImage(user);
               userLabels[i] = new JLabel(user.getUserName(), imageIcon, JLabel.LEFT);
               userLabels[i].setToolTipText(user.getBrief());
                if(user.isState()) {
                    userLabels[i].setEnabled(true);
                }else{
                    userLabels[i].setEnabled(false);
                }
                friends.add(userLabels[i]);
                userLabels[i].addMouseListener(new PrivateLabelAction(user.getUserName()));
                i++;
            }
        }


        /*
        * 滑动面板中的内容只包含朋友列表面板
        * */
        jSPFriend.setViewportView(friends);
        jSPFriend.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

       /*
       * 面板重新生效
       * */
        friends.revalidate();
        jSPFriend.revalidate();
    }

    //图像加载
    public ImageIcon loadImage(User user) {
     ImageIcon imageIcon;
        if(user.getQqImage()!=null) {
            byte[] qqImage = CommonUtil.json2Object(user.getQqImage(), byte[].class);
            File file = new File(path + File.separator + user.getUserName() + File.separator + ".jpg");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            OutputStream outFile = null;
            try {
                outFile = new FileOutputStream(file);
                outFile.write(qqImage);
            } catch (IOException e) {
                logger.error("所加载的图像不存在",e);
            }
            try {
                outFile.close();
            } catch (IOException e) {
                logger.error(e);
            }
            imageIcon = new ImageIcon(file.getAbsolutePath());
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(26,26,Image.SCALE_DEFAULT));
        }else{
            imageIcon = new ImageIcon(Home.class.getClassLoader().getResource("qqPhoto.jpg"));
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(26,26,Image.SCALE_DEFAULT));
        }
     return imageIcon;
 }

    private class DaemonTask implements Runnable {

        private Scanner in = new Scanner(connect2Server.getIn());

        @Override
        public void run() {
            while (true) {
                if (in.hasNextLine()) {
                    String strFromServer = in.nextLine();
                    MessageVO messageVO =  CommonUtil.json2Object(strFromServer, MessageVO.class);
                    switch (messageVO.getType()) {
                        case 6:
                            loadMessage(messageVO);
                              break;
                        case 9:
                            privateChat(messageVO);
                            break;
                        case 10:
                            groupChat(messageVO);
                            break;
                        case 12:
                        case 15:
                            loadMessage(messageVO);
                             break;
                        case 18:
                            loadGroupMsg(messageVO);
                            break;
                        case 20:
                            loadMessage(messageVO);
                            break;
                    }
                }
            }
        }

        //加载群聊信息
        private void loadGroupMsg(MessageVO msg){
            Set<MessageVO> msgFromServer = CommonUtil.json2Set(msg.getContent(),MessageVO.class);
            Iterator<MessageVO> iterator = msgFromServer.iterator();
            while(iterator.hasNext()){
                MessageVO messageVO = iterator.next();
                //设置消息类型，为了使文件内容不再次存储
                messageVO.setType(MessageType.LOADGROUPMSG.getIndex());
                groupChatGUI.insertReceievePaneText(messageVO);
            }
        }

        //群聊
        private void groupChat(MessageVO messageVO) {
            if (groupChatGUI != null) {
                groupChatGUI.getFrame().setVisible(true);
            } else {
                groupChatGUI = new GroupChat(connect2Server,userName,users);
            }
            groupChatGUI.insertReceievePaneText(messageVO);
        }

        //私聊
        public void privateChat(MessageVO messageVO) {
               PrivateChat privateChat;
                if (privateChatGUIList.containsKey(messageVO.getSender())) {
                   privateChat = privateChatGUIList.get(messageVO.getSender());
                    privateChat.getFrame().setVisible(true);
                } else {
                   privateChat = new PrivateChat(connect2Server, messageVO);
                    privateChatGUIList.put(messageVO.getSender(), privateChat);
                }

                /*
                * 将好友发送的信息展示在我的聊天记录栏
                * */
               privateChat.insertReceievePaneText(messageVO);
        }

    }

    public Home(Connect2Server connect2Server) {

        this.setContentPane(Home);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setBounds(760, 340, 800, 550);
        this.setVisible(true);

        this.connect2Server = connect2Server;

        try {
            this.in = new Scanner(connect2Server.getIn());
            this.out = new PrintStream(connect2Server.getOut(),true,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        sendState();
        loadMessage();

        /*
         * 启动后台线程不断监听服务器发送的信息
         * */
        Thread daemonThread = new Thread(new DaemonTask());
        daemonThread.setDaemon(true);
        daemonThread.start();

        dynamicBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI("http://39.96.71.245:8080/dynamic/"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });

        groupListBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                 * 判断群聊界面缓存是否已经有指定的标签
                 * */
                if (groupChatGUI != null) {
                    groupChatGUI.getFrame().setVisible(true);
                } else {
                    /*
                     * 第一次点击，创建私聊界面
                     * */
                    groupChatGUI = new GroupChat(connect2Server,userName,users);
                }
            }
        });

        flushBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                MessageVO msg2Server = new MessageVO();
                msg2Server.setType(MessageType.FLUSH.getIndex());

                out.println(CommonUtil.object2Json(msg2Server));
            }
        });

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             /*
             * 保存私聊的聊天记录
             * */
                for(String friendName:privateChatGUIList.keySet()){
                    privateChatGUIList.get(friendName).saveHistory();
                }

             /*
             * 像服务器发送退出信号
             * */
               MessageVO msg2Server = new MessageVO();
               msg2Server.setType(MessageType.EXIT.getIndex());
               msg2Server.setSender(userName);

               out.println(CommonUtil.object2Json(msg2Server));
               System.exit(0);
            }
        });
    }
}

