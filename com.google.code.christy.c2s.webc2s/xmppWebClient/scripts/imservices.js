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
	
	var userinfotable = $("<table class='marginpadding'>" +
			"<tbody>" +
				"<tr>" +
					"<td>" +
						"<img id='userphoto' src='/resource/userface.bmp'/>" + 
					"</td>" +
					"<td>" +
						"<table>" +
							"<tbody>" +
								"<tr>" +
									"<td valign='middle'>" +
										"<img id='user-status-img' src='/resource/status/available.png' />" +
										"<img id='user-status-menu' src='/resource/statusmenu.png' style='padding-left:2px;padding-right:10px;' />" +
										"<span id='userinfo-username'>Noah</span>" +
									"</td>" +
								"</tr>" +
								"<tr>" +
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
	
	var contactlist = $("<div id='contactlist'></div>");
	contactlist.attr("type", "contact");
	
	imCenter.append(userinfo);
	imCenter.append(contactlist);
	
	var imTopHeight = 30;
	
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
					Height: 60
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
})();