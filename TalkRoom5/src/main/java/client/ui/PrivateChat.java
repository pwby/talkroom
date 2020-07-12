package client.ui;

import client.util.CommonUtil;
import client.vo.MessageType;
import client.vo.MessageVO;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: pwby
 * @create: 2020-04-09 09:54
 **/
public class PrivateChat {

    //界面组件
    private JPanel PrivateChat;
    private JTextPane receievePane;
    private JTextPane sendPane;
    private JButton sendBtn;
    private JLabel fontLabel;
    private JComboBox fontCB;
    private JLabel fontSizeLabel;
    private JComboBox fontSizeCB;
    private JButton sendFile;
    private JButton sendImage;
    private JButton loadHistory;
    private JButton delHistory;
    private JSplitPane jSplitP;
    private JScrollPane jUp;
    private JPanel jDown;
    private JPanel jDownOfNorth;
    private JPanel jDownOfSouth;
    private JLabel bladitalicLabel;
    private JToggleButton boldBtn;
    private JToggleButton italicBtn;
    private JLabel fontColorLabel;
    private JButton foreBtn;
    private JButton sendExpress;

    //表情栏
    private JPopupMenu expressframe;

    private JFrame frame;
    private Color foreColor;
    private FontAttribute fontAttribute;
    private StyledDocument document;

    private PrintStream out;

    private String friendName;
    private String userName;
    private String path;

    private Map<String, ImageIcon> expressMap = new HashMap<>();

    private Logger logger = Logger.getLogger(PrivateChat.class);

    /*
     * 点击表情包的处理
     * */
    private class ExpressLabelListen implements MouseListener {
        private String fileName;
        private ExpressLabelListen(String str) {
            this.fileName = str;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            MessageVO msg2Server = new MessageVO();
            msg2Server.setSender(userName);
            msg2Server.setReceiver(friendName);
            msg2Server.setTime(CommonUtil.getTime());
            msg2Server.setType(MessageType.PRIVATE_CHAT.getIndex());
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

    /*
     * 图片的过滤
     * */
    private class PNGFileFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.getName().endsWith(".png");
        }

        @Override
        public String getDescription() {
            return "png文件(*.png)";
        }
    }

