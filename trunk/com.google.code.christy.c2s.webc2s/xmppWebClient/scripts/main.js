Main = {};
Main.init = function() {
	var mainDiv = $("<div id='main'></div>").css({
		"position":"absolute",
		"top":"0px",
		"left":"0px",
		"width": "100%",
		"height": "100%",
		"z-index": "9999"
	});
	
	var topBar = $("<div id='topBar'>" +
						"<table style='width:100%;'>" +
							"<tr style='text-align:center;'>" +
								"<td>" +
									"<div id='userStatus'>" + $.i18n.prop("topBar.status", "状态") + "</div>" +
								"</td>" +
								"<td>" +
									"<div id='userJid'>Noah@example.com</div>" +
								"</td>" +
								"<td>" +
									"<div id='search'>" + $.i18n.prop("topBar.search", "搜索") + "</div>" +
								"</td>" +
							"</tr>" +
						"</table>" +
						"<div id='statusitemsContainer' class='status-items-container' style='display:none;'>" +
							"<div id='statusItems' class='status-items'>" +
								"<div class='selected'>" + $.i18n.prop("statusContainer.status.available", "在线") + "</div>" +
								"<div>" + $.i18n.prop("status.away", "离开") + "</div>" +
								"<div>" + $.i18n.prop("status.chat", "空闲") + "</div>" +
								"<div>" + $.i18n.prop("status.dnd", "忙碌") + "</div>" +
								"<div>" + $.i18n.prop("status.xa", "离开") + "</div>" +
							"</div>" +
							"<table>" +
								"<tr>" +
									"<td style='width:100%;'>" +
										"<input id='statusMessage' type='text' style='width:100%'/>" +
									"</td>" +
									"<td>" +
										"<input id='changeStatus' type='button' value='" + $.i18n.prop("statusContainer.update", "更新") + "'/>" +
									"</td>" +
								"</tr>" +
							"</table>" +
						"</div>" +
					"</div>");
	
	topBar.find("#userStatus").click(function(){
		var userStatus = $(this);
		var statusitemsContainer = $("#statusitemsContainer");
		if (statusitemsContainer.is(":visible")) {
			userStatus.removeClass("status-menu-active");
			statusitemsContainer.slideUp("fast");
		} else {
			userStatus.addClass("status-menu-active");
			statusitemsContainer.slideDown("fast");
			
			// TODO reset status
		}
	});
	
	topBar.find("#statusItems div").click(function(){
		var statusItem = $(this);
		statusItem.siblings().removeClass("selected");
		statusItem.addClass("selected");
	});
	mainDiv.append(topBar);
	
	var tabs = $("<div id='tabs'>" +
 					"<div class='ui-tab-container'>" +
 						"<div class='clearfix'>" +
 							"<u class='ui-tab-active'>" + $.i18n.prop("tabs.contact", "联系人") + "</u>" +
							"<u>" + $.i18n.prop("tabs.around", "附近") + "</u>" +
							"<u>" + $.i18n.prop("tabs.map", "地图") + "</u>" +
							"<u>" + $.i18n.prop("tabs.profile", "资料") + "</u>" +
 						"</div>" +
 						"<div>" +
 							"<div id='im' class='ui-tab-content ui-tab-active'>" +
 							"</div>" +
	 						"<div id='around' class='ui-tab-content' style='display:none'>" +
	 							"<p>content2</p>" +
	 							"<p>content2</p>" +
	 							"<p>content2</p>" +
	 						"</div>" +
	 						"<div id='map' class='ui-tab-content' style='display:none'>" +
	 							"<p>content3</p>" +
	 							"<p>content3</p>" +
	 							"<p>content3</p>" +
	 						"</div>" +
	 						"<div id='profile' class='ui-tab-content' style='display:none'>" +
	 							"<p>content4</p>" +
	 							"<p>content4</p>" +
	 							"<p>content4</p>" +
	 						"</div>" +
 						"</div>" +
 					"</div>" +
 				"</div>");
	
    mainDiv.append(tabs);
	
	$("body").append(mainDiv);
	
	Main.tabs = new $.fn.tab({
        tabList:"#tabs .ui-tab-container div u",
        contentList:"#tabs .ui-tab-container .ui-tab-content",
        showType:"fade",
        callBackStartEvent:function(index){
//            alert(index);
        },
        callBackHideEvent:function(index){
//            alert("hideEvent"+index);
        },
        callBackShowEvent:function(index){
//            alert("showEvent"+index);
        }
    });
    
    IM.init();
    
    // TODO test code
    var rosterItem = new IqRosterItem(JID.createJID("Noah1@example.com"), "Noah1NickName");
    var contact1 = new XmppContact(rosterItem);
    /**
	 * Resource {
	 * 	resource: resource
	 * 	oldPresence: oldPresence
	 * 	currentPresence: currentPresence
	 * }
	 */
	userResource = {
		resource: "Res",
		currentPresence: new Presence(PresenceType.AVAILABLE)
	}
	contact1.addResource(userResource);
	
    IM.updateContact(contact1, false);
    
    
};

