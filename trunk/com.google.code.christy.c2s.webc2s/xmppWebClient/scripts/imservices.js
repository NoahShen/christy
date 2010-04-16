(function() {
		
	var imservices = $("<div id='imservices'></div>");
	
	// im tabs
	var imTop = $("<div id='imTop'></div>");
	// contact tab
	var contactTab = $("<b id='contact-tab' class='marginpadding'></b>");
	contactTab.attr("type", "contact");
	contactTab.addClass("sexybutton");
	contactTab.text($.i18n.prop("imservices.contact"));
	imTop.append(contactTab);
	
	var chatTab = $("<b id='chat-tab' class='marginpadding'></b>");
	// chat tab
	chatTab.attr("type", "chat");
	chatTab.addClass("sexybutton");
	chatTab.text($.i18n.prop("imservices.chat"));
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

	
	// search contact
//	var searchbar = $("<table style='padding-left:5px;width:100%;'>" +
//						"<tbody>" +
//							"<tr>" +
//								"<td>" +
//									"<input id='search-contact-input' type='text' style='width:100%;'/>" +
//								"</td>" +
//								"<td style='width:40px;'>" +
//									"<button id='search-contact-button' >Search</button>" +
//								"</td>" +
//							"</tr>" +
//						"</tbody>" +
//					"</table>");
					
	// user's status menu 
	var statusMenu = $("<ul id='myMenu' class='contextMenu'>" +
			"<li class='available'><a href='#available'>" + $.i18n.prop("imservices.status.available") + "</a></li>" +
			"<li class='away'><a href='#away'>" + $.i18n.prop("imservices.status.away") + "</a></li>" +
			"<li class='chat'><a href='#chat'>" + $.i18n.prop("imservices.status.chat") + "</a></li>" +
			"<li class='dnd'><a href='#dnd'>" + $.i18n.prop("imservices.status.dnd") + "</a></li>" +
			"<li class='xa'><a href='#xa'>" + $.i18n.prop("imservices.status.xa") + "</a></li>" +
	"</ul>");
	statusMenu.hide();
	
	
	
	userinfo.append(statusMenu);
	userinfo.append(userinfotable);
//	userinfo.append(searchbar);
	
	
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
	
	$.layoutEngine(imContactlayoutSettings);
	
	var menuX = $("#user-status-img").offset().left;
	var menuY = $("#user-status-img").offset().top - imTopHeight + $("#user-status-img").height();

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
			x: menuX,
			y: menuY
		}, statusMenuHandler
	);
	
	$("#user-status-img").contextMenu({
			menu: 'myMenu',
			leftButton: true,
			x: menuX,
			y: menuY
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
	
	
	//TODO test code
//	var contact1 = new XmppContact(new IqRosterItem(new JID("Noah", "example.com", "res"), "Noah"));
//	contact1.getRosterItem().addGroup("g1");
//	var contact2 = new XmppContact(new IqRosterItem(new JID("aa", "example.com", "res"), "aa"));
//	updateContact(contactlist, contact1);
//	updateContact(contactlist, contact2);
//	updateContact(contactlist, new XmppContact(new IqRosterItem(new JID("bb", "example.com", "res"), "bb")));
//	updateContact(contactlist, new XmppContact(new IqRosterItem(new JID("cc", "example.com", "res"), null)));
//	updateContact(contactlist, new XmppContact(new IqRosterItem(new JID("D", "example.com", "res"), "dd")));
//	updateContact(contactlist, new XmppContact(new IqRosterItem(new JID("ee", "example.com", "res"), null)));
//	updateContact(contactlist, new XmppContact(new IqRosterItem(new JID("ff", "example.com", "res"), null)));
//	updateContact(contactlist, new XmppContact(new IqRosterItem(new JID("gg", "example.com", "res"), null)));
//	updateContact(contactlist, new XmppContact(new IqRosterItem(new JID("hh", "example.com", "res"), null)));
//	updateContact(contactlist, new XmppContact(new IqRosterItem(new JID("ii", "example.com", "res"), "ii")));
//	updateContact(contactlist, new XmppContact(new IqRosterItem(new JID("jj", "example.com", "res"), null)));
//	updateContact(contactlist, new XmppContact(new IqRosterItem(new JID("KK", "example.com", "res"), null)));
//	
//	removeContact(contactlist, new XmppContact(new IqRosterItem(new JID("kk", "example.com", "res"), null)));
//	var presence = new Presence(PresenceType.AVAILABLE);
//	presence.setUserStatus("status");
//	presence.setShow(PresenceShow.AWAY);
//	contact1.addResource({
//		resource: "res1",
//		currentPresence: presence
//	});
//	contact1.getRosterItem().removeGroupName("g1");
//	
//	
//	var presence2 = new Presence(PresenceType.AVAILABLE);
//	presence2.setUserStatus("status2");
//	presence2.setShow(PresenceShow.AWAY);
//	contact2.addResource({
//		resource: "res2",
//		currentPresence: presence2
//	});
//	
//	updateContact(contactlist, contact1);
//	updateContact(contactlist, contact2);

	// TODO end of test
	
		// TODO test code
//	createChatHtml(chatScrollHeader, chatPanel, {
//		jid: "Noah1@example.com",
//		showName: "Noah1"
//	});
//	
//	createChatHtml(chatScrollHeader, chatPanel, {
//		jid: "Noah2@example.com",
//		showName: "Noah2"
//	});
	
	// end of chat html
})();


