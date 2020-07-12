package client.ui;

import client.entry.User;
import client.util.CommonUtil;
import client.vo.MessageType;
import client.vo.MessageVO;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GroupChat extends JFrame {
    //界面组件
    private JTextField msgText;
    private JButton sendBtn;
    private JButton sendFileBtn;
    private JButton sendExpressionBtn;
    private JButton viewHistory;
    private JPanel GroupChat;
    private JScrollPane jSPLeft;
    private JTextPane receievePane;
    private JSplitPane jSP;
    private JPanel jSPRight;
    private JScrollPane jScrollPane;

    private Connect2Server connect2Server;
    private PrintStream out;
    private StyledDocument document;
    private SimpleAttributeSet attribute;
    private String userName;
    private Set<User> users;
    private String path = System.getProperty(("java.io.tmpdir"));
    private JPopupMenu expressframe;
    private Map<String, ImageIcon> expressMap = new HashMap<>();

    public GroupChat(Connect2Server connect2Server, String userName, Set<User> users) {
        this.setTitle("群聊");
        this.setContentPane(GroupChat);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setBounds(730, 340, 600, 500);
        this.setResizable(false);
        this.setVisible(true);

        jSP.setDividerLocation(200);

        this.connect2Server = connect2Server;
        this.userName = userName;
        this.users = users;
        try {
            out = new PrintStream(connect2Server.getOut(), true, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        loadUsers(users);

        document = receievePane.getStyledDocument();

        /*
         * 表情弹出框
         * */
        expressframe = new JPopupMenu();
        JPanel expressJpanel = new JPanel();
        expressJpanel.setLayout(new GridLayout(2, 3));
        JLabel[] expressLabel = new JLabel[6];
        for (int i = 0; i < 6; i++) {
            String fileName = i + ".gif";
            ImageIcon imageIcon = new ImageIcon(PrivateChat.class.getClassLoader().getResource(fileName));
            expressMap.put(fileName, imageIcon);
            expressLabel[i] = new JLabel(imageIcon);
            expressLabel[i].addMouseListener(new ExpressLabelListen(fileName));
            expressJpanel.add(expressLabel[i]);
        }
        expressframe.add(expressJpanel);

        sendFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageVO msg2Server = new MessageVO();
                msg2Server.setType(MessageType.GROUP_CHAT.getIndex());
                msg2Server.setContentType(MessageType.FILE.getIndex());
                msg2Server.setSender(userName);
                msg2Server.setTime(CommonUtil.getTime());
                try {
                    sendFile(msg2Server);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                /*
                 * 发送文件
                 * */
                if (msg2Server.getFileName() != null) {
                    out.println(CommonUtil.object2Json(msg2Server));
                    insertReceievePaneText(msg2Server);
                }
            }
        });

        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
          sendFieldInfo();
            }
        });

        msgText.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {

                sendFieldInfo();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        sendExpressionBtn.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                expressframe.show(sendExpressionBtn, e.getX(), e.getY());
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
        });

        viewHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageVO msg2Server = new MessageVO();
                msg2Server.setType(MessageType.LOADGROUPMSG.getIndex());
                out.println(CommonUtil.object2Json(msg2Server));
                try {
                    document.insertString(document.getLength(), "[聊天记录]\n", new FontAttribute(4).getAttributeSet());
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    /*
     * 历史栏中插入信息
     * */
    public void insertReceievePaneText(MessageVO msg) {
        try {
            insertReceievePaneTime(msg);
            attribute = new FontAttribute(5).getAttributeSet();
            switch (msg.getContentType()) {
                case 7:
                    document.insertString(document.getLength(), msg.getContent() + "\n", attribute);
                    break;
                case 8:
                    handleFile(msg);
                    break;
                case 13:
                    handleExepress(msg);
                    break;
            }

            /*
             * 使接收框永远处于最底端，浏览到最新消息
             * */
            {
                receievePane.setSelectionStart(receievePane.getText().length());
                JScrollBar vertical = jScrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void handleExepress(MessageVO msg) {
        /*
         * 设置插入符号
         * */
        receievePane.setCaretPosition(document.getLength());
        /*
         * 插入表情
         * */
        receievePane.insertIcon(expressMap.get(msg.getContent()));
        /*
         * 插入表情后换行
         * */
        try {
            document.insertString(document.getLength(), "\n", attribute);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(MessageVO sendMsg) throws IOException {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectFile = jfc.getSelectedFile();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(selectFile));
            byte[] byteContent = new byte[in.available()];
            in.read(byteContent);
            in.close();
            sendMsg.setContent(CommonUtil.object2Json(byteContent));
            sendMsg.setFileName(selectFile.getName());
        }

    }

    public void receieveFile(MessageVO msgFromServer) {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("保存文件");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = jfc.showSaveDialog(null);
        File file = null;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                if (jfc.getSelectedFile().isDirectory()) {
                    file = new File(jfc.getSelectedFile().getAbsolutePath() + File.separator + msgFromServer.getFileName());
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(CommonUtil.json2Object(msgFromServer.getContent(), byte[].class));
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleFile(MessageVO msg) {
        try {
            if (msg.getSender().equals(userName)) {
                document.insertString(document.getLength(), "发送了文件", attribute);
                document.insertString(document.getLength(), "[" + msg.getFileName() + "]\n", new FontAttribute(1).getAttributeSet());
            } else {
                if (msg.getType() != MessageType.LOADGROUPMSG.getIndex()) {
                    receieveFile(msg);
                }
                document.insertString(document.getLength(), "接收到文件", attribute);
                document.insertString(document.getLength(), "[" + msg.getFileName() + "]\n", new FontAttribute(1).getAttributeSet());
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }


    }

    public void insertReceievePaneTime(MessageVO msg) {
        try {

            document.insertString(document.getLength(), msg.getTime() + "\n", new FontAttribute(1).getAttributeSet());
            if (msg.getSender().equals(userName)) {
                document.insertString(document.getLength(), userName + ">>>", new FontAttribute(2).getAttributeSet());
            } else {
                document.insertString(document.getLength(), msg.getSender() + ">>>", new FontAttribute(3).getAttributeSet());
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void loadUsers(Set<User> users) {

        JLabel[] userLabels = new JLabel[users.size()];

        /*
         * 垂直排列的好友面板
         * */
        JPanel friends = new JPanel();
        friends.setLayout(new GridLayout(20, 1, 15, 10));

        /*
         * 遍历我的所有好友
         * */

        Iterator<User> iterator = users.iterator();
        int i = 0;

        while (iterator.hasNext()) {
            User user = iterator.next();

            ImageIcon imageIcon = loadImage(user);
            userLabels[i] = new JLabel(user.getUserName(), imageIcon, JLabel.LEFT);
            userLabels[i].setToolTipText(user.getBrief());
            if (user.isState()) {
                userLabels[i].setEnabled(true);
            } else {
                userLabels[i].setEnabled(false);
            }
            friends.add(userLabels[i]);
            i++;

        }


        /*
         * 滑动面板中的内容只包含朋友列表面板
         * */
        jSPLeft.setViewportView(friends);
        jSPLeft.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        /*
         * 面板重新生效
         * */
        friends.revalidate();
        jSPLeft.revalidate();
    }

    public ImageIcon loadImage(User user) {
        ImageIcon imageIcon;
        if (user.getQqImage() != null) {
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
                e.printStackTrace();
            }
            try {
                outFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageIcon = new ImageIcon(file.getAbsolutePath());
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(26, 26, Image.SCALE_DEFAULT));
        } else {
            imageIcon = new ImageIcon(Home.class.getClassLoader().getResource("qqPhoto.jpg"));
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(26, 26, Image.SCALE_DEFAULT));
        }
        return imageIcon;
    }

    public JFrame getFrame() {
        return this;
    }

    private class ExpressLabelListen implements MouseListener {
        String fileName;

        public ExpressLabelListen(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            MessageVO msg2Server = new MessageVO();
            msg2Server.setSender(userName);
            msg2Server.setTime(CommonUtil.getTime());
            msg2Server.setType(MessageType.GROUP_CHAT.getIndex());
            msg2Server.setContentType(MessageType.Express.getIndex());
            msg2Server.setContent(fileName);

            insertReceievePaneText(msg2Server);
            out.println(CommonUtil.object2Json(msg2Server));

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

    public void sendFieldInfo(){
        String content = msgText.getText();
        if (!content.trim().equals("") && !content.isEmpty()) {
            MessageVO msg2Server = new MessageVO();
            msg2Server.setSender(userName);
            msg2Server.setType(MessageType.GROUP_CHAT.getIndex());
            msg2Server.setContentType(MessageType.TEXT.getIndex());
            msg2Server.setContent(content);
            msg2Server.setTime(CommonUtil.getTime());

            insertReceievePaneText(msg2Server);
            msgText.setText("");
            out.println(CommonUtil.object2Json(msg2Server));
        }
    }
}
