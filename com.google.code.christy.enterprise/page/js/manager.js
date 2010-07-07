Ext.onReady(function(){
    Ext.QuickTips.init();
 	Ext.BLANK_IMAGE_URL = "/js/extjs/resources/images/default/s.gif";
 	

    var mainPanel = createMainPanel();
	
	
	var viewport = new Ext.Viewport({
        layout: 'border',
        items: [{
            region: 'north',
            id: 'north',
            split: false,
            height: 40,
            collapsible: false,
            margins: '0 0 0 0',
            contentEl: 'topPanel'
        },
        	mainPanel
        ]
    });
	
});


function createMainPanel() {
	
	var westPanel = createWestPanel();
	
	var mainPanel = new Ext.Panel({
        layout: 'border',
        region: 'center',
        items: [westPanel,{
            region: 'center',
            split: false,
            collapsible: false,
            margins: '0 0 0 0',
            html: "test"
        }]
    });
    
    return mainPanel;
}

function createWestPanel() {
    
    var shopManagerMenuTreePanel = new Ext.tree.TreePanel({
    	region: 'west',
        margins: '5 0 5 5',
        split: true,
        width: 160,
        
        useArrows: true,
        autoScroll: true,
        animate: true,
        containerScroll: true,
        border: false,
        rootVisible: false,
        root: new Ext.tree.AsyncTreeNode({
        	id: 'mangerTree',
            text: '管理',
            draggable: false,
            expanded: true,
            children: [{
            	id: 'shopManager',
                text: '商铺管理',
                expanded: true,
                children: [{
                	id: 'shops',
                	text: '管理商铺',
                	leaf: true
	            }, {
	            	id: 'addshop',
	                text: '添加商铺',
	                leaf: true
	            }, {
	            	id: 'comments',
	                text: '查看评论',
	                leaf: true
	            }]
            }]
        })
    });
    
//    shopManagerMenuTreePanel.expandAll();
    
    return shopManagerMenuTreePanel;
}
