<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>RESTFul API Test</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
</head>
<body>
<div
	style="width:800px;margin-top:10px;margin-left: auto;margin-right: auto;text-align: center;">
</div>
<div style="width:800px;margin-left: auto;margin-right: auto;">
	<!-- 
	<h2>RESTFul API Test</h2>
	<button class="uk-button uk-button-primary uk-button-large" id="btnGet">获取人员GET</button>
	<button class="uk-button uk-button-primary uk-button-large" id="btnAdd">添加人员POST</button>
	<button class="uk-button uk-button-primary uk-button-large" id="btnUpdate">更新人员PUT</button>
	<button class="uk-button uk-button-danger uk-button-large" id="btnDel">删除人员DELETE</button>
	<button class="uk-button uk-button-primary uk-button-large" id="btnList">查询列表PATCH</button> -->
	<h2>短信服务</h2>
</div>

<script type="text/javascript" src="static/components/jquery/3.1.1/jquery-3.1.1.min.js"></script>
<script type="text/javascript" src="static/components/jiami/MD5.js"></script>
<script type="text/javascript" src="static/components/jiami/base64.js"></script>

<script type="text/javascript">
	(function(window,$){
		var dekota={
			url:'test/',
			init:function(){
				dekota.url='<%=basePath%>';
				console.info("页面初始化完成");
				$("#btnGet").click(dekota.getPerson);
				$("#btnAdd").click(dekota.addPerson);
				$("#btnDel").click(dekota.delPerson);
				$("#btnUpdate").click(dekota.updatePerson);
				$("#btnList").click(dekota.listPerson);
				$("#btnSendSms").click(dekota.sendSms);
			},
			getPerson:function(){
				$.ajax({
					url: dekota.url + 'test/person/101/',
					type: 'GET',
					dataType: 'json'
				}).done(function(data, status, xhr) {
					console.log("获取人员信息成功data-GET",data);
				}).fail(function(xhr, status, error) {
					console.log("获取人员信息请求失败data-GET",error);
				});
			},
			addPerson:function(){
				$.ajax({
					url: dekota.url + 'test/person',
					type: 'POST',
					dataType: 'json',
					data: {id: 1,username:'张三',sex:true,}
				}).done(function(data, status, xhr) {
					console.log("添加人员信息成功data-POST",data);
				}).fail(function(xhr, status, error) {
					console.log("添加人员信息请求失败data-POST",error);
				});
			},
			delPerson:function(){
				$.ajax({
					url: dekota.url + 'test/person/109',
					type: 'DELETE',
					dataType: 'json'
				}).done(function(data, status, xhr) {
					console.log("删除人员信息成功data-DELETE",data);
				}).fail(function(xhr, status, error) {
					console.log("删除人员信息请求失败data-DELETE",error);
				});
			},
			updatePerson:function(){
				$.ajax({
					url: dekota.url + 'test/person',
					type: 'POST',//注意在传参数时，加：_method:'PUT'　将对应后台的PUT请求方法
					dataType: 'json',
					data: {_method:'PUT',id: 221,username:'王五',sex:true}
				}).done(function(data, status, xhr) {
					console.log("更新人员信息成功data-POST-PUT",data);
				}).fail(function(xhr, status, error) {
					console.log("更新人员信息请求失败data-POST-PUT",error);
				});
			},
			listPerson:function(){
				$.ajax({
					url: dekota.url + 'test/person',
					type: 'POST',//注意在传参数时，加：_method:'PATCH'　将对应后台的PATCH请求方法
					dataType: 'json',
					data: {_method:'PATCH',name: '张三'}//PATCH
				}).done(function(data, status, xhr) {
					console.log("查询人员信息成功data-POST-PATCH",data);
				}).fail(function(xhr, status, error) {
					console.log("查询人员信息请求失败data-POST-PATCH",error);
				});
			},
			sendSms:function(){
				var userName = $("#userName").val();
		        var password = $("#password").val();
		        
 				var dataArr = new Array();
 				
			 	var phoneNum = new Object();
				phoneNum.name = "phoneNum";
				phoneNum.value = $("#phoneNum").val();
				dataArr[0]=phoneNum;
				
				var notifyType = new Object();
				notifyType.name = "notifyType";
				notifyType.value = $("#notifyType").val();
				dataArr[dataArr.length]=notifyType;
				
				var type = new Object();
				type.name = "type";
				type.value = $("#type").val();
				dataArr[dataArr.length]=type;
				
				var messages = $("#messages").val().split(",");
				$(messages).each(function(i){
					var message = new Object();
					message.name = "messages";
					message.value = this;
					dataArr[dataArr.length]=message;
				})
		        
				$.ajax({
					url: dekota.url + 'smss',
					type: 'POST',
					dataType: 'json',
					data: dataArr,
					beforeSend: function(xhr){
			            xhr.setRequestHeader("Authorization",
			                "Basic " + new Base64().encode(userName + ":" + password));
			        }
				}).done(function(data, status, xhr) {
					console.log("data",data);
					console.info("string data:"+JSON.stringify(data));
		        	$("#result").val(JSON.stringify(data));
				}).fail(function(xhr, status, error) {
					console.log("error:",error);
		        	$("#result").val(error);
				});
				
			}
		};
		window.dekota=(window.dekota)?window.dekota:dekota;
		$(function(){
			dekota.init();
		});
	})(window,jQuery);

</script>
</body>
</html>