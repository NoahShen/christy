ShopService = {};
ShopService.shopSearchResult = {};

ShopService.init = function() {
	
	var shopservices = $("<div id='shopservices'></div>");	
	
	var controlBar = $("<table id='shopcontrolbar' style='width:100%;'>" +
							"<tr>" +
								"<td style='width:33%;'>" +
									"<button style='float:left;margin-left:0.2cm;' class='sexybutton sexysimple sexymygray sexysmall'>Back</button>" +
								"</td>" +
								"<td style='width:33%;'>" +
									"<div id='shopTitle'>Search</div>" +
								"</td>" +
								"<td>" +
									"<button id='searchShop' style='float:right;margin-right:1cm;' class='sexybutton sexysimple sexymygray sexysmall'>Search Nearby</button>" +
									"<button id='maplistShop' style='float:right;margin-right:1cm;display:none;' class='sexybutton sexysimple sexymygray sexysmall'>Map List</button>" +
									"<button id='showshopinmap' style='float:right;margin-right:1cm;display:none;' class='sexybutton sexysimple sexymygray sexysmall'>Map</button>" +
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
			
			var showButton = null;
			if (prev.attr("id") == "shopsearch") {
				showShopSearchPanel();
			} else if (prev.attr("id") == "shoplist") {
				showShopListPanel();
			} else if (prev.attr("id") == "shopdetail") {
				showShopDetailPanel();
			}
			$.layoutEngine(shopserviceTablayoutSettings);
		}
		
	});
	
	$(buttons.get(1)).click(function(){
		var shopList = $("#shoplist");
		$(shopList.children()[0]).empty();
		
		getCurrentPosition(function(p) {
			
			searchShopsByLoc(shopList, p, 1, 2, true);
			
		}, function(){});		
		
		$("#shopTitle").text(shopList.attr("title"));
		shopList.siblings().hide();
		shopList.show();
		
		var nextButton = $(this).next();
		nextButton.siblings().hide();
		nextButton.show();
		
		$.layoutEngine(shopserviceTablayoutSettings);
	});
	
	$(buttons.get(2)).click(function(){
		
	});
	
	shopservices.append(controlBar);
	
	var shopCenter = $("<div id='shopCenter'></div>");
	var shopSearch = $("<div id='shopsearch' title='Search' class='marginpadding'></div>");
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

	
	var shopList = $("<div id='shoplist' title='Search Result' class='marginpadding' style='display:none;'>" +
						"<div style='width:100%;height:100%;'></div>" +
						"<div id='pagination' class='pagination'></div>" +
					"</div>");
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
	
}


ShopService.show = function() {
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
										"<div>" + shopInfo.overall.score + " "+ shopInfo.overall.perCapita + "</div>" +
										"<div>" + shopInfo.street + "</div>" +
									"</td>" +
								"</tr>" +
							"</table>");
	shopInfoTable.click(function(){
		var shopId = $(this).attr("shopId");
		$.ajax({
			url: "/shop/",
			dataType: "json",
			cache: false,
			type: "get",
			contentType: "application/x-www-form-urlencoded; charset=UTF-8",
			data: {
				action: "getshopdetail",
				shopid: shopId
			},
			success: function(shopDetail){
				showShopDetail(shopDetail);
			},
			error: function (xmlHttpRequest, textStatus, errorThrown) {
				
			},
			complete: function(xmlHttpRequest, textStatus) {
				
			}
		});

	});
	
	return shopInfoTable;
}

function showShopDetail(shopDetail) {
	
	var shopDetailJqObj = $("#shopdetail");
	shopDetailJqObj.empty();
	shopDetailJqObj.attr("title", "上海1号私藏菜");
	var baseInfo = shopDetail.basicInfo;
	var overall = shopDetail.overall;
	var shopBaseInfo = $("<table shopId='" + baseInfo.id + "'>" +
							"<tr>" +
								"<td>" +
									"<img src='" + baseInfo.imgSrc + "' width='50' height='50' />" +
								"</td>" +
								"<td>" +
									"<div>" + baseInfo.name + " "+ baseInfo.hasCoupon + "</div>" +
									"<div>" + overall.score + " "+ overall.perCapita + "</div>" +
									"<span>Service:" + overall.service + "</span>" + 
									"<span>Taste:" + overall.taste + "</span>" + 
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
	
	shopDetailJqObj.append("<br/>");
	
	var comms = shopDetail.comments;
	var commsJqObj = $("<div></div>");
	for (i = 0; i < comms.length; ++i) {
		var onecomment = comms[i];
		commsJqObj.append("<table>" +
								"<tr>" +
									"<td>" + onecomment.username + "</td>" +
									"<td>" + new Date(onecomment.time).format("yyyy-MM-dd hh:mm:ss") + "</td>" +
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
	
	var showInMap = $("#showshopinmap");
	showInMap.siblings().hide();
	showInMap.show();
	
	$.layoutEngine(shopserviceTablayoutSettings);
}

function showShopSearchPanel() {
	var shopSearch = $("#shopsearch");
	shopSearch.siblings().hide();
	shopSearch.show();
	
	var searchShop = $("#searchShop");
	searchShop.siblings().hide();
	searchShop.show();
	
	$.layoutEngine(shopserviceTablayoutSettings);
}

function showShopListPanel() {
	var shopList = $("#shoplist");
	shopList.siblings().hide();
	shopList.show();
	
	var mapListShop = $("#maplistShop");
	mapListShop.siblings().hide();
	mapListShop.show();
	
	$.layoutEngine(shopserviceTablayoutSettings);
}

function showShopDetailPanel() {
	var shopDetail = $("#shopdetail");
	shopDetail.siblings().hide();
	shopDetail.show();
	
	var showShopInMap = $("#showshopinmap");
	showShopInMap.siblings().hide();
	showShopInMap.show();
	
	$.layoutEngine(shopserviceTablayoutSettings);
}

function searchShopsByLoc(shopList, p, page, count, updatePage) {
	
	$.ajax({
		url: "/shop/",
		dataType: "json",
		cache: false,
		type: "get",
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		data: {
			action: "search",
			latitude: p.coords.latitude,
			longitude: p.coords.longitude,
			page: page,
			count: count
		},
		success: function(searchResult){
			shopSearchResult = searchResult;
			$(shopList.children()[0]).empty();
			var shops = searchResult.shops;
			for (i = 0; i < shops.length; ++i) {
				var shopTable = createShopInfo(shops[i]);
				$(shopList.children()[0]).append(shopTable);
			}
			if (updatePage) {
				$("#pagination").pagination(10, {
					num_edge_entries: 1,
					num_display_entries: 8,
					items_per_page: count,
	                callback: function(page_id, jq) {
						searchShopsByLoc(shopList, p, page_id + 1, count, false);
	                }
	            });
			}
			
			$.layoutEngine(shopserviceTablayoutSettings);
		},
		error: function (xmlHttpRequest, textStatus, errorThrown) {
			
		},
		complete: function(xmlHttpRequest, textStatus) {
			
		}
	});
	
}