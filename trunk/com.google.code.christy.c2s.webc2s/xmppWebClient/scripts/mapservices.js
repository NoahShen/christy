(function() {
	var mapServices = $("<div id='mapservices'></div>");
	
	
	var controlBar = $("<table id='mapControlbar' style='width:100%;'>" +
							"<tr>" +
								"<td style='width:50%;'>" +
									"<button style='float:left;margin-left:0.2cm;' class='sexybutton sexysimple sexymygray sexysmall'>Route</button>" +
								"</td>" +
								"<td style='width:50%;'>" +
									"<button style='float:right;margin-right:1cm;' class='sexybutton sexysimple sexymygray sexysmall'>Result List</button>" +
								"</td>" +
							"</tr>" +
						"</table>");
						
	mapServices.append(controlBar);
	
	var map = $("<iframe id='mapcanvas' name='mapcanvas' src='/mapcanvas.html' width='100%' height='100%' scrolling='no' frameborder='0'>" +
					"</iframe>");
	mapServices.append(map);		
	
	$("#main").append(mapServices);
	mapServices.siblings().hide();
	
	mapserviceTablayoutSettings = {
		Name: "Main",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "main",
		Children:[{
			Name: "Top",
			Dock: $.layoutEngine.DOCK.FILL,
			EleID: "mapservices",
			Children:[{
	 			Name: "Top2",
				Dock: $.layoutEngine.DOCK.TOP,
				EleID: "mapControlbar",
				Height: 30
			}, {
				Name: "Fill2",
				Dock: $.layoutEngine.DOCK.FILL,
		 		EleID: "mapcanvas"
			}]
		}]  
	};
	$.layoutEngine(mapserviceTablayoutSettings);

})();

function showMapServices() {
	var mapServices = $("#mapservices");
	mapServices.siblings().hide();
	mapServices.show();
	$.layoutEngine(mapserviceTablayoutSettings);
}

function mapFrameLoaded() {
	$.layoutEngine(mapserviceTablayoutSettings);
}