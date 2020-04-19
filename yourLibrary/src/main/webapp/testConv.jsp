<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<title>TemplateSample</title>
<head>
<body>

    <ul>
        <li>Conversation ID : ${convBean.cid}</li>
        <li>Counter : ${convBean.count}</li>
    </ul>
    <form method="GET" action="conversationCountup">
　　　　　　<!-- リクエストにCIDを必ず含める -->
        <input id="cid" type="hidden" value="${convBean.cid}" name="cid"/>
        <input type="submit" value="CountUp"/>
    </form>
    <input id="end" type="button" value="End Conversion"/>

</body>
</html>