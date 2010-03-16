$(document).ready(function() {	
//	$.ajax({
//	  url: "/webc2scontroller.do",
//	  dataType: "json",
//	  cache: false,
//	  async: false,
//	  type: "post",
//	  data: "{'a':'a', 'b':'b'}",
//	  processData: false,
//	  success: function(data){
//	    alert(data);
//	  },
//	  error: function (xmlHttpRequest, textStatus, errorThrown) {
//	  	
//	  }
//	});

//	$.getJSON("http://127.0.0.1:7777/webc2scontroller.do?format=json&jsoncallback=?", {"type": "get", "requestFields": "started;domain;webClientPort;" }, function(data){
//		var html = "";
//		for ( var key in data ) {
//			html += key + ":" + data[key] + "<br/>";
//		}
//		$("#testId1").html(html);
//	}); 
	
	$.getJSON("http://127.0.0.1:7878/defaultc2scontroller.do?format=json&jsoncallback=?", {"type": "get", "requestFields": "started;domain;clientLimit;" }, function(data){
		var html = "";
		for ( var key in data ) {
			html += key + ":" + data[key] + "<br/>";
		}
		$("#testId1").html(html);
	}); 
});