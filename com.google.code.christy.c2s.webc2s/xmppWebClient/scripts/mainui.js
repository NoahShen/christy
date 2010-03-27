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
	
	var center = $("<div id=\"center\" style=\"background-color:#FF3300\">Center<br/><br/><br/><br/><br/><br/></div>");
	mainDiv.append(center);
	var bottom = $("<div id=\"bottom\" style=\"background-color:#CCCCCC\"></div>");
	mainDiv.append(bottom);
	
	
	var layoutSettings = {
	Name: "Main",
	        Dock:   $.layoutEngine.DOCK.FILL,
	        EleID:   "main",        
	        Children:[{
				Name    :   "Fill",
				Dock    :   $.layoutEngine.DOCK.FILL,
		 		EleID   :   "center"
			},{
				Name    :   "Bottom",
				Dock    :   $.layoutEngine.DOCK.BOTTOM,
				EleID   :   "bottom",
				Height  :   125
			}]
	};
	
	$.layoutEngine(layoutSettings);
	
	
	$("body").append(mainDiv);
	
//	var mainDiv = $("<div></div>").css({
//		"position":"absolute",
//		"top": "0px",
//		"width": "100%",
//		"height": "100%"
//	});
//	
//	var serviceDiv = $("<div></div>").css({
//		"position":"relative",
//		"top": "0px",
//		"width": "100%",
//		"height": "100%"
//	}).html("AAAAAAAAAAAAAAAA");
//	
//	
//	var systemDiv = $("<div></div>").css({
//		"position":"relative",
//		"bottom": "20px",
//		"height": "20px"
//		
//	});
//	
	var statusIcon = $("<img id=\"status-img\" src=\"/resource/status/available.png\"></img>");
	
	bottom.append(statusIcon);
	
	//start of scroll 
	var scrollDiv = $("<div></div>")
		.attr("id", "scrollable")
		.addClass("lists");
	
	var prev = $("<a></a>").attr("href", "#").addClass("prev");
	scrollDiv.append(prev);
	
	var scrollOutDiv = $("<div class=\"items\" style=\"overflow: hidden; position: relative; visibility: visible; width: 478px;\">");
	
	var scrollInnerDiv = $("<div style=\"position: absolute; width: 200000em; left: 0px;\" class=\"scrollable_demo\">");
	for (i = 0; i < 10; ++i) {
		var a = $("<a>" + i + "</a>");
		scrollInnerDiv.append(a);
	}
	scrollOutDiv.append(scrollInnerDiv);
	
	scrollOutDiv.append($("<br clear=\"all\"/>"));
	
	scrollDiv.append(scrollOutDiv);
	scrollDiv.append("<a class=\"next\" href=\"#\"></a>");
	
	var $content = $(".scrollable_demo");
	var i = 5;  //已知显示的<a>元素的个数
	var m = 5;  //用于计算的变量
	var count = $content.find("a").length;//总共的<a>元素的个数
	$(".next").click(function(){
		if( !$content.is(":animated")){  //判断元素是否正处于动画，如果不处于动画状态，则追加动画。
			if(m<count){  //判断 i 是否小于总的个数
				m++;
				$content.animate({left: "-=96px"}, 600);
			}
		}
	});
	
	$(".prev").click(function(){
		if( !$content.is(":animated")){
			if(m>i){ //判断 i 是否小于总的个数
				m--;
				$content.animate({left: "+=96px"}, 600);
			}
		}
	});
	//end of scroll 
	
//	mainDiv.append(serviceDiv);
//	mainDiv.append(systemDiv);
	
	
	$("body").append(mainDiv);
})();