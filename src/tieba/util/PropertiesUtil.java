package tieba.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 键值对工具类
 * 
 * @author Vincent
 * 
 */
public class PropertiesUtil {

	public static Map<String, String> parse(String kvStr) {
		Map<String, String> map = new HashMap<String, String>();

		String[] kvPairs = kvStr.split("&");
		for (int i = 0; i < kvPairs.length; i++) {
			String[] kvPair = kvPairs[i].split("=");
			String key = kvPair[0];
			String value = kvPair.length == 2 ? kvPair[1] : "";

			map.put(key, value);

		}

		return map;
	}

}
