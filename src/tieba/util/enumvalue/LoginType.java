package tieba.util.enumvalue;

/**
 * 登录类型枚举类
 * 
 * @author Vincent
 * 
 */
public enum LoginType {
	userNameLogin(0, "用户名登录"), PhoneLogin(1, "手机登录"), MailLogin(2, "邮箱登录");

	int value;
	String msg;

	LoginType(int value, String msg) {
		this.value = value;
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public int getValue() {
		return value;
	}

}
