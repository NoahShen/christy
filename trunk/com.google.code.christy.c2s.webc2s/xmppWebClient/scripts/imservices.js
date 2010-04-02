(function() {
	var imTop = $("<div id='imTop'></div>");
	var contactTab = $("<b class='marginpadding'></b>");
	contactTab.attr("type", "contact");
	contactTab.addClass("sexybutton");
	contactTab.text($.i18n.prop("imservices.contact"));
	imTop.append(contactTab);
	
	var chatTab = $("<b class='marginpadding'></b>");
	chatTab.attr("type", "chat");
	chatTab.addClass("sexybutton");
	chatTab.text($.i18n.prop("imservices.chat"));
	imTop.append(chatTab);
	
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
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("Noah", "example.com", "res"), "Noah")));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("aa", "example.com", "res"), "aa")));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("bb", "example.com", "res"), "bb")));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("cc", "example.com", "res"), null)));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("dd", "example.com", "res"), "dd")));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("ee", "example.com", "res"), null)));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("ff", "example.com", "res"), null)));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("gg", "example.com", "res"), null)));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("hh", "example.com", "res"), null)));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("ii", "example.com", "res"), "ii")));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("jj", "example.com", "res"), null)));
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("kk", "example.com", "res"), null)));

	removeContact(contactlist, new XmppContact(new IqRosterItem(new JID("kk", "example.com", "res"), null)));
	
	
	
	imCenter.append(userinfo);
	imCenter.append(contactlist);
	
	var imTopHeight = 40;
	
	var imlayoutSettings = {
		Name: "Main",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "main",        
        Children:[{
			Name: "Fill",
			Dock: $.layoutEngine.DOCK.FILL,
	 		EleID: "center",
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
	
	$("#center").append(imTop);
	$("#center").append(imCenter);
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
	
})();


function addContact(contactlistJqObj, newContact) {
	
	var newJid = newContact.getBareJid();
	
	var insertJqObj = getInsertJqObj(contactlistJqObj, newJid.toPrepedBareJID());
	
	var showName = (newContact.getNickname()) ? newContact.getNickname() : newJid.toPrepedBareJID();
	var newContactJqObj = $("<div>" +
								"<table style='width:100%;'>" +
									"<tr>" +
										"<td >" +
											"<img src='/resource/status/unavailable.png'/>" +
										"</td>" +
										"<td style='width:100%;'>" +
											"<table>" +
												"<tr>" +
													"<td>" +
														"<div>" + showName + "</div>" +
													"</td>" +
												"</tr>" +
												"<tr>" +
													"<td>" +
														"<div>Status Messsage</div>" +
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
							
//	var statusImg = $("<img src='/resource/status/unavailable.png'/>");
//	if (newContact.isResourceAvailable()) {
//		var userResource = newContact.getMaxPriorityResource();
//		var presence = userResource.currentPresence;
//		if (presence.getShow() == PresenceShow.AVAILABLE) {
//			statusImg.attr("src", "/resource/status/available.png");
//		} else if (presence.getShow() == PresenceShow.AWAY) {
//			statusImg.attr("src", "/resource/status/away.png");
//		} else if (presence.getShow() == PresenceShow.CHAT) {
//			statusImg.attr("src", "/resource/status/chat.png");
//		} else if (presence.getShow() == PresenceShow.DND) {
//			statusImg.attr("src", "/resource/status/dnd.png");
//		} else if (presence.getShow() == PresenceShow.XA) {
//			statusImg.attr("src", "/resource/status/xa.png");
//		} 
//	}
	
	newContactJqObj.attr("contactJid", newJid.toPrepedBareJID());
		
	if (insertJqObj == null) {
		contactlistJqObj.append(newContactJqObj);
	} else {
		newContactJqObj.insertBefore(insertJqObj);
	}
	
}

function removeContact(contactlistJqObj, contact) {
	var bareJid = contact.getBareJid();
	var contactEl = contactlistJqObj.children("div[contactJid='" + bareJid.toPrepedBareJID() + "']");
	var contactJqObj = $(contactEl);
	contactJqObj.remove();
}

function getInsertJqObj(contactlistJqObj, newBareJid) {
	var insertJqObj = null;
	var contacts = contactlistJqObj.children();
	$.each(contacts, function(index, value) {
		var contactJqObj = $(value);
		var bareJid = contactJqObj.attr("contactJid");
		if (newBareJid < bareJid) {
			insertJqObj = contactJqObj;
			return false;
		}
	});
	return insertJqObj;
}