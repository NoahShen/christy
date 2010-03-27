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
	
	var serviceDiv = $("<div></div>").css({
		"position":"absolute",
		"top": "0px",
		"background-color": "red",
		"width": "100%",
		"height": "100%"
	}).text("AAAAAAAAAA");
	
	
	var systemDiv = $("<div></div>")
	.attr("id", "scrollable")
	.addClass("lists")
	.css({
		"position":"absolute",
		"bottom": "0px"
		
	});
	
	var prev = $("<a></a>").attr("href", "#").addClass("prev");
	systemDiv.append(prev);
	
	var scrollOutDiv = $("<div class=\"items\" style=\"overflow: hidden; position: relative; visibility: visible; width: 478px;\">");
	
	var scrollInnerDiv = $("<div style=\"position: absolute; width: 200000em; left: 0px;\" class=\"scrollable_demo\">");
	for (i = 0; i < 10; ++i) {
		var a = $("<a>" + i + "</a>");
		scrollInnerDiv.append(a);
	}
	scrollOutDiv.append(scrollInnerDiv);
	
	scrollOutDiv.append($("<br clear=\"all\"/>"));
	
	systemDiv.append(scrollOutDiv);
	systemDiv.append("<a class=\"next\" href=\"#\"></a>");
	
	$("body").append(serviceDiv);
	$("body").append(systemDiv);
	
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

	
})();