IM = {};
IM.init = function() {
	var imPanel = $("#im");
	
	var contactPanel = $("<div id='contact'></div>");
	
	var activeChat = $("<div id='activeChat'></div>");
	var activeLabel = $("<div id='activeLabel'>" + 
							$.i18n.prop("contact.chating", "正在聊天") + "(0)" +
						"</div>");
	activeChat.append(activeLabel);
	
	var activeChatItems = $("<div id='activeChatItems'></div>");
	activeChat.append(activeChatItems);
	
	activeLabel.click(function(){
		if (activeChatItems.is(":visible")) {
			activeChatItems.hide();
		} else {
			activeChatItems.show();
		}
	});
	contactPanel.append(activeChat);
	
	var contactlist = $("<div id='contactlist'></div>");
	contactPanel.append(contactlist);
	imPanel.append(contactPanel);
	
	
	
	var chatPanel = $("<div id='chatPanel'></div>");
	imPanel.append(chatPanel);
};

IM.updateContact = function(contact, remove) {
	var contactlistJqObj = $("#contactlist");
	var bareJid = contact.getBareJid();
	var contactJqObj = contactlistJqObj.find("div[contactjid='" + bareJid.toPrepedBareJID() + "']");
	
	var addContact = false;
	
	if (contactJqObj.length == 0) {
		addContact = true;
	} 
	
	contactJqObj.remove();
	
	if (!remove) {
		
		contactJqObj = IM.createContactJqObj(contact);
	
		var statusImgSrc = null;
		var statusMessage = null;
		if (contact.isResourceAvailable()) {
			var userResource = contact.getMaxPriorityResource();
			var presence = userResource.currentPresence;
			
			var statusInfo = IM.getStatusInfo(presence);
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
		
		var statusImg = contactJqObj.find("img[statusImg]");
		statusImg.attr("src", statusImgSrc);
		
		var showName = contact.getShowName();
		var showNameJqObj = contactJqObj.find("div[showName]");
		showNameJqObj.text(showName);
		
		var statusMessageJqObj = contactJqObj.find("div[statusMessage]");
		statusMessageJqObj.text(statusMessage);
		
		var groupNames = contact.getGroups();
		if (groupNames.length == 0) {
			IM.addContact2Group(contactlistJqObj, contactJqObj, "general", $.i18n.prop("contact.default", "默认"));
		} else {
			//add contact to group
			for (j = 0; j < groupNames.length; ++j) {
				var groupName = groupNames[j];
				IM.addContact2Group(contactlistJqObj, contactJqObj, groupName, groupName);
			}
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
			var groupN = groupJqObj.attr("displayName");
			groupJqObj.children(":first").text(groupN + "(" + onlineCount + "/" + contacts.length + ")");
			
		}
	});
	
	// TODO update chat tab
};

IM.addContact2Group = function( contactlistJqObj, contactJqObj, groupName, displayName) {
	var groupJqObj = contactlistJqObj.find("div[groupname='" + groupName + "']");
	if (groupJqObj.length == 0) {
		groupJqObj = IM.addGroup(contactlistJqObj, groupName, displayName);
	}
	var inserted = false;
	var contacts = groupJqObj.children("[contactJid]");
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
};

IM.addGroup = function(contactlistJqObj, groupName, displayName) {
	var newGroupJqObj = $("<div></div>").attr("groupname", groupName).attr("displayName", displayName);
	var groupLabel = $("<div id='" + groupName + "-label' class='contactGroup'></div>").text(displayName);
	
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
};

