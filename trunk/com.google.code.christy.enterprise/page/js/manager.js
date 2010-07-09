$(document).ready(function() {
	
	try {
		
	    $('body').layout({
			resizable: false,
			closable: false,
			spacing_open: 0
		});
	} catch (e) {
		if (window.console) {
			window.console.log(e);
		}
	}
	
	var mainTabs = new $.fn.tab({
        tabList:'#mainTabs .clearfix u',
        contentList:'.main-ui-tab-content',
//        showType:'fade',
        callBackStartEvent:function(index) {
        },
        callBackHideEvent:function(index) {
        },
        callBackShowEvent:function(index) {
        }
    });
    mainTabs.triggleTab(0);
    
    var shopManagerTabs = new $.fn.tab({
        tabList:'#shopManagerTabs .clearfix div',
        contentList:'.shopManage-ui-tab-content',
//        showType:'fade',
        callBackStartEvent:function(index) {
        },
        callBackHideEvent:function(index) {
        },
        callBackShowEvent:function(index) {
        }
    });
    shopManagerTabs.triggleTab(1);
    
    $('#flexme1').flexigrid({
    	height: 'auto'
    });
    
    $('#chooseShopLoc').click(function(){
    	var mapFrame = $('#mapFrame');
    	if (!mapFrame.attr("src")) {
    		mapFrame.attr('src', 'http://www.google.com');
    	}
    	
    	$.blockUI({ 
            message: $('#mapDiv'),
            
            css: {
            	width: '700px',
            	height: '400px',
            	top: '',
				left: '',
                padding: '5px'
            } 
        });
    });
});