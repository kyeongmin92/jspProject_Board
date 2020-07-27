<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="jdbc.connection.ConnectionProvider" %>
<%@ page import= "java.sql.*" %>

        
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Insert title here</title>
</head>
<body>
<%
	try(Connection conn = ConnectionProvider.getConnection()){
		out.println("커넥션 연결 성공함");
	} catch(SQLException ex){
		out.println("커넥션 연결 실패함: "+ ex.getMessage());
		application.log("커넥션 연결 실패", ex);
	}
%>
</body>
</html>