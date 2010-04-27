MainUI = {};
MainUI.appMenuEvents = new Array();
MainUI.init = function() {
	
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
			"<li class=''><a href='#personal'>Personal</a></li>" +
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
			
			var eventInfo = MainUI.appMenuEvents[action];
			if (eventInfo) {
				var handler = eventInfo.handler;
				if (handler) {
					handler();
				}
				MainUI.removeAppEventInfo(action);
				return;
			}
			
			
			if (action == "imservices") {
				ImService.show();
			} else if (action == "shopServices") {
				ShopService.show();
			} else if (action == "mapServices") {
				MapService.show();
			} else if (action  == "personal") {
				Personal.show();
			}
			
			
		}
	);

	var connectionMgr = XmppConnectionMgr.getInstance();
	connectionMgr.addConnectionListener([
			ConnectionEventType.ConnectionClosed
		],
		function(event) {
			appMenu.children("img").attr("src", "/resource/status/unavailable.png");
			alert($.i18n.prop("app.connectionClosed", "连接已断开"));
		}
	);
	
	ImService.init();
	ShopService.init();
	MapService.init();
	Personal.init();
	
	ImService.show();
}

MainUI.addAppEventInfo = function(eventInfo) {
	var eventId = eventInfo.eventId;
	if (eventId == null) {
		return;
	}
	if (MainUI.appMenuEvents[eventId] || $("#" + eventId)[0]) {
		MainUI.updateAppEventInfo(eventInfo);
		return;
	}
	
	var eventName = eventInfo.eventName;
	eventName = mCutStr(eventName, 7);
	var eventCss = eventInfo.css;
	var appMenu = $("#appMenuItems");
	appMenu.prepend("<li id='" + eventId + "' class='" + eventCss + "'>" +
						"<a href='#" + eventId + "'>" + 
							eventName + "</a></li>");
	
	MainUI.appMenuEvents[eventId] = eventInfo;
}

MainUI.updateAppEventInfo = function(eventInfo) {
	var eventId = eventInfo.eventId;
	if (eventId == null) {
		return;
	}

	var item = $("#" + eventId);
	item.removeClass();
	item.addClass(eventInfo.css);
	item.children("a").text(eventInfo.eventName);
	
	
	MainUI.appMenuEvents[eventId] = eventInfo;
}
MainUI.removeAppEventInfo = function(eventId) {
	delete MainUI.appMenuEvents[eventId];
	$("#" + eventId).remove();
}


