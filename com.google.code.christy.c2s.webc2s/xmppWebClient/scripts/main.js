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
									"<div id='sys'>" + $.i18n.prop("topBar.sys", "系统") + "</div>" +
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
										"<input id='statusMessage' type='text'/>" +
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
							"<u>" + $.i18n.prop("tabs.search", "搜索") + "</u>" +
							"<u>" + $.i18n.prop("tabs.map", "地图") + "</u>" +
							"<u>" + $.i18n.prop("tabs.profile", "资料") + "</u>" +
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
		$("#chatPanel [messagearea]").height(IM.getMessageContentHeight());
		$("#mapCanvas").height(Map.getMapCanvasHeight());
	});
	
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
	var newGroupJqObj = $("<div></div>").attr("groupname", groupName).attr("displayname", displayName);
	var groupLabel = $("<div id='" + groupName + "-label' class='contactGroup'></div>").text(displayName);
	
	newGroupJqObj.append(groupLabel);
	groupLabel.click(function(){
		var groupContacts = newGroupJqObj.children("[contactjid]");
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
							

	
	newContactJqObj.attr("contactjid", newBareJid.toPrepedBareJID());
	newContactJqObj.attr("statuscode", 0);
	
	var tdFirst = newContactJqObj.find("td:first");
	tdFirst.click(function(){
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
	});
	
	
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
	var chatPanel = $("#chatPanel");
	var jid = contact.getBareJid();
	var jidStr = jid.toPrepedBareJID();
	
	var contactChatPanel = chatPanel.children("div[chatContactJid='" + jidStr + "']");
	if (contactChatPanel[0]) {
		return contactChatPanel;
	}

	
	var contentHeight = IM.getMessageContentHeight();
	
	contactChatPanel = $("<div chatContactJid='" + jidStr + "' style='display:none;'>" +
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
															"<input type='text'/>" +
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