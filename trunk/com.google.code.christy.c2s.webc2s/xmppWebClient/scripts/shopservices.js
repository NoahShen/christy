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
	
	
	
	var controlBar = $("<table id='shopcontrolbar' style='width:100%;'>" +
							"<tr>" +
								"<td style='width:33%;'>" +
									"<button style='float:left;margin-left:0.2cm;' class='sexybutton sexysimple sexymygray sexysmall'>Back</button>" +
								"</td>" +
								"<td style='width:33%;'>" +
									"<div id='shopTitle'>Search</div>" +
								"</td>" +
								"<td>" +
									"<button style='float:right;margin-right:1cm;' class='sexybutton sexysimple sexymygray sexysmall'>Search Near</button>" +
									"<button style='float:right;margin-right:1cm;display:none;' class='sexybutton sexysimple sexymygray sexysmall'>Map List</button>" +
									"<button style='float:right;margin-right:1cm;display:none;' class='sexybutton sexysimple sexymygray sexysmall'>Map</button>" +
								"</td>" +
							"</tr>" +
						"</table>");
				
	var buttons = controlBar.find("button");		
	$(buttons.get(0)).click(function(){
		var showItem = $("#shopCenter").children("div:visible");
		var prev = showItem.prev();
		if (prev[0]) {
			$("#shopTitle").text(prev.attr("title"));
			prev.siblings().hide();
			prev.show();
			$.layoutEngine(shopserviceTablayoutSettings);
		}
		
	});
	
	$(buttons.get(1)).click(function(){
		var shopList = $("#shoplist");
		shopList.empty();
		
		var shopInfo = {
			id: 0,
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
		
		$("#shopTitle").text(shopList.attr("title"));
		shopList.siblings().hide();
		shopList.show();
	});
	
	$(buttons.get(2)).click(function(){
		var shopInfo = {
			id: 0,
			name: "上海1号私藏菜",
			imgSrc: "/resource/hongshaorou.jpg",
			hasCoupon: true,
			score: 90,
			perCapita: 50,
			street: "鲁班路",
			type: "本帮菜",
			longitude: 31.221891,
			latitude: 121.443297
		};
	});
	
	shopservices.append(controlBar);
	
	var shopCenter = $("<div id='shopCenter'></div>");
	var shopSearch = $("<div id='shopSearch' title='Search' class='marginpadding'></div>");
	var searchBar = $("<div style='text-align:center;margin:auto;'>" +
							"<input type='text' style='margin-right:0.1cm;'/>" +
							"<button class='sexybutton sexysimple sexymygray sexysmall'>Search</button>" +
						"</div>");
	
	shopSearch.append(searchBar);
	
	shopSearch.append("<br/>");
	
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
	shopCenter.append(shopSearch);	

	
	var shopList = $("<div id='shoplist' title='Search Result' class='marginpadding' style='display:none;'></div>");
	shopCenter.append(shopList);
	
	var shopDetailJqObj = $("<div id='shopdetail' class='marginpadding' style='display:none;'></div>");
	shopCenter.append(shopDetailJqObj);
	
	shopservices.append(shopCenter);
	
	
	shopserviceTablayoutSettings = {
		Name: "Main",
        Dock: $.layoutEngine.DOCK.FILL,
        EleID: "main",
		Children:[{
			Name: "Top",
			Dock: $.layoutEngine.DOCK.FILL,
			EleID: "shopservices",
			Children:[{
	 			Name: "Top2",
				Dock: $.layoutEngine.DOCK.TOP,
				EleID: "shopcontrolbar",
				Height: 30
			}, {
				Name: "Fill2",
				Dock: $.layoutEngine.DOCK.FILL,
		 		EleID: "shopCenter"
			}]
		}]
        
	};
	
	
	$("#main").append(shopservices);
	shopservices.siblings().hide();
	
	
	$.layoutEngine(shopserviceTablayoutSettings);
	
})();

function showShopServices() {
	var shopservices = $("#shopservices");
	shopservices.siblings().hide();
	shopservices.show();
	$.layoutEngine(shopserviceTablayoutSettings);
}

