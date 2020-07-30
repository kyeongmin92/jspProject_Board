package article.service;

// 게시글 삭제에 필요한 게시글번호, 사용자 아이디
public class DeleteRequest {
	
	private String userId;
	private int articleNumber;
	
	public DeleteRequest(String userId, int articleNumber) {
		this.userId = userId;
		this.articleNumber = articleNumber;		
	}
	
	public String getUserId() {
		return userId;
	}

	public int getArticleNumber() {
		return articleNumber;
	}

	
}
