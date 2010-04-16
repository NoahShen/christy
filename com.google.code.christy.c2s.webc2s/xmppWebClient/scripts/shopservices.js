(function() {
	
	var shopservices = $("<div id='shopservices'></div>");
	
	var shopservicesTop = $("<div id='shopTop'></div>");

	var shopsearchTab = $("<b id='shopsearch-tab' class='marginpadding'></b>");
	shopsearchTab.attr("type", "shopsearch");
	shopsearchTab.addClass("sexybutton");
	shopsearchTab.text("shopsearchTab");
	shopservicesTop.append(shopsearchTab);
	
	var shoprankTab = $("<b id='shoprank-tab' class='marginpadding'></b>");
	shoprankTab.attr("type", "shoprank");
	shoprankTab.addClass("sexybutton");
	shoprankTab.text("shoprankTab");
	shopservicesTop.append(shoprankTab);
	
	var shopinfoTab = $("<b id='shopinfo-tab' class='marginpadding'></b>");
	shopinfoTab.attr("type", "shopinfo");
	shopinfoTab.addClass("sexybutton");
	shopinfoTab.text("shopinfoTab");
	shopservicesTop.append(shopinfoTab);
	
	shopservices.append(shopservicesTop);
	
	shopservicesTop.find("b:first").addClass("sexysimple sexyorange");
	shopservicesTop.find("b").click(function(){
		$(this).addClass("sexysimple sexyorange").siblings("b").removeClass("sexysimple sexyorange");
		var type = $(this).attr("type");
		var centerchildren = $("#shopCenter").children();
		$.each(centerchildren, function(index, value) {
			var jqEl = $(value);
			if (type == jqEl.attr("type")) {
				jqEl.show();
			} else {
				jqEl.hide();
			}
		});
		if (type == "shopsearch") {
			$.layoutEngine(shopsearchTablayoutSettings);
		} else if (type == "shoprank") {
			
		} else if (type == "shopinfo") {
			
		}
	});
	
	var shopservicesCenter = $("<div id='shopCenter'></div>");
	
	var searchBar = $("<div style='text-align:center;margin:auto;'>" +
							"<input type='text' style='margin-right:0.1cm;'/>" +
							"<button class='sexybutton sexysimple sexymygray sexysmall'>Search</button>" +
							"<button class='sexybutton sexysimple sexymygray sexysmall'>Search Near</button>" +
						"</div>");

	searchBar.attr("type", "shopsearch");
	
	shopservicesCenter.append(searchBar);
	
	shopservicesCenter.append("<br/>");
	
	var popularArea = $("<table>" +
							"<tr>" +
								"<td>Popular Area Title</td>" +
							"</tr>" +
							"<tr>" +
								"<td>Popular Area1</td>" +
							"</tr>" +
							"<tr>" +
								"<td>Popular Area2</td>" +
							"</tr>" +
							"<tr>" +
								"<td>Popular Area3</td>" +
							"</tr>" +
							"<tr>" +
								"<td>Popular Area4</td>" +
							"</tr>" +
						"</table>");
	
	shopservicesCenter.append(popularArea);

	shopservices.append(shopservicesCenter);
	
	shopsearchTablayoutSettings = {
		Name: "Main",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "main",        
        Children:[{
			Name: "Fill",
			Dock: $.layoutEngine.DOCK.FILL,
	 		EleID: "shopservices",
	 		Children:[{
	 			Name: "Top2",
				Dock: $.layoutEngine.DOCK.TOP,
				EleID: "shopTop",
				Height: 40
	 		},{
	 			Name: "Fill2",
				Dock: $.layoutEngine.DOCK.FILL,
		 		EleID: "shopCenter"
	 		}]
		}]
	};
	
	
	$("#main").append(shopservices);
	
	$.layoutEngine(shopsearchTablayoutSettings);
	
	shopservices.siblings().hide();
})();