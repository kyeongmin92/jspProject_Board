package member.command;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import auth.service.User;
import member.service.InvalidPasswordException;
import member.service.MemberNotFoundException;
import mvc.controller.CommandHandler;


public class ChangePasswordHandler implements CommandHandler {
	
	private static final String FORM_VIEW ="/WEB-INF/view/changePwdForm.jsp";
	private ChangePasswordService changePwdSvc = new ChangePasswordService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// get 요청시 폼을 위한 뷰 경로 리턴
		if(req.getMethod().equalsIgnoreCase("GET")) {
			return processForm(req, res);
			// post 요청시 폼 전송을 처리
		} else if(req.getMethod().equalsIgnoreCase("POST")) {
			return processSubmit(req, res);
		} else {
			res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}
	
	private String processForm(HttpServletRequest req, HttpServletResponse res) {
		return FORM_VIEW;
	}
	
	private String processSubmit(HttpServletRequest req, HttpServletResponse res) throws Exception{
		// 로그인에 성공하면 "authUser"속성에 사용자 정보를 저장했으므로 세션에서 사용자 정보 구함
		User user = (User)req.getSession().getAttribute("authUser");
		
		//에러 정보를 담을 맵 객체 생성하고 errors 속성에 저장
		Map<String, Boolean> errors = new HashMap<>();
		req.setAttribute("errors", errors);
		
		//curPwd 요청 파라미터와 newPwd 요청 파라미터 값을 구함
		String curPwd = req.getParameter("curPwd");
		String newPwd = req.getParameter("newPwd");
		
		//파라미터가 값이 없는 경우 errors 맵 객체에 에러 코드 추가
		if(curPwd == null || curPwd.isEmpty()) {
			errors.put("curPwd", Boolean.TRUE);
		}		
		if(curPwd==null || curPwd.isEmpty()) {
			errors.put("newPwd", Boolean.TRUE);
		}
		//에러가 존재하면 폼을 위한 뷰 경로 리턴
		if(!errors.isEmpty()) {
			return FORM_VIEW;
		}
		
		try {
			//암호 변경 기능 실행
			changePwdSvc.changePassword(user.getId(), curPwd, newPwd);
			//암호 변경에 성공하면 changePwdSuccess.jsp 뷰로 리턴
			return "/WEB-INF/view/changePwdSuccess.jsp";
		} catch(InvalidPasswordException e) {
			// curPws(현재암호)가 올바르지 않아 익셉션이 발생하면 에러코드를 추가하고 폼 뷰 경로 리턴
			errors.put("badCurPwd", Boolean.TRUE);
			return FORM_VIEW;
		} catch (MemberNotFoundException e) {
			e.printStackTrace();
			//암호 변경할 회원 아이디가 존재하지 않아 익셉션이 발생하면 
			//잘못된 요청 400(BAD_REQUEST) 상태 코드를 응답으로 전송
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
		
	}
}









