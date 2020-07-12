package client;

import client.ui.Connect2Server;
import client.ui.Home;
import client.ui.Login;
import client.ui.Reg;
import client.vo.MessageType;


/**
 * @description:
 * @author: pwby
 * @create: 2020-03-31 09:51
 **/
public class ClientStart {

    public static void main(String[] args) {

        /*
         *  连接到服务器端
         **/
        Connect2Server connect2Server = new Connect2Server();


        /*
         * 主页面,登录页面,注册页面
         * */
        Reg reg = null;
        Login login = new Login();
        Home home = null;

        /*
         * 去往登录页面还是注册页面的标志
         * */
        int type = -1;

        /*
         * 处理登录页面
         * */
        while (type != MessageType.LOGINSUCCED.getIndex()) {

            /*
             * 监听注册/登录按钮
             * */
            type = login.actionListenr();


            /*
             *  登陆页面
             **/
            if (type == MessageType.LOGIN.getIndex()) {
                type = login.handleLogin(connect2Server);
                if (type == MessageType.LOGINSUCCED.getIndex()) {

                    login.dispose();
                    home = new Home(connect2Server);
                    break;
                }
            } else if (type == MessageType.REG.getIndex()) {
                break;
            }
        }


        /*
         * 处理注册页面
         * */
        if (type == MessageType.REG.getIndex()) {
            login.dispose();
            reg = new Reg(connect2Server);

        }
        while (type != MessageType.LOGINSUCCED.getIndex() && type != MessageType.REGSUCCED.getIndex()) {
            type = reg.actionListenr();
            if (type == MessageType.REG.getIndex()) {
                type = reg.handleReg();
                if (type == MessageType.REGSUCCED.getIndex()) {
                    reg.dispose();
                    home = new Home(connect2Server);
                    break;
                }
            }
        }

    }

}
