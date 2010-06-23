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

Main.updateLocInterval = 10; // seconds

Main.notifyOpts = { 
    message: "", 
    fadeIn: 700, 
    fadeOut: 700, 
    timeout: 2000, 
    showOverlay: false, 
    centerY: false,
    centerX: false,
    applyPlatformOpacityRules: false,
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
						"<div id='userStatus' style='position:absolute;left:0px;text-align:center;'><div>" + $.i18n.prop("topBar.status", "状态") + "</div></div>" +
						"<div id='sys' style='position:absolute;right:0px;text-align:center;'>" + $.i18n.prop("topBar.sys", "系统") + "</div>" +
						"<div id='userJid' style='text-align:center;'></div>" +
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
										"<input id='changeStatus' type='button' class='button-base' value='" + $.i18n.prop("statusContainer.update", "更新") + "'/>" +
									"</td>" +
								"</tr>" +
							"</table>" +
						"</div>" +
						"<div id='sysPanel' class='sys-panel-container' style='display:none;'>" +
							"<div id='preferences' style='display:none;'>" +
								"<div id='sysAction'>" +
									"<div style='text-align:center;'>" +
										"<input id='exit' type='button' class='button-base' value='" + $.i18n.prop("preferences.exit", "退出") + "'/>" +
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
		panelAdded: function(panel) {
			$("#sys").text($.i18n.prop("topBar.sys", "系统") + 
							"(" + sysPanel.getPanelCount() + ")");
		},
		panelRemoved: function(panel) {
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
			Preferences.updatePreferencesUI();
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
 						"<div id='topTabs' class='clearfix'>" +
 							"<u id='contactTab' class='ui-tab-active'>" + $.i18n.prop("tabs.contact", "联系人") + "<span id='unreadCount'><span></u>" +
							"<u id='searchTab'>" + $.i18n.prop("tabs.search", "搜索") + "</u>" +
							"<u id='mapTab'>" + $.i18n.prop("tabs.map", "地图") + "</u>" +
							"<u id='profileTab'>" + $.i18n.prop("tabs.profile", "资料") + "</u>" +
 						"</div>" +
 						"<div>" +
 							"<div id='preferencesProfilePanel' class='ui-tab-content' style='display:none;'>" +
	 							"123" +
	 						"</div>" +
 							"<div id='im' class='ui-tab-content ui-tab-active'>" +
 							"</div>" +
	 						"<div id='search' class='ui-tab-content' style='display:none;'>" +
	 						"</div>" +
	 						"<div id='map' class='ui-tab-content' style='display:none;'>" +
	 						"</div>" +
	 						"<div id='profile' class='ui-tab-content' style='display:none;'>" +
	 						"</div>" +
	 						
 						"</div>" +
 					"</div>" +
 				"</div>");
	
    mainDiv.append(tabs);
	
	$("body").append(mainDiv);
	
    IM.init();
    Search.init();
    Map.init();
    Profile.init();
    Preferences.init();
    
	Main.tabs = new $.fn.tab({
        tabList:"#tabs .ui-tab-container .clearfix u, #preferencesProfile",
        contentList:"#tabs .ui-tab-container .ui-tab-content",
//        showType:"fade",
        callBackStartEvent:function(index) {
//            alert(index);
        },
        callBackHideEvent:function(index) {
			if (index == 2) {
				Map.stopSelectPos();
			}
        },
        callBackShowEvent:function(index) {
        	if (index == 1) {
        		var chatPanel = $("#chatPanel");
        		var jidStr = chatPanel.children("div:visible").attr("chatcontactjid");
        		if (jidStr) {
        			IM.showChatPanel(JID.createJID(jidStr));
        		}
        		
        	}
        	//init map
			else if (index == 3) {
				var mapCanvas = $("#mapCanvas");
				if (!mapCanvas.attr("src")) {
					mapCanvas.attr("src", "/mapcanvas.html");
					Main.updatePanelSize();
				}
			}
			// init favorite
			else if (index == 4) {
				if (!Profile.isFirst) {
					Profile.queryFavoriteShop(1, Profile.pageCount, true, true);
					Profile.isFirst = true;
				}
			}
        }
    });
    
    Main.tabs.triggleTab(1);
    
    //adjustment message area
	$(window).resize(function(){
		Main.updatePanelSize();
	});
	
	Main.geoLocIntervalId = setInterval(Main.updateMyPos, Main.updateLocInterval * 1000);
	
	Preferences.addItemChangedListener({
		preferenceNames: ["showContactPos"],
		handler: function(preferenceName, oldValue, newValue) {
			if (newValue != "true") {
				Map.hideContactPos();
			} else {
				Map.showContactPos();
			}
		}
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
				if (Preferences.preferencesItems["showContactPos"] == "true") {
					Map.showContactPos();
				}
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
									"<input id='ok' type='button' class='button-base' style='margin: 2px 2px 2px 2px' value='" + 
										$.i18n.prop("button.ok", "确定") + 
									"'/>" +
								"</div>" +
							"</div>");
					inputField.find("#ok").click(function() {
						$.unblockUI();
						window.location.reload();
					});
					
					$.blockUI({
						message: inputField,
						applyPlatformOpacityRules: false
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
												"<input id='ok' type='button' class='button-base' style='margin: 2px 2px 2px 2px' value='" + 
													$.i18n.prop("contact.accept", "接受") + 
												"'/>" +
												"<input id='cancel' type='button' class='button-base' style='margin: 2px 2px 2px 2px' value='" + 
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
		
		Preferences.updatePreferences(true);
		
		// TODO test code
//		var mapItem = {
//			id: "itemId",
//			title: "title",
//			isShow: true,
//			closeable: true,
//			positions: [{
//				message: "pos",
//				lat: 31.221891,
//				lon: 121.443297
//			}]
//		};
//		
//		Map.updateMapItem(mapItem);
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

Main.updateMyPos = function() {
	GeoUtils.getCurrentPosition(function(p) {
		var lat = p.coords.latitude;
		var lon = p.coords.longitude;
				
		var myPositionMapItem = Map.mapItems["myPosition"];
		if (myPositionMapItem == null) {
			var title = $.i18n.prop("map.myPosition", "我的位置");
			var titleJqObj = $("<div>" + title + "</div>");
		
			myPositionMapItem = {
				id: "myPosition",
				title: title,
				isShow: true,
				closeable: false,
				itemVisible: true,
				positions: [{
					message: titleJqObj[0],
					lat: lat,
					lon: lon,
					image: "/resource/i.png",
					imageSize: {width: 13, height: 18}
				}]
			}
			Map.updateMapItem(myPositionMapItem);
		} else {
			myPositionMapItem.positions[0].lat = lat;
			myPositionMapItem.positions[0].lon = lon;
			Map.updateMapItemAttribute(myPositionMapItem);
		}

		

		if (Preferences.preferencesItems["shareLoc"] == "true") {
			var connectionMgr = XmppConnectionMgr.getInstance();
			var conn = connectionMgr.getAllConnections()[0];
			if (conn) {
				var currentP = conn.currentPresence;
				var geoLocExtension = new GeoLocExtension();
				geoLocExtension.setType(GeoLocType.LATLON);
				geoLocExtension.setLat(lat);
				geoLocExtension.setLon(lon);
				
				currentP.removePacketExtension(GeoLocExtension.ELEMENTNAME, GeoLocExtension.NAMESPACE);
				currentP.addPacketExtension(geoLocExtension);
				conn.changeStatus(currentP);
			}
		}
		
		
	}, 
	function(){
		if (Main.geoLocIntervalId) {
			clearInterval(Main.geoLocIntervalId);
		}
	}, false);		
};

Main.updatePanelSize = function() {
	$("#chatPanel [messagearea]").height(IM.getMessageContentHeight());
	$("#mapCanvas").height(Map.getMapCanvasHeight());
};


IM = {};
IM.init = function() {
	var imPanel = $("#im");
	
	var contactPanel = $("<div id='contact'></div>");
	
	var addContact = $("<div id='addContact' class='icon'></div>");
	addContact.click(function() {
		
		var inputField = $("<div>" +
								"<div>" +
									$.i18n.prop("contact.inputJid", "请输入JID：") +
								"</div>" +
								"<div>" +
									"<input id='userJid' type='text' style='margin: 2px 0px 2px 0px' />" +
								"</div>" +
								"<div>" +
									"<input id='ok' type='button' class='button-base' style='margin: 2px 2px 2px 2px' value='" + 
										$.i18n.prop("button.ok", "确定") + 
									"'/>" +
									"<input id='cancel' type='button' class='button-base' style='margin: 2px 2px 2px 2px' value='" + 
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
			message: inputField,
			applyPlatformOpacityRules: false
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
												"<a id='contactInfoBack' href='javascript:void(0);'>&lt;&lt;" + $.i18n.prop("contact.closeContactInfo", "关闭") + "</a>" +
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
	
	if (groupJqObj.find(".contact-group").attr("opened") == "0") {
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
	var groupLabel = $("<div id='" + groupName + "-label' class='contact-group' opened='1'></div>");
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
	var newContactJqObj = $("<div class='list-item'>" +
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
										"<td style='width:40px;'>" +
											"<img src='/resource/userinfo.png'/>" +
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
	if (newBareJid == null) {
		return;
	}
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
								"<input id='saveContactInfo' type='button' class='button-base' value='" + $.i18n.prop("contact.saveContactInfo", "保存") + "'>" +
							"</div>" +
							"<div>" +
								"<input id='deleteContact' type='button' class='button-base' value='" + $.i18n.prop("contact.deleteContact", "删除") + "'>" +
							"</div>" +
						"</div>");
	contactInfo.find("#deleteContact").click(function(){
		var inputField = $("<div>" +
								"<div>" +
									$.i18n.prop("contact.confirmRemoveContact", "确认删除？") +
								"</div>" +
								"<div>" +
									"<input id='ok' type='button' class='button-base' style='margin: 2px 2px 2px 2px' value='" + 
										$.i18n.prop("button.ok", "确定") + 
									"'/>" +
									"<input id='cancel' type='button' class='button-base' style='margin: 2px 2px 2px 2px' value='" + 
										$.i18n.prop("button.cancel", "取消") + 
									"'/>" +
								"</div>" +
							"</div>");
		inputField.find("#ok").click(function() {
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
						var opts = MainUtils.cloneObj(Main.notifyOpts);
						if (iqResponse.getType() == IqType.RESULT) {
							opts.message = $.i18n.prop("contact.removeContactSuccess", "删除成功！");
							$("#contactInfoBack").click();
						} else {
							opts.message = $.i18n.prop("contact.removeContactFailed", "删除失败！");
							opts.css.backgroundColor = "red";
						}
						$.blockUI(opts); 
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
			$.unblockUI();
		});
		
		inputField.find("#cancel").click(function() {
			$.unblockUI();
		});
		
		$.blockUI({
			message: inputField,
			applyPlatformOpacityRules: false
		});	
		
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
	var pageHeight = getPageHeight();
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
															"<div id='backToContactList'></div>" +
														"</td>" +
														"<td style='width:100%;'>" +
															"<input inputfield='1' type='text'/>" +
														"</td>" +
														"<td>" +
															"<input id='sendMessage' type='button' class='button-base' value='"+ $.i18n.prop("chatPanel.send", "发送") + "' />" + 
														"</td>" +
													"</tr>" +
												"</table>" +
											"</td>" +
										"</tr>" +
									"</table>" +
								"</div>");
	
	contactChatPanel.find("#backToContactList").click(function(){
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
					
					var messageJqObj = $("<div class='my-message'>" + $.i18n.prop("contact.chating.isaid", "我") + ":" + text + "</div>");
					IM.appendMessageToArea(contactChatPanel, messageJqObj);
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
											"<div class='icon closeButton'></div>" +
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
	
	var messageJqObj = $("<div class='contact-message'>" + showName + ":" + message.getBody() + "</div>");
	IM.appendMessageToArea(chatPanel, messageJqObj);
	
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

IM.maxMessageCount = 50;

IM.appendMessageToArea =function(contactChatPanel, messageJqObj) {
	var messageArea = contactChatPanel.find("div[messagearea]");
	var messageCount = messageArea.children().size();
	if (messageCount >= IM.maxMessageCount) {
		messageArea.children(":first").remove();
	}
	messageArea.append(messageJqObj);
	var messageAreaElem = messageArea[0];
	messageAreaElem.scrollTop = messageAreaElem.scrollHeight;
}

Search = {};
Search.pageCount = 5;
Search.distance = 10; //10 km
Search.init = function() {
	var searchPanel = $("#search");
		
	var searchInnerPanel = $("<div id='searchInnerPanel'></div>");
	
	var searchInput = $("<div id='searchInput'>" +
							"<div class='search-bar'>" +
								"<table>" +
									"<tr>" +
										"<td>" +
											"<div id='searchByLoc'></div>" +
										"</td>" +
										"<td style='width:100%;'>" +
											"<input id='searchKey' type='text' style='margin-right:0.1cm;'/>" +
										"</td>" +
										"<td>" +
											"<input id='doSearch' type='button' class='button-base' value='" + $.i18n.prop("search.searchInput.doSearch", "搜索") + "'/>" +
										"</td>" +
									"</tr>" +
								"</table>" +								
							"</div>" +
							"<div class='search-item-title'>" +
								"<div>" + $.i18n.prop("search.searchInput.searchNearby", "搜索附近") + "<div>" +
								"<table id='searchType' style='width:100%;'>" +
									"<tr>" +
										"<td>" +
											"<div id='restaurant' class='search-type'>" +
												"<div class='restaurant-type-item'></div>" +
												"<div class='forward'>" +
													$.i18n.prop("search.searchType.restaurant", "餐馆") + 
												"</div>" +
											"</div>" +
										"</td>" +
									"</tr>" +
									"<tr>" +
										"<td>" +
											"<div id='hotel' class='search-type'>" +
												"<div class='hotel-type-item'></div>" +
												"<div class='forward'>" +
													$.i18n.prop("search.searchType.hotel", "宾馆") + 
												"</div>" +
											"</div>" + 
										"</td>" +
									"</tr>" +
									"<tr>" +
										"<td>" +
											"<div id='bar_cafe' class='search-type'>" +
												"<div class='barCafe-type-item'></div>" +
												"<div class='forward'>" +
													$.i18n.prop("search.searchType.bar_cafe", "酒吧/咖啡厅") + 
												"</div>" +
											"</div>" + 
										"</td>" +
									"</tr>" +
									"<tr>" +
										"<td>" +
											"<div id='market' class='search-type'>" +
												"<div class='market-type-item'></div>" +
												"<div class='forward'>" +
													$.i18n.prop("search.searchType.market", "商场/超市") + 
												"</div>" +
											"</div>" + 
										"</td>" +
									"</tr>" +
									"<tr>" +
										"<td>" +
											"<div id='activity' class='search-type'>" +
												"<div class='activity-type-item'></div>" +
												"<div class='forward'>" +
													$.i18n.prop("search.searchType.activity", "折扣/促销活动") + 
												"</div>" +
											"</div>" + 
										"</td>" +
									"</tr>" +
								"</table>" +
							"</div>" + 
						"</div>");
	searchInput.find("#searchByLoc").click(function() {
		Map.startSelectPos(function(lat, lon) {
			Main.tabs.triggleTab(2);
			var utm = GeoUtils.convertLatLon2UTM(lat, lon);
			Search.searchShops(utm, 1, Search.pageCount, 
								"all", true, true, 
								$.i18n.prop("search.searchTitle.searchByLoc", "搜索指定位置"));
		});
	});
	
	searchInput.find("#searchType .search-type").click(function() {
		var typeJqObj = $(this);
		var type = typeJqObj.attr("id");
		
		if (type == "activity") {
			GeoUtils.getCurrentPosition(function(p) {
				Search.searchActivities(p, 1, Search.pageCount, 
									true, true,
									$.i18n.prop("search.searchTitle.searchNearby", "搜索附近"));
			}, function(){}, true);		
		} else {
			GeoUtils.getCurrentPosition(function(p) {
				Search.searchShops(p, 1, Search.pageCount, 
									type, true, true,
									$.i18n.prop("search.searchTitle.searchNearby", "搜索附近"));
			}, function(){}, true);		
		}
		
	});
	
	searchInput.find("#doSearch").click(function() {
		var searchKeyInput = $("#searchKey");
		var key = searchKeyInput.val();
		if (key && key != "") {
			Search.searchShops(key, 1, Search.pageCount, "all", true, true, key);
		}
	});
	
	searchInnerPanel.append(searchInput);
	
	// =====start of shopResultContainer
	var shopResultContainer = $("<div id='shopResultContainer'></div>");
	shopResultContainer.hide();
	
	var shopResultBar = $("<div id='shopResultBar' style='width:100%;text-align:center;' class='search-bar'>" +
								"<div style='position:absolute;left:0px;'>" +
									"<a id='backToSearch' href='javascript:void(0);'>&lt;&lt;" + $.i18n.prop("search.back", "返回") + "</a>" +
								"</div>" +
								"<div id='searchTitle'></div>" +
							"</div>");
	
	var backToSearch = shopResultBar.find("#backToSearch");
	backToSearch.click(function() {
		searchInput.siblings().hide();
		searchInput.show();
	});
	
	shopResultContainer.append(shopResultBar);
	
	var shopResult = $("<div id='shopResult'></div>");
	shopResultContainer.append(shopResult);

	searchInnerPanel.append(shopResultContainer);
	
	// =====end of shopResultContainer
	
	// =====start of shopDetail
	var shopDetail = $("<div id='shopDetail'></div>");
	shopDetail.hide();
	
	var shopDetailBar = $("<div id='shopDetailBar' style='width:100%;text-align:center;' class='search-bar'>" +
								"<div style='position:absolute;left:0px;'>" +
									"<a id='backToShopResult' href='javascript:void(0);'>&lt;&lt;" + $.i18n.prop("search.back", "返回") + "</a>" +
								"</div>" +
								"<div style='position:absolute;right:0px;'>" +
									"<input id='viewComments' type='button' class='button-base' value='" + $.i18n.prop("search.shopDetail.viewComments", "查看评论") + "'/>" + 
								"</div>" +
								"<div id='shopName'></div>" +
							"</div>");
							
	shopDetailBar.find("#backToShopResult").click(function() {
		if ($("#shopResult").children().size() > 0) {
			shopResultContainer.siblings().hide();
			shopResultContainer.show();
		} else {
			searchInput.siblings().hide();
			searchInput.show();
		}
		
	});
	shopDetailBar.find("#viewComments").click(function() {
		var shopDetailJson = Search.currentShopDetail;
		if (shopDetailJson) {
			var shopId = shopDetailJson.basicInfo.id;
			Search.viewsShopComments(shopId, 1, 10, true);
		}
	});
	
	shopDetail.append(shopDetailBar);
	
	var shopDetailPanel = $("<div id='shopDetailPanel'></div>");
	shopDetail.append(shopDetailPanel);
	searchInnerPanel.append(shopDetail);
	// =====end of shopDetail
	
	var shopCommentsContainer = $("<div id='shopCommentsContainer'></div>");
	shopCommentsContainer.hide();
	
	var shopCommentsBar = $("<div id='shopCommentsBar' style='width:100%;text-align:center;' class='search-bar'>" +
								"<div style='position:absolute;left:0px;'>" +
									"<a id='backToShopDetail' href='javascript:void(0);'>&lt;&lt;" + $.i18n.prop("search.back", "返回") + "</a>" +
								"</div>" +
								"<div style='position:absolute;right:0px;'>" +
									"<input id='commentOnShop' type='button' class='button-base' value='" + $.i18n.prop("search.shopDetail.comment", "评论") + "'/>" + 
								"</div>" +
								"<div id='shopNameComments'></div>" +
							"</div>");
							
	shopCommentsBar.find("#backToShopDetail").click(function() {
		shopDetail.siblings().hide();
		shopDetail.show();
	});
	
	shopCommentsBar.find("#commentOnShop").click(function() {
		Search.showShopCommentInputPanel();
	});

	shopCommentsContainer.append(shopCommentsBar);
	
	
	var shopComments = $("<div id='shopComments'></div>");
	shopCommentsContainer.append(shopComments);
	
	
	searchInnerPanel.append(shopCommentsContainer);
	
	var commentOnShopContainer = $("<div id='commentOnShopContainer'></div>");
	commentOnShopContainer.hide();
	
	var commentOnShopBar = $("<div id='commentOnShopBar' style='width:100%;text-align:center;' class='search-bar'>" +
								"<div style='position:absolute;left:0px;'>" +
									"<a id='backToShopComments' href='javascript:void(0);'>&lt;&lt;" + $.i18n.prop("search.back", "返回") + "</a>" +
								"</div>" +
								"<div style='position:absolute;right:0px;'>" +
									"<input id='submintShopComment' type='button' class='button-base' value='" + $.i18n.prop("search.comment.submit", "提交") + "'/>" + 
								"</div>" +
								"<div id='shopNameComment'></div>" +
							"</div>");
	commentOnShopBar.find("#backToShopComments").click(function() {
		shopCommentsContainer.siblings().hide();
		shopCommentsContainer.show();
	});
	
	commentOnShopBar.find("#submintShopComment").click(function() {
		var shopDetail = Search.currentShopDetail;
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (conn) {
			var username = conn.getJid().getNode();
			var shopId = shopDetail.basicInfo.id;
			
			var comentContent = "";			
			var shopCommentItems = $("#shopCommentItems");
			var items = shopCommentItems.find("[item]");
			
			var inputValid = true;
			$.each(items, function(index, value) {		
				var commentItem = $(value);
				var itemName = commentItem.attr("item");
				var itemValue = commentItem.val();
				if (itemName != "content") {
					if (isNaN(itemValue) 
						|| itemValue < 1 
						|| itemValue　> 100) {
						alert($.i18n.prop("search.comment.inputNumber", "请输入数字(1~100)！"));
						commentItem[0].focus();
						inputValid = false;
						return false;
					}
					
					if (itemValue == null || itemValue == "") {
						alert($.i18n.prop("search.comment.inputScore", "请评分！"));
						commentItem[0].focus();
						inputValid = false;
						return false;
					}
				}
				comentContent += itemName + ":" + itemValue + ";";
			});
			
			if (!inputValid) {
				return;
			}
			
			$.ajax({
				url: "/shop/",
				cache: false,
				type: "post",
				dataType: "json",
				contentType: "application/x-www-form-urlencoded; charset=UTF-8",
				data: {
					action: "submitShopComment",
					shopid: shopId,
					username: username,
					comentContent: comentContent
				},
				success: function(response){
					if (response.result == "success") {
						var opts = MainUtils.cloneObj(Main.notifyOpts);
						opts.message = $.i18n.prop("search.comment.commentSuccess", "评论成功！");
						$.blockUI(opts); 

						$("#backToShopDetail").click();
					} else {
						var opts = MainUtils.cloneObj(Main.notifyOpts);
						opts.message = $.i18n.prop("search.comment.commentFailed", "评论失败！");
						opts.css.backgroundColor = "red";
						$.blockUI(opts); 
					}
				},
				error: function(xmlHttpRequest, textStatus, errorThrown) {
					var opts = MainUtils.cloneObj(Main.notifyOpts);
					opts.message = $.i18n.prop("search.comment.commentFailed", "评论失败！");
					opts.css.backgroundColor = "red";
					$.blockUI(opts); 
				},
				complete: function(xmlHttpRequest, textStatus) {
					
				}
			});
			
		}
	});
	
	commentOnShopContainer.append(commentOnShopBar);
	
	var commentOnShopPanel = $("<div id='commentOnShopPanel'></div>");
	commentOnShopContainer.append(commentOnShopPanel);
	
	searchInnerPanel.append(commentOnShopContainer);
	
	// =====start of activityResultContainer
	var activityResultContainer = $("<div id='activityResultContainer'></div>");
	activityResultContainer.hide();
	
	var activityResultBar = $("<div id='activityResultBar' style='width:100%;text-align:center;' class='search-bar'>" +
								"<div style='position:absolute;left:0px;'>" +
									"<a id='activityBackToSearch' href='javascript:void(0);'>&lt;&lt;" + $.i18n.prop("search.back", "返回") + "</a>" +
								"</div>" +
								"<div id='activitSearchTitle'></div>" +
							"</div>");
	
	var activityBackToSearch = activityResultBar.find("#activityBackToSearch");
	activityBackToSearch.click(function() {
		searchInput.siblings().hide();
		searchInput.show();
	});
	
	activityResultContainer.append(activityResultBar);
	
	var activityResult = $("<div id='activityResult'></div>");
	activityResultContainer.append(activityResult);

	searchInnerPanel.append(activityResultContainer);
	
	// =====end of activityResultContainer
	
	
	// =====start of activityDetail
	var activityDetail = $("<div id='activityDetail'></div>");
	activityDetail.hide();
	
	var activityDetailBar = $("<div id='activityDetailBar' style='width:100%;text-align:center;' class='search-bar'>" +
								"<div style='position:absolute;left:0px;'>" +
									"<a id='backToActivityResult' href='javascript:void(0);'>&lt;&lt;" + $.i18n.prop("search.back", "返回") + "</a>" +
								"</div>" +
								"<div id='activityName'></div>" +
							"</div>");
							
	activityDetailBar.find("#backToActivityResult").click(function() {
		if ($("#activityResult").children().size() > 0) {
			activityResultContainer.siblings().hide();
			activityResultContainer.show();
		} else {
			searchInput.siblings().hide();
			searchInput.show();
		}
		
	});
	
	activityDetail.append(activityDetailBar);
	
	var activityDetailPanel = $("<div id='activityDetailPanel'></div>");
	activityDetail.append(activityDetailPanel);
	
	searchInnerPanel.append(activityDetail);
	// =====end of activityDetail
	
	searchPanel.append(searchInnerPanel);
};

Search.viewsShopComments = function(shopId, page, count, updatePage) {
	$.ajax({
		url: "/shop/",
		dataType: "json",
		cache: false,
		type: "get",
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		data: {
			action: "getshopcomments",
			shopid: shopId,
			page: page,
			count: count
		},
		success: function(shopCommentsJson){
			var shopComments = $("#shopComments");
			shopComments.empty();
			
			var commsJqObj = $("<div></div>");
			for (var i = 0; i < shopCommentsJson.length; ++i) {
				var onecomment = shopCommentsJson[i];
				commsJqObj.append("<div>" +
										"<div>" +
											"<span>" + onecomment.username + "</span>" +
											"<span>" + new Date(onecomment.time).format("yyyy-MM-dd hh:mm:ss") + "</span>" +
										"</div>" +
										"<div>" +
											$.i18n.prop("search.shopDetail.restaurant.score", "总分：") +
											onecomment.score + 
										"</div>" + 
										"<div>" + onecomment.content + "</div>" +
									"</div>");
			}
			shopComments.append(commsJqObj);
			if (updatePage) {
				var shopCommentsContainer = $("#shopCommentsContainer");
				shopCommentsContainer.siblings().hide();
				shopCommentsContainer.show();
				
				var detail = Search.currentShopDetail;
				var shopNameComments = $("#shopNameComments");
				shopNameComments.text(detail.basicInfo.name);
	
				var shopCommentsPagination = shopCommentsContainer.children("#shopCommentsPagination");
				if (shopCommentsPagination.size() != 0) {
					shopCommentsPagination.remove();
				}
				
				var shopCommentsPagination = $("<div id='shopCommentsPagination' style='text-align:center;' page='1'>" +
													"<div id='shopCommentsPaginationNext'></div>" +
													"<div id='shopCommentsPaginationPrev'></div>" +
												"</div>");
				shopCommentsPagination.children("#shopCommentsPaginationPrev").click(function() {
					var currentPage = parseInt(shopCommentsPagination.attr("page"));
					if (currentPage == 1) {
						return;
					}
					shopCommentsPagination.attr("page", currentPage - 1);
					Search.viewsShopComments(shopId, currentPage - 1, count, false);
					
				});
				
				shopCommentsPagination.children("#shopCommentsPaginationNext").click(function() {
					if (shopComments.children("div").children().size() == 0) {
						return;
					}
					var currentPage = parseInt(shopCommentsPagination.attr("page"));
					shopCommentsPagination.attr("page", currentPage + 1);
					Search.viewsShopComments(shopId, currentPage + 1, count, false);
				});
				
				shopCommentsContainer.append(shopCommentsPagination);
			}
		},
		error: function(xmlHttpRequest, textStatus, errorThrown) {
			
		},
		complete: function(xmlHttpRequest, textStatus) {
			
		}
	});
};

Search.showShopCommentInputPanel = function(shopId) {
	var detail = Search.currentShopDetail;
	if (detail == null) {
		return;
	}
	var commentOnShopPanel = $("#commentOnShopPanel");
	commentOnShopPanel.empty();

	var overall = detail.overall;
	if (overall) {
		var shopCommentItems = $("<div id='shopCommentItems' style='margin:5px;'></div>");
		var i = 1;
		for(var key in overall) {
			shopCommentItems.append("<span style='margin:5px;'>" +
										"<span>" + $.i18n.prop("search.shopDetail.restaurant." + key, key + ":") + "</span>" +
										"<input item='" + key + "' type='text' style='width:50px;margin:5px;'/>" +
									"</span>");
			if (i != 0 && i % 2 == 0) {
				shopCommentItems.append("<br/>");
			}
			++i;
		}
		
		var content = $("<div>" +
							"<div>" + $.i18n.prop("search.comment.comment", "评论：") + "</div>" +
							"<textarea rows='10' item='content' style='width:100%;'>" +
							"</textarea>" + 
						"</div>");
		shopCommentItems.append(content);
		
		commentOnShopPanel.append(shopCommentItems);
	}
		
	var shopNameComment = $("#shopNameComment");
	shopNameComment.text(detail.basicInfo.name);
	
	var commentOnShopContainer = $("#commentOnShopContainer");
	commentOnShopContainer.siblings().hide();
	commentOnShopContainer.show();

	
};

Search.searchShops = function(query, page, count, type, updatePage, getTotal, searchTitle) {
	
	var shopResult = $("#shopResult");
	shopResult.text($.i18n.prop("search.searching", "正在搜索..."));
	
	var shopResultContainer = $("#shopResultContainer");
	shopResultContainer.siblings().hide();
	shopResultContainer.show();
			
	var data = {
		action: "search",
		type: type,
		page: page,
		count: count
	};
	if (getTotal) {
		data.gettotal = 1;
	}
	
	if (typeof query == "string") {
		data.searchKey = query;
	} else {
		data.easting = query.easting;
		data.northing = query.northing;
		data.distance = Search.distance;
	}
	
	$.ajax({
		url: "/shop/",
		dataType: "json",
		cache: false,
		type: "get",
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		data: data,
		success: function(searchResult){
			Search.currentResult = searchResult;
			shopResult.empty();
			
			$("#searchTitle").text(searchTitle);
			var shops = searchResult.shops;
			for (var i = 0; i < shops.length; ++i) {
				var shopPanel = Search.createShopInfo(shops[i]);
				shopResult.append(shopPanel);
			}
			
			if (updatePage) {
				var shopPagination = shopResultContainer.children("#shopPagination");
				
				if (shops.length == 0) {
					shopResult.append($.i18n.prop("search.noResult", "未搜索到相关的结果"));
					shopPagination.empty();
					return;
				}			
				
				if (shopPagination.size() == 0) {
					var shopPagination = $("<div id='shopPagination' style='text-align: center;'></div>");
					shopResultContainer.append(shopPagination)
				}
				
				var pagination = new $.fn.Pagination({
						renderTo: shopPagination,
						total: Math.ceil(searchResult.total / count),
						current: 1,
						onChanged: function(page) {
							Search.searchShops(query, page, count, type, false, false, searchTitle);
						}
				});
			}
		},
		error: function(xmlHttpRequest, textStatus, errorThrown) {
			shopResult.empty();
		},
		complete: function(xmlHttpRequest, textStatus) {
			
		}
	});
	
};


Search.createShopInfo = function(shopInfo) {
	var distance = "";
	if (shopInfo.distance != null) {
		distance = "<span class='distance-style'>" + 
						$.i18n.prop("search.result.aboutMeter", "约{0}米", [Math.round(shopInfo.distance)]) + 
					"</span>";
	}
	var shopInfoPanel = $("<div shopId='" + shopInfo.id + "' class='forward list-item'>" +
								"<table>" +
									"<tr>" +
										"<td>" +
											"<img src='" + shopInfo.imgSrc + "' width='50' height='50' />" +
										"</td>" +
										"<td>" +
											"<div>" + shopInfo.name + "</div>" +
											"<div>" + shopInfo.tel + "</div>" +
											"<div>" +
												"<span>" + shopInfo.street + "</span>" +
												distance +
											"</div>" +
										"</td>" +
									"</tr>" +
								"</table>" +
							"</div>");
	shopInfoPanel.click(function(){
		var shopId = $(this).attr("shopId");
		Search.getShopDetail(shopId, false);
	});
	
	return shopInfoPanel;
};

Search.getShopDetail = function(shopId, changeTab) {
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
			Search.currentShopDetail = shopDetail;
			Search.showShopDetail(shopDetail);
			if (changeTab) {
				Main.tabs.triggleTab(2);
			}
		},
		error: function(xmlHttpRequest, textStatus, errorThrown) {
			
		},
		complete: function(xmlHttpRequest, textStatus) {
			
		}
	});
};

Search.showShopDetail = function(shopDetail) {
	var shopDetailPanel = $("#shopDetailPanel");
	shopDetailPanel.empty();
	
	var baseInfo = shopDetail.basicInfo;
	var overall = shopDetail.overall;
	
	var shopBaseInfo = $("<div shopId='" + baseInfo.id + "'>" +
							"<table>" +
								"<tr>" +
									"<td>" +
										"<img src='" + baseInfo.imgSrc + "' width='50' height='50' />" +
									"</td>" +
									"<td>" +
										"<div>" +
											"<span>" + baseInfo.name + "</span>" +
											"<span>"+ baseInfo.hasCoupon + "</span>" +
											"<input id='adddFavorite' type='button' class='button-base' value='" +
												$.i18n.prop("search.shopDetail.adddFavorite", "收藏") + 
											"' />" +
											"<input id='showShopPos' type='button' class='button-base' value='" +
												$.i18n.prop("search.shopDetail.showShopPos", "地图") + 
											"' />" +
										"</div>" +
										"<div>" +
											"<span>" + 
												$.i18n.prop("search.shopDetail.restaurant.score", "总分：") + 
												overall.score + 
											"</span>" +
											"<span>" + 
												$.i18n.prop("search.shopDetail.restaurant.perCapita", "人均：") + 
												overall.perCapita + 
											"</span>" +
										"</div>" +
										"<span>" + 
											$.i18n.prop("search.shopDetail.restaurant.service", "服务：") + 
											overall.service + 
										"</span>" +
										"<span>" + 
											$.i18n.prop("search.shopDetail.restaurant.taste", "口味：") + 
											overall.taste + 
										"</span>" +
									"</td>" +
								"</tr>" +
							"</table>" +
						"</div>");
	
	shopBaseInfo.find("#adddFavorite").click(function() {
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (!conn) {
			return;
		}
		
		var streamId = connectionMgr.getStreamId();
		var username = conn.getJid().getNode();
		
		$.ajax({
			url: "/shop/",
			dataType: "json",
			cache: false,
			type: "post",
			contentType: "application/x-www-form-urlencoded; charset=UTF-8",
			data: {
				action: "addfavoriteshop",
				username: username,
				streamid: streamId,
				shopid: baseInfo.id
			},
			success: function(returnValue) {
				var opts = MainUtils.cloneObj(Main.notifyOpts);
				if (returnValue.result == "success") {
					opts.message = $.i18n.prop("search.shopDetail.addFavoriteSuccess", "收藏成功!");
				} else {
					opts.message = $.i18n.prop("search.shopDetail.addFavoriteFailed", "收藏失败!");
					opts.css.backgroundColor = "red";
				}
				$.blockUI(opts);
			},
			error: function(xmlHttpRequest, textStatus, errorThrown) {
				var opts = MainUtils.cloneObj(Main.notifyOpts);
				opts.message = $.i18n.prop("search.shopDetail.addFavoriteFailed", "收藏失败!");
				opts.css.backgroundColor = "red";
				$.blockUI(opts);
			},
			complete: function(xmlHttpRequest, textStatus) {
				
			}
		});
		
	});
	
	shopBaseInfo.find("#showShopPos").click(function() {
		var lat = baseInfo.lat;
		var lon = baseInfo.lon;
		
		var posInfoJqObj = $("<div>" +
								"<table style='width:100%'>" +
									"<tr>" +
										"<td>" +
											"<div>" + baseInfo.name + "</div>" +
										"</td>" +
									"</tr>" +
									"<tr>" +
										"<td>" +
											"<div style='text-align:right;'>" +
												"<a href='javascript:void(0);'>" +
													$.i18n.prop("search.shopDetail.viewRoute", "路线") +
												"</a>" +
											"</div>" +
										"</td>" +
									"</tr>" +
								"</table>" +
								
							"</div>");
		posInfoJqObj.find("a").click(function() {
			GeoUtils.getCurrentPosition(function(p) {
				var myLat = p.coords.latitude;
				var myLon = p.coords.longitude;
				Map.getRoute(myLat, myLon, lat, lon);
				$("#mapRoute").show();
			}, function() {}, false);
			
		});
		
		var mapItem = {
			id: "shopId_" + baseInfo.id,
			title: baseInfo.name,
			isShow: true,
			closeable: true,
			itemVisible: true,
			positions: [{
				message: posInfoJqObj[0],
				lat: lat,
				lon: lon,
				image: "/resource/" + baseInfo.type + ".png",
				imageSize: {width: 32, height: 37}
			}]
		};
		Map.updateMapItem(mapItem);
		Map.showMap(function(){
			Map.setMapItemCenter(mapItem.id);
		});
		
	});
	
	shopDetailPanel.append(shopBaseInfo);

	var contactIntro = $("<table>" +
							"<tr>" +
								"<td style='word-break:break-all;'>" +
									"<div>" +
										$.i18n.prop("search.shopDetail.address", "地址：") +
										baseInfo.addr + 
									"</div>" +
									"<div>" +
										$.i18n.prop("search.shopDetail.tel", "电话：") +
										baseInfo.phone + 
									"</div>" +
								"</td>" +
							"</tr>" +
							"<tr>" +
								"<td style='word-break:break-all;'>" +
									shopDetail.intro +
								"</td>" +
							"</tr>" +
						"</table>");
					
	shopDetailPanel.append(contactIntro);

	$("#shopName").text(baseInfo.name);

	
	var shopDetail = $("#shopDetail");
	shopDetail.siblings().hide();
	shopDetail.show();

};


Search.searchActivities = function(position, page, count, updatePage, getTotal, searchTitle) {
	
	var activityResult = $("#activityResult");
	activityResult.text($.i18n.prop("search.searching", "正在搜索..."));
	
	var activityResultContainer = $("#activityResultContainer");
	activityResultContainer.siblings().hide();
	activityResultContainer.show();
			
	var data = {
		action: "search",
		type: "activity",
		page: page,
		count: count
	};
	if (getTotal) {
		data.gettotal = 1;
	}
	
	data.easting = position.easting;
	data.northing = position.northing;
	data.distance = Search.distance;
	
	$.ajax({
		url: "/shop/",
		dataType: "json",
		cache: false,
		type: "get",
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		data: data,
		success: function(searchResult){
			Search.currentResult = searchResult;
			activityResult.empty();
			
			$("#activitSearchTitle").text(searchTitle);
			var activities = searchResult.activities;
			for (var i = 0; i < activities.length; ++i) {
				var activityPanel = Search.createActivityInfo(activities[i]);
				activityResult.append(activityPanel);
			}
			
			if (updatePage) {
				var activityPagination = activityResultContainer.children("#activityPagination");
				
				if (activities.length == 0) {
					activityResult.append($.i18n.prop("search.noResult", "未搜索到相关的结果"));
					activityPagination.empty();
					return;
				}			
				
				if (activityPagination.size() == 0) {
					var activityPagination = $("<div id='activityPagination' style='text-align: center;'></div>");
					activityResultContainer.append(activityPagination)
				}
				
				var pagination = new $.fn.Pagination({
						renderTo: activityPagination,
						total: Math.ceil(searchResult.total / count),
						current: 1,
						onChanged: function(page) {
							Search.searchActivities(query, page, count, type, false, false, searchTitle);
						}
				});
			}
		},
		error: function(xmlHttpRequest, textStatus, errorThrown) {
			activityResult.empty();
		},
		complete: function(xmlHttpRequest, textStatus) {
			
		}
	});
	
};


Search.createActivityInfo = function(activityInfo) {
	var distance = "";
	if (activityInfo.distance != null) {
		distance = "<span class='distance-style'>" + 
						$.i18n.prop("search.result.aboutMeter", "约{0}米", [Math.round(activityInfo.distance)]) + 
					"</span>";
	}
	
	var startDate = "";
	if (activityInfo.startDate) {
		startDate = new Date(activityInfo.startDate).format("yyyy-MM-dd");
	}
	
	var endDate = "";
	if (activityInfo.endDate) {
		endDate = new Date(activityInfo.endDate).format("yyyy-MM-dd");
	}
	var activityInfoPanel = $("<div activityId='" + activityInfo.activityId + "' shopId='" + activityInfo.shopId + "' class='forward list-item'>" +
								"<table>" +
									"<tr>" +
										"<td>" +
											"<div>" + activityInfo.title + distance + "</div>" +
											"<div>" + activityInfo.intro + "</div>" +
											"<div>" +
												$.i18n.prop("search.activityResult.periodOfValidity", "有效期：") +
												startDate + 
												" " + $.i18n.prop("search.activityResult.to", "至") + " " + 
												endDate + 
											"</div>" +
										"</td>" +
									"</tr>" +
								"</table>" +
								
							"</div>");
	activityInfoPanel.click(function(){
		var activityId = $(this).attr("activityId");
		Search.getActivityDetail(activityId, false);
	});
	
	return activityInfoPanel;
};

Search.getActivityDetail = function(activityId, changeTab) {
	$.ajax({
		url: "/shop/",
		dataType: "json",
		cache: false,
		type: "get",
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		data: {
			action: "getactivitydetail",
			activityid: activityId
		},
		success: function(activityDetail){
			Search.currentActivityDetail = activityDetail;
			Search.showActivityDetail(activityDetail);
			if (changeTab) {
				Main.tabs.triggleTab(2);
			}
		},
		error: function(xmlHttpRequest, textStatus, errorThrown) {
			
		},
		complete: function(xmlHttpRequest, textStatus) {
			
		}
	});
};


Search.showActivityDetail = function(activityDetail) {
	var activityDetailPanel = $("#activityDetailPanel");
	activityDetailPanel.empty();
	

	var startDate = "";
	if (activityDetail.startDate) {
		startDate = new Date(activityDetail.startDate).format("yyyy-MM-dd");
	}
	
	var endDate = "";
	if (activityDetail.endDate) {
		endDate = new Date(activityDetail.endDate).format("yyyy-MM-dd");
	}
	
	var activityDetailHtml = $("<div activityId='" + activityDetail.activityId + "' shopId='" + activityDetail.shopId + "'>" +
									"<table>" +
										"<tr>" +
											"<td>" +
												"<div>" + activityDetail.title + "</div>" +
												"<div>" +
													"<img />" +
												"</div>" + 
												"<div>" +
													"<div>" + 
														activityDetail.content + 
													"</div>" +
													"<div>" +
														$.i18n.prop("search.activityResult.periodOfValidity", "有效期：") +
														startDate + 
														" " + $.i18n.prop("search.activityResult.to", "至") + " " + 
														endDate + 
													"</div>" +
												"</div>" +
											"</td>" +
										"</tr>" +
									"</table>" +
								"</div>");
	
	if (activityDetail.activityImg) {
		var imgJqObj = activityDetailHtml.find("img");
		imgJqObj.attr("src", activityDetail.activityImg);
		imgJqObj.load(function(){
			var thisImg = $(this);
			if (thisImg.width() > getPageWidth()) {
				thisImg.width(getPageWidth() * 0.9);
			}
		});
	}
	
	activityDetailPanel.append(activityDetailHtml);


	$("#activityName").text(activityDetail.title);

	
	var activityDetail = $("#activityDetail");
	activityDetail.siblings().hide();
	activityDetail.show();

};


Map = {};
Map.MAP_CONTROLBAR_HEIGHT = 20
Map.init = function() {
	var mapPanel = $("#map");
	var mapTabs = $("<div id='mapTabs'>" +
						"<div class='map-ui-tab-container'>" +
							"<div class='clearfix'>" +
								"<div class='map-ui-tab-parent-active'>" +
									"<span id='mapCanvasContainer' class='map-tab map-ui-tab-active'>" + 
										$.i18n.prop("map.tabs.map", "地图") + 
									"</span>" +
								"</div>" +
								"<div>" +
									"<span id='mapItems' class='map-tab'>" + 
										$.i18n.prop("map.tabs.mapItems", "地图项") + 
									"</span>" +
								"</div>" +
								"<div>" +
									"<span id='mapRoute' class='map-tab' style='display:none;'>" + 
										$.i18n.prop("map.tabs.route", "路线") +
									"</span>" +
									"<div id='closeRoute' class='icon closeButton' style='display:none;'></div>" +
								"</div>" +
							"</div>" +
							"<div>" +
								"<div id='mapCanvasPanel' class='map-ui-tab-content map-ui-tab-active'>" +
									"<iframe id='mapCanvas' name='mapCanvas' style='width:100%;height:" + Map.getMapCanvasHeight() + ";' scrolling='no' frameborder='0' />" +
								"</div>" +
		 						"<div id='mapItemsPanel' class='map-ui-tab-content' style='display:none;'>" +
		 							"<a id='mapItemsSelectAll' href='javascript:void(0);' style='margin:3px 3px;'>" + $.i18n.prop("map.mapItems.selectAll", "全选") + "</a>" +
		 							"<a id='mapItemsSelectNone' href='javascript:void(0);' style='margin:3px 3px;'>" + $.i18n.prop("map.mapItems.selectNone", "全不选") + "</a>" +
		 							"<a id='mapItemsRemoveAll' href='javascript:void(0);' style='margin:3px 3px;'>" + $.i18n.prop("map.mapItems.removeAll", "全部删除") + "</a>" +
		 						"</div>" +
		 						"<div id='mapRoutePanel' class='map-ui-tab-content' style='display:none;'>" +
		 							"<div id='mapDirection'></div>" +
		 						"</div>" +
							"</div>" +
						"</div>" +
					"</div>");
	
	mapTabs.find("#closeRoute").click(function() {
		Map.tabs.triggleTab(0);
		$("#mapRoute").hide();
		Map.removeRoute();
	});
	
	mapTabs.find("#mapItemsSelectAll").click(function() {
		var mapItemsDivJqObj = $("#mapItemsPanel").find("div[mapItem]:visible");
		$.each(mapItemsDivJqObj, function(index, value) {
			var mapItemsDivJqObj = $(value);
			var checkboxJqObj = mapItemsDivJqObj.children("input[type='checkbox']");
			checkboxJqObj.attr("checked", true);
		});
		
		$.each(mapItemsDivJqObj, function(index, value) {
			var mapItemsDivJqObj = $(value);
			var mapItem = Map.mapItems[mapItemsDivJqObj.attr("mapItem")];
			if (mapItem) {
				var checkboxJqObj = mapItemsDivJqObj.children("input[type='checkbox']");
				mapItem.isShow = (checkboxJqObj.attr("checked") == true);
				Map.updateMapItem(mapItem);
			}
		});
		
	});
	
	mapTabs.find("#mapItemsSelectNone").click(function() {
		var mapItemsDivJqObj = $("#mapItemsPanel").find("div[mapItem]:visible");
		$.each(mapItemsDivJqObj, function(index, value) {
			var mapItemsDivJqObj = $(value);
			var checkboxJqObj = mapItemsDivJqObj.children("input[type='checkbox']");
			checkboxJqObj.attr("checked", false);
		});
		
		$.each(mapItemsDivJqObj, function(index, value) {
			var mapItemsDivJqObj = $(value);
			var mapItem = Map.mapItems[mapItemsDivJqObj.attr("mapItem")];
			if (mapItem) {
				var checkboxJqObj = mapItemsDivJqObj.children("input[type='checkbox']");
				mapItem.isShow = (checkboxJqObj.attr("checked") == true);
				Map.updateMapItem(mapItem);
			}
		});
	});
	
	mapTabs.find("#mapItemsRemoveAll").click(function() {
		$("#mapItemsPanel").find("div[mapItem] > input[type='button']:visible").click();
	});
	
	
	mapPanel.append(mapTabs);
					
	Map.tabs = new $.fn.tab({
        tabList: "#mapTabs .map-ui-tab-container .clearfix .map-tab",
        contentList: "#mapTabs .map-ui-tab-container .map-ui-tab-content",
        tabActiveClass: "map-ui-tab-active",
        tabDisableClass: "map-ui-tab-disable",
        tabParentActiveClass: "map-ui-tab-parent-active",
		callBackHideEvent: function(index) {
        	if (index == 2) {
        		$("#closeRoute").hide();
        	}
        },
        callBackShowEvent: function(index) {
        	if (index == 2) {
        		$("#mapRoute").show();
        	}
        	if (index == 2) {
				$("#closeRoute").show();
        	}
        }
    });
    Map.tabs.triggleTab(0);
	
};

Map.getMapCanvasHeight = function(){
	var pageHeight = getPageHeight();
	var topBarHeight = $("#topBar").height();
	var tabsBarHeight = $("#topTabs").height();
	
	var canvasHeigh = pageHeight - topBarHeight - tabsBarHeight - Map.MAP_CONTROLBAR_HEIGHT;
	
	// 10px adjustment
	return canvasHeigh - 10;
};

Map.mapLoadedCallBack = [];
Map.showMap = function(mapShownCallBack) {
	if (Map.mapLoaded) {
		if (mapShownCallBack) {
			mapShownCallBack();
		}
	} else {
		if (mapShownCallBack) {
			mapShownCallBack();
		}
		Map.mapLoadedCallBack.push(mapShownCallBack);
	}
	Main.tabs.triggleTab(3);
};

Map.mapFrameLoaded = function() {
	var mapCanvas = $("#mapCanvas");
	for (var key in Map.mapItems) {
		var mapItem = Map.mapItems[key];
		if (mapItem.isShow) {
			var marker = {
				id: mapItem.id,
				title: mapItem.title,
				positions: mapItem.positions
			};
			mapCanvas[0].contentWindow.updateMapMarker(marker);
		}
	}
	Map.mapLoaded = true;
	for (var i = 0; i < Map.mapLoadedCallBack.length; ++i) {
		Map.mapLoadedCallBack[i]();
		Map.mapLoadedCallBack.splice(i,1);
	}
};

Map.getRoute = function(fromLat, fromLon, toLat, toLon) {
	Map.showMap(function() {
		var mapCanvas = $("#mapCanvas");
		mapCanvas[0].contentWindow.getRoute(fromLat, fromLon, toLat, toLon);
	});
};

Map.removeRoute = function() {
	if (Map.mapLoaded) {
		var mapCanvas = $("#mapCanvas");
		mapCanvas[0].contentWindow.removeRoute();
	}
};

//var mapItem = {
//	id: itemId,
//	title: "title",
//	isShow: true,
//	closeable: true,
//	itemVisible: true;
//	positions: [{
//		message: "marker",
//		lat: lat,
//		lon: lon
//	}]
//};
Map.mapItems = {};

Map.containMapItem = function(mapItemId) {
	var mapItem = Map.mapItems[mapItemId];
	if (mapItem) {
		return true;
	}
	return false;
};

Map.updateMapItem = function(mapItem) {
	var mapItemId = mapItem.id;	
	Map.mapItems[mapItemId] = mapItem;
	
	var mapItemsPanel = $("#mapItemsPanel");
	var itemDivJqObj = mapItemsPanel.children("div[mapItem='" + mapItemId + "']");
	if (itemDivJqObj.size() == 0) {
		itemDivJqObj = $("<div mapItem='" + mapItemId + "'>" + 
								"<input id='" + mapItem.id + "-mapItem' name='" + mapItem.id + "-mapItem' type='checkbox' checked='checked'/>" +
								"<label id='" + mapItem.id + "-mapItemLabel' for='" + mapItem.id + "-mapItem'>" + mapItem.title + "</label>" +
								"<input type='button' class='button-base' value='" + $.i18n.prop("map.mapItems.remove", "删除") + "'/>" + 
							"</div>");
		itemDivJqObj.children("input:first").click(function() {
			var checkbox = $(this);
			var mapItem = Map.mapItems[mapItemId];
			if (mapItem) {
				mapItem.isShow = (checkbox.attr("checked") == true);
				Map.updateMapItemAttribute(mapItem);
			}
		});
		itemDivJqObj.children("input:last").click(function() {
			Map.removeMapItem(mapItemId);
			itemDivJqObj.remove();
		});
		mapItemsPanel.append(itemDivJqObj);
	}

	if (!mapItem.closeable) {
		itemDivJqObj.find("input:last").hide();
	}
	
	if (!mapItem.itemVisible) {
		itemDivJqObj.hide();
	}

	Map.updateMapItemAttribute(mapItem);
};

Map.removeMapItem = function(mapItemId) {
	delete Map.mapItems[mapItemId];
	if (Map.mapLoaded) {
		var mapCanvas = $("#mapCanvas");
		mapCanvas[0].contentWindow.removeMapMarker(mapItemId);
	}
};

Map.updateMapItemAttribute = function(mapItem) {
	if (Map.mapLoaded) {
		var mapCanvas = $("#mapCanvas");
		if (mapItem.isShow) {
			var marker = {
				id: mapItem.id,
				title: mapItem.title,
				positions: mapItem.positions.slice(0)
			};
			mapCanvas[0].contentWindow.updateMapMarker(marker);
		} else {
			mapCanvas[0].contentWindow.removeMapMarker(mapItem.id);
		}
	}
};

Map.setMapItemCenter = function(mapItemId) {
	var mapItem = Map.mapItems[mapItemId];
	if (mapItem) {
		var mapCanvas = $("#mapCanvas");
		if (mapCanvas.attr("src")) {
			mapCanvas[0].contentWindow.setMapMarkerCenter(mapItemId);
		}
	}
	
};

Map.showContactPos = function() {
	var connectionMgr = XmppConnectionMgr.getInstance();
	var conn = connectionMgr.getAllConnections()[0];
	if (conn) {
		var contacts = conn.getAllContacts();
		
		var positions = [];
		
		for (var i = 0; i < contacts.length; ++i) {
			var contact = contacts[i];
			var userResource = contact.getMaxPriorityResource();
			if (userResource) {
				var presence = userResource.currentPresence;
				var geolocX = presence.getPacketExtension(GeoLocExtension.ELEMENTNAME, GeoLocExtension.NAMESPACE);
				if (geolocX && geolocX.getType() == GeoLocType.LATLON) {
					var lat = geolocX.getLat();
					var lon = geolocX.getLon();
					
					var position = {
						message: contact.getShowName(),
						lat: lat,
						lon: lon
					};
					positions.push(position);
				}		
			}			
		}
		
		var mapItem = {
			id: "allContact",
			title: "allContact",
			isShow: true,
			closeable: false,
			itemVisible: false,
			positions: positions
		};
		Map.updateMapItem(mapItem);
	}
};

Map.hideContactPos = function() {
	Map.removeMapItem("allContact");
};

Map.startSelectPos = function(callBack) {
	Map.showMap(function() {
		var opts = MainUtils.cloneObj(Main.notifyOpts);
		opts.message = $.i18n.prop("search.searchInput.selectPosInMap", "请指定位置");
		$.blockUI(opts);
		
		var mapCanvas = $("#mapCanvas");
		mapCanvas[0].contentWindow.startSelectPos(callBack);
	});
	
};

Map.stopSelectPos = function() {
	if (Map.mapLoaded) {
		var mapCanvas = $("#mapCanvas");
		mapCanvas[0].contentWindow.stopSelectPos();
	}
}

Profile = {};
Profile.pageCount = 5;
Profile.init = function() {
	var profile = $("#profile");
	
	var profileTabs = $("<div id='profileTabs'>" +
		 					"<div class='profile-ui-tab-container'>" +
		 						"<div class='clearfix'>" +
		 							"<div class='profile-ui-tab-parent-active'>" +
		 								"<span class='profile-tab profile-ui-tab-active'>" + 
		 									$.i18n.prop("profile.tabs.favorite", "收藏") + 
		 								"</span>" +
		 								"<div id='favoriteRefresh' class='icon' style='display:inline;'></div>" +
		 							"</div>" +
		 							"<div>" +
		 								"<span class='profile-tab'>" + 
		 									$.i18n.prop("profile.tabs.comments", "评论") + 
		 								"</span>" +
		 								"<div id='commentsRefresh' class='icon' style='display:none;'></div>" +
		 							"</div>" +
		 						"</div>" +
		 						"<div>" +
		 							"<div id='favoriteContainer' class='profile-ui-tab-content profile-ui-tab-active'>" +
		 								"<div id='favoriteItems'>" +
		 								"</div>" +
		 								"<div id='favoritePagination' style='text-align: center;'>" +
		 								"</div>" +
		 							"</div>" +
		 							"<div id='commentsContainer' class='profile-ui-tab-content profile-ui-tab-active'>" +
		 								"<div id='commentsItems'>" +
		 								"</div>" +
		 								"<div id='commentsPagination' style='text-align: center;'>" +
		 								"</div>" +
		 							"</div>" +
		 						"</div>" +
		 					"</div>" +
		 				"</div>");

	profile.append(profileTabs);
    
    var favoriteRefresh = profile.find("#favoriteRefresh");
    favoriteRefresh.click(function() {
    	Profile.queryFavoriteShop(1, Profile.pageCount, true, true);
    });
    
    var commentsRefresh = profile.find("#commentsRefresh");
    commentsRefresh.click(function() {
    	Profile.queryMyComments(1, Profile.pageCount, true, true);
    });
    
	Profile.tabs = new $.fn.tab({
        tabList: "#profileTabs .profile-ui-tab-container .clearfix div .profile-tab",
        contentList: "#profileTabs .profile-ui-tab-container .profile-ui-tab-content",
        tabActiveClass: "profile-ui-tab-active",
        tabDisableClass: "profile-ui-tab-disable",
        tabParentActiveClass: "profile-ui-tab-parent-active",
        callBackHideEvent: function(index) {
        	if (index == 0) {
        		favoriteRefresh.hide();
        	} else if (index == 1) {
        		commentsRefresh.hide();
        		commentsRefresh.css("display", "none");
        	}
        },
        callBackShowEvent: function(index) {
        	if (index == 0) {
        		favoriteRefresh.show();
			} else if (index == 1) {
				commentsRefresh.show();
        		if (!Profile.hasQueriedComments) {
					Profile.queryMyComments(1, Profile.pageCount, true, true);
					Profile.hasQueriedComments = true;
				}
        	}
        }
    });
    
    
};

Profile.queryMyComments = function(pageIndex, max, updatePage, getTotal) {
	var connectionMgr = XmppConnectionMgr.getInstance();
	var conn = connectionMgr.getAllConnections()[0];
	if (!conn) {
		return;
	}
	
	var streamId = connectionMgr.getStreamId();
	var username = conn.getJid().getNode();
	
	var data = {
		action: "getusershopcomments",
		username: username,
		streamid: streamId,
		page: pageIndex,
		count: max
	};
	
	if (getTotal) {
		data.gettotal = 1;
	}
	$.ajax({
		url: "/shop/",
		dataType: "json",
		cache: false,
		type: "post",
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		data: data,
		success: function(queryResult){
			var commentsItems = $("#commentsItems");
			commentsItems.empty();
			
			
			var comments = queryResult.comments;
			var commsJqObj = $("<div></div>");
			for (var i = 0; i < comments.length; ++i) {
				var onecomment = comments[i];
				var commentJqObj = $("<div shopId='" + onecomment.shopId + "' class='list-item'>" +
										"<div>" +
											"<span>" + onecomment.shopTitle + "</span>" +
											"<span>" + new Date(onecomment.time).format("yyyy-MM-dd hh:mm:ss") + "</span>" +
										"</div>" +
										"<div>" +
											$.i18n.prop("search.shopDetail.restaurant.score", "总分：") +
											onecomment.score + 
										"</div>" + 
										"<div>" + onecomment.content + "</div>" +
									"</div>");
				commentJqObj.click(function() {
					Search.getShopDetail(onecomment.shopId, true);
				});	
				commsJqObj.append(commentJqObj);
				
			}
			commentsItems.append(commsJqObj);
			if (updatePage) {
				var total = queryResult.total;
				var commentsPagination = $("#commentsPagination");
				
				var commentsPaginationJqObj = new $.fn.Pagination({
					renderTo: commentsPagination,
					total: Math.ceil(total / max),
					current: 1,
					onChanged: function(page) {
						Profile.queryMyComments(page, max, false, false);
					}
				});
			}
		},
		error: function(xmlHttpRequest, textStatus, errorThrown) {
			
		},
		complete: function(xmlHttpRequest, textStatus) {
			
		}
	});
};

Profile.queryFavoriteShop = function(pageIndex, count, updatePage, getTotal) {
	
	var connectionMgr = XmppConnectionMgr.getInstance();
	var conn = connectionMgr.getAllConnections()[0];
	if (!conn) {
		return;
	}
	
	var streamId = connectionMgr.getStreamId();
	var username = conn.getJid().getNode();
	
	var data = {
		action: "getfavoriteshops",
		username: username,
		streamid: streamId,
		page: pageIndex,
		count: count,
	};
	
	if (getTotal) {
		data.gettotal = 1;
	}
	
	$.ajax({
		url: "/shop/",
		dataType: "json",
		cache: false,
		type: "post",
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		data: data,
		success: function(queryResult){
			var favoriteItems = $("#favoriteItems");
			favoriteItems.empty();
			
			var shops = queryResult.shops;
			var shopsJqObj = $("<div></div>");
			for (var i = 0; i < shops.length; ++i) {
				var shop = shops[i];
				var shopItemJqObj = Profile.createFavoriteItem(shop);
				shopsJqObj.append(shopItemJqObj);								
			}
			
			favoriteItems.append(shopsJqObj);
			if (updatePage) {
				var total = queryResult.total;
				var favoritePaginationJqObj = $("#favoritePagination");
				
				var favoritePagination = new $.fn.Pagination({
					renderTo: favoritePaginationJqObj,
					total: Math.ceil(total / count),
					current: 1,
					onChanged: function(page) {
						Profile.queryFavoriteShop(page, count, false, false);
					}
				});
			}
		},
		error: function(xmlHttpRequest, textStatus, errorThrown) {
			
		},
		complete: function(xmlHttpRequest, textStatus) {
			
		}
	});
	
};


Profile.createFavoriteItem = function(favoriteItem) {
	var favoriteItemPanel = $("<div favoriteShopId='" + favoriteItem.favoriteShopId + "' shopId='" + favoriteItem.shopId + "' class='list-item'>" +
									"<table style='width:100%;'>" +
										"<tr>" +
											"<td style='width:100%;'>" +
												"<div>" + favoriteItem.shopName + " " + favoriteItem.street +"</div>" +
												"<div>" + favoriteItem.tel + "</div>" +
											"</td>" +
											"<td valign='right'>" +
												"<div class='icon closeButton'></div>" + 											"</td>" +
										"</tr>" +
									"</table>" +
								"</div>");
	favoriteItemPanel.find("td:first").click(function(){	
		var shopId = favoriteItem.shopId;
		Search.getShopDetail(shopId, true);
	});
	
	favoriteItemPanel.find(".closeButton").click(function() {
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (!conn) {
			return;
		}
		
		var streamId = connectionMgr.getStreamId();
		var username = conn.getJid().getNode();
		
		$.ajax({
			url: "/shop/",
			dataType: "json",
			cache: false,
			type: "post",
			contentType: "application/x-www-form-urlencoded; charset=UTF-8",
			data: {
				action: "removefavoriteshop",
				username: username,
				streamid: streamId,
				favoriteshopid: favoriteItem.favoriteShopId
			},
			success: function(returnValue) {
				var opts = MainUtils.cloneObj(Main.notifyOpts);
				if (returnValue.result == "success") {
					Profile.queryFavoriteShop(1, Profile.pageCount, true, true);
					opts.message = $.i18n.prop("profile.favorite.removeSuccess", "删除成功！");
				} else {
					opts.message = $.i18n.prop("contact.removeContactFailed", "删除失败！");
					opts.css.backgroundColor = "red";
				}
				$.blockUI(opts);
			},
			error: function(xmlHttpRequest, textStatus, errorThrown) {
				var opts = MainUtils.cloneObj(Main.notifyOpts);
				opts.message = $.i18n.prop("contact.removeContactFailed", "删除失败！");
				opts.css.backgroundColor = "red";
				$.blockUI(opts);
			},
			complete: function(xmlHttpRequest, textStatus) {
				
			}
		});
		
	});
	
	
	return favoriteItemPanel;
};


Preferences = {};
Preferences.preferencesItems = {};

Preferences.setPreferenceItem = function(preferenceName, value) {
	var oldValue = Preferences.preferencesItems[preferenceName];
	if (oldValue != value) {
		Preferences.preferencesItems[preferenceName] = value;
		Preferences.fireItemChangedListener(preferenceName, oldValue, value);
	}
};

Preferences.getPreferenceItem = function(preferenceName) {
	return Preferences.preferencesItems[preferenceName];
};

Preferences.preferencesItems["shareLoc"] = false;
Preferences.itemChangedListeners = [];
Preferences.addItemChangedListener = function(listener) {
	if (!isArray(listener.preferenceNames)) {
		listener.preferenceNames = [listener.preferenceNames];
	}
	
	Preferences.itemChangedListeners.push(listener);
};

Preferences.removeItemChangedListener = function(listener) {
	var listeners = Preferences.itemChangedListeners;
	for (var i = 0; i < listeners.length; ++i){
		if (listeners[i] == listener){
			listeners.splice(i,1);
			break;
		}
	}
};

Preferences.fireItemChangedListener = function(preferenceName, oldValue, newValue) {
	var listeners = Preferences.itemChangedListeners;
	for (var i = 0; i < listeners.length; ++i){
		var l = listeners[i];
		if (l.preferenceNames.contains(preferenceName)){
			l.handler(preferenceName, oldValue, newValue);
			break;
		}
	}
};

Preferences.init = function() {
	var preferencesItems = $("<div id='preferencesItems'>" +
								"<table>" +
									"<tr>" +
										"<td>" +
											"<input id='shareloc' name='shareloc' type='checkbox' checked='checked'/>" +
											"<label id='shareloc_label' for='shareloc'>" +
												$.i18n.prop("preferences.shareLoc", "共享位置信息") +
											"</label>" +
										"</td>" +
									"</tr>" +
									"<tr>" +
										"<td>" +
											"<div>" +
												"<input id='showContactPos' name='showContactPos' type='checkbox' checked='checked'/>" +
												"<label id='showContactPos_label' for='showContactPos'>" +
													$.i18n.prop("contact.showContactPos", "显示联系人位置") +
												"</label>" +
											"</div>" + 
										"</td>" +
									"</tr>" +
									"<tr>" +
										"<td>" +
											"<div style='text-align:center;'>" +
												"<a id='preferencesProfile' href='javascript:void(0);'>" +
													$.i18n.prop("preferences.profile", "资料") + 
												"</a>" +
											"</div>" +
										"</td>" +
									"</tr>" +
									"<tr>" +
										"<td>" +
											"<div style='text-align:center;'>" +
												"<input id='savePreferences' type='button' class='button-base' value='" + $.i18n.prop("preferences.savePreferences", "保存") + "'/>" +
											"</div>" +
										"</td>" +
									"</tr>" +
								"</table>" +
							"</div>");
						
	preferencesItems.find("#savePreferences").click(function(){
		var iq = new Iq(IqType.SET);
	
		var privateXml = new IqPrivateXml();
		var preferencesExtension = new PreferencesExtension();
		
		var showContactPosCheckbox = $("#showContactPos");
		var showContactPosValue = (showContactPosCheckbox.attr("checked") == true) + "";
		preferencesExtension.setPreference("showContactPos", showContactPosValue);
		
		var sharelocCheckbox = $("#shareloc");
		var sharelocValue = (sharelocCheckbox.attr("checked") == true) + "";
		preferencesExtension.setPreference("shareLoc", sharelocValue);
	
		privateXml.setPacketExtension(preferencesExtension);
		iq.addPacketExtension(privateXml);
		
		var connectionMgr = XmppConnectionMgr.getInstance();
		var conn = connectionMgr.getAllConnections()[0];
		if (conn) {
			conn.handleStanza({
				filter: new PacketIdFilter(iq.getStanzaId()),
				timeout: Christy.loginTimeout,
				handler: function(iqResponse) {
					var opts = MainUtils.cloneObj(Main.notifyOpts);
					if (iqResponse.getType() == IqType.RESULT) {
						opts.message = $.i18n.prop("preferences.saveSuccess", "保存成功！");
						Preferences.setPreferenceItem("shareLoc", sharelocValue);
						Preferences.setPreferenceItem("showContactPos", showContactPosValue);
					} else {
						opts.message = $.i18n.prop("preferences.saveFailed", "保存失败！");
						opts.css.backgroundColor = "red";
					}
					$.blockUI(opts);
					$("#sys").click();
				},
				timeoutHandler: function() {
					var opts = MainUtils.cloneObj(Main.notifyOpts);
					opts.message = $.i18n.prop("preferences.saveFailed", "保存失败！");
					opts.css.backgroundColor = "red";
					$.blockUI(opts);
					$("#sys").click();
				}
			});
			conn.sendStanza(iq);
		}
		
	});
	$("#sysAction").before(preferencesItems);
};

Preferences.updatePreferences = function(updateUI) {
	var iq = new Iq(IqType.GET);
	
	var privateXml = new IqPrivateXml();
	var preferencesExtension = new PreferencesExtension();
	privateXml.setPacketExtension(preferencesExtension);
	iq.addPacketExtension(privateXml);
	
	var connectionMgr = XmppConnectionMgr.getInstance();
	var conn = connectionMgr.getAllConnections()[0];
	if (conn) {
		conn.handleStanza({
			filter: new PacketIdFilter(iq.getStanzaId()),
			timeout: Christy.loginTimeout,
			handler: function(iqResponse) {
				if (iqResponse.getType() == IqType.RESULT) {
					var privateXmlX = iqResponse.getPacketExtension(IqPrivateXml.ELEMENTNAME, IqPrivateXml.NAMESPACE);
					if (privateXmlX) {
						var preferencesX = privateXmlX.getPacketExtension();
						if (preferencesX) {
							var preferences = preferencesX.getAllPreferences();
							for (var key in preferences) {
								Preferences.setPreferenceItem(key, preferences[key]);
							}							
							if (updateUI) {
								Preferences.updatePreferencesUI();
							}
						}
					}
					
				}				
			}
		});
		
		conn.sendStanza(iq);
	}
};

Preferences.updatePreferencesUI = function() {
	var sharelocValue = (Preferences.preferencesItems["shareLoc"] == "true");				
	var sharelocCheckbox = $("#shareloc");
	sharelocCheckbox.attr("checked", sharelocValue);
	
	var showContactPosValue = (Preferences.preferencesItems["showContactPos"] == "true");				
	var showContactPosCheckbox = $("#showContactPos");
	showContactPosCheckbox.attr("checked", showContactPosValue);
	
};