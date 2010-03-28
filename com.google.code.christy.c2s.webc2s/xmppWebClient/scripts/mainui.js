(function() {
//	var serviceDiv = $("<div></div>").css({
//		"position":"relative",
//		"top":"0px",
//		"left":"0px",
//		"width":"100%",
//		"height":"97%",
//	});
//	
//	$("body").append(serviceDiv);
	
	var mainDiv = $("<div></div>")
	.attr("id", "main")
	.css({
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
	
	
	var statusIcon = $("<img id='status-img' src='/resource/status/available.png'/>");
	
	bottom.append(statusIcon);
	
	$("body").append(mainDiv);
	$.layoutEngine(layoutSettings);

	$.include(["/scripts/imservices.js"]);
})();