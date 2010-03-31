(function() {
	
	var mainDiv = $("<div id='main'></div>").css({
		"position":"absolute",
		"top":"0px",
		"left":"0px",
		"width": "100%",
		"height": "100%"
	});
	
	var center = $("<div id='center'></div>");
	mainDiv.append(center);
	var bottom = $("<div id='bottom' style='background-color:#CCCCCC'></div>");
	mainDiv.append(bottom);
	
	
	var layoutSettings = {
		Name: "Main",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "main",        
        Children:[{
			Name: "Fill",
			Dock: $.layoutEngine.DOCK.FILL,
	 		EleID: "center"
		},{
			Name: "Bottom",
			Dock: $.layoutEngine.DOCK.BOTTOM,
			EleID: "bottom",
			Height: 20
		}]
	};
	
	
	var appStatusImg = $("<img id='app-status-img' src='/resource/status/available.png'/>");
	
	bottom.append(appStatusImg);
	
	$("body").append(mainDiv);
	$.layoutEngine(layoutSettings);
	
	$.include(["/scripts/lib/jquerycontextmenu/jquery.contextMenu.css",
				"/scripts/lib/jquerycontextmenu/jquery.contextMenu.js"
				], function(){
		$.include(["/scripts/imservices.js"]);
	});
	
})();