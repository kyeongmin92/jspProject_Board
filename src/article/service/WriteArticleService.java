package article.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import article.dao.ArticleContentDao;
import article.dao.ArticleDao;
import article.model.Article;
import article.model.ArticleContent;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class WriteArticleService {
	
	private ArticleDao articleDao = new ArticleDao();
	private ArticleContentDao contentDao = new ArticleContentDao();
	
	//write메서드는 WrtieRequest 타입의 req 파라미터를 이용해서 게시글 등록,
	//결과로 게시글 번호 리턴
	public Integer write(WriteRequest req) {
		Connection conn = null;
		
		try {
			conn = ConnectionProvider.getConnection();
			//트랜잭션 시작
			conn.setAutoCommit(false);
			
			//WriteRequest로 부터 Article 객체 생성, insert 쿼리를 실행해야 id를 알 수 있으므로
			// toArticle에서 Article 객체를 생성할 때 number 값으로 null 전달
			Article article = toArticle(req);
			//ArticleDao의 insert메서드를 실행하고 그결과를 savedArticle에 할당
			//데이터를 삽입한 경우 null이 아니고, article 테이블에 추가한 데이터의 주요 키값을 number로 갖는다
			Article savedArticle = articleDao.insert(conn, article);
			//savedArticle이 null이면 article 테이블에 삽입한 레코드가 없단 뜻, 익셉션 발생
			if(savedArticle == null) {
				throw new RuntimeException("fail to insert article");
			}
			
			//AricleContent 객체 생성
			ArticleContent content = new ArticleContent(
					//savedArticle의 게시글 번호를 사용
					savedArticle.getNumber(),					
					// 위에서 (34행) 삽입한 데이터와 동일한 번호를 값으로 갖는 ArticleContent객체 생성
					req.getContent(), req.getFileName());
			
			//ArticleContentDao의 insert메서드를 실행해서 article_content테이블에 데이터 삽입
			ArticleContent savedContent = contentDao.insert(conn, content);
			//ArticleContentDao의 insert 실행 결과가 null이면 삽입된 데이터가 없는 것 ->익셉션 발생
			if(savedContent == null) {
				throw new RuntimeException("fail to insert article_content");
			}
			
			// 트랜잭션 커밋
			conn.commit();
			// 새로 추가한 게시글 번호 리턴
			return savedArticle.getNumber();
			
			//익셉션 발생하면 트랜잭션 리턴
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} catch(RuntimeException e) {
			JdbcUtil.rollback(conn);
			throw e;
		} finally {
			JdbcUtil.close(conn);
		}
	}
	
	private Article toArticle(WriteRequest req) {
		Date now = new Date();
		return new Article(null, req.getWriter(), req.getTitle(), now, now, 0);
	}
}








