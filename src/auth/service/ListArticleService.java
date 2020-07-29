package auth.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import article.dao.ArticleDao;
import article.model.Article;
import jdbc.connection.ConnectionProvider;

public class ListArticleService {
	
	private ArticleDao articleDao = new ArticleDao();
	private int size = 10;
	
	public ArticlePage getArticlePage(int pageNum) {
		try(Connection conn = ConnectionProvider.getConnection()){
			
			//전체 게시글의 개수를 구한다
			int total = articleDao.selectCount(conn);
			
			//pageNum에 해당하는 게시글 목록을 구한다
			//articleDao.select의 두번째 파라미터는 조회할 레코드의 시작 행
			List<Article> content = articleDao.select(conn, (pageNum -1) * size, size);
			
			//ArticlePage 객체 리턴
			return new ArticlePage(total, pageNum, size, content);
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
