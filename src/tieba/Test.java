package tieba;

public class Test {

	public static void main(String[] args) throws Exception {
		BaiduClient client = new BaiduClient("13652218916", "cwc19940302");
		client.login();
		System.out.println("--------进入贴吧--------");
		client.toTieBa("蒙其d小伟");

		// String string =
		// "\\n\\n_.Module.use('ihome/component/UserVisitCard',{'uname':'蒙其D小伟','is_login':1,'tbs':'4ace9ff3f4b4e00c1391071405'});\\n\\n";
		// System.out.println(string
		// .matches("\\n*.+'uname':.+,'is_login':.+,'tbs':.+\\n*"));

	}
}
