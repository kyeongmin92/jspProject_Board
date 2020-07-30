package article.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import article.service.ArticleNotFoundException;
import article.service.DeleteArticleService;
import article.service.DeleteRequest;
import article.service.PermissionDeniedException;
import article.service.ReadArticleService;
import auth.service.User;
import mvc.controller.CommandHandler;

public class DeleteArticleHandler implements CommandHandler {
	
	private static final String FORM_VIEW = "/WEB-INF/view/deleteSuccess.jsp";
	
	private ReadArticleService readService = new ReadArticleService();
	private DeleteArticleService deleteService = new DeleteArticleService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		try {
			String noVal = req.getParameter("no");
			int no = Integer.parseInt(noVal);
			
			//현재 로그인한 사용자 정보를 구한다
			User authUser = (User) req.getSession().getAttribute("authUser");
						
			//폼에 데이터를 보여줄 때 사용할 객체 생성하여 request의 delReq 속성에 저장
			DeleteRequest delReq = new DeleteRequest(authUser.getId(), no);
			
			req.setAttribute("delReq", delReq);
			
			deleteService.delete(delReq);
			
			// 뷰 리턴
			return FORM_VIEW;			
		} catch(ArticleNotFoundException e) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} catch(PermissionDeniedException e) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
	}
	
}
