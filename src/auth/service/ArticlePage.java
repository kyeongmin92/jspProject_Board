package auth.service;

import java.util.List;

import article.model.Article;

public class ArticlePage {
	
	private int total; 				// 전체 게시글 개수	
	private int currentPage;		// 사용자가 요청한 페이지 번호
	private List<Article> content;	// 화면에 출력할 게시글 목록
	private int totalPages;			// 전체 페이지 개수
	private int startPage;			// 페이지 이동 링크의 시작번호
	private int endPage;			// 끝번호
	
	//게시글 데이터와 페이징 관련 정보를 담을 클래스
	public ArticlePage(int total, int currentPage, int size, List<Article> content) {
		this.total = total;
		this.currentPage = currentPage;
		this.content = content;
		
		//전체 게시글 개수가 0개면 
		if(total == 0) {
			totalPages = 0;	// 전체 페이지 개수 0
			startPage = 0;	// 시작 번호 0
			endPage = 0;	// 끝 번호 0으로 할당				
		} else {
			// 한페이지에 보여줄 게시글 개수를 size로 전달 받음
			
			//전체 게시글 개수를 size로 나눈 값을 페이지 개수로 사용 
			totalPages = total / size;
			//전페 게시글 개수를 size로 나눈 나머지가 0 보다 크면 페이지 수를 1 증가시킴
			if(total % size > 0) {
				totalPages++;
			}
			
			/*int modVal = currentPage % 5;
			startPage = currentPage / 5 * 5 + 1;
			if(modVal == 0) startPage -= 5;*/		
			// 화면 하단에 보여줄 페이지 이동 링크의 시작 페이지 번호 
			startPage = (currentPage-1) / 5 * 5 + 1;
			
			//화면 하단에 보여줄 페이지 이동 링크의 끝 페이지 번호 (5개의 페이지씩 이동 링크 출력)
			endPage = startPage + 4;
//			if(endPage > totalPages) endPage = totalPages;
			endPage = Math.min(endPage, totalPages); 	//Math.min()함수는 주어진 숫자 중 가장 작은 값을 반환
		}
	}
	
	public int getTotal() {
		return total;
	}
	
	public boolean hasNoArticles() {
		return total == 0;		
	}
	
	public boolean hasArticles() {
		return total > 0;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public int getTotalPages() {
		return totalPages;
	}
	
	public List<Article> getContent(){
		return content;
	}
	
	public int getStartPage() {
		return startPage;
	}
	
	public int getEndPage() {
		return endPage;
	}

}
