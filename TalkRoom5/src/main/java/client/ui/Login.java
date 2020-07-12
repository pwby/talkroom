package client.ui;

import client.entry.User;
import client.util.CommonUtil;
import client.vo.MessageType;
import client.vo.MessageVO;
import org.apache.log4j.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

public class Login extends JFrame {
    //界面组件
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JButton regBtn;
    public JButton loginBtn;
    private JPanel login;
    private JLabel qqImage;
    private JPanel jp2;
    private JLabel userName;
    private JLabel password;
    private JPanel jp3;
    private JPanel jp1;

    //Socket
    private PrintStream out;
    private InputStream in;

    //
    public int type = -1;

    //日志
    private Logger logger = Logger.getLogger(Login.class);


    /* 初始化登录页面*/
    public Login() {
        this.setTitle("登录页面");
        ImageIcon icon = new ImageIcon(Login.class.getClassLoader().getResource("qq.jpg"));
        icon.setImage(icon.getImage().getScaledInstance(300,120,Image.SCALE_AREA_AVERAGING));
        qqImage.setIcon(icon);
        this.setContentPane(login);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(760, 340, 300, 300);
        this.setResizable(false);
        this.setVisible(true);
    }

    /*
     * @Date 13:39 2020-03-31  13:39:13
     * @Description 监听 注册/登录按钮
     **/
    public int actionListenr() {
        while (true) {
            regBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    type = MessageType.REG.getIndex();
                }
            });
            if (type == MessageType.REG.getIndex()) {
                break;
            }
            loginBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    type = MessageType.LOGIN.getIndex();
                }
            });
            if (type == MessageType.LOGIN.getIndex()) {
                break;
            }
        }
        return type;
    }

    /*
     * @Date 14:01 2020-03-31  14:01:11
     * @Description 处理登录页面的信息
     **/
    public int handleLogin(Connect2Server connect2Server) {

        /*
         * 获取注册页面的用户名和密码
         **/
        String userName = userNameText.getText();
        String password = String.valueOf(passwordText.getPassword());
        User user = new User();
        user.setUserName(userName);
        user.setPassword(password);

        /*
         * @Description 封装到vo中
         **/
        MessageVO messageVO = new MessageVO();
        messageVO.setType(MessageType.LOGIN.getIndex());
        messageVO.setContent(CommonUtil.object2Json(user));

        /*
         *将封装好的数据发送到服务器端
         **/
        try {
            out = new PrintStream(connect2Server.getOut(), true, "UTF-8");
            out.println(CommonUtil.object2Json(messageVO));
        } catch (UnsupportedEncodingException e) {
           logger.error("流通道接接收失败",e);
        }

        /*
         * @Description 接收并解析从服务器发送的消息
         **/
        in = connect2Server.getIn();
        Scanner scanner = new Scanner(in);
            if (scanner.hasNextLine()) {
                String fromServer = scanner.nextLine();
                messageVO =  CommonUtil.json2Object(fromServer, MessageVO.class);
                if (messageVO.getType() == MessageType.LOGINSUCCED.getIndex()) {
                    JOptionPane.showMessageDialog(this, "登陆成功", "提示信息",
                            JOptionPane.INFORMATION_MESSAGE);
                    type = MessageType.LOGINSUCCED.getIndex();

                } else {
                    JOptionPane.showMessageDialog(this, "登陆失败", "提示信息",
                            JOptionPane.INFORMATION_MESSAGE);
                    type = MessageType.LOGINFAILED.getIndex();
                }
            }
        return type;
    }
}
