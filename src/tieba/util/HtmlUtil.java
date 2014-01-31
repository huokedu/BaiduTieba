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

}
