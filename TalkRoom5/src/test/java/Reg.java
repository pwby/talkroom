import javax.swing.*;
import java.util.Scanner;

/**
 * @description:
 * @author: pwby
 * @create: 2020-03-23 18:45
 **/
public class Reg {
	private JFrame jFrame;

	public Reg() {
		jFrame = new JFrame("测试");
		//JOptionPane.show;
//	String passwd= JOptionPane.showInputDialog(null, "请输入密码");
//	System.out.println(passwd);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jFrame.setVisible(true);
		String str = (String) JOptionPane.showInputDialog(null, "请输入密码：\n", "重置密码", JOptionPane.PLAIN_MESSAGE, null, null, null);
		System.out.println(str);
		if(str.trim().equals("")){
			System.out.println(true);
		}
	}


	public static void main(String args[]) {
new Reg();
    }
	

}