function createContactJqObj(newContact) {
	
	var newBareJid = newContact.getBareJid();
	
	var showName = (newContact.getNickname()) ? newContact.getNickname() : newBareJid.toBareJID();
	var statusMessage = $.i18n.prop("imservices.status.unavailable");
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
		var contact = conn.getContact(JID.createJID(contactJid));
		var chatScrollHeader = $("#chat-scroller-header");
		var chatPanel = $("#chat-panel");
		createChatHtml(chatScrollHeader, chatPanel, true, {
			jid: contactJid,
			showName: contact.getShowName()
		});
		
		
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
		statusMessage = $.i18n.prop("imservices.status.unavailable");
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
			removeAppEventInfo(StringUtils.hash(contactInfo.jid, "md5"));
		};
		
		chatPanelTab.click(chatHandlerFunc);
		chatScrollHeader.append(chatPanelTab);
		
//		var contactChatPanel = $("<div chatPanelId='" + contactInfo.jid + "-chatPanel' style='display:none;'>" +
//									"<div style='width:100%;height:100%;'>" +
//										"<div messagearea='1'></div>" +
//										"<div>&nbsp<br/>&nbsp</div>" +
//									"</div>" +
//								"</div>");
								
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
															"<button>" + $.i18n.prop("imservices.action.close") + "</button>" +
														"</td>" +
														"<td style='width:100%;'>" +
															"<input type='text' style='width:100%;'/>" +
														"</td>" +
														"<td>" +
															"<button class='sexybutton sexysimple sexymygray'>" +
																$.i18n.prop("imservices.action.send") +
															"</button>" +
														"</td>" +
													"</tr>" +
												"</table>" +
											"</td>" +
										"</tr>" +
									"</table>" +
								"</div>");
		
//		var controlBar = $("<table style='bottom:0pt;right:3pt;left:3pt;position:fixed;'>" +
//								"<tr>" +
//									"<td>" +
//										"<button style='float:left;'>Close</button>" +
//									"</td>" +
//									"<td style='width:100%;'>" +
//										"<input type='text' style='width:100%;'/>" +
//									"</td>" +
//									"<td>" +
//										"<button style='float:right;'>Send</button>" +
//									"</td>" +
//								"</tr>" +
//							"</table>");
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
				addAppEventInfo({
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
	var statusMessage = $.i18n.prop("imservices.status.unavailable");
	var statusCode = 0;
	if (presence != null && presence.isAvailable()) {
		if (presence.getShow() == PresenceShow.AWAY) {
			statusMessage = $.i18n.prop("imservices.status.away");
			imgPath = "/resource/status/away.png";
			statusCode = 3;
		} else if (presence.getShow() == PresenceShow.CHAT) {
			statusMessage = $.i18n.prop("imservices.status.chat");
			imgPath = "/resource/status/chat.png";
			statusCode = 5;
		} else if (presence.getShow() == PresenceShow.DND) {
			statusMessage = $.i18n.prop("imservices.status.dnd");
			imgPath = "/resource/status/dnd.png";
			statusCode = 2;
		} else if (presence.getShow() == PresenceShow.XA) {
			statusMessage = $.i18n.prop("imservices.status.xa");
			imgPath = "/resource/status/xa.png";
			statusCode = 1;
		} else {
			statusMessage = $.i18n.prop("imservices.status.available");
			imgPath = "/resource/status/available.png";
			statusCode = 4;
		}
	}

	return {imgPath: imgPath, 
				statusMessage: statusMessage, 
				statusCode: statusCode};
	
	
}