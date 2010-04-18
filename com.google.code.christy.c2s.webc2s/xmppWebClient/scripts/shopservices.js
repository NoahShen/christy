(function() {
	
	var shopservices = $("<div id='shopservices'></div>");
	
//	var shopservicesTop = $("<div id='shopTop'></div>");

//	var shopsearchTab = $("<b id='shopsearch-tab' class='marginpadding'></b>");
//	shopsearchTab.attr("type", "shopsearch");
//	shopsearchTab.addClass("sexybutton");
//	shopsearchTab.text("shopsearchTab");
//	shopservicesTop.append(shopsearchTab);
//	
//	var shoprankTab = $("<b id='shoprank-tab' class='marginpadding'></b>");
//	shoprankTab.attr("type", "shoprank");
//	shoprankTab.addClass("sexybutton");
//	shoprankTab.text("shoprankTab");
//	shopservicesTop.append(shoprankTab);
//	
//	var shopinfoTab = $("<b id='shopinfo-tab' class='marginpadding'></b>");
//	shopinfoTab.attr("type", "shopinfo");
//	shopinfoTab.addClass("sexybutton");
//	shopinfoTab.text("shopinfoTab");
//	shopservicesTop.append(shopinfoTab);
	
//	shopservices.append(shopservicesTop);
	
//	shopservicesTop.find("b:first").addClass("sexysimple sexyorange");
//	shopservicesTop.find("b").click(function(){
//		$(this).addClass("sexysimple sexyorange").siblings("b").removeClass("sexysimple sexyorange");
//		var type = $(this).attr("type");
//		var centerchildren = $("#shopCenter").children();
//		$.each(centerchildren, function(index, value) {
//			var jqEl = $(value);
//			if (type == jqEl.attr("type")) {
//				jqEl.show();
//			} else {
//				jqEl.hide();
//			}
//		});
//		if (type == "shopsearch") {
//			$.layoutEngine(shopsearchTablayoutSettings);
//		} else if (type == "shoprank") {
//			
//		} else if (type == "shopinfo") {
//			
//		}
//	});
	
	var shopCenter = $("<div id='shopCenter'></div>");
	var shopSearch = $("<div id='shopSearch' class='marginpadding'></div>");
	var searchBar = $("<div style='text-align:center;margin:auto;'>" +
							"<input type='text' style='margin-right:0.1cm;'/>" +
							"<button class='sexybutton sexysimple sexymygray sexysmall'>Search</button>" +
						"</div>");
	
	shopSearch.append(searchBar);
	
	var br = $("<br/>");
	shopSearch.append(br);
	
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
	shopSearch.append(popularArea);

	var controlBar = $("<div id='shopcontrolbar'>" +
							"<button style='float:right;margin-right:0.2cm;' class='sexybutton sexysimple sexymygray sexysmall'>Search Near</button>" +
						"</div>");
	
	controlBar.children("button").click(function(){
		var shopList = $("#shoplist");
		shopList.empty();
		shopList.siblings().hide();
		shopList.show();
		
		var shopInfo = {
			name: "上海1号私藏菜",
			imgSrc: "/resource/hongshaorou.jpg",
			hasCoupon: true,
			score: 90,
			perCapita: 50,
			street: "鲁班路",
			type: "本帮菜"
		};
		
		var shopTable = createShopInfo(shopInfo);
		shopList.append(shopTable);
	});
	
	shopCenter.append(shopSearch);
	
	var shopList = $("<div id='shoplist' class='marginpadding' style='display:none;'></div>");
	shopCenter.append(shopList);
	
	shopservices.append(shopCenter);
	shopservices.append(controlBar);
	
	shopsearchTablayoutSettings = {
		Name: "Main",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "main",        
        Children:[{
			Name: "Fill",
			Dock: $.layoutEngine.DOCK.FILL,
	 		EleID: "shopCenter"
		},{
 			Name: "Bottom",
			Dock: $.layoutEngine.DOCK.BOTTOM,
			EleID: "shopcontrolbar",
			Height: 30
		}]
	};
	
	
	$("#main").append(shopservices);
	shopservices.siblings().hide();
	
	$.layoutEngine(shopsearchTablayoutSettings);
})();

function createShopInfo(shopInfo) {
	var shopInfoTable = $("<table>" +
								"<tr>" +
									"<td>" +
										"<img src='" + shopInfo.imgSrc + "' width='50' height='50' />" +
									"</td>" +
									"<td>" +
										"<div>" + shopInfo.name + "&nbsp"+ shopInfo.hasCoupon + "</div>" +
										"<div>" + shopInfo.score + "&nbsp"+ shopInfo.perCapita + "</div>" +
										"<div>" + shopInfo.street + "&nbsp"+ shopInfo.type + "</div>" +
									"</td>" +
								"</tr>" +
							"</table>");
	return shopInfoTable;
}