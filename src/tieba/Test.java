package tieba;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import tieba.bean.Post;
import tieba.bean.TieBa;

class T extends Thread {
	BaiduClient client;
	String tieBaName;

	public T(BaiduClient client, String tieBaName) throws Exception {
		// TODO 自动生成的构造函数存根
		this.client = client;
		this.tieBaName = tieBaName;
	}

	@Override
	public void run() {
		// TODO 自动生成的方法存根
		try {
			client.signIn(tieBaName);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

}

public class Test {

	public static void main(String[] args) throws Exception {
		
		System.out.println(new String("\u6210\u529f"));
		
		// 要回复的内容
		StringBuffer content = new StringBuffer();
		content.append("2楼");
		// content.append("时间:" + new Date().toString() + "<br>");
		// content.append("User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36\n");

		String userName = "13652218916";
		String password = "cwc19940302";
		BaiduClient client = new BaiduClient(userName, password,
				BaiduClient.PHONENUMBERLOGIN);
		client.login();

		if (!client.isLogined())
			return;
		String tieBaName = "蒙其d小伟";
		List<Post> posts = client.toTieBa(tieBaName);

		JSONObject json = client.toPost(tieBaName, posts.get(0).getId());

		client.replyPost(tieBaName, posts.get(0).getId(), content.toString(),
				json.getString("fid"), json.getString("tbs"));

		// List<TieBa> tieBas = client.getTieBaList();
		// for (int i = 0; i < tieBas.size(); i++)
		// client.getSignInfo(tieBas.get(i).getName());

	}
}
