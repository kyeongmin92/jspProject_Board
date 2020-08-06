package member.command;

import java.sql.Connection; 
import java.sql.SQLException;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import member.dao.MemberDao;
import member.model.Member;
import member.service.InvalidPasswordException;
import member.service.MemberNotFoundException;

public class ChangePasswordService {
	
	private MemberDao memberDao = new MemberDao();
	
	// 암호변경 기능 구현, userId 암호변경할 회원 아이디 - curPwd 현재 암호 - newPwd 변경할 암호
	public void changePassword(String userId, String curPwd, String newPwd) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			
			//userId에 해당하는 Member 데이터를 구함
			Member member = memberDao.selectById(conn, userId);
			
			//회원 데이터가 존재하지 않으면 MemberNotFoundException 발생
			if(member == null) {
				throw new MemberNotFoundException();
			}
			// curPwd(현재 암호)가 실제 암호와 일치하지 않으면 InvalidPasswordException 발생
			if(!member.matchPassword(curPwd)) {
				throw new InvalidPasswordException();
			}
			
			//member 객체의 암호 데이터 변경
			member.changePassword(newPwd);
			// 변경한 암호 데이터를 DB테이블에 반영
			memberDao.update(conn, member);
			conn.commit();
			
		} catch(SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
	
}