// start of IMService
ImService = {};
ImService.init = function() {
	
	var imservices = $("<div id='imservices'></div>");
	
	// im tabs
	var imTop = $("<div id='imTop'></div>");
	// contact tab
	var contactTab = $("<b id='contact-tab' class='marginpadding'></b>");
	contactTab.attr("type", "contact");
	contactTab.addClass("sexybutton");
	contactTab.text($.i18n.prop("imservices.contact", "联系人"));
	imTop.append(contactTab);
	
	var chatTab = $("<b id='chat-tab' class='marginpadding'></b>");
	// chat tab
	chatTab.attr("type", "chat");
	chatTab.addClass("sexybutton");
	chatTab.text($.i18n.prop("imservices.chat", "聊天"));
	imTop.append(chatTab);
	
	// tabs click event
	imTop.find("b:first").addClass("sexysimple sexyteal");
	imTop.find("b").click(function(){
		$(this).addClass("sexysimple sexyteal").siblings("b").removeClass("sexysimple sexyteal");
		var type = $(this).attr("type");
		var centerchildren = $("#imCenter").children();
		$.each(centerchildren, function(index, value) {
			var jqEl = $(value);
			if (type == jqEl.attr("type")) {
				jqEl.show();
			} else {
				jqEl.hide();
			}
		});
		if (type == "contact") {
			$.layoutEngine(imContactlayoutSettings);
		} else if (type == "chat") {
			$.layoutEngine(imChatlayoutSettings);
			$("#chat-scroller-header").find("span:first").click();
		}
	});

	
	var imCenter = $("<div id='imCenter'></div>");
	
	// user info ,username,status,photo
	var userinfo = $("<div id='userinfo'></div>");
	userinfo.attr("type", "contact");
	
	var userinfotable = $("<table style='padding-left:5px;'>" +
			"<tbody>" +
				"<tr>" +
					"<td>" +
						"<img id='userphoto' src='/resource/userface.bmp' width='50' height='50' />" + 
					"</td>" +
					"<td>" +
						"<table style='padding-left:5px'>" +
							"<tbody>" +
								"<tr>" +
									"<td>" +
										"<img id='user-status-img' src='/resource/status/unavailable.png' />" +
										"<img id='user-status-menu' src='/resource/statusmenu.png' style='padding-left:2px;padding-right:10px;' />" +
										"<span id='userinfo-username'>Noah</span>" +
									"</td>" +
								"</tr>" +
								"<tr style='height:30px;'>" +
									"<td>" +
										"<div id='user-status-message'>available</div>" +
									"</td>" +
								"</tr>" +
							"</tbody>" +
						"</table>" +
					"</td>" +
				"</tr>" +
			"</tbody>" +
		"</table>");

					
	// user's status menu 
	var statusMenu = $("<ul id='myMenu' class='contextMenu'>" +
			"<li class='available'><a href='#available'>" + $.i18n.prop("imservices.status.available", "在线") + "</a></li>" +
			"<li class='away'><a href='#away'>" + $.i18n.prop("imservices.status.away", "离开") + "</a></li>" +
			"<li class='chat'><a href='#chat'>" + $.i18n.prop("imservices.status.chat", "空闲") + "</a></li>" +
			"<li class='dnd'><a href='#dnd'>" + $.i18n.prop("imservices.status.dnd", "忙碌") + "</a></li>" +
			"<li class='xa'><a href='#xa'>" + $.i18n.prop("imservices.status.xa", "离开") + "</a></li>" +
	"</ul>");
	statusMenu.hide();
	
	
	
	userinfo.append(statusMenu);
	userinfo.append(userinfotable);
	
	
	var contactlist = $("<div id='contactlist'></div>");
	contactlist.attr("type", "contact");
	
	// start of chat html
	
	var chatScrollHeader = $("<div id='chat-scroller-header'></div>");
	chatScrollHeader.attr("type", "chat");
	
	var chatScrollBody = $("<div id='chat-scroller-body'></div>");
	chatScrollBody.attr("type", "chat");
	
	var chatPanel = $("<div id='chat-panel'></div>");	
	chatScrollBody.append(chatPanel);
	
	
	imCenter.append(userinfo);
	imCenter.append(contactlist);
	
	imCenter.append(chatScrollHeader);
	imCenter.append(chatScrollBody);
	
	
	var imTopHeight = 40;
	
	imContactlayoutSettings = {
		Name: "Main",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "main",        
        Children:[{
			Name: "Fill",
			Dock: $.layoutEngine.DOCK.FILL,
	 		EleID: "imservices",
	 		Children:[{
	 			Name: "Top2",
				Dock: $.layoutEngine.DOCK.TOP,
				EleID: "imTop",
				Height: imTopHeight
	 		},{
	 			Name: "Fill2",
				Dock: $.layoutEngine.DOCK.FILL,
		 		EleID: "imCenter",
		 		Children:[{
		 			Name: "Top3",
					Dock: $.layoutEngine.DOCK.TOP,
					EleID: "userinfo",
					Height: 70
		 		},{
		 			Name: "Fill3",
					Dock: $.layoutEngine.DOCK.FILL,
					EleID: "contactlist",
		 		}]
	 		}]
		}]
	};
	
	imChatlayoutSettings = {
		Name: "Main",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "main",        
        Children:[{
			Name: "Fill",
			Dock: $.layoutEngine.DOCK.FILL,
	 		EleID: "imservices",
	 		Children:[{
	 			Name: "Top2",
				Dock: $.layoutEngine.DOCK.TOP,
				EleID: "imTop",
				Height: imTopHeight
	 		},{
	 			Name: "Fill2",
				Dock: $.layoutEngine.DOCK.FILL,
		 		EleID: "imCenter",
		 		Children:[{
		 			Name: "Top3",
					Dock: $.layoutEngine.DOCK.TOP,
					EleID: "chat-scroller-header",
					Height: 30
		 		},{
		 			Name: "Fill3",
					Dock: $.layoutEngine.DOCK.FILL,
					EleID: "chat-scroller-body"
		 		}]
	 		}]
		}]
	};
	
	imservices.append(imTop);
	imservices.append(imCenter);
	
	$("#main").append(imservices);
	imservices.hide();
	
	$.layoutEngine(imContactlayoutSettings);

	var statusMenuHandler = function(action, el, pos) {
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (conn) {
			var currentPresence = conn.currentPresence;
			if (currentPresence != null) {
				var showType = currentPresence.getShow();
				if (showType == null && currentPresence.isAvailable()) {
					showType = PresenceShow.AVAILABLE;
				}
				if (showType == action) {
					return;
				}
			}
			
			var presence = new Presence(PresenceType.AVAILABLE);
			if (action == "away") {
				presence.setShow(PresenceShow.AWAY);
			} else if (action == "chat") {
				presence.setShow(PresenceShow.CHAT);
			} else if (action == "dnd") {
				presence.setShow(PresenceShow.DND);
			} else if (action == "xa") {
				presence.setShow(PresenceShow.XA);
			}
			
			
			var statusMess = $("#user-status-message");
			if (statusMess.attr("emptyStatus") == "false") {
				presence.setUserStatus(statusMess.text());
			} 
			conn.changeStatus(presence);
			var statusInfo = getStatusInfo(presence);
			$("#user-status-img").attr("src", statusInfo.imgPath);
			var presenceStatus = presence.getUserStatus();
			var statusMessContent = (presenceStatus == null) ? statusInfo.statusMessage : presenceStatus;
			statusMess.text(statusMessContent);
		}
	};

	$("#user-status-menu").contextMenu({
			menu: 'myMenu',
			leftButton: true,
			menulocFunc: function() {
				var menuX = $("#user-status-img").offset().left;
				var menuY = $("#user-status-img").offset().top - imTopHeight + $("#user-status-img").height();
				return {x: menuX, y: menuY};
			}
		}, statusMenuHandler
	);
	
	$("#user-status-img").contextMenu({
			menu: 'myMenu',
			leftButton: true,
			menulocFunc: function() {
				var menuX = $("#user-status-img").offset().left;
				var menuY = $("#user-status-img").offset().top - imTopHeight + $("#user-status-img").height();
				return {x: menuX, y: menuY};
			}
		}, statusMenuHandler
	);
	
	contactTab.click();
	
	var statusMessageClickFunc = function(){
		
		$(this).unbind("click");
		var statusMessage = $(this);
		var oldmessage = $(this).text();
		var inputStatusMessage = $("<input id='input-status-message' type='text' value='" + oldmessage + "'/>");
		
		
		inputStatusMessage.keypress(function(event) {
			if (event.keyCode == 13) {
				$(this).blur();
			}
		});
		inputStatusMessage.bind("blur", function(){
			statusMessage.empty();
			statusMessageVal = $(this).val();
			var connectionMgr = XmppConnectionMgr.getInstance();
			if (statusMessageVal != oldmessage) {
				var conn = connectionMgr.getAllConnections()[0];
				if (conn) {
					var currentPres = conn.currentPresence;
					if (currentPres == null) {
						currentPres = new Presence(PresenceType.AVAILABLE);
					}
					currentPres.setStanzaId(null);
					currentPres.setUserStatus(statusMessageVal);
					conn.changeStatus(currentPres);
				}
			}
			
			if (statusMessageVal == null || statusMessageVal == "") {
				var conn = connectionMgr.getAllConnections()[0];
				if (conn) {
					var imgPathAndStatusMess = getStatusInfo(conn.currentPresence);
					statusMessageVal = imgPathAndStatusMess.statusMessage;
				}
				statusMessage.attr("emptyStatus", true);
			} else {
				statusMessage.attr("emptyStatus", false);
			}
			statusMessage.text(statusMessageVal);
			statusMessage.bind("click", statusMessageClickFunc);
			
		});
		$(this).empty();
		$(this).append(inputStatusMessage);
		inputStatusMessage.select();
		inputStatusMessage[0].focus();
	};
	$("#user-status-message").bind("click", statusMessageClickFunc);
	
		
	var connectionMgr = XmppConnectionMgr.getInstance();
	
	connectionMgr.addConnectionListener([
			ConnectionEventType.ContactUpdated,
			ConnectionEventType.ContactRemoved,
			ConnectionEventType.ContactStatusChanged,
			ConnectionEventType.ChatCreated
		],
		
		function(event) {
			var contact = event.contact;
			var eventType = event.eventType;
			if (eventType == ConnectionEventType.ContactUpdated
				|| eventType == ConnectionEventType.ContactStatusChanged) {
				updateContact(contactlist, contact);
			} else if (eventType == ConnectionEventType.ContactRemoved) {
				removeContact(contactlist, contact);
			} else if (eventType == ConnectionEventType.ChatCreated) {
				var connection = event.connection;
				var bareJID = event.chat.bareJID;
				var contact = connection.getContact(bareJID);
				var showName = (contact) ? contact.getShowName() : bareJID.toBareJID();
				
				createChatHtml(chatScrollHeader, chatPanel, false, {
					jid: bareJID.toBareJID(),
					showName: showName
				});
				if (chatScrollHeader.children().size() == 1) {
					chatScrollHeader.children().click();
				}
			}
		}
	);
	
	var conn = connectionMgr.getAllConnections()[0];
	if (conn) {
		conn.queryRoster();
		if (conn.initPresence) {
			conn.changeStatus(conn.initPresence);
			var imgPathAndStatusMess = getStatusInfo(conn.initPresence);
			
			$("#user-status-img").attr("src", imgPathAndStatusMess.imgPath);
			var statusMessage = imgPathAndStatusMess.statusMessage;
			if (conn.initPresence.getUserStatus() != null) {
				statusMessage = conn.initPresence.getUserStatus();
			}
			$("#user-status-message").text(statusMessage);
			
		}
		$("#userinfo-username").text(conn.getJid().toBareJID());
		
		var vCardIq = new Iq(IqType.GET);
		vCardIq.setTo(new JID(conn.getJid().getNode(), conn.getJid().getDomain(), null));
		vCardIq.addPacketExtension(new IqVCard());
		conn.handleStanza({
			filter: new PacketIdFilter(vCardIq.getStanzaId()),
			timeout: Christy.loginTimeout,
			handler: function(iqResponse) {
				if (iqResponse.getType() == IqType.RESULT) {
					var vCard = iqResponse.getPacketExtension(IqVCard.ELEMENTNAME, IqVCard.NAMESPACE);
					if (vCard.getNickName()) {
						$("#userinfo-username").text(vCard.getNickName());
					}
					if (vCard.hasPhoto()) {
						$("#userphoto").attr("src", "data:" + vCard.getPhotoType() + ";base64," + vCard.getPhotoBinval());
					}
				}
			}
		});
		
		conn.sendStanza(vCardIq);
	}
}

