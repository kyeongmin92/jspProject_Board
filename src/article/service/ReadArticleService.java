package article.service;

import java.sql.Connection;
import java.sql.SQLException;

import article.dao.ArticleContentDao;
import article.dao.ArticleDao;
import article.model.Article;
import article.model.ArticleContent;
import jdbc.connection.ConnectionProvider;

public class ReadArticleService {
	
	private ArticleDao articleDao = new ArticleDao();
	private ArticleContentDao contentDao = new ArticleContentDao();
	
	public ArticleData getArticle(int articleNum, boolean increaseReadCount) {
		
		try(Connection conn = ConnectionProvider.getConnection()){
			
			//article 테이블에서 지정한 번호의 Article 객체를 구한다
			Article article = articleDao.selectById(conn, articleNum);	
			//게시글 데이터가 존재하지 않으면 익셉션 발생
			if(article == null) {
				throw new ArticleNotFoundException();
			}
			
			//article_content테이블에서 지정한 번호의 ArticleContent 객체를 구한다
			ArticleContent content = contentDao.selectById(conn, articleNum);
			//게시글 내용 데이터가 존재하지 않으면 익셉션 발생
			if(content == null) {
				throw new ArticleContentNotFoundException();
			}
			//increaseReadCount가 true면 조회수 증가
			if(increaseReadCount) {
				articleDao.increaseReadCount(conn, articleNum);
			}
			//ArticleData 객체 리턴
			return new ArticleData(article, content);			
		} catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
