package member.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import member.dao.MemberDao;
import member.model.Member;

public class JoinService {
	
	private MemberDao memberDao = new MemberDao();
	
	public void join(JoinRequest joinReq) {
		Connection conn = null;
		try {
			// DB커넥션을 구하고, 트랜잭션 시작
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			
			//MemberDao의 selectById()를 이용해서 joinReq.getId()에 해당하는 회원 데이터를 구한다
			Member member = memberDao.selectById(conn, joinReq.getId());
			//가입하려는 ID에 해당하는 데이터가 이미 존재하면 트랜잭션 롤백, DuplicateIdException 발생
			if(member != null) {
				JdbcUtil.rollback(conn);
				throw new DuplicateIdException();
			}
			
			// joinReq 이용하여 Member 객체 생성, MemberDao의 insert()를 실행하여 데이터 삽입
			memberDao.insert(conn, new Member(
										joinReq.getId(),
										joinReq.getName(),
										joinReq.getPassword(),
										new Date()));
			// 트래잭션 커밋
			conn.commit();
			//SQLException 발생시 트랜잭션 롤백, RuntimeException 발생
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			// 커넥션 종료
			JdbcUtil.close(conn);
		}
	}
	
}
