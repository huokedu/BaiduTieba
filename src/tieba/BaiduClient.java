package tieba;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tieba.bean.Post;
import tieba.bean.TieBa;
import tieba.bean.UserInfo;
import tieba.util.BaiduClientUtil;
import tieba.util.HtmlUtil;
import tieba.util.HttpUtil;

/**
 * 百度客户端类
 * 
 * @author vincent
 * 
 */
public class BaiduClient {
	/**
	 * sessionId
	 */
	private String baiduId;
	private String token;

	private String stoken;
	private String ptoken;
	private String bduss;
	private String ubi;
	private String saveuserid;

	private String tieBaUserType;
	private String tieBaUId;

	private boolean logined = false;

	/**
	 * 登录类型(用户名、手机、邮箱登录)
	 */
	private int loginType;

	public static final int USERNAMELOGIN = 1;// 用户名登录
	public static final int PHONENUMBERLOGIN = 2;// 手机号码登录
	public static final int MAILLOGIN = 3;// 邮箱登录
	/**
	 * 用户信息
	 */
	private UserInfo userInfo = new UserInfo();

	public BaiduClient(String account, String password, int loginType)
			throws Exception {
		// TODO 自动生成的构造函数存根
		userInfo.setAccount(account);
		userInfo.setPassword(password);
		this.loginType = loginType;

		switch (loginType) {
		case USERNAMELOGIN:
			userInfo.setUserName(account);
			break;
		case PHONENUMBERLOGIN:
			userInfo.setPhoneNumber(account);
			break;
		case MAILLOGIN:
			userInfo.setMail(account);
			break;
		default:
			throw new Exception("loginType error");

		}

	}

	/**
	 * 获取关注贴吧列表
	 * 
	 * @return
	 * @throws IOException
	 * @throws ParserException
	 */
	public List<TieBa> getTieBaList() throws IOException, ParserException {
		List<TieBa> tieBaList = new ArrayList<>();
		URL url = new URL("http://tieba.baidu.com/f/like/mylike");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Cookie", createCookies());

		Document document = Jsoup.parse(HtmlUtil.getRespondContent(connection));
		Elements elements = document.getElementsByTag("a");

		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			if (!element.attr("class").equals(""))
				continue;
			TieBa tieBa = new TieBa();
			tieBa.setName(element.attr("title"));
			tieBaList.add(tieBa);
			System.out.println(tieBa.getName());
		}

