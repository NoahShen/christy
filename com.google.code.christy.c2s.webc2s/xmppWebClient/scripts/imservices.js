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
	
	
	var contactlist = $("<div id='contactlist' style='overflow:auto;padding-left:5px;'></div>");
	contactlist.attr("type", "contact");
	addContact(contactlist, new XmppContact(new IqRosterItem(new JID("Noah", "example.com", "res"), "Noah")))
	
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
		},{
			Name: "Bottom",
			Dock: $.layoutEngine.DOCK.BOTTOM,
			EleID: "bottom",
			Height: 20
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
	
	var insertJqObj = null;
	var contacts = contactlistJqObj.children();
	$.each(contacts, function(index, value) {
		var contactJqObj = $(value);
		var contact = contactJqObj.attr("contact");
		var jid = contact.getBareJid();
		if (newJid.toPrepedBareJID() < jid.toPrepedBareJID()) {
			insertJqObj = contactJqObj;
			return false;
		}
	});
	
	var newContactJqObj = $("<div>" + newJid.toBareJID()+ "</div>");
	newContactJqObj.attr("contact", newContact);
		
	if (insertJqObj == null) {
		contactlistJqObj.append(newContactJqObj);
	} else {
		newContactJqObj.insertBefore(insertJqObj);
	}
	
}