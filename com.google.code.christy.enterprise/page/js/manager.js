Ext.onReady(function(){
    Ext.QuickTips.init();
 	Ext.BLANK_IMAGE_URL = "/js/extjs/resources/images/default/s.gif";
 	
	
	var viewport = new Ext.Viewport({
        layout: 'border',
        items: [{
            region: 'north',
            id: 'north',
            split: false,
            height: 40,
            collapsible: false,
            margins: '0 0 0 0',
            autoLoad: 'TopPanel.html'
        }, 
        new Ext.TabPanel({
        	id:'tabs',
            region: 'center',
            plain:true,
            activeTab: 0,
            defaults:{autoScroll: true},
            frame:true,
            items:[{
                title: '商铺管理',
                autoLoad:'ShopManagement.html'
	        },{
	        	title: '优惠管理',
                autoLoad:'BenefitsManagement.html'
	        },{
	        	title: '统计信息',
                autoLoad:'Statistics.html'
	        }]
        })]
    });
	
});
