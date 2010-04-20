var appMenuEvents = new Array();

(function() {
	
	var mainDiv = $("<div id='main'></div>").css({
		"position":"absolute",
		"top":"0px",
		"left":"0px",
		"width": "100%",
		"height": "100%"
	});

	
	
	var layoutSettings = {
		Name: "Main",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "main",
        Children:[{
			Name: "Fill",
			Dock: $.layoutEngine.DOCK.FILL,
	 		EleID: "main"
		}]
	};
	var appMenuItems = $("<ul id='appMenuItems' class='contextMenu' style='display:none'>" +
			"<li class=''><a href='#imservices'>IM</a></li>" +
			"<li class=''><a href='#shopServices'>Shop</a></li>" +
			"<li class=''><a href='#mapServices'>Map</a></li>" +
	"</ul>");
	
	var appMenu = $("<div id='appMenu'><img id='appMenuImg' src='/resource/status/available.png'/></div>");
	appMenu.css({"position": "fixed",
					"right":"0", 
					"top": "0", 
					"z-index": "8888",
					"padding": "10px"});
	
	
	$("body").append(appMenu);
	$("body").append(appMenuItems);
	$("body").append(mainDiv);
	
	$.layoutEngine(layoutSettings);
	
	appMenu.contextMenu({
			menu: 'appMenuItems',
			leftButton: true,
			menulocFunc: function() {
				var menuX = appMenu.offset().left + 10 - appMenuItems.width() + appMenu.width();
				var menuY = appMenu.offset().top + 10 + appMenu.height();
				return {x: menuX, y: menuY};
			}
			
		}, function(action, el, pos) {
			
			if (action == "imservices") {
				var imservices = $("#imservices");
				if (imservices[0]) {
					showImServices();
				}
				return;
			} else if (action == "shopServices") {
				var shopservices = $("#shopservices");
				if (shopservices[0]) {
					showShopServices();
				} else {
					$.include(["/scripts/shopservices.js"]);
				}
				
				return;
			} else if (action == "mapServices") {
				var mapServices = $("#mapservices");
				if (mapServices[0]) {
					showMapServices();
				} else {
					$.include(["/scripts/mapservices.js"]);
				}
				
				return;
			}
			
			var eventInfo = appMenuEvents[action];
			if (eventInfo) {
				var handler = eventInfo.handler;
				if (handler) {
					handler();
				}
				
			}
			removeAppEventInfo(action);
		}
	);

	var connectionMgr = XmppConnectionMgr.getInstance();
	connectionMgr.addConnectionListener([
			ConnectionEventType.ConnectionClosed
		],
		function(event) {
			appMenu.children("img").attr("src", "/resource/status/unavailable.png");
			alert($.i18n.prop("app.connectionClosed"));
		}
	);
	
	$.include(["/resource/imchat.css"], function(){
		$.include(["/scripts/imservices.js"]);
	});
	
	
})();

function addAppEventInfo(eventInfo) {
	var eventId = eventInfo.eventId;
	if (eventId == null) {
		return;
	}
	if (appMenuEvents[eventId] || $("#" + eventId)[0]) {
		updateAppEventInfo(eventInfo);
		return;
	}
	
	var eventName = eventInfo.eventName;
	eventName = mCutStr(eventName, 7);
	var eventCss = eventInfo.css;
	var appMenu = $("#appMenuItems");
	appMenu.prepend("<li id='" + eventId + "' class='" + eventCss + "'>" +
						"<a href='#" + eventId + "'>" + 
							eventName + "</a></li>");
	
	appMenuEvents[eventId] = eventInfo;
}

function updateAppEventInfo(eventInfo) {
	var eventId = eventInfo.eventId;
	if (eventId == null) {
		return;
	}

	var item = $("#" + eventId);
	item.removeClass();
	item.addClass(eventInfo.css);
	item.children("a").text(eventInfo.eventName);
	
	
	appMenuEvents[eventId] = eventInfo;
}

function removeAppEventInfo(eventId) {
	delete appMenuEvents[eventId];
	$("#" + eventId).remove();
}