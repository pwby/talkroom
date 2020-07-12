package client.ui;

import client.util.CommonUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;
public class Connect2Server {
    private static final String IP;   //IP
    private static final int PORT;    //端口号

    //加载资源文件，初始化端口号和资源文件
    static {
        Properties properties = CommonUtil.loadProperties("socket.properties");
        IP = properties.getProperty("address");
        PORT = Integer.parseInt(properties.getProperty("port"));
        Properties logger = CommonUtil.loadProperties("log4j.properties");
    }

    private Socket client;            //Socket
    private InputStream in;           //输入流
    private OutputStream out;         //输出流
    private Logger logger = Logger.getLogger(Connect2Server.class);     //日志

    //初始化Sokcet
    public Connect2Server() {
        try {
            client = new Socket(IP, PORT);
            in = client.getInputStream();
            out = client.getOutputStream();
        } catch (IOException e) {
            logger.error("与服务器建立连接失败", e);
            e.printStackTrace();
        }
    }

    public InputStream getIn() {
        return in;
    }

    public OutputStream getOut() {
        return out;
    }

    public Socket getClient() {
        return client;
    }

}
