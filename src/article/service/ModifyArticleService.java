package article.service;

import java.sql.Connection;
import java.sql.SQLException;

import article.dao.ArticleContentDao;
import article.dao.ArticleDao;
import article.model.Article;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class ModifyArticleService {
	
	private ArticleDao articleDao = new ArticleDao();
	private ArticleContentDao contentDao = new ArticleContentDao();
	
	public void modify(ModifyRequest modReq) {
		Connection conn = null;
		
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			
			//게시글 번호에 해당하는 Article 객체를 구한다
			Article article = articleDao.selectById(conn, modReq.getArticleNumber());
			//해당 번호를 가진 게시글이 존재하지 않으면 익셉션 발생
			if(article == null) {
				throw new ArticleNotFoundException();
			}
			//수정하려는 사용자가 해당 게시글을수정할 수 잇는지 검사
			if(!canModify(modReq.getUserId(), article)) {
				// 수정할 수 없으면 익셉션 발생
				throw new PermissionDeniedException();
			}
			
			//두개의 Dao의 update 메서드를 이용해서 제목과 내용 수정
			articleDao.update(conn, modReq.getArticleNumber(), modReq.getTitle());
			contentDao.update(conn, modReq.getArticleNumber(), modReq.getContent());
			
			conn.commit();
			
		} catch(SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} catch(PermissionDeniedException e) {
			JdbcUtil.rollback(conn);
			e.printStackTrace();
		} finally {
			JdbcUtil.close(conn);
		}
	}
	
	//게시글을 수정할 수 있는지 검사하는 기능
	private boolean canModify(String modfyingUserId, Article article) {
		
		//수정하려는 사용자 ID가 게시글 작성자 ID와 동일하면 true 리턴
		return article.getWriter().getId().equals(modfyingUserId);
	}
}
