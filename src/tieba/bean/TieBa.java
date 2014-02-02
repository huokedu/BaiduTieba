package tieba.bean;

/**
 * 贴吧类
 * 
 * @author Vincent
 * 
 */
public class TieBa {
	/**
	 * 贴吧名
	 */
	String name;
	/**
	 * 贴吧id
	 */
	String forumId;

	public String getForumId() {
		return forumId;
	}

	public String getName() {
		return name;
	}

	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	public void setName(String name) {
		this.name = name;
	}
}