ImService.show = function() {
	var imservices = $("#imservices");
	imservices.siblings().hide();
	imservices.show();
	
	var imTop = $("#imTop");
	imTop.children(".sexysimple").click();
}


function createContactJqObj(newContact) {
	
	var newBareJid = newContact.getBareJid();
	
	var showName = (newContact.getNickname()) ? newContact.getNickname() : newBareJid.toBareJID();
	var statusMessage = $.i18n.prop("imservices.status.unavailable", "离线");
	var newContactJqObj = $("<div>" +
								"<table style='width:100%;'>" +
									"<tr>" +
										"<td >" +
											"<img status-img='true' src='/resource/status/unavailable.png'/>" +
										"</td>" +
										"<td style='width:100%;'>" +
											"<table>" +
												"<tr>" +
													"<td>" +
														"<div showname='true'>" + showName + "</div>" +
													"</td>" +
												"</tr>" +
												"<tr>" +
													"<td>" +
														"<div status-message='true'>" + statusMessage + "</div>" +
													"</td>" +
												"</tr>" +
											"</table>" + 
										"</td>" +
										"<td>" +
											"<img src='/resource/statusmenu.png'/>" +
										"</td>" +
									"<tr/>" +
								"</table>" +
							"</div>");
							

	
	newContactJqObj.attr("contactJid", newBareJid.toPrepedBareJID());
	newContactJqObj.attr("statusCode", 0);
	
	newContactJqObj.click(function(){
		var contactJid = $(this).attr("contactJid");
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (conn) {
			var contact = conn.getContact(JID.createJID(contactJid));
			var chatScrollHeader = $("#chat-scroller-header");
			var chatPanel = $("#chat-panel");
			createChatHtml(chatScrollHeader, chatPanel, true, {
				jid: contactJid,
				showName: contact.getShowName()
			});
		}
		
		
	});
	
	return newContactJqObj;
}