function createShopInfo(shopInfo) {
	var shopInfoTable = $("<table shopId='" + shopInfo.id + "'>" +
								"<tr>" +
									"<td>" +
										"<img src='" + shopInfo.imgSrc + "' width='50' height='50' />" +
									"</td>" +
									"<td>" +
										"<div>" + shopInfo.name + " "+ shopInfo.hasCoupon + "</div>" +
										"<div>" + shopInfo.score + " "+ shopInfo.perCapita + "</div>" +
										"<div>" + shopInfo.street + " "+ shopInfo.type + "</div>" +
									"</td>" +
								"</tr>" +
							"</table>");
	shopInfoTable.click(function(){
		var shopId = $(this).attr("shopId");
		
		var shopDetail = {
			basicInfo: {
				id: 0,
				name: "上海1号私藏菜",
				imgSrc: "/resource/hongshaorou.jpg",
				hasCoupon: true,
				score: 90,
				perCapita: 50,
				addr: "上海市静安区南京西路1856号",
				phone: 123456789
			},
			
			intro: "私藏菜比私房菜更多一点点“藏”的意思，有“酒香不怕巷子深”的傲气，正合了中国人爱追根究底的惯常。" +
					"所以对于私藏变为公众皆知的秘密也就理所当然，无数的欲说还休。老上海的韵味一边敛一边放。老式台灯、" +
					"桌案、杨州漆器、铁质鸟笼、欧式沙发、回纹走廊等等，尽数着婉约复古的气息，美食暖胃，缓如流水。" +
					"上海1号私藏菜是以本帮菜、海派菜为主打，每一道菜都是玩过花样儿的。即使冠着简单寻常的名字，" +
					"厨师们却下了无数的心思在里面，让时尚上海人的健康饮食观念贯彻得更透，浓油赤酱皆改作了清爽耐品，" +
					"许多烹饪秘方私家独创，精致耐品，故名之“私藏菜”。细碟精巧的手撕豇豆藏着淡淡芥末味，毫无疑问地手工制作；" +
					"老弄堂红烧肉的选材更是讲究，只用野猪与家猪杂交的第五代猪肉；火山石器烧裙翅用功深，滋补功效好，中看中吃",
			
			evaluation:[{
				name: "服务",
				value: 90
			}, {
				name: "口味",
				value: 91
			}],
			
			comments:[{
				username: "Noah",
				time: new Date().format("yyyy-MM-dd hh:mm:ss"),
				score: 90,
				content: "早就听说这家的菜很好吃了 很多人都喜欢来这家的哦 我是和家人一起来的 那次我们吃的都是很满意呢 这家的环境还是不错哦 价格也是公道的 我们都是可以接受呢 真的是不错哦 热情的服务也是我们非常的满意呢 值得来试试哦"
			}, {
				username: "Noah2",
				time: new Date().format("yyyy-MM-dd hh:mm:ss"),
				score: 95,
				content: "很奇怪的一家店，11点过去，刚开门的时候，居然就排队，排队的都是5、60的老人。诺大的店堂，居然只有非常小的电梯可以上去，一次也就6个人。中午的午市的火山石器烧裙翅吃口不错，才43，的确是特色了。下次有机会来吃点心"
			}]
		};
		
		var shopDetailJqObj = $("#shopdetail");
		shopDetailJqObj.empty();
		shopDetailJqObj.attr("title", "上海1号私藏菜");
		var baseInfo = shopDetail.basicInfo;
		var shopBaseInfo = $("<table shopId='" + baseInfo.id + "'>" +
								"<tr>" +
									"<td>" +
										"<img src='" + baseInfo.imgSrc + "' width='50' height='50' />" +
									"</td>" +
									"<td>" +
										"<div>" + baseInfo.name + " "+ baseInfo.hasCoupon + "</div>" +
										"<div>" + baseInfo.score + " "+ baseInfo.perCapita + "</div>" +
									"</td>" +
								"</tr>" +
							"</table>");
		shopDetailJqObj.append(shopBaseInfo);

		var contact = $("<table>" +
							"<tr>" +
								"<td style='word-break:break-all;'>" +
									shopDetail.intro +
								"</td>" +
							"</tr>" +
							"<tr>" +
								"<td style='word-break:break-all;'>" +
									"address:" + baseInfo.addr + "<br/>" +
									"phone:" + baseInfo.phone + 
								"</td>" +
							"</tr>" +
						"</table>");
						
		shopDetailJqObj.append(contact);
		
		var evaluJqObj = $("<div></div>");		
		var evalus = shopDetail.evaluation;
		for (i = 0; i < evalus.length; ++i) {
			var evaluation = evalus[i];
			evaluJqObj.append("<span>" + evaluation.name + ":" + evaluation.value + "</span>");
		}
		shopDetailJqObj.append(evaluJqObj);
		
		shopDetailJqObj.append("<br/>");
		
		var comms = shopDetail.comments;
		var commsJqObj = $("<div></div>");
		for (i = 0; i < comms.length; ++i) {
			var onecomment = comms[i];
			commsJqObj.append("<table>" +
									"<tr>" +
										"<td>" + onecomment.username + "</td>" +
										"<td>" + onecomment.time + "</td>" +
									"</tr>" +
									"<tr>" +
										"<td colspan='2'>" + onecomment.score + "</td>" +
									"</tr>" +
									"<tr>" +
										"<td colspan='2'>" + onecomment.content + "</td>" +
									"</tr>" +
								"</table>");
		}
		
		$("#shopTitle").text(shopDetailJqObj.attr("title"));
		shopDetailJqObj.append(commsJqObj);
		shopDetailJqObj.siblings().hide();
		shopDetailJqObj.show();
		$.layoutEngine(shopserviceTablayoutSettings);
	});
	
	return shopInfoTable;
}