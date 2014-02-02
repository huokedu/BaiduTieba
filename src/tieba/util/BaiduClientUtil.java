package tieba.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import tieba.bean.Post;

/**
 * 百度客户端工具类
 * 
 * @author Vincent
 * 
 */
public class BaiduClientUtil {

	/**
	 * 获取页面中的参数tbs
	 * 
	 * @param url
	 * @param cookies
	 * @return
	 * @throws ParserException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String getTbsFromUrl(String urlStr, String cookies)
			throws ParserException, UnsupportedEncodingException, IOException {
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Cookie", cookies);
		Parser parser = new Parser(connection);
		NodeList scriptList = parser.parse(new TagNameFilter("script"));
		String tbs = BaiduClientUtil.getTbsFromScript(scriptList);
		return tbs;
	}

	/**
	 * 从js中获取参数tbs
	 * 
	 * @param html
	 * @return
	 * @throws ParserException
	 */
	public static String getTbsFromScript(NodeList scriptList)
			throws ParserException {
		// System.out.println("获取参数tbs");
		String tbs = null;
		// 获取相应的js段
		StringBuffer script = null;
		for (int i = 0; i < scriptList.size(); i++) {
			String html = scriptList.elementAt(i).toHtml();
			if (html.indexOf("'is_login':1,'tbs':'") != -1) {
				script = new StringBuffer(scriptList.elementAt(i).toHtml());
				break;
			}
		}

		// System.out.println(script);
		JSONObject jsonObject = new JSONObject(script.substring(
				script.indexOf("{"), script.indexOf("}") + 1));
		// System.out.println(jsonObject);
		tbs = jsonObject.getString("tbs");
		// System.out.println("tbs=" + tbs);
		return tbs;
	}

	/**
	 * 从js中获取参数fid
	 * 
	 * @param parser
	 * @return
	 */
	public static String getFidFromScript(NodeList scriptList) {
		// System.out.println("获取参数fid");
		String fid = null;
		StringBuffer script = null;
		// 获取相应的js段
		for (int i = 0; i < scriptList.size(); i++) {
			String html = scriptList.elementAt(i).toHtml();
			if (html.indexOf("ie:'utf-8',rich_text:'1',floor_num:") != -1) {
				script = new StringBuffer(scriptList.elementAt(i).toHtml());
				break;
			}
		}
		// System.out.println(script);

		StringBuffer dataStr = new StringBuffer(script.substring(
				script.indexOf(",data :") + 7, script.indexOf("},radarData :")));
		dataStr.delete(dataStr.indexOf("["), dataStr.indexOf("]") + 1);
		JSONObject jsonObject = new JSONObject(dataStr.toString());
		// System.out.println(jsonObject);
		// System.out.println("fid=" + jsonObject.getString("fid"));

		fid = jsonObject.getString("fid");
		return fid;
	}

	/**
	 * 从页面中获取帖子列表
	 * 
	 * @return
	 */
	public static List<Post> getPostList(String html) {
		Document document = Jsoup.parse(html);
		Elements elements = document.getElementsByAttributeValueMatching(
				"class", "j_thread_list clearfix.*");

		List<Post> postList = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			// System.out.println(elements.get(i).attr("data-field"));
			JSONObject jsonObject = new JSONObject(elements.get(i).attr(
					"data-field"));
			// 获取id
			long id = jsonObject.getLong("id");
			// 获取发帖者
			String authorName = jsonObject.getString("author_name");
			// 获取回复数量
			long replyNum = jsonObject.getLong("reply_num");
			// 获取标题
			String title = elements.get(i)
					.getElementsByAttributeValue("class", "j_th_tit").get(0)
					.text();
			// 获取内容
			String content = elements
					.get(i)
					.getElementsByAttributeValue("class",
							"threadlist_abs threadlist_abs_onlyline").get(0)
					.text();
			// 最后回复者
			String lastReplyer = elements.get(i)
					.getElementsByAttributeValue("class", "j_user_card").get(0)
					.text();
			// 最后回复时间
			String lastReplyTime = elements
					.get(i)
					.getElementsByAttributeValue("class",
							"threadlist_reply_date j_reply_data").get(0).text();

			Post post = new Post(id, authorName, replyNum, title, content,
					lastReplyer, lastReplyTime);
			postList.add(post);
		}

		return postList;
	}

	/**
	 * 保存所有信息到本地
	 */
	public static void saveInfo() {

	}

	/**
	 * 读取信息
	 */
	public static void readInfo() {

	}

}