function updateContact(contactlistJqObj, contact) {
	var bareJid = contact.getBareJid();
	var contactJqObj = contactlistJqObj.find("div[contactjid='" + bareJid.toPrepedBareJID() + "']");
	
	var addContact = false;
	
	if (contactJqObj.length == 0) {
		addContact = true;
	} 
	
	contactJqObj.remove();
	
	contactJqObj = createContactJqObj(contact);

	var statusImgSrc = null;
	var statusMessage = null;
	if (contact.isResourceAvailable()) {
		var userResource = contact.getMaxPriorityResource();
		var presence = userResource.currentPresence;
		
		var statusInfo = getStatusInfo(presence);
		contactJqObj.attr("statusCode", statusInfo.statusCode);
		statusImgSrc = statusInfo.imgPath;
		statusMessage = statusInfo.statusMessage;
		
		if (presence.getUserStatus() != null) {
			statusMessage = presence.getUserStatus();
		}
	} else {
		statusImgSrc = "/resource/status/unavailable.png";
		statusMessage = $.i18n.prop("imservices.status.unavailable", "离线");
		contactJqObj.attr("statusCode", 0);
	}
	
	var statusImg = contactJqObj.find("img[status-img]");
	statusImg.attr("src", statusImgSrc);
	
	var showName = (contact.getNickname()) ? contact.getNickname() : bareJid.toBareJID();
	var showNameJqObj = contactJqObj.find("div[showname]");
	showNameJqObj.text(showName);
	
	var statusMessageJqObj = contactJqObj.find("div[status-message]");
	statusMessageJqObj.text(statusMessage);
	
	var groupNames = contact.getGroups();
	if (contact.getGroups().length == 0) {
		groupNames[0] = "general";
	}
	
	//add contact to group
	for (j = 0; j < groupNames.length; ++j) {
		var groupName = groupNames[j];
		
		var groupJqObj = contactlistJqObj.find("div[groupname='" + groupName + "']");
		if (groupJqObj.length == 0) {
			groupJqObj = addGroup(contactlistJqObj, groupName);
		}
		var inserted = false;
		var contacts = groupJqObj.children("[contactjid]");
		$.each(contacts, function(index, value) {		
			var oldContactJqObj = $(value);
			var oldContactStatusCode = oldContactJqObj.attr("statusCode");
			var contactStatusCode = contactJqObj.attr("statusCode");
			
			if (contactStatusCode > oldContactStatusCode) {
				contactJqObj.clone(true).insertBefore(oldContactJqObj);
				inserted = true;
				return false;
			} else if (contactStatusCode == oldContactStatusCode) {
				var oldBareJid = oldContactJqObj.attr("contactJid");
				if (bareJid.toPrepedBareJID() < oldBareJid) {
					contactJqObj.clone(true).insertBefore(oldContactJqObj);
					inserted = true;
					return false;
				}
			}
		});
		if (!inserted) {
			groupJqObj.append(contactJqObj.clone(true));
		}	
	}
	
	//update group
	var groupJqObjs = contactlistJqObj.children("[groupname]");
	$.each(groupJqObjs, function(index, value) {		
		var groupJqObj = $(value);
		//remove emtpy group
		var contacts = groupJqObj.children("[contactJid]");
		if (contacts.length == 0) {
			groupJqObj.remove();
		} else {
			// calculate online user
			var onlineCount = 0;
			$.each(contacts, function(index, valueContact) {		
				var groupContact = $(valueContact);
				var status = groupContact.attr("statusCode");
				if (status > 0) {
					++onlineCount;
				}
			});
			var groupN = groupJqObj.attr("groupname");
			groupJqObj.children(":first").text(groupN + "(" + onlineCount + "/" + contacts.length + ")");
			
		}
	});
		
	if (addContact 
		&& $("#contactlist").is(":visible")) {
		$.layoutEngine(imContactlayoutSettings);
	}
	
	// TODO update chat tab
}

function addGroup(contactlistJqObj, groupName) {
	var newGroupJqObj = $("<div></div>").attr("groupname", groupName);
	var groupLabel = $("<div id='" + groupName + "-label' class='contactGroup'></div>").text(groupName);
	
	newGroupJqObj.append(groupLabel);
	groupLabel.click(function(){
		var groupContacts = newGroupJqObj.children("[contactJid]");
		if (groupContacts.is(":visible")) {
			groupContacts.hide();
		} else {
			groupContacts.show();
		}
	});
	
	var inserted = false;
	var groupJqObjs = contactlistJqObj.children("[groupname]");
	$.each(groupJqObjs, function(index, value) {		
		var oldGroupJqObj = $(value);
		var oldGroupName = oldGroupJqObj.attr("groupname");
		if (groupName < oldGroupName) {
			newGroupJqObj.insertBefore(oldGroupJqObj);
			inserted = true;
			return false;
		}
	});
	if (!inserted) {
		contactlistJqObj.append(newGroupJqObj);
	}
	return newGroupJqObj;
}

function removeContact(contactlistJqObj, contact) {
	var bareJid = contact.getBareJid();
	var contactEl = contactlistJqObj.children("div[contactJid='" + bareJid.toPrepedBareJID() + "']");
	var contactJqObj = $(contactEl);
	contactJqObj.remove();
}

function createChatHtml(chatScrollHeader, chatPanel, showChatPanel, contactInfo) {
	
	var chatPanelTab = chatScrollHeader.children("span[tabContactJid='" + contactInfo.jid + "']");
	if (chatPanelTab[0] == null) {
		chatPanelTab = $("<span tabContactJid='" + contactInfo.jid + "'>" + contactInfo.showName + "</span>");
	
		var chatHandlerFunc = function(){
			$("span[tabContactJid]").removeClass('selected');
			$(this).addClass("selected");	
			$(this).removeClass("hasNewMessage");
			var currentChatPanel = $("#chat-panel > div[chatPanelId=" + $(this).attr('tabContactJid') + "-chatPanel]");
			currentChatPanel.siblings().hide();	
			currentChatPanel.show();
			$.layoutEngine(imChatlayoutSettings);
			var messageArea = currentChatPanel.find("div[messagearea]");
			if (messageArea.is(":visible")) {
				scrollToWindowBottom();
			}
			MainUI.removeAppEventInfo(StringUtils.hash(contactInfo.jid, "md5"));
		};
		
		chatPanelTab.click(chatHandlerFunc);
		chatScrollHeader.append(chatPanelTab);
										
		var contactChatPanel = $("<div chatPanelId='" + contactInfo.jid + "-chatPanel' style='display:none;'>" +
									"<table style='width:100%;height:100%;'>" +
										"<tr style='height:100%;'>" +
											"<td style='padding-left:7px;'>" +
												"<div messagearea='1' style='width:100%;height:100%;word-break:break-all;'></div>" +
											"</td>" +
										"</tr>" +
										"<tr>" +
											"<td>" +
												"<table>" +
													"<tr>" +
														"<td>" +
															"<button>" + $.i18n.prop("imservices.action.close", "关闭") + "</button>" +
														"</td>" +
														"<td style='width:100%;'>" +
															"<input type='text' style='width:100%;'/>" +
														"</td>" +
														"<td>" +
															"<button class='sexybutton sexysimple sexymygray'>" +
																$.i18n.prop("imservices.action.send", "发送") +
															"</button>" +
														"</td>" +
													"</tr>" +
												"</table>" +
											"</td>" +
										"</tr>" +
									"</table>" +
								"</div>");
								
		var controlBar = contactChatPanel.find("table:last");
									
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		var contactJid = JID.createJID(contactInfo.jid);
		var chat = conn.getChat(contactJid, true);
		
		var messageArea = contactChatPanel.find("div[messagearea]");
		
		var messageReceivedHandler = function(event) {
			var eventChat = event.chat;
			if (eventChat != chat) {
				return;
			}
			var contact = conn.getContact(eventChat.bareJID);
			var showName = (contact) ? contact.getShowName() : eventChat.bareJID.getNode();
			var message = event.stanza;
			messageArea.append("<div class='contactMessage'>" + showName + ":" + message.getBody() + "</div>");
			$.layoutEngine(imChatlayoutSettings);
			if (messageArea.is(":visible")) {
				scrollToWindowBottom();
			}

			if (!contactChatPanel.is(":visible")) {
				chatPanelTab.addClass("hasNewMessage");
				MainUI.addAppEventInfo({
					eventId: StringUtils.hash(eventChat.bareJID.toPrepedBareJID(), "md5"),
					eventName: showName,
					handler: function() {
						$("#chat-tab").click();
						chatPanelTab.click();
					}
				});
			}
		};
		
		connectionMgr.addConnectionListener([
				ConnectionEventType.MessageReceived
			],
			messageReceivedHandler
		);
			
		controlBar.find("button:first").click(function(){
			var selectedTab = chatPanelTab.prev();
			if (selectedTab[0] == null) {
				selectedTab = chatPanelTab.next();
			}
			
			selectedTab.click();
			
			chatPanelTab.remove();
			contactChatPanel.remove();
			conn.removeChat(contactJid);
			connectionMgr.removeConnectionListener(messageReceivedHandler);
		});
		
		var sendMessageAction = function(){
			var text = controlBar.find("input").val();
			if (text != null && text != "") {
				conn.sendChatText(chat, text);
				controlBar.find("input").val("");
				messageArea.append("<div class='myMessage'>" + $("#userinfo-username").text() + ":" + text + "</div>");
				$.layoutEngine(imChatlayoutSettings);
				if (messageArea.is(":visible")) {
					scrollToWindowBottom();
				}
				
				
			}
			
		};
		controlBar.find("input").keypress(function(event) {
			if (event.keyCode == 13) {
				sendMessageAction();
			}
		});
		controlBar.find("button:last").click(sendMessageAction);

	
//		contactChatPanel.append(controlBar);
		chatPanel.append(contactChatPanel);
	}
	
	if (showChatPanel) {
		$("#chat-tab").click();
		chatPanelTab.click();
	}
	
}

