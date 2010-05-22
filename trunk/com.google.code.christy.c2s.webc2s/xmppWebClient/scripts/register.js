(function() {
	
    var registerUsername = $.i18n.prop("register.username", "用户名：");
    var registerPassword = $.i18n.prop("register.password", "密码：");
    var registerEmail = $.i18n.prop("register.email", "邮箱：");
	var register = $.i18n.prop("register.registerAction", "注册");
    
    $("#register_username_div").text(registerUsername);
    $("#register_password_div").text(registerPassword);
    $("#register_emaile_div").text(registerEmail);
	$("#register_action").text(register);
	
	$("#register_action").click(function() {
		
		var username = $("#register_username").val();
		var password = $("#register_password").val();
		var email = $("#register_email").val();
		if (username == null || username.length == 0) {
			var usernameMessage = $.i18n.prop("register.username.missingMessage", "请输入用户名！");
			alert(usernameMessage);
			$("#username")[0].focus();
			return;
		}
		
		if (password == null || password.length == 0) {
			var pwdMessage = $.i18n.prop("register.password.missingMessage", "请输入密码！");
			alert(pwdMessage);
			$("#password")[0].focus();
			return;
		}
		
		if (email == null || email.length == 0) {
			var emailMessage = $.i18n.prop("register.email.missingEmail", "请输入邮箱！");
			alert(emailMessage);
			$("#register_email")[0].focus();
			return;
		}
		
		$.ajax({
			url: "/shop/",
			cache: false,
			type: "post",
			dataType: "json",
			contentType: "application/x-www-form-urlencoded; charset=UTF-8",
			data: {
				action: "register",
				username: username,
				password: password,
				email: email
			},
			success: function(response){
				if (response) {
					if (response.result == "success") {
						alert($.i18n.prop("register.registerSuccess", "注册成功！"));
						window.location.reload();
						return;
					} else if (response.result == "failed") {
						if (response.reason == "usernameDuplicated") {
							alert($.i18n.prop("register.registerFailed.usernameDuplicated", "用户名已经存在！"));
							return;
						} else if (response.reason == "emailDuplicated") {
							alert($.i18n.prop("register.registerFailed.emailDuplicated", "邮箱已经被注册！"));
							return;
						}
					}
				}
				
				alert($.i18n.prop("register.registerFailed", "注册失败！"));
			},
			error: function (xmlHttpRequest, textStatus, errorThrown) {
				alert($.i18n.prop("register.registerFailed", "注册失败！"));
			},
			complete: function(xmlHttpRequest, textStatus) {
				
			}
		});
	});
	
//	$.i18n.properties({
//	    name: "i18n",
//	    path: "/i18n/",
//	    mode: "map",
//	    language: "zh_CN",
//	    callback: function() {
//
//	        var registerUsername = $.i18n.prop("register.username", "用户名：");
//	        var registerPassword = $.i18n.prop("register.password", "密码：");
//	        var registerEmail = $.i18n.prop("register.email", "邮箱：");
//			var register = $.i18n.prop("register.registerAction", "注册");
//	        
//	        $("#register_username_div").text(loginUsername);
//	        $("#register_password_div").text(loginPassword);
//	        $("#register_emaile_div").text(rememberUsername);
//			$("#register_action").text(register);
//	    }
//	});
	
})();