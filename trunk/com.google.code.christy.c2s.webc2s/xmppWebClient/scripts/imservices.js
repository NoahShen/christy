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
										"<div id='user-status-message'>status</div>" +
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
			"<li class='edit'><a href='#edit'>Edit</a></li>" +
			"<li class='cut separator'><a href='#cut'>Cut</a></li>" +
			"<li class='copy'><a href='#copy'>Copy</a></li>" +
			"<li class='paste'><a href='#paste'>Paste</a></li>" +
			"<li class='delete'><a href='#delete'>Delete</a></li>" +
			"<li class='quit separator'><a href='#quit'>Quit</a></li>" +
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

	$("#user-status-menu").contextMenu({
			menu: 'myMenu',
			leftButton: true,
			x: menuX,
			y: menuY
		},function(action, el, pos) {
			alert(action);
		}
	);
	
	$("#user-status-img").contextMenu({
			menu: 'myMenu',
			leftButton: true,
			x: menuX,
			y: menuY
		},function(action, el, pos) {
			alert(action);
		}
	);
	
	contactTab.click();
	
	var statusMessageClickFunc = function(){
		
		$(this).unbind("click");
		var statusMessage = $(this);
		var oldmessage = $(this).text();
		var messageChanged = false;
		var inputStatusMessage = $("<input id='input-status-message' type='text' value='" + oldmessage + "'/>");
		inputStatusMessage.select();
		inputStatusMessage.change(function(){
			messageChanged = true;
		});
		
		inputStatusMessage.keypress(function(event) {
			if (event.keyCode == 13) {
				inputStatusMessage.blur();
			}
		});
		inputStatusMessage.bind("blur", function(){
			statusMessage.empty();
			statusMessageVal = $(this).val();
			if (statusMessageVal == null || statusMessageVal == "") {
				var conn = connectionMgr.getAllConnections()[0];
				if (conn) {
					var imgPathAndStatusMess = getStatusInfo(conn.currentPresence);
					statusMessageVal = imgPathAndStatusMess.statusMessage;
				}
				
			}
			statusMessage.text(statusMessageVal);
			statusMessage.bind("click", statusMessageClickFunc);
			if (messageChanged) {
				var conn = connectionMgr.getAllConnections()[0];
				if (conn) {
					var currentPres = conn.currentPresence;
					if (currentPres == null) {
						currentPres = new Presence(PresenceType.AVAILABLE);
					}
					currentPres.setStanzaId(null);
					currentPres.setUserStatus($(this).val());
					conn.changeStatus(currentPres);
				}
			}
		});
		$(this).empty();
		$(this).append(inputStatusMessage);
		inputStatusMessage[0].focus();
	};
	$("#user-status-message").bind("click", statusMessageClickFunc);
	
		
	var connectionMgr = XmppConnectionMgr.getInstance();
	
	connectionMgr.addConnectionListener([
			ConnectionEventType.ContactUpdated,
			ConnectionEventType.ContactRemoved
		],
		
		function(event) {
			var contact = event.contact;
			var eventType = event.eventType;
			if (eventType == ConnectionEventType.ContactUpdated) {
				updateContact(contactlist, contact);
			} else if (eventType == ConnectionEventType.ContactRemoved) {
				removeContact(contactlist, contact);
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
	createChatHtml(chatScrollHeader, chatPanel, {
		jid: "Noah1@example.com",
		showName: "Noah1"
	});
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
		createChatHtml(chatScrollHeader, chatPanel, {
			jid: contactJid,
			showName: contact.getShowName()
		});
		
		
	});
	
	return newContactJqObj;
}



function updateContact(contactlistJqObj, contact) {
	var bareJid = contact.getBareJid();
	var contactEl = contactlistJqObj.find("div[contactJid='" + bareJid.toPrepedBareJID() + "']");
	
	var addContact = false;
	var contactJqObj = $(contactEl);
	
	if (contactJqObj.length == 0) {
		contactJqObj = createContactJqObj(contact);
		addContact = true;
	}
	
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
//		var contactJqObj2 = contactJqObj.clone(true);
		$.each(contacts, function(index, value) {		
			var oldContactJqObj = $(value);
			var oldContactStatusCode = oldContactJqObj.attr("statusCode");
			var contactStatusCode = contactJqObj.attr("statusCode");
			
			if (contactStatusCode > oldContactStatusCode) {
				contactJqObj.insertBefore(oldContactJqObj);
				inserted = true;
				return false;
			} else if (contactStatusCode == oldContactStatusCode) {
				var oldBareJid = oldContactJqObj.attr("contactJid");
				if (bareJid.toPrepedBareJID() < oldBareJid) {
					contactJqObj.insertBefore(oldContactJqObj);
					inserted = true;
					return false;
				}
			}
		});
		if (!inserted) {
			groupJqObj.append(contactJqObj);
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
}

function addGroup(contactlistJqObj, groupName) {
	var newGroupJqObj = $("<div></div>").attr("groupname", groupName).append();
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

function createChatHtml(chatScrollHeader, chatPanel, contactInfo) {
	
	var chatPanelTab = chatScrollHeader.children("span[tabContactJid='" + contactInfo.jid + "']");
	if (chatPanelTab[0] == null) {
		chatPanelTab = $("<span tabContactJid='" + contactInfo.jid + "'>" + contactInfo.showName + "</span>");
	
		var chatHandlerFunc = function(){
	
			$("span[tabContactJid]").removeClass('selected');
			$(this).addClass('selected');	
	
			var currentChatPanel = $("#chat-panel > div[chatPanelId=" + $(this).attr('tabContactJid') + "-chatPanel]");
			currentChatPanel.siblings().hide();	
			currentChatPanel.show();
			$.layoutEngine(imChatlayoutSettings);
		};
		
		chatPanelTab.click(chatHandlerFunc);
		chatScrollHeader.append(chatPanelTab);
		
		var contactChatPanel = $("<div chatPanelId='" + contactInfo.jid + "-chatPanel' style='display:none;'>" +
									"<div style='width:100%;height:100%;bottom:20pt;'>" +
										"a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>" +
										"<div>" +
											"&nbsp" +
										"</div>" +
										"<div>" +
											"&nbsp" +
										"</div>" +
									"</div>" +
								"</div>");
		
		var controlBar = $("<table style='bottom:0pt;right:3pt;left:3pt;position:fixed;'>" +
								"<tr>" +
									"<td>" +
										"<button href='#XXX' style='float:left;'>Close</button>" +
									"</td>" +
									"<td style='width:100%;'>" +
										"<input type='text' style='width:100%;'/>" +
									"</td>" +
									"<td>" +
										"<button style='float:right;'>Send</button>" +
									"</td>" +
								"</tr>" +
							"</table>");
		
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		var chat = conn.getChat(JID.createJID(contactInfo.jid), true);
		controlBar.find("button:first").click(function(){
			chatPanelTab.remove();
			contactChatPanel.remove();
		});
		
		controlBar.find("button:last").click(function(){
			var text = controlBar.find("input").val();
			if (text != null && text != "") {
				conn.sendChatText(chat, text);
			}
			
		});
		
		contactChatPanel.append(controlBar);
		
		chatPanel.append(contactChatPanel);
	}
	
	$("#chat-tab").click();
	chatPanelTab.click();
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