function getStatusInfo(presence) {
	var imgPath = "/resource/status/unavailable.png";
	var statusMessage = $.i18n.prop("imservices.status.unavailable", "离线");
	var statusCode = 0;
	if (presence != null && presence.isAvailable()) {
		if (presence.getShow() == PresenceShow.AWAY) {
			statusMessage = $.i18n.prop("imservices.status.away", "离开");
			imgPath = "/resource/status/away.png";
			statusCode = 3;
		} else if (presence.getShow() == PresenceShow.CHAT) {
			statusMessage = $.i18n.prop("imservices.status.chat", "空闲");
			imgPath = "/resource/status/chat.png";
			statusCode = 5;
		} else if (presence.getShow() == PresenceShow.DND) {
			statusMessage = $.i18n.prop("imservices.status.dnd", "忙碌");
			imgPath = "/resource/status/dnd.png";
			statusCode = 2;
		} else if (presence.getShow() == PresenceShow.XA) {
			statusMessage = $.i18n.prop("imservices.status.xa", "离开");
			imgPath = "/resource/status/xa.png";
			statusCode = 1;
		} else {
			statusMessage = $.i18n.prop("imservices.status.available", "在线");
			imgPath = "/resource/status/available.png";
			statusCode = 4;
		}
	}

	return {imgPath: imgPath, 
				statusMessage: statusMessage, 
				statusCode: statusCode};
	
	
}
// end of IMService


// start of ShopService

ShopService = {};
ShopService.shopSearchResult = {};

