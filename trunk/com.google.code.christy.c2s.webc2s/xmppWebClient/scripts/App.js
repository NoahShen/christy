$(document).ready(function() {
	
	$("#background").gradientz({
		start: "#CEF6CE",     // start color: default is the background color
		end: "#FFFFFF",     // end color: default is the background color.
		distance: 100,
		fillWidth: true
	});
	
	
	$.ImportBasePath = "/scripts/";
	
	// TODO test code
//	sessionBindedSuccess2();
//	return;
	
	disabledInputController(false);
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
			var usernameMessage = $.i18n.prop("login.username.missingMessage", "请输入用户名！");
			alert(usernameMessage);
			$("#username")[0].focus();
			return;
		}
		
		if (password == null || password.length == 0) {
			var pwdMessage = $.i18n.prop("login.password.missingMessage", "请输入密码！");
			alert(pwdMessage);
			$("#password")[0].focus();
			return;
		}
		
		disabledInputController(true);
		var connecting = $.i18n.prop("login.connecting", "正在连接...");
		$("#login_status").css("color", "black");
		$("#login_status").text(connecting);
		
		$.include(["lib/jClass.js", "xmpputils.js", "xmppcore.js"], function(){
			var connectionMgr = XmppConnectionMgr.getInstance();
			var listener = function(event){
				var conn = event.connection;
				var initPresence = new Presence(PresenceType.AVAILABLE);
				conn.login($("#username").val(), $("#password").val(), "Christy", initPresence);
				connectionMgr.removeConnectionListener(listener);
			};
			connectionMgr.addConnectionListener(ConnectionEventType.Created, listener);
			doLogin();
		});
		
	}
	
	var doLogin = function() {
		
		var connectionMgr = XmppConnectionMgr.getInstance();
		if (connectionMgr.isWorking()){
			return;
		}
		
		var lognListener = function(event){
			var type = event.eventType;
			var loginStatus = null;
			var loginFailed = false;
			if (type == ConnectionEventType.SaslSuccessful) {
				loginStatus = $.i18n.prop("login.saslsuccess", "验证成功...");
				saslSuccess();
			} else if (type == ConnectionEventType.SaslFailed) {
				loginStatus = $.i18n.prop("login.saslfailed", "验证失败...");
				loginFailed = true;
			} else if (type == ConnectionEventType.ResourceBinded) {
				loginStatus = $.i18n.prop("login.resourcebinded", "资源绑定成功...");
			} else if (type == ConnectionEventType.BindResourceFailed) {
				loginStatus = $.i18n.prop("login.bindresourcefailed", "资源绑定失败...");
				loginFailed = true;
			} else if (type == ConnectionEventType.SessionBinded) {
				loginStatus = $.i18n.prop("login.sessionbinded", "会话已绑定...");
				// TODO
				sessionBindedSuccess2();
				connectionMgr.removeConnectionListener(lognListener);
			} else if (type == ConnectionEventType.BindSessionFailed) {
				loginStatus = $.i18n.prop("login.bindsessionfailed", "会话绑定失败...");
				loginFailed = true;
			}
			
			$("#login_status").text(loginStatus);
			
			if (loginFailed) {
				$("#login_status").css("color", "red");
				$("#login_loader_img").css({"display": "none"});
				connectionMgr.removeAllConnections();
			}
			disabledInputController(!loginFailed);
			
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
			wait: "60"
		});
		
		var logging_in = $.i18n.prop("login.logging_in", "正在登录...");
		$("#login_status").text(logging_in);
		
		$("#login_loader_img").show();
		
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
	
	var loginUsername = $.i18n.prop("login.username", "用户名：");
	var loginPassword = $.i18n.prop("login.password", "密码：");
	var rememberUsername = $.i18n.prop("login.remember_username", "记住帐号");
	var login = $.i18n.prop("login.loginAction", "登录");
	
	$("#login_username").text(loginUsername);
	$("#login_password").text(loginPassword);
	$("#login_RememberUsername").text(rememberUsername);
	$("#button_login").text(login);
	        
//	$.i18n.properties({
//	    name: "i18n",
//	    path: "/i18n/",
//	    mode: "map",
//	    language: "zh_CN",
//	    callback: function() {
//
//	        var loginUsername = $.i18n.prop("login.username", "用户名：");
//	        var loginPassword = $.i18n.prop("login.password", "密码：");
//	        var rememberUsername = $.i18n.prop("login.remember_username", "记住帐号");
//	        var login = $.i18n.prop("login.loginAction", "登录");
//	        
//	        $("#login_username").text(loginUsername);
//	        $("#login_password").text(loginPassword);
//	        $("#login_RememberUsername").text(rememberUsername);
//	        $("#button_login").text(login);
//	    }
//	});
	
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

