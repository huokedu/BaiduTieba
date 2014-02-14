package tieba.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.jws.soap.SOAPBinding.Use;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import tieba.BaiduClient;
import tieba.bean.TieBa;
import tieba.bean.UserInfo;

public class LoginFrame extends JFrame {

	private JTextField account = new JTextField("13822192563", 15);
	private JPasswordField password = new JPasswordField("century", 15);
	private JTextField verifyCode = new JTextField(6);
	private JLabel verifyCodeImage = new JLabel();

	private JButton login = new JButton("登录");
	private JButton exit = new JButton("退出");

	private BaiduClient baiduClient;

	public LoginFrame() throws Exception {
		// TODO 自动生成的构造函数存根
		baiduClient = new BaiduClient(account.getText(), password.getText(),
				BaiduClient.PHONENUMBERLOGIN);
		initUi();
		initListener();
	}

	private void initUi() {
		setSize(300, 180);
		setTitle("百度贴吧");
		setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel();

		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();

		mainPanel.setLayout(new GridLayout(3, 1));
		mainPanel.add(panel1);
		mainPanel.add(panel2);
		mainPanel.add(panel3);

		JLabel label1 = new JLabel("帐号：");
		JLabel label2 = new JLabel("密码：");
		JLabel label3 = new JLabel("验证码：");

		panel1.add(label1);
		panel1.add(account);
		panel2.add(label2);
		panel2.add(password);
		panel3.add(label3);
		panel3.add(verifyCode);
		panel3.add(verifyCodeImage);

		verifyCode.setVisible(false);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.add(login);
		buttonPanel.add(exit);

		add(mainPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

	}

	private void initListener() {
		login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				try {
					baiduClient.setVerifyCode(verifyCode.getText());

					Map<String, String> map = baiduClient.login();
					// 若需要验证码
					if (!map.get("err_no").equals("0")) {

						verifyCode.setVisible(true);

						verifyCodeImage.setIcon(new ImageIcon(baiduClient
								.getCaptcha(map.get("codeString"))));
						System.out.println("登录失败");
						return;
					}

					System.out.println("登录成功");

					List<TieBa> tieBas = baiduClient.getTieBaList();

					for (int i = 0; i < tieBas.size(); i++)
						baiduClient.signIn(tieBas.get(i).getName());

					baiduClient.getUserInfo();

				} catch (Exception e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}

			}
		});
	}

	public static void main(String[] args) throws Exception {
		LoginFrame loginFrame = new LoginFrame();
		loginFrame.setVisible(true);
	}
}