ShopService.init = function() {
	
	var shopservices = $("<div id='shopservices'></div>");	
	
	var controlBar = $("<table id='shopcontrolbar' style='width:100%;'>" +
							"<tr>" +
								"<td style='width:33%;float:left;'>" +
									"<button id='back' style='margin-left:0.2cm;' class='sexybutton sexysimple sexymygray sexysmall'>Back</button>" +
								"</td>" +
								"<td style='width:33%;'>" +
									"<div id='shopTitle'>Search</div>" +
								"</td>" +
								"<td style='float:right;'>" +
									"<button id='searchShop' style='margin-right:1cm;' class='sexybutton sexysimple sexymygray sexysmall'>Search Nearby</button>" +
									"<button id='maplistShop' style='margin-right:1cm;display:none;' class='sexybutton sexysimple sexymygray sexysmall'>Map List</button>" +
									"<button id='favorButton' style='display:none;margin-right:5px;' class='sexybutton sexysimple sexymygray sexysmall'>Favor</button>" +
									"<button id='commentShopButton' style='display:none;margin-right:5px;' class='sexybutton sexysimple sexymygray sexysmall'>Comment</button>" +
									"<button id='showshopinmap' style='margin-right:1cm;display:none;' class='sexybutton sexysimple sexymygray sexysmall'>Map</button>" +									
								"</td>" +
							"</tr>" +
						"</table>");
	
	shopservices.append(controlBar);
	
	var shopCenter = $("<div id='shopCenter'></div>");
	var shopSearch = $("<div id='shopsearch' title='Search' class='marginpadding'></div>");
	var searchBar = $("<div style='text-align:center;margin:auto;'>" +
							"<input type='text' style='margin-right:0.1cm;'/>" +
							"<button class='sexybutton sexysimple sexymygray sexysmall'>Search</button>" +
						"</div>");
	
	shopSearch.append(searchBar);
	
	shopSearch.append("<br/>");
	
	var popularArea = $("<table>" +
							"<tr>" +
								"<td>Popular Area Title</td>" +
							"</tr>" +
							"<tr>" +
								"<td>Popular Area1</td>" +
							"</tr>" +
							"<tr>" +
								"<td>Popular Area2</td>" +
							"</tr>" +
							"<tr>" +
								"<td>Popular Area3</td>" +
							"</tr>" +
							"<tr>" +
								"<td>Popular Area4</td>" +
							"</tr>" +
						"</table>");
	shopSearch.append(popularArea);	
	shopCenter.append(shopSearch);	

	
	var shopList = $("<div id='shoplist' title='Search Result' class='marginpadding' style='display:none;'>" +
						"<div></div>" +
						"<div id='pagination' class='pagination'></div>" +
					"</div>");
	shopCenter.append(shopList);
	
	var shopDetailJqObj = $("<div id='shopdetail' class='marginpadding' style='display:none;'></div>");
	shopCenter.append(shopDetailJqObj);
	
	shopservices.append(shopCenter);
	
	var commentShop = $("<table id='commentShop' style='display:none;width:100%;height:100%;'>" +
							"<tr>" +
								"<td style='float:left;'>" +
									"<span>" + $.i18n.prop("shopservice.totalScore", "总分：") + "</span><input id='shopScore' type='text' size='3'/>" +
								"</td>" +
							"</tr>" +
							"<tr>" +
								"<td style='float:left;'>" +
									"<div id='shopCommentItems'>" +
										"<div>" +
											"<span>" + $.i18n.prop("shopservice.taste", "口味：") + "</span><input id='shopTaste' type='text' size='7'/>" +
											"<span>" + $.i18n.prop("shopservice.environment", "环境：") + "</span><input id='shopEnvironment' type='text' size='7'/>" +
										"</div>" +
										"<div>" +
											"<span>" + $.i18n.prop("shopservice.service", "服务：") + "</span><input id='shopService' type='text' size='7'/>" +
										"</div>" +
									"</div>" +
								"</td>" +
							"</tr>" +
							"<tr style='width:100%;height:100%;'>" +
								"<td style='float:left;width:100%;height:100%;'>" +
									"<input id='commentContent' type='text' style='width:100%;height:100%;' />" +
								"</td>" +
							"</tr>" +
							"<tr >" +
								"<td>" +
									"<button id='submitShopComment'>" + $.i18n.prop("shopservice.submit", "提交") + "</button>" +
								"</td>" +
							"</tr>" +
						"</table>");
	shopCenter.append(commentShop);
	
	
	shopserviceTablayoutSettings = {
		Name: "Main",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "main",
		Children:[{
			Name: "Top",
			Dock: $.layoutEngine.DOCK.FILL,
			EleID: "shopservices",
			Children:[{
	 			Name: "Top2",
				Dock: $.layoutEngine.DOCK.TOP,
				EleID: "shopcontrolbar",
				Height: 30
			}, {
				Name: "Fill2",
				Dock: $.layoutEngine.DOCK.FILL,
		 		EleID: "shopCenter"
			}]
		}]
        
	};
	
	
	$("#main").append(shopservices);
	
	$("#back").click(function() {
		var showItem = $("#shopCenter").children(":visible");
		var prev = showItem.prev();
		if (prev[0]) {
			$("#shopTitle").text(prev.attr("title"));
			prev.siblings().hide();
			prev.show();
			
			var showButton = null;
			if (prev.attr("id") == "shopsearch") {
				showShopSearchPanel();
			} else if (prev.attr("id") == "shoplist") {
				showShopListPanel();
			} else if (prev.attr("id") == "shopdetail") {
				showShopDetailPanel();
			}
			$.layoutEngine(shopserviceTablayoutSettings);
		}
		
	});
	
	$("#searchShop").click(function() {
		var shopList = $("#shoplist");
		$(shopList.children()[0]).empty();
		
		getCurrentPosition(function(p) {
			
			searchShopsByLoc(shopList, p, 1, 5, true);
			
		}, function(){});		
		
		$("#shopTitle").text(shopList.attr("title"));
		shopList.siblings().hide();
		shopList.show();
		
		var nextButton = $(this).next();
		nextButton.siblings().hide();
		nextButton.show();
		
		$.layoutEngine(shopserviceTablayoutSettings);
	});
	
	
	
	$("#commentShopButton").click(function(){
		showShopComment(ShopService.currentShopDetail.basicInfo.name);
	});
	
	$("#submitShopComment").click(function() {
		submitShopComment();
	});
	
	$.layoutEngine(shopserviceTablayoutSettings);
	
}


ShopService.show = function() {
	var shopservices = $("#shopservices");
	shopservices.siblings().hide();
	shopservices.show();
	$.layoutEngine(shopserviceTablayoutSettings);
}

function createShopInfo(shopInfo) {
	var shopInfoTable = $("<table shopId='" + shopInfo.id + "'>" +
								"<tr>" +
									"<td>" +
										"<img src='" + shopInfo.imgSrc + "' width='50' height='50' />" +
									"</td>" +
									"<td>" +
										"<div>" + shopInfo.name + " "+ shopInfo.hasCoupon + "</div>" +
										"<div>" + shopInfo.overall.score + " "+ shopInfo.overall.perCapita + "</div>" +
										"<div>" + shopInfo.street + "</div>" +
									"</td>" +
								"</tr>" +
							"</table>");
	shopInfoTable.click(function(){
		var shopId = $(this).attr("shopId");
		$.ajax({
			url: "/shop/",
			dataType: "json",
			cache: false,
			type: "get",
			contentType: "application/x-www-form-urlencoded; charset=UTF-8",
			data: {
				action: "getshopdetail",
				shopid: shopId
			},
			success: function(shopDetail){
				ShopService.currentShopDetail = shopDetail;
				showShopDetail(shopDetail);
			},
			error: function (xmlHttpRequest, textStatus, errorThrown) {
				
			},
			complete: function(xmlHttpRequest, textStatus) {
				
			}
		});

	});
	
	return shopInfoTable;
}

function submitShopComment() {
	var shopDetail = ShopService.currentShopDetail;
	var connectionMgr = XmppConnectionMgr.getInstance();
	var conn = connectionMgr.getAllConnections()[0];
	if (conn) {
		var username = conn.getJid().getNode();
		var shopId = shopDetail.basicInfo.id;
		
		var comentContent = "";
		var shopScore = $("#shopScore");
		comentContent += "shopScore:" + shopScore.val() + ";";
		
		var commentShopTable = $("#commentShop");
		var items = commentShopTable.find("input[item]");

		$.each(items, function(index, value) {		
			var commentItem = $(value);
			var itemName = commentItem.attr("item");
			var itemValue = commentItem.val();
			comentContent += itemName + ":" + itemValue + ";";
		});
		
		var content = $("#commentContent");
		comentContent += "content" + ":" + content.val() + ";";
		
		
		$.ajax({
			url: "/shop/",
			cache: false,
			type: "get",
			contentType: "application/x-www-form-urlencoded; charset=UTF-8",
			data: {
				action: "submitShopComment",
				shopid: shopId,
				username: username,
				comentContent: comentContent
			},
			success: function(response){
				if (response.result == "success") {
					alert($.i18n.prop("shopservice.commentSuccess", "评论成功！"));
					$("#back").click();
				}
			},
			error: function (xmlHttpRequest, textStatus, errorThrown) {
				
			},
			complete: function(xmlHttpRequest, textStatus) {
				
			}
		});
		
	}
}

