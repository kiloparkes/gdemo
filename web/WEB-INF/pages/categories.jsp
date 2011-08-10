<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>

<!--  
■	CAUTION:CAUTION:
 	Always place the XML declaration (<?xml?>) on the first line of your JSP, as the XML 
 	specification requires this.
 
 	If you place any JSP elements, such as the taglib directive on the first line, 
 	a blank line will appear when the content is rendered to the client.
 
 	This will create non-conforming XHTML.
-->
 
<head>
	<title>My Media Content Browsing</title>
</head>
<body>
	Hello Katz!!
	<fmt:message key="section.name.category" var="name" />
	${name}:
	
	<br></br>
	<c:set var="url"><%=request.getContextPath() %>/categoryEdit.do</c:set>
	<c:out value="${url}"></c:out>
	<form action="${url}" method="get">
		 <script type="text/javascript">
        	function doIt(oform, cid) {
        		alert(cid);
        		alert(oform.action);
        		oform.elements['id'].value = cid;
        		oform.method= "get";
        		oform.submit();
        	}   
    	</script>
    <input type="hidden" value="4" name="id"> </input>
	<c:forEach items="${categories}" var="cat" >
		<input type="submit" name="submit" value="submit" onclick="doIt(this.form, '${cat.id}')"> </input>
		<a href="#"> <c:out value="${cat.displayName}"></c:out> </a>
		<br></br>
	</c:forEach>
	</form>
	
	
</body>

</html>