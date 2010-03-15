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

	alert("ds");
	$.getJSON("http://127.0.0.1:7777/webc2scontroller.do?format=json&jsoncallback=?", {"name": "John", "time": "2pm" }, function(data){
		  alert(data.name);
	}); 
});