function showShopComment(shopTitleText) {
	
	var commentShopTable = $("#commentShop");

	var shopDetail = ShopService.currentShopDetail;
	var overall = shopDetail.overall;
	if (overall) {
		var shopCommentItems = $("#shopCommentItems");
		shopCommentItems.empty();
		var i = 1;
		for(var key in overall) {
			if ("score" != key) {
				shopCommentItems.append("<span>" + $.i18n.prop("shopservice." + key, "项目：") + "</span><input item='" + key + "' type='text' size='7'/>");
				if (i != 0 && i % 2 == 0) {
					shopCommentItems.append("<br/>");
				}
				++i;
			}
		}
	}
	

	commentShopTable.find("input").text("");
	commentShopTable.siblings().hide();
	commentShopTable.show();
	
	var shopTitle = $("#shopTitle");
	shopTitle.text(shopTitleText);
	
	var commentShopButton = $("#commentShopButton");
	commentShopButton.siblings().hide();
	commentShopButton.hide();
	$.layoutEngine(shopserviceTablayoutSettings);
	
}

function showShopDetail(shopDetail) {
	
	var shopDetailJqObj = $("#shopdetail");
	shopDetailJqObj.empty();
	shopDetailJqObj.attr("title", "上海1号私藏菜");
	var baseInfo = shopDetail.basicInfo;
	var overall = shopDetail.overall;
	var shopBaseInfo = $("<table shopId='" + baseInfo.id + "'>" +
							"<tr>" +
								"<td>" +
									"<img src='" + baseInfo.imgSrc + "' width='50' height='50' />" +
								"</td>" +
								"<td>" +
									"<div>" + baseInfo.name + " "+ baseInfo.hasCoupon + "</div>" +
									"<div>" + overall.score + " "+ overall.perCapita + "</div>" +
									"<span>Service:" + overall.service + "</span>" + 
									"<span>Taste:" + overall.taste + "</span>" + 
								"</td>" +
							"</tr>" +
						"</table>");
	shopDetailJqObj.append(shopBaseInfo);

	var contact = $("<table>" +
						"<tr>" +
							"<td style='word-break:break-all;'>" +
								shopDetail.intro +
							"</td>" +
						"</tr>" +
						"<tr>" +
							"<td style='word-break:break-all;'>" +
								"address:" + baseInfo.addr + "<br/>" +
								"phone:" + baseInfo.phone + 
							"</td>" +
						"</tr>" +
					"</table>");
					
	shopDetailJqObj.append(contact);
	
	shopDetailJqObj.append("<br/>");
	
	var comms = shopDetail.comments;
	var commsJqObj = $("<div></div>");
	for (i = 0; i < comms.length; ++i) {
		var onecomment = comms[i];
		commsJqObj.append("<table>" +
								"<tr>" +
									"<td>" + onecomment.username + "</td>" +
									"<td>" + new Date(onecomment.time).format("yyyy-MM-dd hh:mm:ss") + "</td>" +
								"</tr>" +
								"<tr>" +
									"<td colspan='2'>" + onecomment.score + "</td>" +
								"</tr>" +
								"<tr>" +
									"<td colspan='2'>" + onecomment.content + "</td>" +
								"</tr>" +
							"</table>");
	}
	
	$("#shopTitle").text(shopDetailJqObj.attr("title"));
	shopDetailJqObj.append(commsJqObj);
	shopDetailJqObj.siblings().hide();
	shopDetailJqObj.show();
	
	var showInMap = $("#showshopinmap");
	var favorButton = $("#favorButton");
	var commentShopButton = $("#commentShopButton");
	showInMap.siblings().hide();
	showInMap.show();
	favorButton.show();
	commentShopButton.show();
	
	$.layoutEngine(shopserviceTablayoutSettings);
}

function showShopSearchPanel() {
	var shopSearch = $("#shopsearch");
	shopSearch.siblings().hide();
	shopSearch.show();
	
	var searchShop = $("#searchShop");
	searchShop.siblings().hide();
	searchShop.show();
	
	$.layoutEngine(shopserviceTablayoutSettings);
}

function showShopListPanel() {
	var shopList = $("#shoplist");
	shopList.siblings().hide();
	shopList.show();
	
	var mapListShop = $("#maplistShop");
	mapListShop.siblings().hide();
	mapListShop.show();
	
	$.layoutEngine(shopserviceTablayoutSettings);
}

function showShopDetailPanel() {
	var shopDetail = $("#shopdetail");
	shopDetail.siblings().hide();
	shopDetail.show();
	
	var showShopInMap = $("#showshopinmap");
	
	showShopInMap.siblings().hide();
	showShopInMap.show();
	
	var commentShopButton = $("#commentShopButton");
	commentShopButton.show();
	
	var favorButton = $("#favorButton");
	favorButton.show();
	
	$.layoutEngine(shopserviceTablayoutSettings);
}

function searchShopsByLoc(shopList, p, page, count, updatePage) {
	
	$.ajax({
		url: "/shop/",
		dataType: "json",
		cache: false,
		type: "get",
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		data: {
			action: "search",
			latitude: p.coords.latitude,
			longitude: p.coords.longitude,
			page: page,
			count: count
		},
		success: function(searchResult){
			shopSearchResult = searchResult;
			$(shopList.children()[0]).empty();
			var shops = searchResult.shops;
			for (i = 0; i < shops.length; ++i) {
				var shopTable = createShopInfo(shops[i]);
				$(shopList.children()[0]).append(shopTable);
			}
			if (updatePage) {
				$("#pagination").pagination(10, {
					num_edge_entries: 1,
					num_display_entries: 8,
					items_per_page: count,
	                callback: function(page_id, jq) {
						searchShopsByLoc(shopList, p, page_id + 1, count, false);
	                }
	            });
			}
			
			$.layoutEngine(shopserviceTablayoutSettings);
		},
		error: function (xmlHttpRequest, textStatus, errorThrown) {
			
		},
		complete: function(xmlHttpRequest, textStatus) {
			
		}
	});
	
}
// end of ShopService


