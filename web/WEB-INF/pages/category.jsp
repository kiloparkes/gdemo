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
	Category Form
	<br/>
	
	<c:out value="${category.displayName}"></c:out>
	
	<br />
	<b>Referenced Data </b>
	<c:out value="${language}"></c:out>
	
</body>

</html>