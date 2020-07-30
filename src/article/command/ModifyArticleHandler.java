package article.command;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import article.service.ArticleData;
import article.service.ArticleNotFoundException;
import article.service.ModifyArticleService;
import article.service.ModifyRequest;
import article.service.PermissionDeniedException;
import article.service.ReadArticleService;
import auth.service.User;
import mvc.controller.CommandHandler;

public class ModifyArticleHandler implements CommandHandler {
	
	private static final String FORM_VIEW = "/WEB-INF/view/modifyForm.jsp";
	
	private ReadArticleService readService = new ReadArticleService();
	private ModifyArticleService modifyService = new ModifyArticleService();
	
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
	
	//폼 요청 처리
	private String processForm(HttpServletRequest req, HttpServletResponse res) throws IOException{
		
		try {
			String noVal = req.getParameter("no");
			int no = Integer.parseInt(noVal);
			
			//폼에 보여줄 게시글을 구한다, 게시글이 존재하지 않으면 ArticleNotFoundException 발생
			ArticleData articleData = readService.getArticle(no, false);
			//현재 로그인한 사용자 정보를 구한다
			User authUser = (User) req.getSession().getAttribute("authUser");
			
			//현재 로그인한 사용자가 게시글의 작성자가 아니면 403응답 전송
			if(!canModify(authUser, articleData)) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				return null;
			}
			//폼에 데이터를 보여줄 때 사용할 객체 생성하여 request의 modReq 속성에 저장
			ModifyRequest modReq = new ModifyRequest(authUser.getId(), no,
					articleData.getArticle().getTitle(), articleData.getContent());
			
			req.setAttribute("modReq", modReq);
			
			//폼을 위한 뷰 리턴
			return FORM_VIEW;
		} catch(ArticleNotFoundException e) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}

	private boolean canModify(User authUser, ArticleData articleData) {
		String writerId = articleData.getArticle().getWriter().getId();
		
		return authUser.getId().equals(writerId);
	}
	
	// 폼 전송 처리
	private String processSubmit(HttpServletRequest req, HttpServletResponse res) throws Exception{
		//게시글 수정을 요청한 사용자 정보를 구한다
		User authUser = (User) req.getSession().getAttribute("authUser");
		String noVal = req.getParameter("no");
		int no = Integer.parseInt(noVal);
		
		//ModifyRequest 객체를 request의 modReq 속성에 저장
		ModifyRequest modReq = new ModifyRequest(authUser.getId(), no, 
				req.getParameter("title"), req.getParameter("content"));		
		req.setAttribute("modReq", modReq);
		
		//에러 정보를 담을 객체 생성
		Map<String, Boolean> errors = new HashMap<>();
		//ModifyRequest 객체가 유효한지 검사
		req.setAttribute("errors", errors);
		modReq.validate(errors);
		//유효하지 않은 데이터가 존재하면 다시 폼을 보여줌
		if(!errors.isEmpty()) {
			return FORM_VIEW;
		}
		
		//게시글 수정 기능 실행
		try {
			modifyService.modify(modReq);
			
			return "/WEB-INF/view/modifySuccess.jsp";			
		} catch(ArticleNotFoundException e) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} catch(PermissionDeniedException e) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		
	}
	
	
}

