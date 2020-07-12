package client.ui;
import client.entry.User;
import client.util.CommonUtil;
import client.vo.MessageType;
import client.vo.MessageVO;
import net.coobird.thumbnailator.Thumbnails;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

public class Reg extends JFrame{

    //界面组件
    private JPanel reg;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JButton modifyImage;
    private JTextField briefText;
    private JButton regBtn;
    private JLabel photoStick;
    private JPanel jp1;
    private JPanel jp2;
    private JLabel userName;
    private JLabel password;
    private JLabel confirmPassword;
    private JLabel brief;
    private JPasswordField confirmText;

    //与服务器建立连接
    private Connect2Server connect2Server;
    private Scanner in;
    private PrintStream out;
    private String qqPhoto;

    //点击注册按钮的标志
    public  int type = -1;

    //JPG图片过滤器
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
    public  Reg(Connect2Server connect2Server) {

        //Windows界面风格
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //组件设置
       this.setTitle("REG");
       this.setContentPane(reg);
       this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       this.setBounds(736,340,400,300);
       this.setResizable(false);
       this.setVisible(true);

       //Socket初始化
       this.connect2Server = connect2Server;
        try {
            in = new Scanner(connect2Server.getIn());
            out = new PrintStream(connect2Server.getOut(),true,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //监听图片修改按钮
        modifyImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = chooseImage();
                loadImage(str);
            }
        });
    }

    /*
    * 监听注册按钮
    * */
    public int actionListenr() {
        while (true) {
            regBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    type = MessageType.REG.getIndex();
                }
            });
            if (type == MessageType.REG.getIndex()) {
                break;
            }
        }
        return type;
    }

    /*
    * 获取用户所选择图像的路径
    * */
    public String chooseImage(){
        JFileChooser jfc = new JFileChooser((FileSystemView.getFileSystemView().getHomeDirectory()));
        jfc.setFileFilter(new JPGFileFilter());
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectFile = jfc.getSelectedFile();
            String path = selectFile.getAbsolutePath();
            return path;
        }else{
            return null;
        }
    }

    /*
    * 根据参数：路径加载图像
    * */
    public void loadImage(String srcFilePath){
        if(srcFilePath != null){
            ImageIcon imageIcon = new ImageIcon(srcFilePath);
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(123,123,Image.SCALE_DEFAULT));
            photoStick.setIcon(imageIcon);
            try {
                String path = System.getProperty(("java.io.tmpdir"));
                String destFilePath = path + Math.random()+".jpg";
                Thumbnails.of(srcFilePath).forceSize(26, 26).toFile(destFilePath);
                FileInputStream inFile = new FileInputStream(new File(destFilePath));
                byte[] byteContent = new  byte[inFile.available()];
                inFile.read(byteContent);
                this.qqPhoto = CommonUtil.object2Json(byteContent);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * 处理数据
    * */
    public int handleReg(){

        /*
        * 获取界面数据
        * */
        String userName  = userNameText.getText();
        String password = String.valueOf(passwordText.getPassword());
        String confirm = String.valueOf(confirmText.getPassword());

        /*
        * 确保用户名密码不为空
        * */
        if (!userName.trim().equals("") && !password.trim().equals("")) {
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "密码与确认密码不一致", "提示信息", JOptionPane.ERROR_MESSAGE);
            } else {
                String brief = briefText.getText();
                User user = new User();
                user.setUserName(userName);
                user.setPassword(password);
                user.setBrief(brief);
                user.setQqImage(qqPhoto);

                /*
                 * 将数据封装到VO 中
                 * */
                MessageVO msg2Client = new MessageVO();
                msg2Client.setType(MessageType.REG.getIndex());
                msg2Client.setContent(CommonUtil.object2Json(user));

                /*
                 * 发送数据
                 * */
                out.println(CommonUtil.object2Json(msg2Client));

                /*
                 * 解析服务器发送来的信息
                 * */
                if (in.hasNextLine()) {
                    String jsonStr = in.nextLine();
                    MessageVO msgfromServer = CommonUtil.json2Object(jsonStr, MessageVO.class);
                    if (msgfromServer.getType() == MessageType.REGSUCCED.getIndex()) {
                        JOptionPane.showMessageDialog(this, "注册成功", "提示信息", JOptionPane.INFORMATION_MESSAGE);
                        return MessageType.REGSUCCED.getIndex();
                    } else {
                        JOptionPane.showMessageDialog(this, "注册失败,该账号已被注册,再次尝试", "提示信息", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "注册失败", "提示信息", JOptionPane.INFORMATION_MESSAGE);
        }
        type = MessageType.REGFAILED.getIndex();
        return type;
    }
}
