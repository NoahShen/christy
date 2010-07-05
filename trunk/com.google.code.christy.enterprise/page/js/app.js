$(document).ready(function() {
	
	$('#login').form({
	    success:function(data){
	        $.messager.alert('Info', data, 'info');
	    }
	});
});