IM.createContactJqObj = function(newContact) {
	
	var newBareJid = newContact.getBareJid();
	var showName = newContact.getShowName();
	var statusMessage = $.i18n.prop("status.unavailable", "离线");
	var newContactJqObj = $("<div>" +
								"<table style='width:100%;'>" +
									"<tr>" +
										"<td>" +
											"<img statusImg='true' src='/resource/status/unavailable.png'/>" +
										"</td>" +
										"<td style='width:100%;'>" +
											"<table>" +
												"<tr>" +
													"<td>" +
														"<div showName='1'>" + showName + "</div>" +
													"</td>" +
												"</tr>" +
												"<tr>" +
													"<td>" +
														"<div statusMessage='1'>" + statusMessage + "</div>" +
													"</td>" +
												"</tr>" +
											"</table>" + 
										"</td>" +
										"<td>" +
											"<img src='/resource/statusmenu.png'/>" +
										"</td>" +
										"<td>" +
											"<img src='/resource/ajax-loader2.gif'/>" +
										"</td>" +
									"<tr/>" +
								"</table>" +
							"</div>");
							

	
	newContactJqObj.attr("contactJid", newBareJid.toPrepedBareJID());
	newContactJqObj.attr("statusCode", 0);
	
	var tdFirst = newContactJqObj.find("td:first");
	
	var clickFunc = function(){
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (conn) {
			var contact = conn.getContact(newBareJid);
			var contactChatPanel = IM.createChatPanel(contact);
			var chatPanel = $("#chatPanel");
			chatPanel.siblings().hide();
			chatPanel.show();
			contactChatPanel.siblings().hide();
			contactChatPanel.show();
		}
		
		// TODO test code
		var rosterItem = new IqRosterItem(JID.createJID("Noah1@example.com"), "Noah1NickName");
   		var contact2 = new XmppContact(rosterItem);
		var contactChatPanel = IM.createChatPanel(contact2);
		var chatPanel = $("#chatPanel");
		chatPanel.siblings().hide();
		chatPanel.show();
		contactChatPanel.siblings().hide();
		contactChatPanel.show();
		
	};
	tdFirst.click(clickFunc);
	tdFirst.next().click(clickFunc);
	
	var contactInfoButton = tdFirst.next().next();
	contactInfoButton.click(function(){
		var iq = new Iq(IqType.GET);		
		var vCard = new IqVCard();
		iq.setTo(newBareJid);
		iq.addPacketExtension(vCard);
		
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (conn) {
			conn.handleStanza({
				filter: new PacketIdFilter(iq.getStanzaId()),
				timeout: Christy.loginTimeout,
				handler: function(iqResponse) {
					if (iqResponse.getType() == IqType.RESULT) {
						var contact = conn.getContact(newBareJid);
						showContactInfo(contact, iqResponse);
					} else {
						alert($.i18n.prop("imservices.getvcardFailed", "获取失败！"));
					}
				}
			});
			
			conn.sendStanza(iq);
		}
	});
	
	var locButton = contactInfoButton.next();
	locButton.click(function(){
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (conn) {
			var contact = conn.getContact(newBareJid);
			if (contact.isResourceAvailable()) {
				var userResource = contact.getMaxPriorityResource();
				var presence = userResource.currentPresence;
				var geolocX = presence.getPacketExtension(GeoLocExtension.ELEMENTNAME, GeoLocExtension.NAMESPACE);
				if (geolocX && geolocX.getType() == GeoLocType.LATLON) {
					var lat = geolocX.getLat();
					var lon = geolocX.getLon();
					
					var mapItem = {
	    				id: newBareJid.toPrepedBareJID(),
	    				title: contact.getShowName(),
	    				isShow: true,
	    				positions: [{
	    					lat: lat,
	    					lon: lon
	    				}]
	    			};
					MapService.updateMapItem(mapItem);
					MapService.show();
				}					
			} else {
				MapService.removeMapItem(newBareJid.toPrepedBareJID());
			}
		}
		
		
		
	});
	return newContactJqObj;
};

IM.createChatPanel = function(contact){
	var chatPanel = $("#chatPanel");
	var jid = contact.getBareJid();
	var jidStr = jid.toPrepedBareJID();
	
	var contactChatPanel = chatPanel.children("div[chatContactJid='" + jidStr + "']");
	if (contactChatPanel[0]) {
		return contactChatPanel;
	}
	contactChatPanel = $("<div chatContactJid='" + jidStr + "' style='display:none;'>" +
									"<table style='width:100%;'>" +
										"<tr>" +
											"<td style='text-align:center;'>" +
												contact.getShowName() +
											"</td>" +
										"</tr>" +
										"<tr style='height:100%;'>" +
											"<td style='padding-left:7px;'>" +
												"<div messagearea='1' style='width:100%;height:100%;word-break:break-all;'></div>" +
											"</td>" +
										"</tr>" +
										"<tr>" +
											"<td>" +
												"<table style='margin-left:10px;'>" +
													"<tr>" +
														"<td>" +
															"<input id='backToList' type='button' value='返回'/>" +
														"</td>" +
														"<td style='width:100%;'>" +
															"<input type='text' style='width:100%;'/>" +
														"</td>" +
														"<td>" +
															"<input id='sendMessage' type='button' value='"+ $.i18n.prop("chatPanel.send", "发送") + "' />" + 
														"</td>" +
													"</tr>" +
												"</table>" +
											"</td>" +
										"</tr>" +
									"</table>" +
								"</div>");
	
	contactChatPanel.find("#backToList").click(function(){
		var contactPanel = $("#contact");
		contactPanel.siblings().hide();
		contactPanel.show();
	});
	
	chatPanel.append(contactChatPanel);
	
	var activeChatItem = $("<div></div>",{
		activeChatJid: jidStr,
		text: contact.getShowName(),
		click: function() {
			chatPanel.siblings().hide();
			chatPanel.show();
			contactChatPanel.siblings().hide();
			contactChatPanel.show();
		}
	});
	
	var activeChatItems = $("#activeChatItems");
	activeChatItems.append(activeChatItem);
	$("#activeLabel").text($.i18n.prop("contact.chating", "正在聊天") + "(" + activeChatItems.size() + ")");
	
	return contactChatPanel;
};

IM.getStatusInfo = function(presence) {
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
	
	
};