		return tieBaList;
	}

	/**
	 * 获取签到信息
	 * 
	 * @param tieBaName
	 * @return
	 * @throws IOException
	 */
	public JSONObject getSignInfo(String tieBaName) throws IOException {
		URL url = new URL("http://tieba.baidu.com/sign/loadmonth?kw="
				+ encode(tieBaName) + "&ie=utf-8&t=0.5714449905790389");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Cookie", createCookies());
		JSONObject respondJson = new JSONObject(
				HtmlUtil.getRespondContent(connection));

		System.out.println(respondJson);

		JSONObject data = respondJson.getJSONObject("data");
		JSONObject signUserInfo = data.getJSONObject("sign_user_info");

		System.out.println("连续签到=" + signUserInfo.getInt("sign_keep"));
		System.out.println("共签到=" + signUserInfo.getInt("sign_total"));
		System.out.println("今日签到排名=" + signUserInfo.getInt("rank"));
		return respondJson;
	}

	/**
	 * 签到
	 * 
	 * @param tieBaName
	 * @throws Exception
	 */
	public boolean signIn(String tieBaName) throws Exception {
		System.out.println("---------开始签到(" + tieBaName + ")---------");
		// 获取页面中的参数tbs(签到要用)
		String tbs = BaiduClientUtil.getTbsFromUrl(
				"http://tieba.baidu.com/f?ie=utf-8&kw=" + encode(tieBaName),
				createCookies());

		// 签到
		URL url = new URL("http://tieba.baidu.com/sign/add");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Cookie", createCookies());
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		// 构造请求正文
		StringBuffer requestContent = new StringBuffer();
		requestContent.append("ie=utf-8");
		requestContent.append("&kw=" + encode(tieBaName));
		requestContent.append("&tbs=" + tbs);

		PrintWriter writer = new PrintWriter(connection.getOutputStream());
		writer.write(requestContent.toString());
		writer.flush();

		JSONObject respondJson = new JSONObject(
				HtmlUtil.getRespondContent(connection));

		System.out.println(respondJson);
		if (respondJson.getInt("no") != 0) {
			System.out.println("签到失败");
			return false;
		}
		System.out.println("签到成功");
		return true;
	}

	/**
	 * 进入贴吧
	 * 
	 * @param tieBaName
	 * @return
	 * @throws Exception
	 */
	public List<Post> toTieBa(String tieBaName) throws Exception {
		System.out.println("----------进入贴吧(" + tieBaName + ")---------");
		URL url = new URL("http://tieba.baidu.com/f?ie=utf-8&kw="
				+ encode(tieBaName) + "&pn=0");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.addRequestProperty("Cookie", createCookies());

		// 获取cookies并保存
		List<String> cookies = HttpUtil.getCookies(connection);
		tieBaUserType = HttpUtil.getCookieValue(cookies, "TIEBA_USERTYPE");
		tieBaUId = HttpUtil.getCookieValue(cookies, "TIEBAUID");

		// System.out.println(cookies);
		System.out.println("--------帖子列表(" + tieBaName + "吧)---------");

		String html = HtmlUtil.getRespondContent(connection);

		// 获取帖子列表
		List<Post> postList = BaiduClientUtil.getPostList(html);
		for (int i = 0; i < postList.size(); i++) {
			System.out.println(postList.get(i));
		}

		return postList;
	}

	// fid即forum_id(一个贴吧对应一个)
	/**
	 * 根据帖子id进入帖子 返回参数fid和tbs
	 * 
	 * @param tieBaName
	 * @param postId
	 * @return
	 * @throws Exception
	 */
	public JSONObject toPost(String tieBaName, long postId) throws Exception {
		System.out.println("---------进入帖子----------");
		URL url = new URL("http://tieba.baidu.com/p/" + postId);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Cookie", createCookies());

		Parser parser = new Parser(connection);
		NodeList scriptList = parser.parse(new TagNameFilter("script"));
		// 获取网页中的参数fid和tbs(发帖要用)
		String fid = BaiduClientUtil.getFidFromScript(scriptList);
		String tbs = BaiduClientUtil.getTbsFromScript(scriptList);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("fid", fid);
		jsonObject.put("tbs", tbs);
		jsonObject.put("tid", postId);

		return jsonObject;
	}

	/**
	 * 根据帖子id回复帖子
	 * 
	 * @param postId
	 * @param msg
	 */
	public boolean replyPost(String tieBaName, long postId, String content,
			String fid, String tbs) throws Exception {
		URL url = new URL("http://tieba.baidu.com/f/commit/post/add");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Cookie", createCookies());
		// 构造请求正文
		StringBuffer requestContent = new StringBuffer();
		requestContent.append("ie=utf-8");
		requestContent.append("&kw=" + encode(tieBaName)); // 贴吧名
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
			System.out.println("回复失败");
			return false;
		}
		System.out.println("回复成功");
		return true;
	}

	/**
	 * 获取用户信息
	 * 
	 * @throws IOException
	 * @throws ParserException
	 */
	public void getUserInfo() throws IOException, ParserException {
		URL url = new URL("http://tieba.baidu.com/f/user/json_userinfo");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Cookie", createCookies());

		JSONObject respondJson = new JSONObject(
				HtmlUtil.getRespondContent(connection));
		JSONObject dataJson = respondJson.getJSONObject("data");
		String userName = dataJson.getString("user_name_show");
		String userPortrait = dataJson.getString("user_portrait");

		userInfo.setUserName(userName);
		userInfo.setUserPortrait(userPortrait);

		System.out.println("用户名=" + userInfo.getUserName());
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
		requestContent.append("&verifycode=");
		requestContent.append("&mem_pass=on");
		requestContent.append("&ppui_logintime=18566");
		requestContent.append("&callback=parent.bd__cbs__woepyg");
		requestContent.append("&password=" + userInfo.getPassword());
		requestContent.append("&username=" + userInfo.getAccount());

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
		// 登录成功则获取用户信息
		getUserInfo();
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
	 * 获取登录类型
	 * 
	 * @return
	 */
	public int getLoginType() {
		return loginType;
	}

	/**
	 * 获取系统时间
	 * 
	 * @return
	 */
	public String getSystemTime() {
		return "" + Calendar.getInstance().getTimeInMillis();
	}

}
