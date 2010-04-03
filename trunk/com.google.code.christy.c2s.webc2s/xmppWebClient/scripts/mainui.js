(function() {
	
	var mainDiv = $("<div id='main'></div>").css({
		"position":"absolute",
		"top":"0px",
		"left":"0px",
		"width": "100%",
		"height": "100%"
	});

	
	
	var layoutSettings = {
		Name: "Main",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "main",
        Children:[{
			Name: "Fill",
			Dock: $.layoutEngine.DOCK.FILL,
	 		EleID: "main"
		}]
	};
	
	
	var appMenu = $("<div id='appMenu'><img id='appMenuImg' src='/resource/status/available.png'/></div>");
	appMenu.css({"position": "fixed",
					"right":"0", 
					"top": "0", 
					"z-index": "8888",
					"padding": "10px"});
	
	$("body").append(appMenu);
	$("body").append(mainDiv);
	$.layoutEngine(layoutSettings);
	
	$.include(["/scripts/lib/jquerycontextmenu/jquery.contextMenu.css",
				"/scripts/lib/jquerycontextmenu/jquery.contextMenu.js",
				"/resource/imchat.css"
				], function(){
		$.include(["/scripts/imservices.js"]);
	});
	
})();