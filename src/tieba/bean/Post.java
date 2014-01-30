package tieba.bean;

/**
 * 帖子实体类
 * 
 * @author vincent
 * 
 */
public class Post {
	/**
	 * 帖子id
	 */
	long id;
	/**
	 * 回复数量
	 */
	long replyNum;
	/**
	 * 发帖者
	 */
	String authorName;

	/**
	 * 最后回复者
	 */
	String lastReplyer;

	/**
	 * 标题
	 */
	String title;

	/**
	 * 1楼内容
	 */
	String content;

	/**
	 * 最后回复时间
	 */
	String lastReplyTime;

	public Post() {

	}

	public Post(Long id, String authorName, long replyNum) {
		// TODO 自动生成的构造函数存根
		this(id, authorName, replyNum, null, null, null, null);
	}

	public Post(Long id, String authorName, long replyNum, String title,
			String content, String lastReplyer, String lastReplyTime) {
		// TODO 自动生成的构造函数存根
		this.id = id;
		this.authorName = authorName;
		this.replyNum = replyNum;
		this.title = title;
		this.content = content;
		this.lastReplyer = lastReplyer;
		this.lastReplyTime = lastReplyTime;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setLastReplyer(String lastReplyer) {
		this.lastReplyer = lastReplyer;
	}

	public void setLastReplyTime(String lastReplyTime) {
		this.lastReplyTime = lastReplyTime;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public String getLastReplyer() {
		return lastReplyer;
	}

	public String getLastReplyTime() {
		return lastReplyTime;
	}

	public String getTitle() {
		return title;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setReplyNum(long replyNum) {
		this.replyNum = replyNum;
	}

	public long getReplyNum() {
		return replyNum;
	}

	public long getId() {
		return id;
	}

	public String getAuthorName() {
		return authorName;
	}

	@Override
	public String toString() {
		// TODO 自动生成的方法存根
		return "id=" + getId() + " 发帖者=" + getAuthorName() + " 回复数量="
				+ getReplyNum() + " 标题=" + getTitle() + " 1楼内容=" + getContent()
				+ " 最后回复者=" + getLastReplyer() + " 最后回复时间="
				+ getLastReplyTime();
	}

}
