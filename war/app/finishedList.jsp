<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<!DOCTYPE html>
<meta charset="UTF-8" />
<title>TODO管理システム</title>
<link rel="stylesheet" href="/css/common.css">

<div id="container">
	<h1>完了済TODO一覧</h1>
	<p id="control">
		<a href="/app/main">未完了TODO一覧</a>
	</p>
	<ui id="todos"></ui>
</div>

<script src="http://code.jquery.com/jquery.min.js"></script>
<script>
	$(function() {
		var loadFinishedTodos = function(){
			$.ajax({
				type: "get",
				url: "/api/todos",
				data: "finished=true",
				dataType: "json"
			}).done(function(todos, status, xhr){
				if (xhr.status != 200){
					alert(res.message);
					return;
				}
				var list = [];
				for (var i = 0; i <todos.length; i++){
					var todo = todos[i];
					var li = $("<li>");
					var createDate = new Date(todo.createdAt).toLocaleString();
					var finishedDate = new Date(todo.finishedAt).toLocaleString();
					$("<span>").addClass("body").text(todo.body).appendTo(li);
					$("<span>").addClass("date").text(finishedDate + "に完了（登録："+ createDate + ")").appendTo(li);
					list.push(li);
				}
				$("#todos").empty().append(list);
			});
		};
		loadFinishedTodos();
	});

</script>
</html>
