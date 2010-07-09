$(document).ready(function() {
	
	
	$('body').layout({
		resizable: false,
		closable: false,
		spacing_open: 0
	});
		
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
        	if (index == 1) {
        		var shopMapCanvas = $('#shopMapCanvas');
        		if (!shopMapCanvas.attr('inited')) {
        			if (GBrowserIsCompatible()) {
	        			var map = new GMap2(shopMapCanvas[0]);
	        			map.setUIToDefault();
		        		map.setCenter(new GLatLng(31.230708, 121.472916), 13);
		        		shopMapCanvas.attr('inited', '1');
	        		}
        		}
        		
        	}
        }
    });
    shopManagerTabs.triggleTab(1);
    
    $('#flexme1').flexigrid({
    	height: 'auto'
    });
    
		
    $('#checkShopBigMap').click(function(){
		var shopBigMap = $('<div id="shopBigMap" style="width:700px;height:300px;"></div>');
		new Boxy(shopBigMap, {
    			title: 'title',
    			closeText: "[关闭]",
    			modal: true,
    			afterShow: function() {
    				if (GBrowserIsCompatible()) {
						var map = new GMap2(shopBigMap[0]);
						map.setUIToDefault();
			    		map.setCenter(new GLatLng(31.230708, 121.472916), 13);
						
					}
    			}
    		});
    });
});