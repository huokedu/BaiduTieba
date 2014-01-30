package tieba.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

/**
 * html工具类
 * 
 * @author vincent
 * 
 */
public class HtmlUtil {
	public static void printHtml(HttpURLConnection connection)
			throws UnsupportedEncodingException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "GB2312"));

		String string = null;
		while ((string = reader.readLine()) != null) {
			System.out.println(string);
		}
		reader.close();
		connection.disconnect();
	}

	/**
	 * 获取http响应内容
	 * 
	 * @param connection
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String getRespondContent(HttpURLConnection connection)
			throws UnsupportedEncodingException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "GB2312"));

		StringBuffer stringBuffer = new StringBuffer();
		String string = null;
		while ((string = reader.readLine()) != null) {
			stringBuffer.append(string);
		}
		reader.close();
		connection.disconnect();
		return stringBuffer.toString();
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
		String tbs = null;
		// 获取相应的js段
		StringBuffer script = null;
		for (int i = 0; i < scriptList.size(); i++) {
			System.out.println(Math.random());
			String html = scriptList.elementAt(i).toHtml();
			if (html.matches("[\\s\\S]*.+'uname':.+,'is_login':.+,'tbs':.+[\\s\\S]*")) {
				script = new StringBuffer(scriptList.elementAt(i).toHtml());
				break;
			}
		}

		System.out.println(script);
		JSONObject jsonObject = new JSONObject(script.substring(
				script.indexOf("{"), script.indexOf("}") + 1));
		System.out.println(jsonObject);
		tbs = jsonObject.getString("tbs");
		System.out.println("tbs=" + tbs);
		return tbs;
	}

	/**
	 * 从js中获取参数fid
	 * 
	 * @param parser
	 * @return
	 */
	public static String getFidFromScript(NodeList scriptList) {
		String fid = null;
		StringBuffer script = null;
		// 获取相应的js段
		for (int i = 0; i < scriptList.size(); i++) {
			System.out.println(Math.random());
			String html = scriptList.elementAt(i).toHtml();
			if (html.matches("[\\s\\S]*.+kw:.+ie:'utf-8',rich_text:.+,floor_num:.+fid:.+[\\s\\S]*")) {
				script = new StringBuffer(scriptList.elementAt(i).getChildren()
						.toHtml());
				break;
			}
		}
		System.out.println(script);

		StringBuffer dataStr = new StringBuffer(script.substring(
				script.indexOf(",data :") + 7, script.indexOf("},radarData :")));
		dataStr.delete(dataStr.indexOf("["), dataStr.indexOf("]") + 1);
		JSONObject jsonObject = new JSONObject(dataStr.toString());
		System.out.println(jsonObject);
		System.out.println("fid=" + jsonObject.getString("fid"));

		fid = jsonObject.getString("fid");
		return fid;
	}

}
