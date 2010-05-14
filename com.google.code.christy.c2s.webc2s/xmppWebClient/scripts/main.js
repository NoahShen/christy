MainUtils = {}
MainUtils.JIDPattern = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
MainUtils.verifyJid = function(jidStr) {
	return MainUtils.JIDPattern.test(jidStr);
};

MainUtils.cloneObj = function(myObj) {
    if(typeof(myObj) != 'object'){
    	return myObj;
    }
    if(myObj == null) {
    	return myObj;
    }
    var myNewObj = new Object(); 
    for(var i in myObj) {
    	myNewObj[i] = MainUtils.cloneObj(myObj[i]);
    }
    return myNewObj;
};

Main = {};
Main.notifyOpts = { 
    message: "", 
    fadeIn: 700, 
    fadeOut: 700, 
    timeout: 2000, 
    showOverlay: false, 
    centerY: false,
    centerX: false,
    css: {
        top: "0px",
        left: "0px",
        width: "100%",
        border: "none", 
        padding: "5px", 
        backgroundColor: "#000",
        color: "#fff" 
    } 
};
Main.init = function() {
	var mainDiv = $("<div id='main'></div>").css({
		"position":"absolute",
		"top":"0px",
		"left":"0px",
		"width": "100%",
		"height": "100%",
		"z-index": "778"
	});
	
	var topBar = $("<div id='topBar'>" +
						"<table style='width:100%;'>" +
							"<tr style='text-align:center;'>" +
								"<td>" +
									"<div id='userStatus'><div>" + $.i18n.prop("topBar.status", "状态") + "</div></div>" +
								"</td>" +
								"<td>" +
									"<div id='userJid'>Noah@example.com</div>" +
								"</td>" +
								"<td>" +
									"<div id='sys'>" + $.i18n.prop("topBar.sys", "系统") + "</div>" +
								"</td>" +
							"</tr>" +
						"</table>" +
						"<div id='statusitemsContainer' class='status-items-container' style='display:none;'>" +
							"<div id='statusItems' class='status-items'>" +
								"<div presenceshow='" + PresenceShow.AVAILABLE + "' class='available selected'><div>" + $.i18n.prop("statusContainer.status.available", "在线") + "</div></div>" +
								"<div presenceshow='" + PresenceShow.AWAY + "' class='away'><div>" + $.i18n.prop("status.away", "离开") + "</div></div>" +
								"<div presenceshow='" + PresenceShow.CHAT + "' class='chat'><div>" + $.i18n.prop("status.chat", "空闲") + "</div></div>" +
								"<div presenceshow='" + PresenceShow.DND + "' class='dnd'><div>" + $.i18n.prop("status.dnd", "忙碌") + "</div></div>" +
							"</div>" +
							"<table>" +
								"<tr>" +
									"<td style='width:100%;'>" +
										"<input id='statusMessage' type='text'/>" +
									"</td>" +
									"<td>" +
										"<input id='changeStatus' type='button' value='" + $.i18n.prop("statusContainer.update", "更新") + "'/>" +
									"</td>" +
								"</tr>" +
							"</table>" +
						"</div>" +
						"<div id='sysPanel' class='sys-panel-container' style='display:none;'>" +
							"<div id='preferences' style='display:none;'>" +
								"<div>" +
									"<div style='text-align:center;'>" +
										"<input id='exit' type='button' value='" + $.i18n.prop("preferences.exit", "退出") + "'/>" +
									"</div>" +
								"</div>" +
							"</div>" +
						"</div>" +
					"</div>");
	
	topBar.find("#userStatus").click(function(){
		var userStatus = $(this);
		var statusitemsContainer = $("#statusitemsContainer");
		if (statusitemsContainer.is(":visible")) {
			userStatus.removeClass("menu-active");
			statusitemsContainer.slideUp("fast", Main.updatePanelSize);
		} else {
			var sysPanel = $("#sysPanel");
			if (sysPanel.is(":visible")) {
				$("#sys").removeClass("menu-active");
				sysPanel.slideUp("fast", function(){
					userStatus.addClass("menu-active");
					statusitemsContainer.slideDown("fast", Main.updatePanelSize);
				});
				return;
			}
			
			userStatus.addClass("menu-active");
			statusitemsContainer.slideDown("fast", Main.updatePanelSize);
		}
	});
	
	topBar.find("#changeStatus").click(function(){
		var show = $("#statusItems > .selected").attr("presenceshow");		
		
		var presence = new Presence(PresenceType.AVAILABLE);
		presence.setShow(show);
		
		var statusMessage = $("#statusMessage");
		var mess = statusMessage.val();
		if (mess && mess != "") {
			presence.setUserStatus(mess);
		}
		
		conn.changeStatus(presence);
		
		var statusInfo = IM.getStatusInfo(presence);
		$("#userStatus").css("background-image", "url(" + statusInfo.imgPath + ")");
		
		$("#userStatus").click();
	});
	
	topBar.find("#statusItems > div").click(function(){
		var statusItem = $(this);
		statusItem.siblings().removeClass("selected");
		statusItem.addClass("selected");
	});
	
		
	var sysPanel = topBar.find("#sysPanel");
	sysPanel.sysPanel({
		panelAdded: function (panel) {
			$("#sys").text($.i18n.prop("topBar.sys", "系统") + 
							"(" + sysPanel.getPanelCount() + ")");
		},
		panelRemoved: function (panel) {
			var text = $.i18n.prop("topBar.sys", "系统");
			var count = sysPanel.getPanelCount();
			
			var sys = $("#sys");
			if (count == 0) {
				sys.removeClass("menu-active");
				sysPanel.slideUp("fast", Main.updatePanelSize);
			} else {
				text += "(" + count + ")";
			}
			sys.text(text);
		}
	});
	
	topBar.find("#sys").click(function(){
		var sys = $(this);
		if (sysPanel.getPanelCount() == 0) {
			var prefPanel = sysPanel.find("#preferences");
			prefPanel.siblings().hide();
			prefPanel.show();
		} else {
			var sysMessageContainer = sysPanel.find("#sysMessageContainer");
			sysMessageContainer.siblings().hide();
			sysMessageContainer.show();
		}
		
		if (sysPanel.is(":visible")) {
			sys.removeClass("menu-active");
			sysPanel.slideUp("fast", Main.updatePanelSize);
		} else {
			var statusitemsContainer = $("#statusitemsContainer");
			if (statusitemsContainer.is(":visible")) {
				$("#userStatus").removeClass("menu-active");
				statusitemsContainer.slideUp("fast", function(){
					sys.addClass("menu-active");
					sysPanel.slideDown("fast", Main.updatePanelSize);
				});
				return;
			}
			sys.addClass("menu-active");
			sysPanel.slideDown("fast", Main.updatePanelSize);
		}
	});
	
	topBar.find("#exit").click(function() {
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (conn) {
			Main.userClose = true;
			conn.close();
		}
	});
	
	mainDiv.append(topBar);
	
	var tabs = $("<div id='tabs'>" +
 					"<div class='ui-tab-container'>" +
 						"<div class='clearfix'>" +
 							"<u id='contactTab' class='ui-tab-active'>" + $.i18n.prop("tabs.contact", "联系人") + "<span id='unreadCount'><span></u>" +
							"<u id='searchTab'>" + $.i18n.prop("tabs.search", "搜索") + "</u>" +
							"<u id='mapTab'>" + $.i18n.prop("tabs.map", "地图") + "</u>" +
							"<u id='profileTab'>" + $.i18n.prop("tabs.profile", "资料") + "</u>" +
 						"</div>" +
 						"<div>" +
 							"<div id='im' class='ui-tab-content ui-tab-active'>" +
 							"</div>" +
	 						"<div id='search' class='ui-tab-content' style='display:none'>" +
	 						"</div>" +
	 						"<div id='map' class='ui-tab-content' style='display:none'>" +
	 						"</div>" +
	 						"<div id='profile' class='ui-tab-content' style='display:none'>" +
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
        callBackStartEvent:function(index) {
//            alert(index);
        },
        callBackHideEvent:function(index) {
//            alert("hideEvent"+index);
        },
        callBackShowEvent:function(index) {
        	//init map
			if (index == 2) {
				var mapCanvas = $("#mapCanvas");
				if (!mapCanvas.attr("src")) {
					mapCanvas.attr("src", "/mapcanvas.html");
				}
			}
        }
    });
    
    IM.init();
    Search.init();
    Map.init();
    Profile.init();
    
    
    //adjustment message area
	$(window).resize(function(){
		Main.updatePanelSize();
	});
	
	var connectionMgr = XmppConnectionMgr.getInstance();
	connectionMgr.addConnectionListener([
			ConnectionEventType.ContactUpdated,
			ConnectionEventType.ContactRemoved,
			ConnectionEventType.ContactStatusChanged,
			ConnectionEventType.ChatCreated,
			ConnectionEventType.ChatRemoved,
			ConnectionEventType.ConnectionClosed,
			ConnectionEventType.MessageReceived,
			ConnectionEventType.ContactSubscribeMe
		],
		
		function(event) {
			var contact = event.contact;
			var eventType = event.eventType;
			if (eventType == ConnectionEventType.ContactUpdated
				|| eventType == ConnectionEventType.ContactStatusChanged) {
				IM.updateContact(contact, false);
			} else if (eventType == ConnectionEventType.ContactRemoved) {
				IM.updateContact(contact, true);
			} else if (eventType == ConnectionEventType.ChatCreated) {
				var connection = event.connection;
				var bareJID = event.chat.bareJID;
				var contact = connection.getContact(bareJID);
				
				if (contact) {
					var contactChatPanel = IM.createChatPanel(contact);
				}
				
			} else if (eventType == ConnectionEventType.ConnectionClosed) {
				if (Main.geoLocIntervalId) {
					clearInterval(Main.geoLocIntervalId);
				}
				if (!Main.userClose) {
					var inputField = $("<div>" +
								"<div>" +
									$.i18n.prop("app.connectionClosed", "连接已断开") +
								"</div>" +
								"<div>" +
									"<input id='ok' type='button' style='margin: 2px 2px 2px 2px' value='" + 
										$.i18n.prop("button.ok", "确定") + 
									"'/>" +
								"</div>" +
							"</div>");
					inputField.find("#ok").click(function() {
						$.unblockUI();
						window.location.reload();
					});
					
					$.blockUI({
						message: inputField
					});
					return;
				}
				window.location.reload();
				
			} else if (eventType == ConnectionEventType.MessageReceived) {
				IM.messageReceived(event);
			} else if (eventType == ConnectionEventType.ChatRemoved) {
				var connection = event.connection;
				var bareJID = event.chat.bareJID;
				var contact = connection.getContact(bareJID);
				IM.removeChatPanel(contact);
			} else if (eventType == ConnectionEventType.ContactSubscribeMe) {
				var connection = event.connection;
				var presence = event.stanza;
				
				var from = presence.getFrom();				
				var userSubscribeMe = $("<div style='text-align:center;'>" +
											"<div>" +
												from.toBareJID() + $.i18n.prop("contact.wantToAddMe", "想要添加您为联系人") +
											"</div>" +
											"<div>" +
												"<input id='add_Checkbox' name='add_Checkbox' type='checkbox' style='margin: 2px 0px 2px 0px' checked='checked'/>" +
												"<label id='add_Label' for='add_Checkbox'>" + 
													$.i18n.prop("contact.addAsContact", "添加对方为联系人") + 
												"</label>" +
											"</div>" +
											"<div>" +
												"<input id='ok' type='button' style='margin: 2px 2px 2px 2px' value='" + 
													$.i18n.prop("contact.accept", "接受") + 
												"'/>" +
												"<input id='cancel' type='button' style='margin: 2px 2px 2px 2px' value='" + 
													$.i18n.prop("contact.reject", "拒绝") + 
												"'/>" +
											"</div>" +
										"</div>");
				userSubscribeMe.find("#ok").click(function() {
					var presence = new Presence(PresenceType.SUBSCRIBED);
					presence.setTo(from);
					
					connection.sendStanza(presence);					
					if (userSubscribeMe.find("#add_Checkbox").attr("checked") == true) {
						IM.addJIDAsContact(from);
					}
					sysPanel.removePanel(userSubscribeMe);
				});
				
				userSubscribeMe.find("#cancel").click(function() {
					sysPanel.removePanel(userSubscribeMe);
				});
				
				sysPanel.addPanel(userSubscribeMe);
			}
			
			
		}
	);
	
	var conn = connectionMgr.getAllConnections()[0];
	if (conn) {
		conn.queryRoster();
		if (conn.initPresence) {
			conn.changeStatus(conn.initPresence);
			var imgPathAndStatusMess = IM.getStatusInfo(conn.initPresence);

			$("#userStatus").css("background-image", "url(" + imgPathAndStatusMess.imgPath + ")");
			var statusMessage = imgPathAndStatusMess.statusMessage;
			if (conn.initPresence.getUserStatus() != null) {
				statusMessage = conn.initPresence.getUserStatus();
			}
			$("#statusMessage").text(statusMessage);
			
		}
		$("#userJid").text(conn.getJid().toBareJID());
//		
//		var vCardIq = new Iq(IqType.GET);
//		vCardIq.setTo(new JID(conn.getJid().getNode(), conn.getJid().getDomain(), null));
//		vCardIq.addPacketExtension(new IqVCard());
//		conn.handleStanza({
//			filter: new PacketIdFilter(vCardIq.getStanzaId()),
//			timeout: Christy.loginTimeout,
//			handler: function(iqResponse) {
//				if (iqResponse.getType() == IqType.RESULT) {
//					var vCard = iqResponse.getPacketExtension(IqVCard.ELEMENTNAME, IqVCard.NAMESPACE);
//					if (vCard.getNickName()) {
//						$("#userinfo-username").text(vCard.getNickName());
//					}
//					if (vCard.hasPhoto()) {
//						$("#userphoto").attr("src", "data:" + vCard.getPhotoType() + ";base64," + vCard.getPhotoBinval());
//					}
//				}
//			}
//		});
//		
//		conn.sendStanza(vCardIq);
	}
    // TODO test code
