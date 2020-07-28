package member.command;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import member.service.DuplicateIdException;
import member.service.JoinRequest;
import member.service.JoinService;
import mvc.controller.CommandHandler;

// CommandHandler 인터페이스 구현
public class JoinHandler implements CommandHandler {
	
	private static final String FORM_VIEW = "/WEB-INF/view/joinForm.jsp";
	private JoinService joinService = new JoinService();
		
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) {
		// get방식으로 요청이 오면 joinForm.jsp 리턴
		if(req.getMethod().equalsIgnoreCase("GET")) {
			return processForm(req, res);
			// post방식으로 요청이 오면 회원가입을 처리하고 결과를 보여주는 뷰 리턴
		} else if(req.getMethod().equalsIgnoreCase("POST")) {
			return processSubmit(req, res);
		} else {
			// get 또는 post가 아니면 405응답 코드 전송
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}
	
	private String processForm(HttpServletRequest req, HttpServletResponse res) {
		return FORM_VIEW;
	}
	
	private String processSubmit(HttpServletRequest req, HttpServletResponse res) {
		// form에 입력한 데이터를 이용해서 joinRequest 객체 생성
		JoinRequest joinReq = new JoinRequest();
		joinReq.setId(req.getParameter("id"));
		joinReq.setName(req.getParameter("name"));
		joinReq.setPassword(req.getParameter("password"));
		joinReq.setConfirmPassword(req.getParameter("confirmPassword"));
		
		//에러 정보를 담을 맵 객체를 생성하여 errors 속성에 저장
		Map<String, Boolean> errors = new HashMap<>();
		req.setAttribute("errors", errors);
		
		// JoinRequest 객체의 값이 올바른지 검사, 올바르지 않으면 errors 맵 객체에 키 추가
		joinReq.validate(errors);
		
		//errors 맵 객체에 데이터가 존재하면 FORM_VIEW경로 리턴
		//(데이터가 존재 => JoinReq 객체의 데이터가 올바르지 않아서 에러와 관련된 키를 추가했다는 것을 의미)
		if(!errors.isEmpty()) {
			return FORM_VIEW;
		}
		
		//JoinService의 join() 메서드 실행
		try {
			// 회원가입에 성공한 경우 joinSuccess.jsp 뷰 리턴
			joinService.join(joinReq);
			return "/WEB-INF/view/joinSuccess.jsp";
		} catch(DuplicateIdException e) {
			// 동일한 아이디로 가입한 회원이 존재
			// => errors에 "duplicateId" 키 추가, joinForm.jsp 뷰 리턴
			errors.put("duplicateId", Boolean.TRUE);
			return FORM_VIEW;
		}
	}
}









