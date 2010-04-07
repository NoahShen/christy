(function() {
		
	var imservices = $("<div id='imservices'></div>");
	
	// im tabs
	var imTop = $("<div id='imTop'></div>");
	// contact tab
	var contactTab = $("<b class='marginpadding'></b>");
	contactTab.attr("type", "contact");
	contactTab.addClass("sexybutton");
	contactTab.text($.i18n.prop("imservices.contact"));
	imTop.append(contactTab);
	
	var chatTab = $("<b class='marginpadding'></b>");
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
										"<img id='user-status-img' src='/resource/status/available.png' />" +
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
	var searchbar = $("<table style='padding-left:5px;width:100%;'>" +
						"<tbody>" +
							"<tr>" +
								"<td>" +
									"<input id='search-contact-input' type='text' style='width:100%;'/>" +
								"</td>" +
								"<td style='width:40px;'>" +
									"<button id='search-contact-button' >Search</button>" +
								"</td>" +
							"</tr>" +
						"</tbody>" +
					"</table>");
					
	// user's status menu 
	var statusMenu = $("<ul id='myMenu' class='contextMenu'>" +
			"<li class='edit'><a href='#edit'>Edit</a></li>" +
			"<li class='cut separator'><a href='#cut'>Cut</a></li>" +
			"<li class='copy'><a href='#copy'>Copy</a></li>" +
			"<li class='paste'><a href='#paste'>Paste</a></li>" +
			"<li class='delete'><a href='#delete'>Delete</a></li>" +
			"<li class='quit separator'><a href='#quit'>Quit</a></li>" +
	"</ul>");
	
	
	
	
	userinfo.append(statusMenu);
	userinfo.append(userinfotable);
	userinfo.append(searchbar);
	
	
	var contactlist = $("<div id='contactlist'></div>");
	contactlist.attr("type", "contact");
	
	
	//TODO test code
	var contact1 = new XmppContact(new IqRosterItem(new JID("Noah", "example.com", "res"), "Noah"));
	var contact2 = new XmppContact(new IqRosterItem(new JID("aa", "example.com", "res"), "aa"));
	addContact(contactlist, contact1);
	addContact(contactlist, contact2);
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("bb", "example.com", "res"), "bb")));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("cc", "example.com", "res"), null)));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("D", "example.com", "res"), "dd")));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("ee", "example.com", "res"), null)));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("ff", "example.com", "res"), null)));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("gg", "example.com", "res"), null)));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("hh", "example.com", "res"), null)));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("ii", "example.com", "res"), "ii")));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("jj", "example.com", "res"), null)));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("KK", "example.com", "res"), null)));
	
	removeContact(contactlist, new XmppContact(new IqRosterItem(new JID("kk", "example.com", "res"), null)));
	var presence = new Presence(PresenceType.AVAILABLE);
	presence.setUserStatus("status");
	presence.setShow(PresenceShow.AWAY);
	contact1.addResource({
		resource: "res1",
		currentPresence: presence
	});
	
	var presence2 = new Presence(PresenceType.AVAILABLE);
	presence2.setUserStatus("status2");
//	presence2.setShow(PresenceShow.AWAY);
	contact2.addResource({
		resource: "res2",
		currentPresence: presence2
	});
	
	updateContact(contactlist, contact1);
	updateContact(contactlist, contact2);
	
	// TODO end of test
	
	// start of chat html
	
	var chatScrollHeader = $("<div id='chat-scroller-header'></div>");
	chatScrollHeader.attr("type", "chat");
	
	var chatScrollBody = $("<div id='chat-scroller-body'></div>");
	chatScrollBody.attr("type", "chat");
	
	var chatPanel = $("<div id='chat-panel'></div>");	
	chatScrollBody.append(chatPanel);
	
	// TODO test code
	createChatHtml(chatScrollHeader, chatPanel, {
		jid: "Noah1@example.com",
		showName: "Noah1"
	});
	
	createChatHtml(chatScrollHeader, chatPanel, {
		jid: "Noah2@example.com",
		showName: "Noah2"
	});
	
	// end of chat html
	
	imCenter.append(userinfo);
	imCenter.append(contactlist);
	
	imCenter.append(chatScrollHeader);
	imCenter.append(chatScrollBody);
	
	
	var imTopHeight = 40;
	
	var imlayoutSettings = {
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
					Height: 110
		 		},{
		 			Name: "Fill3",
					Dock: $.layoutEngine.DOCK.FILL,
					EleID: "contactlist",
		 		}]
	 		}]
		}]
	};
	
	imservices.append(imTop);
	imservices.append(imCenter);
	
	$("#main").append(imservices);
	
	$.layoutEngine(imlayoutSettings);
	
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
		var inputStatusMessage = $("<input id='input-status-message' type='text' value='" + oldmessage + "'/>");
		inputStatusMessage.bind("blur", function(){
			statusMessage.empty();
			statusMessage.text($(this).val());
			statusMessage.bind("click", statusMessageClickFunc);
		});
		$(this).empty();
		$(this).append(inputStatusMessage);
		inputStatusMessage[0].focus();
	};
	$("#user-status-message").bind("click", statusMessageClickFunc);
	
		
	var connectionMgr = XmppConnectionMgr.getInstance();
	
	connectionMgr.addConnectionListener(
		[
			ConnectionEventType.ContactUpdated,
			ConnectionEventType.ContactRemoved
		],
		
		function(event) {
			var contact = event.contact;
			alert(contact.getBareJid().toBareJID());
			var eventType = event.eventType;
			if (eventType == ConnectionEventType.ContactUpdated) {
				var bareJid = contact.getBareJid();
				var contactEl = contactlist.children("div[contactJid='" + bareJid.toPrepedBareJID() + "']");
				if (contactEl.length > 0) {
					updateContact(contactlist, contact);
				} else {
					alert("add:" + contact.getBareJid().toBareJID());
					addContact(contactlist, contact);
				}
			} else if (eventType == ConnectionEventType.ContactRemoved) {
				removeContact(contactlist, contact);
			}
			
			
		}
	);

	var conn = connectionMgr.getAllConnections()[0];
	conn.queryRoster();
	if (conn.initPresence) {
		conn.sendStanza(conn.initPresence);
	}
})();


