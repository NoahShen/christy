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
        tabList:"#mainTabs .clearfix u",
        contentList:".main-ui-tab-content",
//        showType:"fade",
        callBackStartEvent:function(index) {
        },
        callBackHideEvent:function(index) {
        },
        callBackShowEvent:function(index) {
        }
    });
    mainTabs.triggleTab(0);
    
    var shopManagerTabs = new $.fn.tab({
        tabList:"#shopManagerTabs .clearfix div",
        contentList:".shopManage-ui-tab-content",
//        showType:"fade",
        callBackStartEvent:function(index) {
        },
        callBackHideEvent:function(index) {
        },
        callBackShowEvent:function(index) {
        }
    });
    shopManagerTabs.triggleTab(0);
    
    $('#flexme1').flexigrid({
    	height: 'auto'
    });
});