package tieba;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import tieba.bean.Post;

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

		client.getTieBaList();

		// String tieBaName = "c";
		//
		// List<Post> postList = client.toTieBa(tieBaName);
		// JSONObject jsonObject = client.toPost(tieBaName, postList.get(0)
		// .getId());
		// client.replyPost(tieBaName, postList.get(0).getId(),
		// content.toString(), jsonObject.getString("fid"),
		// jsonObject.getString("tbs"));
		//
		// System.out.println(jsonObject.get("fid"));

		String[] tieBaNames = { "华南农业大学", "java", "c语言", "行尸走肉第三季", "进击的巨人",
				"李毅", "海贼王", "火影忍者", "越狱", "c++", "蒙其d小伟", "汇编", "javascript",
				"j2ee", "golang", "ubuntu", "linux", "c#" };

	}

}