//    var rosterItem = new IqRosterItem(JID.createJID("Noah1@example.com"), "Noah1NickName");
//    var contact1 = new XmppContact(rosterItem);
//    /**
//	 * Resource {
//	 * 	resource: resource
//	 * 	oldPresence: oldPresence
//	 * 	currentPresence: currentPresence
//	 * }
//	 */
//	userResource = {
//		resource: "Res",
//		currentPresence: new Presence(PresenceType.AVAILABLE)
//	}
//	contact1.addResource(userResource);
//	
//    IM.updateContact(contact1, false);
    
    
};
Main.updatePanelSize = function() {
	$("#chatPanel [messagearea]").height(IM.getMessageContentHeight());
	$("#mapCanvas").height(Map.getMapCanvasHeight());
};

Main.getPageHeight = function() {
	if($.browser.msie) {
		return document.compatMode == "CSS1Compat"? 
			document.documentElement.clientHeight :
			document.body.clientHeight;
	} else {
		return self.innerHeight;
	}	
};

Main.getPageWidth = function() {
	if($.browser.msie){
		return document.compatMode == "CSS1Compat"? 
				document.documentElement.clientWidth :
				document.body.clientWidth;
	} else {
		return self.innerWidth;
	}
}; 

IM = {};
IM.init = function() {
	var imPanel = $("#im");
	
	var contactPanel = $("<div id='contact'></div>");
	
	var addContact = $("<div id='addContact'></div>");
	addContact.click(function() {
		
		var inputField = $("<div>" +
								"<div>" +
									$.i18n.prop("contact.inputJid", "请输入JID：") +
								"</div>" +
								"<div>" +
									"<input id='userJid' type='text' style='margin: 2px 0px 2px 0px' />" +
								"</div>" +
								"<div>" +
									"<input id='ok' type='button' style='margin: 2px 2px 2px 2px' value='" + 
										$.i18n.prop("button.ok", "确定") + 
									"'/>" +
									"<input id='cancel' type='button' style='margin: 2px 2px 2px 2px' value='" + 
										$.i18n.prop("button.cancel", "取消") + 
									"'/>" +
								"</div>" +
							"</div>");
		
		inputField.find("#ok").click(function() {
			var jidStr = inputField.find("#userJid").val();
			if (jidStr) {
				if (!MainUtils.verifyJid(jidStr)) {
					var opts = MainUtils.cloneObj(Main.notifyOpts);
					opts.message = $.i18n.prop("contact.jidError", "格式错误！");
					opts.css.backgroundColor = "red";
					$.blockUI(opts); 
					return;
				}
				IM.addJIDAsContact(JID.createJID(jidStr));
			}
			$.unblockUI();
		});
		
		inputField.find("#cancel").click(function() {
			$.unblockUI();
		});
		
		$.blockUI({
			message: inputField
		});		
	});
	
	contactPanel.append(addContact);
	
	
	var activeChat = $("<div id='activeChat'></div>");
	var activeLabel = $("<div id='activeLabel' opened='1'>" + 
							$.i18n.prop("contact.chating", "正在聊天") + "(0)" +
						"</div>");
	activeChat.append(activeLabel);
	
	var activeChatItems = $("<div id='activeChatItems'></div>");
	activeChat.append(activeChatItems);
	
	activeLabel.click(function(){
		var opened = activeLabel.attr("opened");
		if (opened == "1") {
			activeChatItems.hide();
			activeLabel.attr("opened", 0);
		} else {
			activeChatItems.show();
			activeLabel.attr("opened", 1);
		}
	});
	contactPanel.append(activeChat);
	
	var contactlist = $("<div id='contactlist'></div>");
	contactPanel.append(contactlist);
	imPanel.append(contactPanel);
	
	
	
	var chatPanel = $("<div id='chatPanel'></div>");
	chatPanel.hide();
	imPanel.append(chatPanel);
	
	var contactInfoPanel = $("<div id='contactInfoPanel'></div>");
	
	var contactInfoBar = $("<div>" +
								"<table>" +
									"<tr>" +
										"<td>" +
											"<div style='float:left;'>" +
												"<input id='contactInfoBack' type='button' value='" + $.i18n.prop("contact.closeContactInfo", "关闭") + "'>" +
											"</div>" +
										"</td>" +
									"</tr>" +
								"</table>" +
							"</div>");
							
	contactInfoBar.find("#contactInfoBack").click(function(){
		contactInfoBar.siblings().remove();
		
		contactPanel.siblings().hide();
		contactPanel.show();
	});
	
	contactInfoPanel.append(contactInfoBar);
	contactInfoPanel.hide();
	
	imPanel.append(contactInfoPanel);
	
};