function addContact(contactlistJqObj, newContact) {
	
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
	
	var inserted = false;
	var contacts = contactlistJqObj.children();
	$.each(contacts, function(index, value) {
		var oldContactJqObj = $(value);
		var bareJid = oldContactJqObj.attr("contactJid");
		var statusCode = oldContactJqObj.attr("statusCode");
		if (statusCode <= 0 && newBareJid.toPrepedBareJID() < bareJid) {
			newContactJqObj.insertBefore(oldContactJqObj);
			inserted = true;
			return false;
		}
		
	});
	
	if (!inserted) {
		contactlistJqObj.append(newContactJqObj);
	} 
	
}



function updateContact(contactlistJqObj, contact) {
	var bareJid = contact.getBareJid();
	var contactEl = contactlistJqObj.children("div[contactJid='" + bareJid.toPrepedBareJID() + "']");
	var contactJqObj = $(contactEl);
	
	var statusImgSrc = null;
	var statusMessage = null;
	if (contact.isResourceAvailable()) {
		var userResource = contact.getMaxPriorityResource();
		var presence = userResource.currentPresence;
		
		if (presence.getShow() == PresenceShow.AWAY) {
			statusImgSrc = "/resource/status/away.png";
			statusMessage = $.i18n.prop("imservices.status.away");
			contactJqObj.attr("statusCode", 3);
		} else if (presence.getShow() == PresenceShow.CHAT) {
			statusImgSrc = "/resource/status/chat.png";
			statusMessage = $.i18n.prop("imservices.status.chat");
			contactJqObj.attr("statusCode", 5);
		} else if (presence.getShow() == PresenceShow.DND) {
			statusImgSrc = "/resource/status/dnd.png";
			statusMessage = $.i18n.prop("imservices.status.dnd");
			contactJqObj.attr("statusCode", 2);
		} else if (presence.getShow() == PresenceShow.XA) {
			statusImgSrc = "/resource/status/xa.png";
			statusMessage = $.i18n.prop("imservices.status.xa");
			contactJqObj.attr("statusCode", 1);
		} else if (presence.isAvailable() 
			|| presence.getShow() == PresenceShow.AVAILABLE) {
			statusImgSrc = "/resource/status/available.png";
			statusMessage = $.i18n.prop("imservices.status.available");
			contactJqObj.attr("statusCode", 4);
		}
		
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
	
	
	var contacts = contactlistJqObj.children();
	$.each(contacts, function(index, value) {		
		var oldContactJqObj = $(value);
		var oldContactStatusCode = oldContactJqObj.attr("statusCode");
		var contactStatusCode = contactJqObj.attr("statusCode");
		
		if (contactStatusCode > oldContactStatusCode) {
			contactJqObj.insertBefore(oldContactJqObj);
			return false;
		} else if (contactStatusCode == oldContactStatusCode) {
			var oldBareJid = oldContactJqObj.attr("contactJid");
			if (bareJid.toPrepedBareJID() < oldBareJid) {
				contactJqObj.insertBefore(oldContactJqObj);
				return false;
			}
		}
		
		
	});
}

function removeContact(contactlistJqObj, contact) {
	var bareJid = contact.getBareJid();
	var contactEl = contactlistJqObj.children("div[contactJid='" + bareJid.toPrepedBareJID() + "']");
	var contactJqObj = $(contactEl);
	contactJqObj.remove();
}

function createChatHtml(chatScrollHeader, chatPanel, contactInfo) {
	
	var chatPanelTab = $("<span tabContactJid='" + contactInfo.jid + "'>" + contactInfo.showName + "</span>");
	
	var chatHandlerFunc = function(){

		$("span[tabContactJid]").removeClass('selected');
		$(this).addClass('selected');	

		var currentChatPanel = $("#chat-panel > div[chatPanelId=" + $(this).attr('tabContactJid') + "-chatPanel]");
		currentChatPanel.siblings().hide();	
		currentChatPanel.show();

	};
	
	chatPanelTab.click(chatHandlerFunc);
	chatScrollHeader.append(chatPanelTab);

//	chatPanel.append("<div chatPanelId='" + contactInfo.jid + "-chatPanel' style='display:none;'>" +
//							"<table style='width:100%;'>" +
//								"<tr style='height:100%;'>" +
//									"<td>" +
//										"Message" +
//									"</td>" +
//								"</tr>" +
//								"<tr style='height:100px;'>" +
//									"<td>" +
//										"<input type='text' style='width:100%;'/>" +
//									"</td>" +
//								"</tr>" +
//							"</table>" +
//						"</div>");

	chatPanel.append("<div chatPanelId='" + contactInfo.jid + "-chatPanel' style='display:none;'>" +
							"<div style='width:100%;'>" +
								"a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>a<br/>" +
							"</div>" +
							"<div style='width:100%;bottom:0;'>" +
								"<input type='text' style='width:100%;'/>" +
							"</div>" +
						"</div>");
}