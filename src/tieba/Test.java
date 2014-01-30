package tieba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.htmlparser.beans.StringBean;
import org.json.JSONArray;
import org.json.JSONObject;

public class Test {

	public static void main(String[] args) throws Exception {

		StringBuffer string = new StringBuffer("{kw:'蒙其d小伟',ie:'utf-8',rich_text:'1',floor_num:\"15\",fid:'9831282',tbs : PageData[\"tbs\"],tid:'2839642768',lp_type: thread_topic_type,lp_sub_type:thread_topic_subtype}");
		
		string.delete(string.indexOf("["), string.indexOf("]")+1);
		System.out.println(string);
		
		
		 JSONObject object = new JSONObject(string.toString());
		 System.out.println(object);
		 System.out.println(object.getString("floor_num"));
		 System.out.println(object.getString("tid"));
		 System.out.println(object.getString("tbs"));
	}
}
