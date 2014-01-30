package tieba;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import tieba.bean.Post;
import tieba.util.HtmlUtil;
import tieba.util.HttpUtil;

/**
 * 百度客户端类
 * 
 * @author vincent
 * 
 */
public class BaiduClient {

	String baiduId;
	String token;

	String stoken;
	String ptoken;
	String bduss;
	String ubi;
	String saveuserid;

	String tieBaUserType;
	String tieBaUId;

	boolean logined = false;

	/**
	 * 帐号
	 */
	String userName;
	/**
	 * 密码
	 */
	String password;

	public BaiduClient(String userName, String password) {
		// TODO 自动生成的构造函数存根
		this.userName = userName;
		this.password = password;
	}

	/**
	 * 进入贴吧
	 * 
	 * @param tieBaName
	 * @return
	 * @throws Exception
	 */
	public List<Post> toTieBa(String tieBaName) throws Exception {
		URL url = new URL("http://tieba.baidu.com/f?ie=utf-8&kw="
				+ encode(tieBaName));
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("Cookie", createCookies());

		// 获取cookies并保存
		List<String> cookies = HttpUtil.getCookies(connection);
		tieBaUserType = HttpUtil.getCookieValue(cookies, "TIEBA_USERTYPE");
		tieBaUId = HttpUtil.getCookieValue(cookies, "TIEBAUID");

		// System.out.println(cookies);

		System.out.println("--------帖子列表---------");

		// 获取帖子列表
		String html = HtmlUtil.getRespondContent(connection);
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

		for (int i = 0; i < postList.size(); i++) {
			System.out.println(postList.get(i));
		}

		// 进入第一贴
		long postId = postList.get(0).getId();
		System.out.println("------进入帖子(" + postId + ")------");
		toPost(tieBaName, postId);

		// 获取首页回复为0的帖子 并自动回复
		// for (int i = 0; i < postList.size(); i++) {
		// Post post = postList.get(i);
		//
		// if (post.getReplyNum() == 0)
		// toPost(tieBaName, post.getId());
		//
		// }

		return postList;
	}

	/**
	 * 根据帖子id进入帖子
	 * 
	 * @param postId
	 */
	public void toPost(String tieBaName, long postId) throws Exception {
		URL url = new URL("http://tieba.baidu.com/p/" + postId);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Cookie", createCookies());

		Parser parser = new Parser(connection);
		NodeList nodeList = parser.parse(new TagNameFilter("script"));
		// 获取含有fid的js
		StringBuffer script1 = null;
		for (int i = 0; i < nodeList.size(); i++)
			if (nodeList.elementAt(i).toHtml().indexOf("fid") != -1) {
				script1 = new StringBuffer(nodeList.elementAt(i).getChildren()
						.toHtml());
				break;
			}
		// System.out.println(script1);

		StringBuffer dataStr = new StringBuffer(script1.substring(
				script1.indexOf(",data :") + 7,
				script1.indexOf("},radarData :")));
		dataStr.delete(dataStr.indexOf("["), dataStr.indexOf("]") + 1);
		JSONObject dataJson = new JSONObject(dataStr.toString());
		System.out.println(dataJson);
		System.out.println("fid=" + dataJson.getString("fid"));
		System.out.println("tid=" + dataJson.getString("tid"));
		System.out.println("floor_num=" + dataJson.getString("floor_num"));

		String fid = dataJson.getString("fid");

		// 获取含有uname的js(含有tbs)
		StringBuffer script2 = null;
		for (int i = 0; i < nodeList.size(); i++)
			if (nodeList.elementAt(i).toHtml().indexOf("uname") != -1) {
				script2 = new StringBuffer(nodeList.elementAt(i).getChildren()
						.toHtml());
				break;
			}

		JSONObject tbsJason = new JSONObject(script2.substring(
				script2.indexOf("{"), script2.indexOf("}") + 1));
		System.out.println(tbsJason);
		String tbs = tbsJason.getString("tbs");
		System.out.println("tbs=" + tbs);

		// 要回复的内容
		StringBuffer content = new StringBuffer();
		content.append("这是2楼?\r\n");
		content.append("自动回复时间:" + new Date().toString() + "\r\n");
		content.append("User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36\n");

		postMessage(tieBaName, postId, content.toString(), fid, tbs);
	}

	/**
	 * 根据帖子id回复帖子
	 * 
	 * @param postId
	 * @param msg
	 */
	public boolean postMessage(String kw, long postId, String content,
			String fid, String tbs) throws Exception {
		URL url = new URL("http://tieba.baidu.com/f/commit/post/add");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Cookie", createCookies());
		// 构造请求正文
		StringBuffer requestContent = new StringBuffer();
		requestContent.append("ie=utf-8");
		requestContent.append("&kw=" + encode(kw)); // 贴吧名
		requestContent.append("&fid=" + fid);
		requestContent.append("&tid=" + postId);// 帖子id
		requestContent.append("&vcode_md5=");
		requestContent.append("&floor_num=0");// 这个参数的值不检测
		requestContent.append("&rich_text=1");
		requestContent.append("&tbs=" + tbs);
		requestContent.append("&content=" + content); // 回复的内容
		requestContent.append("&files=" + encode("[]"));
		requestContent.append("&mouse_pwd=");// 这个参数的值不检测
		requestContent.append("&mouse_pwd_t=" + getSystemTime());
		requestContent.append("&mouse_pwd_isclick=0");
		requestContent.append("&__type__=reply");

		PrintWriter writer = new PrintWriter(connection.getOutputStream());
		writer.write(requestContent.toString());
		writer.flush();

		JSONObject respondJson = new JSONObject(
				HtmlUtil.getRespondContent(connection));
		System.out.println(respondJson);

		if (respondJson.getInt("no") != 0) {
			System.out.println("发帖失败");
			return false;
		}
		System.out.println("发贴成功");
		return true;
	}