IM.addJIDAsContact = function(jid) {
		
	var connectionMgr = XmppConnectionMgr.getInstance();
	var conn = connectionMgr.getAllConnections()[0];
	if (conn) {
		var contact = conn.getContact(jid);
		if (contact) {
			var rosterItem = contact.getRosterItem();
			var subs = rosterItem.getSubscription();
			if (subs == IqRosterSubscription.none
				|| subs == IqRosterSubscription.from) {
				var presence = new Presence(PresenceType.SUBSCRIBE);
				presence.setTo(jid);
				conn.sendStanza(presence);
			}
			return;
		}
		
		var iq = new Iq(IqType.SET);
		var iqRoster = new IqRoster();
	
		var iqRosterItem = new IqRosterItem(jid, null);
		iqRoster.addRosterItem(iqRosterItem);
		
		iq.addPacketExtension(iqRoster);
		
		conn.handleStanza({
			filter: new PacketIdFilter(iq.getStanzaId()),
			timeout: Christy.loginTimeout,
			handler: function(iqResponse) {
				if (iqResponse.getType() == IqType.RESULT) {
					var opts = MainUtils.cloneObj(Main.notifyOpts);
					opts.message = $.i18n.prop("contact.addContactSuccess", "添加成功！");
					$.blockUI(opts); 
	    
					$("#contactInfoBack").click();
				} else {
					var opts = MainUtils.cloneObj(Main.notifyOpts);
					opts.message = $.i18n.prop("contact.addContactFailed ", "添加失败！");
					opts.css.backgroundColor = "red";
					$.blockUI(opts);
				}
			},
			timeoutHandler: function() {
				var opts = MainUtils.cloneObj(Main.notifyOpts);
				opts.message = $.i18n.prop("contact.addContactFailed ", "添加失败！");
				opts.css.backgroundColor = "red";
				$.blockUI(opts); 
			}
		});
		
		conn.sendStanza(iq);
		
		var presence = new Presence(PresenceType.SUBSCRIBE);
		presence.setTo(jid);
		conn.sendStanza(presence);
	}
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
			contactJqObj.attr("statuscode", statusInfo.statusCode);
			statusImgSrc = statusInfo.imgPath;
			statusMessage = statusInfo.statusMessage;
			
			if (presence.getUserStatus() != null) {
				statusMessage = presence.getUserStatus();
			}
		} else {
			statusImgSrc = "/resource/status/unavailable.png";
			statusMessage = $.i18n.prop("imservices.status.unavailable", "离线");
			contactJqObj.attr("statuscode", 0);
		}
		
		var statusImg = contactJqObj.find("div[statusimg]");
		statusImg.css("background-image", "url(" + statusImgSrc + ")");
		
		var showName = contact.getShowName();
		var showNameJqObj = contactJqObj.find("div[showname]");
		showNameJqObj.text(showName);
		
		var statusMessageJqObj = contactJqObj.find("div[statusmessage]");
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
		var contacts = groupJqObj.children("[contactjid]");
		if (contacts.length == 0) {
			groupJqObj.remove();
		} else {
			// calculate online user
			var onlineCount = 0;
			$.each(contacts, function(index, valueContact) {		
				var groupContact = $(valueContact);
				var status = groupContact.attr("statuscode");
				if (status > 0) {
					++onlineCount;
				}
			});
			var groupN = groupJqObj.attr("displayname");
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
	
	if (groupJqObj.find(".contactGroup").attr("opened") == "0") {
		contactJqObj.hide();
	}
	
	var inserted = false;
	var contacts = groupJqObj.children("[contactjid]");
	$.each(contacts, function(index, value) {		
		var oldContactJqObj = $(value);
		var oldContactStatusCode = oldContactJqObj.attr("statuscode");
		var contactStatusCode = contactJqObj.attr("statuscode");
		
		if (contactStatusCode > oldContactStatusCode) {
			contactJqObj.clone(true).insertBefore(oldContactJqObj);
			inserted = true;
			return false;
		} else if (contactStatusCode == oldContactStatusCode) {
			var oldBareJid = oldContactJqObj.attr("contactjid");
			if (contactJqObj.attr("contactjid") < oldBareJid) {
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
	var newGroupJqObj = $("<div></div>").attr("groupname", groupName).attr("displayname", displayName);
	var groupLabel = $("<div id='" + groupName + "-label' class='contactGroup' opened='1'></div>");
	groupLabel.text(displayName);
	
	newGroupJqObj.append(groupLabel);
	groupLabel.click(function(){
		var groupContacts = newGroupJqObj.children("[contactjid]");
		var opened = groupLabel.attr("opened");
		if (opened == "1") {
			groupContacts.hide();
			groupLabel.attr("opened", 0)
		} else {
			groupContacts.show();
			groupLabel.attr("opened", 1)
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
										"<td style='width:100%;'>" +
											"<div statusimg='1'>" +
												"<table>" +
													"<tr>" +
														"<td>" +
															"<div showname='1'>" + showName + "</div>" +
														"</td>" +
													"</tr>" +
													"<tr>" +
														"<td>" +
															"<div statusmessage='1'>" + statusMessage + "</div>" +
														"</td>" +
													"</tr>" +
												"</table>" + 
											"</div>" +
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
							

	var jidStr = newBareJid.toPrepedBareJID();
	newContactJqObj.attr("contactjid", jidStr);
	newContactJqObj.attr("statuscode", 0);
	
	var tdFirst = newContactJqObj.find("td:first");
	tdFirst.click(function(){
		IM.showChatPanel(newBareJid);
	});
	
	
	var contactInfoButton = tdFirst.next();
	contactInfoButton.click(function(){
		var contactInfoPanel = $("#contactInfoPanel");
		contactInfoPanel.siblings().hide();
		contactInfoPanel.show();
		IM.showContactInfo(newContact);
	});
	
	return newContactJqObj;
};

IM.showChatPanel = function(newBareJid) {
	var connectionMgr = XmppConnectionMgr.getInstance();
	var conn = connectionMgr.getAllConnections()[0];
	if (conn) {
		var chat = conn.createChat(newBareJid, null);
		var chatPanel = $("#chatPanel");
		chatPanel.siblings().hide();
		chatPanel.show();
		var contactChatPanel = chatPanel.children("div[chatcontactjid='" + newBareJid.toPrepedBareJID() + "']");
		contactChatPanel.removeAttr("unread");
		contactChatPanel.siblings().hide();
		contactChatPanel.show();
		
		IM.updateUnreadMessage();
	}
};

IM.showContactInfo = function(contact) {
	var contactInfoPanel = $("#contactInfoPanel");
	
	var userJidStr = contact.getBareJid().toPrepedBareJID();
	var nickname = contact.getNickname() ? contact.getNickname() : "";
	var groupName = contact.getGroups()[0] ? contact.getGroups()[0] : "";
		
	var contactInfo = $("<div style='text-align:center;'>" +
							"<div>" +
								"<span id='contactInfoJid'>" + userJidStr + "</span>" +
							"</div>" +
							"<div>" +
								"<table align='center'>" +
									"<tr>" +
										"<td>" +
											"<div>" + 
												$.i18n.prop("contact.nickname", "备注名：") + 
											"</div>" +
										"</td>" +
										"<td>" +
											"<input id='contactNickName' type='text' value='" + nickname + "'/>" +
										"</td>" +
									"</tr>" +
									"<tr>" +
										"<td>" +
											"<div>" + 
												$.i18n.prop("contact.groupName", "组名：") + 
											"</div>" +
										"</td>" +
										"<td>" +
											"<input id='contactInfoGroup' name='contactInfoGroup' type='text' value='" + groupName + "'/>" +
										"</td>" +
									"</tr>" +
								"</table>" +
							"</div>" +
							"<div>" +
								"<input id='saveContactInfo' type='button' value='" + $.i18n.prop("contact.saveContactInfo", "保存") + "'>" +
							"</div>" +
							"<div>" +
								"<input id='deleteContact' type='button' value='" + $.i18n.prop("contact.deleteContact", "删除") + "'>" +
							"</div>" +
						"</div>");
	contactInfo.find("#deleteContact").click(function(){
		if (!confirm($.i18n.prop("contact.confirmRemoveContact", "确认删除？"))) {
			return;
		}
		
		var contactJidStr = $("#contactInfoJid").text();
		var iq = new Iq(IqType.SET);
		var iqRoster = new IqRoster();

		var iqRosterItem = new IqRosterItem(contact.getBareJid(), null);
		iqRosterItem.setSubscription(IqRosterSubscription.remove);
		iqRoster.addRosterItem(iqRosterItem);
		
		iq.addPacketExtension(iqRoster);
		
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (conn) {
			conn.handleStanza({
				filter: new PacketIdFilter(iq.getStanzaId()),
				timeout: Christy.loginTimeout,
				handler: function(iqResponse) {
					if (iqResponse.getType() == IqType.RESULT) {
						var opts = MainUtils.cloneObj(Main.notifyOpts);
						opts.message = $.i18n.prop("contact.removeContactSuccess", "删除成功！");
						$.blockUI(opts);
						$("#contactInfoBack").click();
					} else {
						var opts = MainUtils.cloneObj(Main.notifyOpts);
						opts.message = $.i18n.prop("contact.removeContactFailed", "删除失败！");
						opts.css.backgroundColor = "red";
						$.blockUI(opts); 
					}
				},
				timeoutHandler: function() {
					var opts = MainUtils.cloneObj(Main.notifyOpts);
					opts.message = $.i18n.prop("contact.removeContactFailed", "删除失败！");
					opts.css.backgroundColor = "red";
					$.blockUI(opts); 
				}
			});
			
			conn.sendStanza(iq);
			
			
		}
	});
	
	contactInfo.find("#saveContactInfo").click(function(){
		var contactJidStr = $("#contactInfoJid").text();
		var iq = new Iq(IqType.SET);
		var iqRoster = new IqRoster();
		
		var nickName = $("#contactNickName").val();
		var iqRosterItem = new IqRosterItem(contact.getBareJid(), nickName);
		var group = $("#contactInfoGroup").val();
		if (group && group != "") {
			iqRosterItem.addGroup(group);
		}
		
		iqRoster.addRosterItem(iqRosterItem);
		
		iq.addPacketExtension(iqRoster);
		
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (conn) {
			conn.handleStanza({
				filter: new PacketIdFilter(iq.getStanzaId()),
				timeout: Christy.loginTimeout,
				handler: function(iqResponse) {
					if (iqResponse.getType() == IqType.RESULT) {
						var opts = MainUtils.cloneObj(Main.notifyOpts);
						opts.message = $.i18n.prop("contact.updateContactSuccess", "更新成功！");
						$.blockUI(opts); 
								
						$("#contactInfoBack").click();
					} else {
						var opts = MainUtils.cloneObj(Main.notifyOpts);
						opts.message = $.i18n.prop("contact.updateContactFailed", "更新失败！");
						opts.css.backgroundColor = "red";
						$.blockUI(opts);
					}
				},
				timeoutHandler: function() {
					var opts = MainUtils.cloneObj(Main.notifyOpts);
					opts.message = $.i18n.prop("contact.updateContactFailed", "更新失败！");
					opts.css.backgroundColor = "red";
					$.blockUI(opts);
				}
			});
			conn.sendStanza(iq);
		}
	});
	
	contactInfoPanel.append(contactInfo);
};

IM.CHAT_TITLE_HEIGHT = 20;
IM.INPUT_BAR_HEIGHT = 22;

IM.getMessageContentHeight = function(){
	var pageHeight = Main.getPageHeight();
	var topBarHeight = $("#topBar").height();
	var tabsBarHeight = $("#tabs .clearfix").height();
	
	var contentHeight = pageHeight - topBarHeight - tabsBarHeight - IM.CHAT_TITLE_HEIGHT - IM.INPUT_BAR_HEIGHT;
	
	// 30px adjustment
	return contentHeight - 30;
};

IM.createChatPanel = function(contact){
	var jid = contact.getBareJid();
	var jidStr = jid.toPrepedBareJID();
	var chatPanel = $("#chatPanel");
	var contactChatPanel = chatPanel.children("div[chatcontactjid='" + jidStr + "']");
	if (contactChatPanel[0]) {
		return contactChatPanel;
	}

	
	var contentHeight = IM.getMessageContentHeight();
	
	contactChatPanel = $("<div chatcontactjid='" + jidStr + "' style='display:none;'>" +
									"<table style='width:100%;'>" +
										"<tr style='height:" + IM.CHAT_TITLE_HEIGHT + "px;'>" +
											"<td style='text-align:center;'>" +
												contact.getShowName() +
											"</td>" +
										"</tr>" +
										"<tr messagearea='1' style='height:" + contentHeight + ";'>" +
											"<td style='padding-left:7px;'>" +
												"<div messagearea='1' style='height:" + contentHeight + ";'></div>" +
											"</td>" +
										"</tr>" +
										"<tr style='height:" + IM.INPUT_BAR_HEIGHT + "px;'>" +
											"<td>" +
												"<table style='margin-left:10px;'>" +
													"<tr>" +
														"<td>" +
															"<input id='backToList' type='button' value='返回'/>" +
														"</td>" +
														"<td style='width:100%;'>" +
															"<input inputfield='1' type='text'/>" +
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
	
	var sendMessageAction = function() {
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (conn) {
			var chat = conn.getChat(jid, false);
			if (chat) {
				var inputField = contactChatPanel.find("input[inputfield]");
				var text = inputField.val();
				if (text && text != "") {
					conn.sendChatText(chat, text);
					
					var messageArea = contactChatPanel.find("div[messagearea]");
					messageArea.append("<div class='my-message'>" + $.i18n.prop("contact.chating.isaid", "我") + ":" + text + "</div>");
					var messageAreaElem = messageArea[0];
					messageAreaElem.scrollTop = messageAreaElem.scrollHeight;
					inputField.val("");
				}
			}
		}
	};
	contactChatPanel.find("#sendMessage").click(sendMessageAction);
	var inputField = contactChatPanel.find("input[inputfield]");
	inputField.keypress(function(event) {
		if (event.keyCode == 13) {
			sendMessageAction();
		}
	});
		
	chatPanel.append(contactChatPanel);
	
	var activeChatItem = $("<div activechatjid='" + jidStr + "'>" +
								"<table style='width:100%;'>" +
									"<tr>" +
										"<td style='width:100%;'>" +
											"<span>" +
												contact.getShowName() + 
											"</span>" +
											"<span>" +
											"</span>" +
										"</td>" +
										"<td>" +
											"关闭" +
										"</td>" +
									"<tr/>" +
								"</table>" +
							"</div>");
	
	var activeLabel = $("#activeLabel");
	if (activeLabel.attr("opened") == "0") {
		activeChatItem.hide();
	}
	
	var activeChatItems = $("#activeChatItems");
	activeChatItems.append(activeChatItem);
	
	activeLabel.text($.i18n.prop("contact.chating", "正在聊天") + "(" + activeChatItems.children().size() + ")");
	
	activeChatItem.find("td:first").click(function(){
		IM.showChatPanel(jid);
	});
	
	activeChatItem.find("td:last").click(function(){
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (conn) {
			conn.removeChat(jid);
		}
	});
	
	return contactChatPanel;
};

IM.removeChatPanel = function(contact) {
	var jid = contact.getBareJid();
	var jidStr = jid.toPrepedBareJID();
	var chatPanel = $("#chatPanel");
	var contactChatPanel = chatPanel.children("div[chatcontactjid='" + jidStr + "']");
	contactChatPanel.remove();
	
	var activeChatItems = $("#activeChatItems");
	var activeItem = activeChatItems.find("div[activechatjid='" + jidStr + "']");
	activeItem.remove();
	$("#activeLabel").text($.i18n.prop("contact.chating", "正在聊天") + "(" + activeChatItems.children().size() + ")");
	IM.updateUnreadMessage();
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

IM.messageReceived = function(event) {
	var eventChat = event.chat;
	var conn = event.connection;
	var contact = conn.getContact(eventChat.bareJID);
	var showName = contact.getShowName();
	var message = event.stanza;
	
	var jidStr = eventChat.bareJID.toPrepedBareJID();
	var chatPanel = $("#chatPanel > div[chatcontactjid='" + jidStr + "']");
	var messageArea =  chatPanel.find("div[messagearea]");
	
	messageArea.append("<div class='contact-message'>" + showName + ":" + message.getBody() + "</div>");
	var messageAreaElem = messageArea[0];
	messageAreaElem.scrollTop = messageAreaElem.scrollHeight;
	
	if (!chatPanel.is(":visible")) {
		var unread = chatPanel.attr("unread");
		if (unread == null) {
			unread = 0;
		}
		var i = parseInt(unread) + 1;
		chatPanel.attr("unread", i);
		
		IM.updateUnreadMessage();
	}
};

IM.updateUnreadMessage = function() {
	
	var count = 0;
	var chatPanel = $("#chatPanel");
	var activeChatItems = $("#activeChatItems");
	var chatPanels = chatPanel.children();
	$.each(chatPanels, function(index, value) {
		var panel = $(value);
		var jidStr = panel.attr("chatcontactjid");
		var activeChat = activeChatItems.find("div[activechatjid='" + jidStr + "']");
		var unread = panel.attr("unread");
		if (unread) {
			activeChat.find("span:last").text("(" + unread + ")");
			activeChat.find("td:first").addClass("activechat-unread")
			count += parseInt(unread);
		} else {
			activeChat.find("span:last").text("");
			activeChat.find("td:first").removeClass("activechat-unread");
		}
	});
	
	var contactTab = $("#contactTab");
	var unreadCount = $("#unreadCount");

	if (count > 0) {
		unreadCount.text("(" + count + ")");
		contactTab.addClass("contact-tab-unread");
	} else {
		unreadCount.text("");
		contactTab.removeClass("contact-tab-unread");
	}
		
};

Search = {};
Search.init = function() {
	var searchPanel = $("#search");
	
	var searchNavigation = $("<div>" +
								"<table style='width:100%;'>" +
									"<tr style='text-align:center;'>" +
										"<td>" +
											"<input id='searchBack' type='button' value='" + $.i18n.prop("search.navigation.back", "返回") + "'/>" +
										"</td>" +
										"<td>" +
											"<div id='searchTitle'>Search Title</div>" +
										"</td>" +
										"<td>" +
											"<input id='button1' type='button' value='Button'/>" +
										"</td>" +
									"</tr>" +
								"</table>" +
							"</div>");
	
	searchNavigation.find("#searchBack").click(function() {
		var currentSearchPanel = $("#searchInnerPanel > div:visible");
		var prevPanel = currentSearchPanel.prev();
		if (prevPanel[0]) {
			prevPanel.siblings().hide();
			prevPanel.show();
		}
	});
	
	searchNavigation.hide();
	
	searchPanel.append(searchNavigation);
	
	var searchInnerPanel = $("<div id='searchInnerPanel'></div>");
	
	var searchInput = $("<div id='searchInput'>" +
							"<div>" +
								"<table>" +
									"<tr>" +
										"<td>" +
											"<input id='searchByLoc' type='button' value='" + $.i18n.prop("search.searchInput.searchByLoc", "搜索指定位置") + "'/>" +
										"</td>" +
										"<td style='width:100%;'>" +
											"<input type='text' style='margin-right:0.1cm;'/>" +
										"</td>" +
										"<td>" +
											"<input id='doSearch' type='button' value='" + $.i18n.prop("search.searchInput.doSearch", "搜索") + "'/>" +
										"</td>" +
									"</tr>" +
								"</table>" +								
							"</div>" +
							"<table id='searchType' style='width:100%;'>" +
								"<tr>" +
									"<td>" +
										"<div id='restaurant' class='forward'>" +
											$.i18n.prop("search.searchType.restaurant", "餐馆") + 
										"</div>" +
									"</td>" +
								"</tr>" +
								"<tr>" +
									"<td>" +
										"<div id='hotel' class='forward'>" +
											$.i18n.prop("search.searchType.hotel", "宾馆") + 
										"</div>" + 
									"</td>" +
								"</tr>" +
								"<tr>" +
									"<td>" +
										"<div id='bar_cafe' class='forward'>" +
											$.i18n.prop("search.searchType.bar_cafe", "酒吧/咖啡厅") + 
										"</div>" + 
									"</td>" +
								"</tr>" +
								"<tr>" +
									"<td>" +
										"<div id='market' class='forward'>" +
											$.i18n.prop("search.searchType.market", "商场/超市") + 
										"</div>" + 
									"</td>" +
								"</tr>" +
							"</table>" + 
						"</div>");
	
	searchInput.find("#searchType div").click(function() {
		var typeJqObj = $(this);
		
	});
	
	searchInnerPanel.append(searchInput);
	
	
	searchPanel.append(searchInnerPanel);
};

Map = {};
Map.MAP_CONTROLBAR_HEIGHT = 20
Map.init = function() {
	var mapPanel = $("#map");
	
	var mapControlbar = $("<div id='mapControlbar' style='width:" + Map.MAP_CONTROLBAR_HEIGHT + "px;'>" +
								"<input type='button' style='float:left;margin-left:0.2cm;' value='" + $.i18n.prop("map.mapControlBar.mapItems", "地图项") + "'/>" +
							"</div>");
	mapPanel.append(mapControlbar);
	
	var mapCanvas = $("<iframe id='mapCanvas' name='mapCanvas' width='100%' height='" + Map.getMapCanvasHeight() + "'scrolling='no' frameborder='0' />");
	
	mapPanel.append(mapCanvas);
};

Map.getMapCanvasHeight = function(){
	var pageHeight = Main.getPageHeight();
	var topBarHeight = $("#topBar").height();
	var tabsBarHeight = $("#tabs .clearfix").height();
	
	var canvasHeigh = pageHeight - topBarHeight - tabsBarHeight - Map.MAP_CONTROLBAR_HEIGHT;
	
	// 10px adjustment
	return canvasHeigh - 10;
};

Map.mapFrameLoaded = function() {
	var mapCanvas = $("#mapCanvas");
//	for (var key in MapService.mapItems) {
//		var mapItem = MapService.mapItems[key];
//		if (mapItem.isShow) {
//			var marker = {
//				id: mapItem.id,
//				title: mapItem.title,
//				positions: mapItem.positions
//			};
//			mapcanvas[0].contentWindow.updateMapMarker(marker);
//		}
//	}
};

Profile = {};
Profile.init = function() {
	var profile = $("#profile");
	
	var profileTabs = $("<div id='profileTabs'>" +
		 					"<div class='profile-ui-tab-container'>" +
		 						"<div class='clearfix'>" +
		 							"<u class='profile-ui-tab-active'>" + $.i18n.prop("profile.tabs.favorite", "收藏") + "</u>" +
		 						"</div>" +
		 						"<div>" +
		 							"<div id='favorite' class='profile-ui-tab-content profile-ui-tab-active'>" +
		 							"</div>" +
		 						"</div>" +
		 					"</div>" +
		 				"</div>");
		 				
	profile.append(profileTabs);
	
	Profile.tabs = new $.fn.tab({
        tabList:"#profileTabs .profile-ui-tab-container div u",
        contentList:"#profileTabs .profile-ui-tab-container .profile-ui-tab-content",
        showType:"fade",
        callBackStartEvent:function(index) {
//            alert(index);
        },
        callBackHideEvent:function(index) {
//            alert("hideEvent"+index);
        },
        callBackShowEvent:function(index) {

        }
    });
    
};