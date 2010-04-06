
$(document).ready(function() {
	
	$("#background").gradientz({
		start: "#CEF6CE",     // start color: default is the background color
		end: "#FFFFFF",     // end color: default is the background color.
		distance: 100,
		fillWidth: true
	});
	
	
	// TODO test code
	loginSuccess();
	$.i18n.properties({
	    name: "i18n",
	    path: "/i18n/",
	    mode: "both",
	    language:"zh_CN",
	    callback: function() {

	    }
	});
	return;
	// TODO test code
	
	
	
	var connectionMgr = XmppConnectionMgr.getInstance();
	var listener = function(event){
		var conn = event.connection;
		conn.login($("#username").val(), $("#password").val(), "Christy", new Presence(PresenceShow.AVAILABLE));
		connectionMgr.removeConnectionListener(listener);
	};
	connectionMgr.addConnectionListener(ConnectionEventType.Created, listener);
	
	var cookiesUsername = Cookies.get("username");
	if (cookiesUsername) {
		$("#username").val(cookiesUsername);
		$("#password")[0].focus();
	} else {
		$("#username")[0].focus();
	}
	
	var loginAction = function() {		
		
		var username = $("#username").val();
		var password = $("#password").val();
		if (username == null || username.length == 0) {
			var usernameMessage = $.i18n.prop("login.username.missingMessage");
			alert(usernameMessage);
			$("#username")[0].focus();
			return;
		}
		
		if (password == null || password.length == 0) {
			var pwdMessage = $.i18n.prop("login.password.missingMessage");
			alert(pwdMessage);
			$("#password")[0].focus();
			return;
		}
		
		var connectionMgr = XmppConnectionMgr.getInstance();
		if (connectionMgr.isWorking()){
			return;
		}
		
		var lognListener = function(event){
			var type = event.eventType;
			var loginStatus = null;
			var loginFailed = false;
			if (type == ConnectionEventType.SaslSuccessful) {
				loginStatus = $.i18n.prop("login.saslsuccess");
				saslSuccess();
			} else if (type == ConnectionEventType.SaslFailed) {
				loginStatus = $.i18n.prop("login.saslfailed");
				loginFailed = true;
			} else if (type == ConnectionEventType.ResourceBinded) {
				loginStatus = $.i18n.prop("login.resourcebinded");
			} else if (type == ConnectionEventType.BindResourceFailed) {
				loginStatus = $.i18n.prop("login.bindresourcefailed");
				loginFailed = true;
			} else if (type == ConnectionEventType.SessionBinded) {
				loginStatus = $.i18n.prop("login.sessionbinded");
				loginSuccess();
				connectionMgr.removeConnectionListener(lognListener);
			} else if (type == ConnectionEventType.BindSessionFailed) {
				loginStatus = $.i18n.prop("login.bindsessionfailed");
				loginFailed = true;
			}
			
			$("#login_status").text(loginStatus);
			
			if (loginFailed) {
				$("#login_status").css("color", "red");
				disabledInputController(false);
				$("#login_loader_img").css({"display": "none"});
				connectionMgr.removeAllConnections();
			}
			
			
		};
		
		connectionMgr.addConnectionListener(
			[
				ConnectionEventType.SaslSuccessful,
				ConnectionEventType.SaslFailed,
				ConnectionEventType.ResourceBinded,
				ConnectionEventType.BindResourceFailed,
				ConnectionEventType.SessionBinded,
				ConnectionEventType.BindSessionFailed
			],
			lognListener
		);
		
		
		connectionMgr.requestCreateConnection({
			hold: 1,
			to: "example.com",
			ack: "1",
			ver: "1.6",
			wait: "10"
		});
		
		disabledInputController(true);
		
		
		$("#login_loader_img").css({"display": ""});
		var logging_in = $.i18n.prop("login.logging_in");
		$("#login_status").text(logging_in);
		
	};
	
	$("#username").keypress(function(event) {
		if (event.keyCode == 13) {
			loginAction();
		}
	});
	
	$("#password").keypress(function(event) {
		if (event.keyCode == 13) {
			loginAction();
		}
	});
	
	$("#button_login").click(loginAction);
	
	$.i18n.properties({
	    name: "i18n",
	    path: "/i18n/",
	    mode: "both",
	    language: "zh_CN",
	    callback: function() {

	        var loginUsername = $.i18n.prop("login.username");
	        var loginPassword = $.i18n.prop("login.password");
	        var rememberUsername = $.i18n.prop("login.remember_username");
	        var loginAction = $.i18n.prop("login.loginAction");
	        
	        $("#login_username").text(loginUsername);
	        $("#login_password").text(loginPassword);
	        $("#login_RememberUsername").text(rememberUsername);
	        $("#button_login").text(loginAction);
	    }
	});
	
	var layoutSettings = {
		Name: "Login",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "loginDiv",        
        Children:[{
			Name: "Fill",
			Dock: $.layoutEngine.DOCK.FILL,
	 		EleID: "loginDiv"
		}]
	};
	$.layoutEngine(layoutSettings);
});

function disabledInputController(bool) {
	$("#button_login").attr("disabled", bool);
	$("#username").attr("disabled", bool);
	$("#password").attr("disabled", bool);
	$("#remember_username").attr("disabled", bool);
}

function saslSuccess() {
	if ($("#remember_username").attr("checked") == true) {
		var date=new Date();
		var ms =365 * 24 * 3600 * 1000;
		date.setTime(date.getTime()+ms);
		Cookies.set("username", $("#username").val(), date);
	} else {
		Cookies.remove("username");
	}
}

function loginSuccess() {
	$("#loginDiv").css("display", "none");
	
	$.include(["/scripts/lib/jquerycontextmenu/jquery.contextMenu.css",
				"/scripts/lib/jquerycontextmenu/jquery.contextMenu.js"
				], function(){
		$.include(["/scripts/mainui.js"]);
	});
	
}