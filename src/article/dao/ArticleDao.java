package article.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import article.model.Article;
import article.model.Writer;
import jdbc.JdbcUtil;

public class ArticleDao {
	
	public Article insert(Connection conn, Article article) throws SQLException{
		
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			//insert 쿼리를 실행하여 테이블에 데이터 삽입
			//article_no 컬럼은 자동증가 컬럼이므로 값 지정하지 않음
			pstmt = conn.prepareStatement("insert into article "
					+ "(writer_id, writer_name, title, regdate, moddate, read_cnt)"
					+ "values (?,?,?,?,?,0)");
			pstmt.setString(1, article.getWriter().getId());
			pstmt.setString(2, article.getWriter().getName());
			pstmt.setString(3, article.getTitle());
			pstmt.setTimestamp(4, toTimestamp(article.getRegDate()));
			pstmt.setTimestamp(5, toTimestamp(article.getModifiedDate()));
			int insertedCount = pstmt.executeUpdate();
			
			if(insertedCount > 0) {
				stmt = conn.createStatement();
				
				//테이블에 새롭게 추가한 행의 article_no의 값을 구함
				// last_insert_id =>최근 insert한 자료의 ID 반환 (auto_increment에 따라 생선된 최근 Id)
				rs = stmt.executeQuery("select last_insert_id() from article");
				if(rs.next()) {
					//위(43행)에서 실행한 쿼리로 구한 신규 게시글의 번호를 구함
					Integer newNum = rs.getInt(1);
					//article테이블에 추가한 데이터를 담은 Article 객체 리턴
					return new Article(newNum,
							article.getWriter(),
							article.getTitle(),
							article.getRegDate(),
							article.getModifiedDate(),
							0);
				}
			}
			return null;
		} finally {
			JdbcUtil.close(rs, stmt, pstmt);
		}
	}
	
	private Timestamp toTimestamp(Date date) {
		return new Timestamp(date.getTime());
	}
	
	// 전체 게시글 개수를 구하기 위한 메서드 
	// => selectCount()메서드는 article 테이블의 전체 레코드 수를 리턴
	public int selectCount(Connection conn) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select count(*) from article");
			if(rs.next()) {
				// 첫번째 결과값 리턴
				return rs.getInt(1);
			}
			return 0;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
		}
	}
	
	//지정한 범위의 게시글을 읽어오기 위한 select()메서드 
	public List<Article> select(Connection conn, int startRow, int size) throws SQLException{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {			
			pstmt = conn.prepareStatement("select * from article "
					+ "order by article_no desc limit ?,?"); // 게시글 번호 역순으로 정렬
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, size);
			rs = pstmt.executeQuery();
			
			List<Article> result = new ArrayList<>();
			while(rs.next()) {
				result.add(convertArticle(rs));
			}
			return result;
		} finally {
			JdbcUtil.close(rs, pstmt);
		}				
	}
	
	//convertArticle 메서드는 ResultSet에서 데이터를 읽어와 Article 객체를 생성
	private Article convertArticle(ResultSet rs) throws SQLException {
		return new Article(rs.getInt("article_no"),
				new Writer(
						
						
						
						rs.getString("writer_id"),
						rs.getString("writer_name")),
				rs.getString("title"),
				toDate(rs.getTimestamp("regdate")),
				toDate(rs.getTimestamp("moddate")),
				rs.getInt("read_cnt"));		
	}
	
	private Date toDate(Timestamp timestamp) {
		return new Date(timestamp.getTime());
	}
	
	
	//특정 번호에 해당하는 게시글 데이터 읽기
	public Article selectById(Connection conn, int no) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement(
					"select * from article where article_no=?");
			pstmt.setInt(1, no);
			rs = pstmt.executeQuery();
			Article article = null;
			if(rs.next()) {				
				article = convertArticle(rs);
			}
			return article;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	
	//특정 번호에 해당하는 게시글 데이터의 조회수 증가하기
	public void increaseReadCount(Connection conn, int no) throws SQLException{
		try (PreparedStatement pstmt = 
				conn.prepareStatement(
				"update article set read_cnt = read_cnt + 1 " 
				+ "where article_no = ?")) {
					pstmt.setInt(1, no);
					pstmt.executeUpdate();
			}
	}
	
	//게시글 제목 수정 기능, 파라미터로 전달받은 게시글 번호와 제목을 이용하여 데이터 수정
	public int update(Connection conn, int no, String title) throws SQLException{
		
		try(PreparedStatement pstmt = 
				conn.prepareStatement(
						//moddate 컬럼의 값을 현재시간으로 설정하여 데이터 최근 수정 시간 기록
						"update article set title = ?, moddate = now() "
						+ "where article_no = ?")){
			pstmt.setString(1, title);
			pstmt.setInt(2, no);
			
			return pstmt.executeUpdate();
		}
	}
	
	// 지정한 주요키에 해당하는 행의 데이터를 삭제하는 쿼리를 실행
	public int delete(Connection conn, int no) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("delete from article "
					+ "where article_no=?");
			pstmt.setInt(1, no);
			return pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
	
}