function sessionBindedSuccess() {
	$("#loginDiv").hide();
	
	var progressBar = $("<div style='position:fixed;top:10px;left:10px;'>" +
							"<div>Loading...</div>" +
							"<div class='progressbar'>" +
								"<div style='width:0%;' class='bar'></div>" +
							"</div>" +
						"</div>");
	
	$("body").append(progressBar);
	
	var files = ["lib/jquerycontextmenu/jquery.contextMenu.css",
					"lib/jquerycontextmenu/jquery.contextMenu.js",
					"lib/jquery_pagination/pagination.css",
					"lib/jquery_pagination/jquery.pagination.js",
					"geoutils.js",
					"mainui.css",
					"mainui.js"
				];
	
	var currentIndex = 0;

	var loadFile = function() {
		$.include(files[currentIndex], function(){
			++currentIndex;
//			progressBar.find(".bar").css("width", (currentIndex / files.length) * 100 + "%");
			progressBar.find(".bar").animate({
						width: (currentIndex / files.length) * 100 + "%"
					}, 
					100,
					"swing", 
					function() {
						if (currentIndex < files.length) {
							loadFile();
						} else {
							progressBar.hide();
							MainUI.init();
						}
					}
			);
			
			
			
		});
	}
	loadFile();
}

function sessionBindedSuccess2() {
	$("#loginDiv").remove();
	
	var progressBar = $("<div style='position:fixed;top:10px;left:10px;'>" +
							"<div>Loading...</div>" +
							"<div class='progressbar'>" +
								"<div style='width:0%;' class='bar'></div>" +
							"</div>" +
						"</div>");
	
	$("body").append(progressBar);
	
	var files = [
					// TODO start of test code
//					"lib/jClass.js", 
//					"xmpputils.js", 
//					"xmppcore.js",
					// TODO end of test code
					"component/tab/ui.tab.style.css",
					"component/tab/ui.tab.js",
					"component/jquery.syspanel.style.css",
					"component/jquery.syspanel.js",
					"component/jquery.pagination.style.css",
					"component/jquery.pagination.js",
					"geoutils.js",
					"lib/jquery.blockUI.js",
					"main.css",
					"main.js"
				];
	
	var currentIndex = 0;

	var loadFile = function() {
		$.include(files[currentIndex], function(){
			++currentIndex;
//			progressBar.find(".bar").css("width", (currentIndex / files.length) * 100 + "%");
			progressBar.find(".bar").animate({
						width: (currentIndex / files.length) * 100 + "%"
					}, 
					100,
					"swing", 
					function() {
						if (currentIndex < files.length) {
							loadFile();
						} else {
							progressBar.hide();
							Main.init();
						}
					}
			);
			
			
			
		});
	}
	loadFile();
}


/*******utils**********/
/**
* 时间对象的格式化;
*/
Date.prototype.format = function(format) {
	/*
	 * eg:format="YYYY-MM-dd hh:mm:ss";
	 */
	var o = {
		"M+" :  this.getMonth()+1,  //month
		"d+" :  this.getDate(),     //day
		"h+" :  this.getHours(),    //hour
		"m+" :  this.getMinutes(),  //minute
		"s+" :  this.getSeconds(), //second
		"q+" :  Math.floor((this.getMonth()+3)/3),  //quarter
		"S"  :  this.getMilliseconds() //millisecond
	}
	 
	if(/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	}
	 
	for(var k in o) {
		if(new RegExp("("+ k +")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length));
		}
	}
	return format;
}

function mCutStr(text, len){
    if(text.length < len) {
        return text;
    } else {
        var pos=0;
        for(i=0;i<len;i++) {
            (text.substr(i,1).charCodeAt(0) >= 160) ? i++ : "";
            pos++;
        }
        return text.substr(0,pos)+"...";
    }
}


function scrollToWindowBottom() {
	var c = window.document.body.scrollHeight;
	window.scroll(0,c); 
}

Array.prototype.contains = function(obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}

function isArray(o) {
  return Object.prototype.toString.call(o) === '[object Array]'; 
}

Cookies = {};
Cookies.set = function(name, value){
     var argv = arguments;
     var argc = arguments.length;
     var expires = (argc > 2) ? argv[2] : null;
     var path = (argc > 3) ? argv[3] : '/';
     var domain = (argc > 4) ? argv[4] : null;
     var secure = (argc > 5) ? argv[5] : false;
     document.cookie = name + "=" + escape (value) +
       ((expires == null) ? "" : ("; expires=" + expires.toGMTString())) +
       ((path == null) ? "" : ("; path=" + path)) +
       ((domain == null) ? "" : ("; domain=" + domain)) +
       ((secure == true) ? "; secure" : "");
};

Cookies.get = function(name){
    var arg = name + "=";
    var alen = arg.length;
    var clen = document.cookie.length;
    var i = 0;
    var j = 0;
    while(i < clen){
        j = i + alen;
        if (document.cookie.substring(i, j) == arg)
            return Cookies.getCookieVal(j);
        i = document.cookie.indexOf(" ", i) + 1;
        if(i == 0)
            break;
    }
    return null;
};

Cookies.remove = function(name) {
  if(Cookies.get(name)){
    var expdate = new Date(); 
    expdate.setTime(expdate.getTime() - (86400 * 1000 * 1)); 
    Cookies.set(name, "", expdate); 
  }
};

Cookies.getCookieVal = function(offset){
   var endstr = document.cookie.indexOf(";", offset);
   if(endstr == -1){
       endstr = document.cookie.length;
   }
   return unescape(document.cookie.substring(offset, endstr));
};
