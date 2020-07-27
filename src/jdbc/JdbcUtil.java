package jdbc;

import java.sql.Connection;

public class JdbcUtil {
	// p.434 에 있는 3개의 메소드 대신함
	public static void close(AutoCloseable... resource) {
		try {
			for (AutoCloseable res : resource) {
				res.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void rollback(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
