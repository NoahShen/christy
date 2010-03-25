
$(document).ready(function() {
	$("#background").gradientz({
		start: "white",     // start color: default is the background color
		end: "#A2FF95"      // end color: default is the background color.
	});
	
	$("#button_login").click(function() {		
		
		var username = $("#username").val();
		var password = $("#password").val();
		if (username == null || username.length == 0) {
			var usernameMessage = $.i18n.prop("login.username.missingMessage");
			if (usernameMessage == null || usernameMessage.length == 0){
				usernameMessage = "Please input username!";
			}
			alert(usernameMessage);
			$("#username").focus();
			return false;
		}
		
		if (password == null || password.length == 0) {
			var pwdMessage = $.i18n.prop("login.password.missingMessage");
			if (pwdMessage == null || pwdMessage.length == 0){
				pwdMessage = "Please input password!";
			}
			alert(pwdMessage);
			$("#password").focus();
			return false;
		}
		
		var connectionMgr = XmppConnectionMgr.getInstance();
		if (connectionMgr.isWorking()){
			return false;
		}
		
		
		
		connectionMgr.requestCreateConnection({
			hold: 1,
			to: "example.com",
			ack: "1",
			ver: "1.6",
			wait: "10"
		});
		
		$("#login_loader_img").css({"display": ""});
		var logging_in = $.i18n.prop("login.logging_in");
		if (logging_in == null || logging_in.length == 0){
			logging_in = "Logging in...";
		}
		$("#login_status").text(logging_in);
		
	});
	
	$.i18n.properties({
	    name:"i18n",
	    path:"i18n/",
	    mode:"both",
	    language:"zh_CN", 
	    callback: function() {

	        var loginUsername = $.i18n.prop("login.username");
	        var loginPassword = $.i18n.prop("login.password");
	        var rememberUsername = $.i18n.prop("login.remember_username");
	        var loginAction = $.i18n.prop("login.loginAction");
	        
	        $("#login_username").text(loginUsername);
	        $("#login_password").text(loginPassword);
	        $("#login_RememberUsername").text(rememberUsername);
	        $("#button_login").val(loginAction);
	    }
	});
});