package auth.service;

import java.sql.Connection;
import java.sql.SQLException;

import jdbc.connection.ConnectionProvider;
import member.dao.MemberDao;
import member.model.Member;

public class LoginService {
	
	private MemberDao memberDao = new MemberDao();
	
	public User login(String id, String password) {
		//try-with-resource
		try(Connection conn = ConnectionProvider.getConnection()) {
			// MemberDao를 이용해서 아이디에 해당하는 회원 데이터가 존재하는지 확인
			Member member = memberDao.selectById(conn, id);
			//회원데이터가 존재하지 않거나 암호가 일치하지 않으면 LoginFailException 발생
			if(member == null) {
				throw new LoginFailException();
			}
			if(!member.matchPassword(password)) {
				throw new LoginFailException();
			}
			//암호가 일치하면 회원 아이디와 이름을 담은 User객체 생성하여 리턴
			return new User(member.getId(), member.getName());
		} catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
