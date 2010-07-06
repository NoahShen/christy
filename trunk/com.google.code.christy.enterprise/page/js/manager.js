Ext.onReady(function(){
    Ext.QuickTips.init();
 	Ext.BLANK_IMAGE_URL = "/lib/resources/images/default/s.gif";
 	
	
	var viewport = new Ext.Viewport({
        layout: 'border',
        items: [{
            region: 'north',
            id: 'north',
            split: false,
            height: 50,
            collapsible: false,
            margins: '0 0 0 0'
        }, 
        new Ext.TabPanel({
        	id:'tabs',
            region: 'center',
            plain:true,
            activeTab: 0,
            defaults:{autoScroll: true},
            frame:true,
            items:[{
	                title: 'Normal Tab',
	                html: "My content was added during construction."
	        }]
        })]
    });
	
});
