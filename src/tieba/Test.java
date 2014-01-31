package tieba;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import tieba.bean.Post;

public class Test {

	public static void main(String[] args) throws Exception {
		// 要回复的内容
		StringBuffer content = new StringBuffer();
		content.append("自动回复<br>");
		content.append("时间:" + new Date().toString() + "<br>");
		content.append("User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36\n");

		String userName = "13652218916";
		String password = "cwc19940302";
		BaiduClient client = new BaiduClient(userName, password);
		client.login();
		
		client.getTieBaList();
		
//		String tieBaName = "java吧";
//
//		client.getSignInfo(tieBaName);
		// client.signIn(tieBaName);

		// List<Post> postList = client.toTieBa(tieBaName);
		// JSONObject jsonObject = client.toPost(tieBaName, postList.get(0)
		// .getId());
		// client.reply(tieBaName, postList.get(0).getId(), content.toString(),
		// jsonObject.getString("fid"), jsonObject.getString("tbs"));

	}
}
