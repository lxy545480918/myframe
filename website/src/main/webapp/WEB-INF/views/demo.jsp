<%--
  Created by IntelliJ IDEA.
  User: lxy
  Date: 2015/11/6
  Time: 15:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>demo</title>
  <style>
    input{width: 150px;height: 80px;text-align: center;font-size: 20px;color: #46A1E8;margin: 20px;}
  </style>
</head>
<body>

<input type="button" value="测试异常" onclick="window.location='/demo/exception.html'">

<input type="button" value="测试sql查询" onclick="window.location='/demo/db.html'">

<input type="button" value="测试字典" onclick="window.location='/demo/dic.html'">

<input type="button" value="测试Redis" onclick="window.location='/demo/redis.html'">

<input type="button" value="测试类型转换" onclick="window.location='/demo/convert.html'">



</body>
</html>
