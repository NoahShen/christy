(function() {
//	var imDiv = $("<div class='marginpadding'></div>")
//	.attr("id", "imDiv")
//	.css({
//		"position":"relative",
//		"top":"5px",
//		"left":"5px",
//		"width": "100%",
//		"height": "100%"
//	});
//	
//	var contact = $("<b class='marginpadding'></b>");
//	contact.addClass("sexybutton");
//	contact.text($.i18n.prop("imservices.contact"));
//	imDiv.append(contact);
//	
//	var chat = $("<b style=\"margin:10px;padding:5px\"></b>");
//	chat.addClass("sexybutton");
//	chat.text($.i18n.prop("imservices.chat"));
//	imDiv.append(chat);
//	
//	var contactDiv = $("<div id=\"contactDiv\" style=\"margin:10px;\"></div>");
//	
//	
//	var userinfo = $("<div id=\"userinfo\" style='background:blue;'>userinfo</div>");
//	var contactlist = $("<div id=\"contactlist\" style='background:red;'>contactlist</div>");
//	
//	contactDiv.append(userinfo);
//	contactDiv.append(contactlist);
	
//	var contactLayoutSettings = {
//		Name: "ContactLayout",
//        Dock: $.layoutEngine.DOCK.FILL,
//        EleID: "contactDiv",        
//        Children:[{
//			Name: "Top",
//			Dock: $.layoutEngine.DOCK.TOP,
//			EleID: "userinfo",
//			Height: 30
//		},{
//			Name: "Fill",
//			Dock: $.layoutEngine.DOCK.FILL,
//	 		EleID: "contactlist"
//		}]
//	};
	
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
	
	var userphoto = $("<img src='/resource/userface.bmp' class='marginpadding'/>");
	userinfo.append(userphoto);
	
	var contactlist = $("<div id='contactlist'></div>");
	contactlist.attr("type", "contact");
	
	imCenter.append(userinfo);
	imCenter.append(contactlist);
	
	var layoutSettings = {
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
				Height: 30
	 		},{
	 			Name: "Fill2",
				Dock: $.layoutEngine.DOCK.FILL,
		 		EleID: "imCenter",
		 		Children:[{
		 			Name: "Top3",
					Dock: $.layoutEngine.DOCK.TOP,
					EleID: "userinfo",
					Height: 30
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
	$.layoutEngine(layoutSettings);
	
//	
//	imDiv.append(contactDiv);
//	
//	
//	var chatDiv = $("<div id=\"chatDiv\" style=\"margin:10px;\">Chat</div>");
//	// TODO
//	imDiv.append(chatDiv);
//	
//	imDiv.find("b:first").addClass("sexysimple sexyteal");
//	imDiv.find("div:gt(0)").hide();
//	imDiv.find("b").click(function(){
//		$(this).addClass("sexysimple sexyteal").siblings("b").removeClass("sexysimple sexyteal");
//		imDiv.find("div:eq("+$(this).index()+")").show().siblings("div").hide();
//	});
//	
//	
//	$("#center").append(imDiv);
//	$.layoutEngine(contactLayoutSettings);
//	contactDiv.css("position", "relative");
//	userinfo.css("display","");
//	contactlist.css("display","");
})();