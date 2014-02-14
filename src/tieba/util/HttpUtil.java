package tieba.util;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * http工具类
 * 
 * @author vincent
 * 
 */
public class HttpUtil {
	/**
	 * 获取cookie值
	 * 
	 * @param string
	 * @return
	 */
	public static String getCookieValue(String string) {
		StringBuffer id = new StringBuffer(string);
		id.delete(0, id.indexOf("=") + 1);
		id.delete(id.indexOf(";"), id.length());
		return id.toString();

	}

	/**
	 * 根据cookie名获取cookie值
	 * 
	 * @param cookies
	 * @param cookieName
	 * @return
	 */
	public static String getCookieValue(List<String> cookies, String cookieName) {
		for (int i = cookies.size() - 1; i >= 0; i--)
			if (cookies.get(i).indexOf(cookieName) != -1) {
				String cookie = cookies.get(i);
				String value = cookie.substring(cookie.indexOf("=") + 1,
						cookie.indexOf(";"));
				return value;

			}
		return null;
	}

	/**
	 * 获取http响应中的所有cookie
	 * 
	 * @param connection
	 * @return
	 */
	public static List<String> getCookies(HttpURLConnection connection) {
		Map<String, List<String>> map = connection.getHeaderFields();
		List<String> cookies = map.get("Set-Cookie");
		return cookies;
	}

}