// start of MapService
MapService = {};
MapService.init = function() {
	var mapServices = $("<div id='mapservices'></div>");
	
	
	var controlBar = $("<table id='mapControlbar' style='width:100%;'>" +
							"<tr>" +
								"<td style='width:50%;'>" +
									"<button style='float:left;margin-left:0.2cm;' class='sexybutton sexysimple sexymygray sexysmall'>Route</button>" +
								"</td>" +
								"<td style='width:50%;'>" +
									"<button style='float:right;margin-right:1cm;' class='sexybutton sexysimple sexymygray sexysmall'>Result List</button>" +
								"</td>" +
							"</tr>" +
						"</table>");
						
	mapServices.append(controlBar);
	
	var map = $("<iframe id='mapcanvas' name='mapcanvas' width='100%' height='100%' scrolling='no' frameborder='0'>" +
					"</iframe>");
	mapServices.append(map);		
	
	$("#main").append(mapServices);
	mapServices.hide();
	
	mapserviceTablayoutSettings = {
		Name: "Main",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "main",
		Children:[{
			Name: "Top",
			Dock: $.layoutEngine.DOCK.FILL,
			EleID: "mapservices",
			Children:[{
	 			Name: "Top2",
				Dock: $.layoutEngine.DOCK.TOP,
				EleID: "mapControlbar",
				Height: 30
			}, {
				Name: "Fill2",
				Dock: $.layoutEngine.DOCK.FILL,
		 		EleID: "mapcanvas"
			}]
		}]  
	};
	$.layoutEngine(mapserviceTablayoutSettings);
}


MapService.show = function() {
	var mapServices = $("#mapservices");
	
	var mapcanvas = $("#mapcanvas");
	if (mapcanvas.attr("src") == null || mapcanvas.attr("src") == "") {
		mapcanvas.attr("src", "/mapcanvas.html");
	}
	
	mapServices.siblings().hide();
	mapServices.show();
	$.layoutEngine(mapserviceTablayoutSettings);
}

function mapFrameLoaded() {
	$.layoutEngine(mapserviceTablayoutSettings);
}
// end of MapService


// start of Personal

Personal = {};
Personal.init = function() {
	var personal = $("<div id='personal'></div>");
	
	// personal tabs
	var personalTop = $("<div id='personalTop'></div>");
	// contact tab
	var favorTab = $("<b id='favorTab' tabpanelid='personalFavor' class='marginpadding'></b>");
	favorTab.attr("type", "favor");
	favorTab.addClass("sexybutton");
	favorTab.text($.i18n.prop("personal.favor", "收藏"));
	personalTop.append(favorTab);
	
	
	// tabs click event
	personalTop.find("b:first").addClass("sexysimple sexyteal");
	personalTop.find("b").click(function(){
		$(this).addClass("sexysimple sexyteal").siblings("b").removeClass("sexysimple sexyteal");
		var tabpanelid = $(this).attr("tabpanelid");
		var tabpanel = $("#" + tabpanelid);
		tabpanel.siblings().hide();
		tabpanel.show();
		
		if (tabpanelid == "personalFavor") {
			showPersonalFavor();
		}
	});
	
	personal.append(personalTop);
	
	var personalCenter = $("<div id='personalCenter'></div>");
	
	var favorPanel = $("<div id='personalFavor'></div>");
	personalCenter.append(favorPanel);
	
	personal.append(personalCenter);
	
	$("#main").append(personal);}

function showPersonalFavor() {
	var privateXmlIq = new Iq(IqType.GET);
	
	var iqPrivateXml = new IqPrivateXml();
	
	var unknownEx = new UnknownExtension();
	unknownEx.setElementName("storage");
	unknownEx.setNamespace("storage:bookmarks");
	iqPrivateXml.setUnknownPacketExtension(unknownEx);
	
	privateXmlIq.addPacketExtension(iqPrivateXml);
	var connectionMgr = XmppConnectionMgr.getInstance();
	var conn = connectionMgr.getAllConnections()[0];
	if (conn) {
		conn.handleStanza({
			filter: new PacketIdFilter(privateXmlIq.getStanzaId()),
			timeout: Christy.loginTimeout,
			handler: function(iqResponse) {
				if (iqResponse.getType() == IqType.RESULT) {
					var privateXml = iqResponse.getPacketExtension(IqPrivateXml.ELEMENTNAME, IqPrivateXml.NAMESPACE);
					// TODO
					alert(privateXml.toXml());
				}
			}
		});
		
		conn.sendStanza(privateXmlIq);
	}
	
}

function createPersonalFavorShop(shopInfo) {
	var shopInfoTable = $("<table shopId='" + shopInfo.id + "'>" +
								"<tr>" +
									"<td>" +
										"<img src='" + shopInfo.imgSrc + "' width='50' height='50' />" +
									"</td>" +
									"<td>" +
										"<div>" + shopInfo.name + " "+ shopInfo.hasCoupon + "</div>" +
										"<div>" + shopInfo.overall.score + " "+ shopInfo.overall.perCapita + "</div>" +
										"<div>" + shopInfo.street + "</div>" +
									"</td>" +
								"</tr>" +
							"</table>");
	shopInfoTable.click(function(){
//		var shopId = $(this).attr("shopId");
//		$.ajax({
//			url: "/shop/",
//			dataType: "json",
//			cache: false,
//			type: "get",
//			contentType: "application/x-www-form-urlencoded; charset=UTF-8",
//			data: {
//				action: "getshopdetail",
//				shopid: shopId
//			},
//			success: function(shopDetail){
//				ShopService.currentShopDetail = shopDetail;
//				showShopDetail(shopDetail);
//			},
//			error: function (xmlHttpRequest, textStatus, errorThrown) {
//				
//			},
//			complete: function(xmlHttpRequest, textStatus) {
//				
//			}
//		});

	});
	
	return shopInfoTable;
}

Personal.show = function() {
	var personal = $("#personal");
	
	personal.siblings().hide();
	personal.show();
}


// end of Personal