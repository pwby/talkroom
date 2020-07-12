package client;

import client.entry.User;
import client.ui.Connect2Server;
import client.util.CommonUtil;
import client.vo.MessageType;
import client.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Set;

public class Administrator {

    private Connect2Server connect2Server = new Connect2Server();
    private Scanner in;
    private PrintStream out;
    private JFrame jFrame;
    private JPanel jPanel;
    private JComboBox user, option;
    private JButton submit;
    private JButton exit;

    public Administrator() {
        jFrame = new JFrame("管理员");
        user = new JComboBox();
        option = new JComboBox();
        submit = new JButton("提交");
        exit = new JButton("退出");
        jPanel = new JPanel();
        try {
            this.in = new Scanner(connect2Server.getIn());
            this.out = new PrintStream(connect2Server.getOut(), true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String[] userText = loadUser();
        user.setModel(new DefaultComboBoxModel(userText));
        user.setSelectedIndex(0);

        String[] optionText = new String[]{"===选择操作===", "下线", "重置密码"};
        option.setModel(new DefaultComboBoxModel(optionText));

        jPanel.add(user);
        jPanel.add(option);
        jPanel.add(submit);
        jPanel.add(exit);
        jFrame.add(jPanel);

        jFrame.setBounds(730, 340, 300, 400);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectUser = (String) user.getSelectedItem();
                String selectOption = (String) option.getSelectedItem();

                if (!selectUser.equals(userText[0]) && !selectOption.equals(optionText[0])) {
                    MessageVO messageVO = new MessageVO();
                    if (selectOption.equals(optionText[1])) {
                        //下线操作;
                        offline(messageVO, selectUser);
                    } else {
                        //重置密码操作
                        changPassword(messageVO, selectUser);
                    }
                }
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageVO msg2Server = new MessageVO();
                msg2Server.setType(MessageType.EXIT.getIndex());
                msg2Server.setSender("ADMIN");
                out.println(CommonUtil.object2Json(msg2Server));
                System.exit(0);
            }
        });
    }

    public void offline(MessageVO messageVO, String selectUser) {
        messageVO.setType(MessageType.OFFLINE.getIndex());
        messageVO.setContent(selectUser);

        out.println(CommonUtil.object2Json(messageVO));

        promptMsg();
    }

    public void changPassword(MessageVO messageVO, String selectUser) {

        String password = (String) JOptionPane.showInputDialog(null, "请输入密码：\n",
                "重置密码", JOptionPane.PLAIN_MESSAGE, null, null, null);
        if (password != null && !password.trim().equals("")) {
            messageVO.setType(MessageType.CHANGEPASSWD.getIndex());
            User user = new User();
            user.setUserName(selectUser);
            user.setPassword(password);
            messageVO.setContent(CommonUtil.object2Json(user));

            out.println(CommonUtil.object2Json(messageVO));

            promptMsg();
        }
    }

    /*
     * 提示信息
     * */
    public void promptMsg() {
        if (in.hasNext()) {
            String jsonStr = in.nextLine();
            MessageVO msgFromServer = CommonUtil.json2Object(jsonStr, MessageVO.class);
            JOptionPane.showMessageDialog(null, msgFromServer.getContent(), "提示信息", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public String[] loadUser() {
        MessageVO msg2Server = new MessageVO();
        msg2Server.setType(MessageType.ADMINISTRATOR.getIndex());
        out.println(CommonUtil.object2Json(msg2Server));

        if (in.hasNext()) {
            String jsonStr = in.nextLine();
            MessageVO msgFromServer = CommonUtil.json2Object(jsonStr, MessageVO.class);
            if (msgFromServer.getType() == MessageType.ADMINISTRATOR.getIndex()) {
                Set<User> allUser = CommonUtil.json2Set(msgFromServer.getContent(), User.class);
                String[] users = new String[allUser.size() + 1];
                users[0] = "===请选择用户===";
                int i = 1;
                for (User user : allUser) {
                    users[i++] = user.getUserName();
                }
                return users;
            }

        }
        return null;
    }

    public static void main(String[] args) {
        new Administrator();
    }
}