	/**
	 * 登录
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean login() throws Exception {
		// 登录前先获取baiduId,再通过baiduId获取token
		System.out.println("---------开始获取baiduId---------");
		getBaiduId();
		System.out.println("----------开始获取token----------");
		getToken();
		System.out.println("--------------开始登录---------------");

		URL url = new URL("https://passport.baidu.com/v2/api/?login");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Cookie", "BAIDUID=" + baiduId);
		// 构造请求正文
		StringBuffer requestContent = new StringBuffer();
		String staticPage = "https://passport.baidu.com/static/passpc-account/html/v3Jump.html";
		String u = "https://passport.baidu.com/";
		requestContent.append("staticpage=" + encode(staticPage));
		requestContent.append("&charset=UTF-8");
		requestContent.append("&token=" + token);
		requestContent.append("&tpl=pp");
		requestContent.append("&apiver=v3");
		requestContent.append("&tt=" + getSystemTime());
		requestContent.append("&codestring=");
		requestContent.append("&safeflg=0");
		requestContent.append("&u=" + encode(u));
		requestContent.append("&isPhone=false");
		requestContent.append("&quick_user=0");
		requestContent.append("&loginmerge=true");
		requestContent.append("&logintype=basicLogin");
		requestContent.append("&username=" + userName);
		requestContent.append("&password=" + password);
		requestContent.append("&verifycode=");
		requestContent.append("&mem_pass=on");
		requestContent.append("&ppui_logintime=18566");
		requestContent.append("&callback=parent.bd__cbs__woepyg");

		PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
		printWriter.write(requestContent.toString());
		printWriter.flush();

		// 获取并保存各种的cookie
		List<String> cookies = HttpUtil.getCookies(connection);
		stoken = HttpUtil.getCookieValue(cookies, "STOKEN");
		ptoken = HttpUtil.getCookieValue(cookies, "PTOKEN");
		bduss = HttpUtil.getCookieValue(cookies, "BDUSS");
		saveuserid = HttpUtil.getCookieValue(cookies, "SAVEUSERID");
		ubi = HttpUtil.getCookieValue(cookies, "UBI");

		System.out.println("cookies:" + cookies);

		String respondContent = HtmlUtil.getRespondContent(connection);
		System.out.println(respondContent);

		if (respondContent.indexOf("err_no") != -1)
			System.out.println(respondContent.substring(respondContent
					.indexOf("err_no")));

		if (respondContent.indexOf("err_no=0") == -1) {
			System.out.println("登录失败!");
			return false;
		}
		logined = true;
		System.out.println("登录成功!");
		return true;
	}

	/**
	 * 获取token
	 * 
	 * @throws Exception
	 */
	private void getToken() throws Exception {
		URL url = new URL(
				"https://passport.baidu.com/v2/api/?getapi&tpl=pp&apiver=v3&tt="
						+ getSystemTime()
						+ "&class=login&logintype=basicLogin&callback=bd__cbs__woepyg");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Cookie", "BAIDUID=" + baiduId);

		StringBuffer respondContent = new StringBuffer(
				HtmlUtil.getRespondContent(connection));

		String jsonStr = respondContent.subSequence(
				respondContent.indexOf("(") + 1,
				respondContent.lastIndexOf(")")).toString();
		JSONObject jsonObject = new JSONObject(jsonStr);

		token = jsonObject.getJSONObject("data").getString("token");

		System.out.println(jsonObject);
		System.out.println(token);

	}

	/**
	 * 获取baiduId
	 * 
	 * @return
	 * @throws Exception
	 */
	private void getBaiduId() throws Exception {
		URL url = new URL("http://www.baidu.com");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		List<String> cookies = HttpUtil.getCookies(connection);
		baiduId = HttpUtil.getCookieValue(cookies, "BAIDUID");

		System.out.println(cookies);
		System.out.println("BAIDUID=" + baiduId);
	}

	/**
	 * URL方式编码字符串
	 * 
	 * @param string
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String encode(String string) throws UnsupportedEncodingException {
		return URLEncoder.encode(string, "UTF-8");
	}

	/**
	 * 创建http请求所需要的cookie
	 * 
	 * @return
	 */
	private String createCookies() {
		return "BAIDUID=" + baiduId + ";UBI=" + ubi + ";SAVEUSERID="
				+ saveuserid + ";STOKEN=" + stoken + ";PTOKEN=" + ptoken
				+ ";BDUSS=" + bduss + ";TIEBA_USERTYPE=" + tieBaUserType
				+ ";TIEBAUID=" + tieBaUId;
	}

	/**
	 * 是否登录
	 * 
	 * @return
	 */
	public boolean isLogined() {
		return logined;
	}

	/**
	 * 获取系统时间
	 * 
	 * @return
	 */
	public String getSystemTime() {
		return "" + Calendar.getInstance().getTimeInMillis();
	}

	public static void main(String[] args) throws Exception {
		BaiduClient client = new BaiduClient("13652218916", "cwc19940302");
		client.login();
		System.out.println("--------进入贴吧--------");
		client.toTieBa("蒙其d小伟");

	}

}
