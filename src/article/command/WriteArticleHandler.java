package article.command;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import article.model.Writer;
import article.service.WriteArticleService;
import article.service.WriteFileService;
import article.service.WriteRequest;
import auth.service.User;
import mvc.controller.CommandHandler;

public class WriteArticleHandler implements CommandHandler {
	
	private static final String FORM_VIEW = "/WEB-INF/view/newArticleForm.jsp";
	private WriteArticleService writeService = new WriteArticleService();
	private WriteFileService writeFile = new WriteFileService();

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
	
	private String processSubmit(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Part filePart = req.getPart("file1");
		String fileName = filePart.getSubmittedFileName();
		
		fileName = fileName == null ? "" : fileName;
		
		Map<String, Boolean> errors = new HashMap<>();
		req.setAttribute("errors", errors);
		
		// 세션에서 로그인한 사용자 정보를 구한다
		User user = (User)req.getSession(false).getAttribute("authUser");
		//user와 HttpServletRequest를 이용해서 WriteRequest 객체를 생성
		WriteRequest writeReq = createWriteRequest(user, req, fileName);
		//writeReq 객체가 유효하진 검사
		writeReq.validate(errors);
		
		//에러가 존재하면 폼을 다시 보여줌
		if(!errors.isEmpty()) {
			return FORM_VIEW;
		}		
		
		//WriteArticleService를 이용해서 게시글 등록, 등록된 게시글의 ID 리턴
		int newArticleNo = writeService.write(writeReq);
		
		if(!(fileName == null || fileName.isEmpty() || filePart.getSize() == 0)) {
			writeFile.write(filePart, newArticleNo);
		}
		//새 글의 ID를 request의 newArticleId 속성에 저장, 처리결과를 보여줄 jsp는 이 속성값 사용해서 링크 생성
		req.setAttribute("newArticleNo", newArticleNo);
		
		return "/WEB-INF/view/newArticleSuccess.jsp";		
	}
	
	private WriteRequest createWriteRequest(User user, HttpServletRequest req) {
		return createWriteRequest(user, req, "");
	}
	
	private WriteRequest createWriteRequest(User user,
			HttpServletRequest req, String fileName) {
		return new WriteRequest(
				new Writer(user.getId(), user.getName()),
				req.getParameter("title"),
				req.getParameter("content"),
				fileName);
	}
}

















