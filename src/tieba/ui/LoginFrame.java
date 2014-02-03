package tieba.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import tieba.BaiduClient;

public class LoginFrame extends JFrame {

	private JTextField account = new JTextField("13560457989");
	private JPasswordField password = new JPasswordField("cwc19940302");
	private JTextField verifycode = new JTextField();
	private JLabel verifycodeImage = new JLabel();

	private JButton login = new JButton("登录");
	private JButton exit = new JButton("退出");

	public LoginFrame() {
		// TODO 自动生成的构造函数存根
		initUi();
		initListener();
	}

	private void initUi() {
		setSize(300, 180);
		setTitle("百度贴吧");
		setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel();
		JPanel labelPanel = new JPanel();
		JPanel textFieldPanel = new JPanel();

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(labelPanel, BorderLayout.WEST);
		mainPanel.add(textFieldPanel, BorderLayout.CENTER);

		GridLayout gridLayout = new GridLayout(3, 1, 50, 5);

		labelPanel.setLayout(gridLayout);
		textFieldPanel.setLayout(gridLayout);

		labelPanel.add(new JLabel("帐号："));
		textFieldPanel.add(account);
		labelPanel.add(new JLabel("密码："));
		textFieldPanel.add(password);
		labelPanel.add(new JLabel("验证码："));
		textFieldPanel.add(verifycode);

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
					BaiduClient baiduClient = new BaiduClient(
							account.getText(), password.getText(),
							BaiduClient.PHONENUMBERLOGIN);
					Map<String, String> map = baiduClient.login();
					// 若需要验证码
					if (map.get("err_no").equals("257")) {
						verifycodeImage.setIcon(new ImageIcon(baiduClient
								.getCaptcha(map.get("codeString"))));
						return;
					}

				} catch (Exception e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}

			}
		});
	}

	public static void main(String[] args) {
		LoginFrame loginFrame = new LoginFrame();
		loginFrame.setVisible(true);
	}
}
