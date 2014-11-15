<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>SWB SparQL Query Test!</h1>
<%
    String query=request.getParameter("query");
    if(query==null)
    {
          query="";
    }
%>        
        <form action="sparql" method="post">
            Query:<br>
            <textarea rows="20" cols="80" name="query"><%=query%></textarea><br>
            <input type="submit">
        </form>
    </body>
</html>