    private class JPGFileFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            return f.getName().endsWith(".jpg");
        }

        @Override
        public String getDescription() {
            return "jpg文件(*.jpg)";
        }
    }

    private class GIFFileFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            return f.getName().endsWith(".gif");
        }

        @Override
        public String getDescription() {
            return "gif文件(*.gif)";
        }
    }

    /*
     * 历史栏中插入信息
     * */
    public void insertReceievePaneText(MessageVO msg) {
        try {
            insertReceievePaneTime(msg);
            switch (msg.getContentType()) {
                case 7:
                    document.insertString(document.getLength(), msg.getContent() + "\n", fontAttribute.getAttributeSet());
                    break;
                case 8:
                    handleFile(msg);
                    break;
                case 13:
                    handleExepress(msg);
                    break;
                case 14:
                    handleImage(msg);
                    break;
            }
            {
                //使界面处在最新消息处
                receievePane.setSelectionStart(receievePane.getText().length());
                JScrollBar vertical = jUp.getVerticalScrollBar();
                vertical.setValue( vertical.getMaximum());
            }
        } catch (BadLocationException e) {
            logger.error(e);
        }
    }

    //时间，用户名，好友用户名的设置
    public void insertReceievePaneTime(MessageVO msg) {
        try {
            document.insertString(document.getLength(), msg.getTime() + "\n", new FontAttribute(1).getAttributeSet());
            if (msg.getSender().equals(userName)) {
                document.insertString(document.getLength(), userName + ">>>", new FontAttribute(2).getAttributeSet());
            }
            if (msg.getSender().equals(friendName)) {
                document.insertString(document.getLength(), friendName + ">>>", new FontAttribute(3).getAttributeSet());
            }
        } catch (BadLocationException e) {
          logger.error(e);
        }
    }

    //发送文件
    public void sendFile(MessageVO sendMsg) throws IOException {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showOpenDialog(null);

        //选中文件
        if (returnValue == JFileChooser.APPROVE_OPTION ) {
            File selectFile = jfc.getSelectedFile();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(selectFile));
            byte[] byteContent = new byte[in.available()];
            in.read(byteContent);
            in.close();
            sendMsg.setContent(CommonUtil.object2Json(byteContent));
            sendMsg.setFileName(selectFile.getName());
        }
    }

    //发送图片
    public void sendImage(MessageVO sendMsg) throws IOException {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileFilter(new PNGFileFilter());
        jfc.setFileFilter(new GIFFileFilter());
        jfc.setFileFilter(new JPGFileFilter());
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectFile = jfc.getSelectedFile();
            InputStream in = new FileInputStream(selectFile);
            byte[] byteContent = new byte[in.available()];
            in.read(byteContent);
            in.close();
            sendMsg.setContent(CommonUtil.object2Json(byteContent));
            sendMsg.setFileName(selectFile.getName());
        }
    }

    //接受文件
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
                logger.error("接收文件失败",e);
            }

            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                out.write(CommonUtil.json2Object(msgFromServer.getContent(), byte[].class));
                out.close();
            } catch (IOException e) {
                logger.error("接收文件失败",e);
            }
        }
    }

    public void handleFile(MessageVO msg) {

        try {
            if (msg.getSender().equals(userName)) {
                document.insertString(document.getLength(), "发送了文件", fontAttribute.getAttributeSet());
                document.insertString(document.getLength(), "[" + msg.getFileName() + "]\n", new FontAttribute(1).getAttributeSet());
            }
            if (msg.getSender().equals(friendName)) {
                receieveFile(msg);
                document.insertString(document.getLength(), "接收到文件", fontAttribute.getAttributeSet());
                document.insertString(document.getLength(), "[" + msg.getFileName() + "]\n", new FontAttribute(1).getAttributeSet());
            }
        } catch (BadLocationException e) {
            logger.error(e);
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
            document.insertString(document.getLength(), "\n", fontAttribute.getAttributeSet());
        } catch (BadLocationException e) {
            logger.error(e);
        }
    }

    public void handleImage(MessageVO msg) {
        try {
            /*
             * 缓存地
             * */
            String path = System.getProperty(("java.io.tmpdir"));
            File file = new File(path + msg.getFileName());
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream out = new FileOutputStream(file);
            out.write(CommonUtil.json2Object(msg.getContent(), byte[].class));
            ImageIcon image = new ImageIcon(file.getAbsolutePath());
            image.setImage(image.getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT));
            receievePane.setCaretPosition(document.getLength());
            receievePane.insertIcon(image);
            document.insertString(document.getLength(), "\n", fontAttribute.getAttributeSet());
        } catch (IOException e) {
            logger.error(e);
        } catch (BadLocationException e) {
           logger.error(e);
        }
    }

    public void saveHistory() {
        String path = System.getProperty("user.dir");
        File file = new File(path + File.separator + "聊天室记录保存" + File.separator + userName + friendName + ".txt");
        this.path = file.getAbsolutePath();
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            String content = receievePane.getText();
            int index = content.lastIndexOf("[聊天记录]");
            if(index == -1 ){
                out.writeObject(content);
            }else{
                String store  = content.substring(index+6);
                out.writeObject(store);
            }
        } catch (IOException e) {
            logger.error(e);
        }

    }

    public void loadHistory() {
        String path = System.getProperty("user.dir");
        File file = new File(path + File.separator + "聊天室记录保存" + File.separator + userName + friendName + ".txt");
           if(file.exists()) {
               try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                   if ( file.length() > 7) {
                       document.insertString(document.getLength(), "[聊天记录]\n", fontAttribute.getAttributeSet());
                       document.insertString(document.getLength(), (String) in.readObject(), new FontAttribute(4).getAttributeSet());
                   } else {
                       JOptionPane.showMessageDialog(frame, "聊天记录为空", "提示信息", JOptionPane.INFORMATION_MESSAGE);
                   }
               } catch (BadLocationException e) {
                   logger.error(e);
               } catch (IOException e) {
                   logger.error(e);
               } catch (ClassNotFoundException e) {
                   logger.error(e);
               }
           }else{
               JOptionPane.showMessageDialog(frame, "首次交流不能查看，请再次登录查看或者聊天记录已被删除", "提示信息", JOptionPane.INFORMATION_MESSAGE);
           }
        }

    public void DelHistory() {
        String path = System.getProperty("user.dir");
        File file = new File(path + File.separator + "聊天室记录保存" + File.separator + userName + friendName + ".txt");
        if (file.exists()) {
            file.delete();
        } else {
            JOptionPane.showMessageDialog(frame, "文件不存在", "提示信息", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /*
     * 改变该文本段的格式
     * */
    public void changeTextPane(JTextPane jTextPane) {
        jTextPane.setParagraphAttributes(fontAttribute.getAttributeSet(), true);
    }

    public void sendPaneInfo(){
        String content = sendPane.getText().trim();
        if (!content.equals("") && !content.isEmpty()) {
            /*
             * 封装到VO中
             * */
            MessageVO msg2Server = new MessageVO();
            msg2Server.setType(MessageType.PRIVATE_CHAT.getIndex());
            msg2Server.setContentType(MessageType.TEXT.getIndex());
            msg2Server.setSender(userName);
            msg2Server.setReceiver(friendName);
            msg2Server.setContent(content);
            msg2Server.setTime(CommonUtil.getTime());

            /*
             * 发送私聊页面信息栏中信息
             * */
            out.println(CommonUtil.object2Json(msg2Server));

            /*
             * 信息显示在聊天记录域
             * 信息栏清空
             * */
            insertReceievePaneText(msg2Server);
            sendPane.setText("");
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    public PrivateChat(Connect2Server connect2Server, MessageVO msgfromServer) {

        /*
        * 采用UI界面风格
        * */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            logger.error(e);
        }

        /*
         * 打开通道
         * */
        try {
            this.out = new PrintStream(connect2Server.getOut(), true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }

        this.userName = msgfromServer.getReceiver();
        this.friendName = msgfromServer.getSender();

        /*
         * 初始化
         * */
        frame = new JFrame("与" + friendName + "聊天中");
        frame.setContentPane(PrivateChat);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setBounds(730, 340, 580, 550);
        frame.setVisible(true);

        jSplitP.setDividerLocation(330);

        /*
        * 流式布局
        * */
//        FlowLayout flowLayout = new FlowLayout();
//        flowLayout.setVgap(2);
//        flowLayout.setHgap(1);
//        flowLayout.setAlignment(FlowLayout.LEFT);
//        jDownOfNorth.setLayout(flowLayout);

        fontCB.setModel(new DefaultComboBoxModel(new String[]{"宋体", "黑体", "Dialog", "Gulim"}));
        fontSizeCB.setModel(new DefaultComboBoxModel(new String[]{"8", "10", "12", "14", "16", "18"}));
        fontCB.setSelectedIndex(3);
        fontSizeCB.setSelectedIndex(3);
        foreColor = foreBtn.getBackground();

        /*
         * 属性集
         * */
        this.fontAttribute = new FontAttribute();
        fontAttribute.setName((String) fontCB.getSelectedItem());
        fontAttribute.setSize(Integer.parseInt((String) fontSizeCB.getSelectedItem()));
        fontAttribute.setBold(boldBtn.isSelected());
        fontAttribute.setItalic(italicBtn.isSelected());
        fontAttribute.setForeColor(foreColor);

        /*
         * 历史栏文本格式
         * */
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

        /*
         * 监听按钮
         * */
        boldBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fontAttribute.setBold(boldBtn.isSelected());
                changeTextPane(sendPane);
            }
        });

        italicBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fontAttribute.setItalic(italicBtn.isSelected());
                changeTextPane(sendPane);
            }
        });

        fontCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fontAttribute.setName((String) fontCB.getSelectedItem());
                changeTextPane(sendPane);
            }
        });

        fontSizeCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fontAttribute.setSize(Integer.parseInt((String) fontSizeCB.getSelectedItem()));
                changeTextPane(sendPane);
            }
        });

        foreBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                foreColor = JColorChooser.showDialog(frame, "choose the font foreColor", foreBtn.getForeground());
                fontAttribute.setForeColor(foreColor);
                changeTextPane(sendPane);
            }
        });

        /*
         * 监听所发送的文本类型
         * */
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              sendPaneInfo();
            }
        });

        sendPane.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                 if(e.getKeyChar() == KeyEvent.VK_ENTER){
                    sendPaneInfo();
                 }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        sendExpress.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                expressframe.show(sendExpress, e.getX(), e.getY());
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

        sendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                MessageVO msg2Server = new MessageVO();
                msg2Server.setType(MessageType.PRIVATE_CHAT.getIndex());
                msg2Server.setContentType(MessageType.FILE.getIndex());
                msg2Server.setSender(userName);
                msg2Server.setReceiver(friendName);
                msg2Server.setTime(CommonUtil.getTime());
                try {
                    sendFile(msg2Server);
                } catch (IOException e1) {
                    logger.error(e);
                }

                /*
                 * 发送文件
                 * */
                if(msg2Server.getFileName()!=null){
                    out.println(CommonUtil.object2Json(msg2Server));
                    insertReceievePaneText(msg2Server);
                }
            }
        });

        sendImage.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            MessageVO msg2Server = new MessageVO();
                                            msg2Server.setType(MessageType.PRIVATE_CHAT.getIndex());
                                            msg2Server.setContentType(MessageType.IMAGE.getIndex());
                                            msg2Server.setSender(userName);
                                            msg2Server.setReceiver(friendName);
                                            msg2Server.setTime(CommonUtil.getTime());
                                            try {
                                                sendImage(msg2Server);
                                            } catch (IOException e1) {
                                                logger.error(e1);
                                            }

                                            /*
                                             * 发送图片
                                             * */
                                            if(msg2Server.getFileName()!=null ) {
                                                out.println(CommonUtil.object2Json(msg2Server));
                                                insertReceievePaneText(msg2Server);
                                            }
                                        }
                                    });

        loadHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadHistory();
            }
        });

        delHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DelHistory();
            }
        });
    }
}
