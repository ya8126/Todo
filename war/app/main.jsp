<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<!DOCTYPE html>
<meta charset="UTF-8" >
<title>TODO管理システム</title>
<link rel="stylesheet" href="/css/common.css">

<div id="container">
	<h1>未完了TODO一覧</h1>
	<p id="control">
		<a href="/app/finishedlist">完了済TODO一覧</a>
	</p>
	<p id="message"></p>
	<form id="post_todo">
		<input type="text" name="body">
		<button class="post_button">登録</button>
	</form>
	<ui id="todos"></ui>
</div>

<script src="http://code.jquery.com/jquery.min.js"></script>
<script>
	$(function() {
		var loadTodos = function(){
			$.ajax({
				type: "get",
				url: "/api/todos",
				data: "finished=false",
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
					var date = new Date(todo.createdAt).toLocaleString();
					$("<a href='#' class='finish_link'>完了</a>").data("todoKey", todo.key).appendTo(li);
					$("<a href='#' class='delete_link'>削除</a>").data("todoKey", todo.key).appendTo(li);
					$("<span>").addClass("body").text(todo.body).appendTo(li);
					$("<span>").addClass("date").text(date).appendTo(li);
					list.push(li);
				}
				$("#todos").empty().append(list);
			});
		};
		
		var showMessage = function(msg, clazz){
			var messageView = $("#message");
			
			messageView
				.removeAttr("class")
				.addClass(clazz)
				.text(msg)
				.css("opacity", 1);
			
			setTimeout(function(){
				messageView.animate({opacity: 0}, 1000)
			}, 1000);			
		}
		
		var form = $("#post_todo");
		form.find(".post_button").on("click", function(e){
			$.ajax({
				type: "post",
				url: "/api/todos",
				data: form.serialize(),
				dataType : "json"
			}).done(function(res, status, xhr){
				if (xhr.status === 200){
					form.find("[name=body]").val("");
					showMessage("TODOを登録しました", "success");
					loadTodos();
				}else{
					showMessage("TODOの登録に失敗しました", "error");
				}

			});
			e.preventDefault();
		});
		
		$("#todos").on("click", "li a.delete_link", function(e){
			var li= $(this).parent();
			$ajax({
				type: "DELETE",
				url: "/api/todos/" + $(this).data("todokey")
			}).done(function(res, status, xhr){
				if (xhr.status !== 200){
					showMessage("TODO削除に失敗しました", "error");
					return;
				}
				li.animate({opacity:0}, 500, function(){
					li.remove();
				});
			});
			e.preventDefault();
		});
		
		$("#todos").on("click", "li a.finish_link", function(e){
			var li= $(this).parent();
			$ajax({
				type: "PUT",
				url: "/api/todos/" + $(this).data("todokey"),
				data: "finished=true",
				dataType: "json"
			}).done(function(res, status, xhr){
				if (xhr.status !== 200){
					showMessage("TODO完了に失敗しました", "error");
					return;
				}
				li.animate({opacity:0}, 500, function(){
					li.remove();
				});
			});
			e.preventDefault();
		});		
		// ページが表示されたときに未完了TODO一覧をサーバから取得・表示
		loadTodos();
	});

</script>