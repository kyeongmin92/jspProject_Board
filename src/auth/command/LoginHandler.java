package auth.command;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import auth.service.LoginFailException;
import auth.service.LoginService;
import auth.service.User;
import mvc.controller.CommandHandler;

public class LoginHandler implements CommandHandler {
	
	private static final String FORM_VIEW = "/WEB-INF/view/loginForm.jsp";
	private LoginService loginService = new LoginService();
	
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if(req.getMethod().equalsIgnoreCase("GET")) {
			return processForm(req, res);
		} else if(req.getMethod().equalsIgnoreCase("POST")) {
			return processSubmit(req, res);
		} else {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}

	private String processForm(HttpServletRequest req, HttpServletResponse res) {
		return FORM_VIEW;
	}

	private String processSubmit(HttpServletRequest req, HttpServletResponse res) throws Exception{
		// 폼에서 전송한 id 파라미터와 password 파라미터 값을 구함
		String id = trim(req.getParameter("id"));
		String password = trim(req.getParameter("password"));
		
		//에러 정보를 담을 맵 객체 생성하여 errors 속성에 저장
		Map<String, Boolean> errors = new HashMap<>();
		req.setAttribute("errors", errors);
		
		//id나 password값이 없을 경우 에러 추가
		if(id == null || id.isEmpty()) {
			errors.put("id", Boolean.TRUE);
		}
		if(password == null || password.isEmpty()) {
			errors.put("password", Boolean.TRUE);
		}
		
		//에러가 존재하면 폼 뷰 리턴
		if(!errors.isEmpty()) {
			return FORM_VIEW;
		}
		
		try {
			//loginService.login()을 이용하여 인증 수행, 로그인에 성공하면 User객체 리턴
			User user = loginService.login(id, password);
			//User객체를 세션의 authUser 속성에 저장
			req.getSession().setAttribute("authUser", user);
			// /index.jsp로 리다이렉트
			res.sendRedirect(req.getContextPath() + "/index.jsp");
			return null;
		} catch (LoginFailException e) {
			// 로그인 실패하여 LoginFailException 발생시 에러추가, 폼 뷰 리턴
			e.printStackTrace();
			errors.put("idOrPwNotMatch", Boolean.TRUE);
			return FORM_VIEW;
		}
	}	
	
	private String trim(String str) {
		return str == null ? null : str.trim();
	}
	
	
	
}
