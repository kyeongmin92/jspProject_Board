<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>

        
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css">
<script src="https://kit.fontawesome.com/a076d05399.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdnjs.
cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.min.js"></script>

<title>회원제 게시판 예제</title>
</head>
<body>

<u:isLogin>
	${authUser.name }님, 안녕하세요 <br />
	<a href="logout.do" class="text-danger">[로그아웃하기]</a>
	<a href="changePwd.do" class="text-success">[암호변경하기]</a>
	<a href="${ctxPath }/article/write.do">[글 작성]</a>
	<a href="/board/article/list.do">[게시글목록보기]</a>
</u:isLogin>

<u:notLogin>
	<a href="join.do">[회원가입하기]</a>
	<a href="login.do">[로그인하기]</a>
</u:notLogin>

<!-- <div><img src="/images/rabbit.jpg" alt="" /></div> -->


</body>
</html>