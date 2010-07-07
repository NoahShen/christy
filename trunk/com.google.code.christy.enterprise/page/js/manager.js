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
	
	var centerPanel = createCenterPanel();
	
	var mainPanel = new Ext.Panel({
        layout: 'border',
        region: 'center',
        items: [
        	westPanel,
        	centerPanel
        ]
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
        }),
        
        listeners: {
            click: function(node) {
                var id = node.attributes.id;
                if ("shops" == id) {
                	showShops();
                }
            }
        }
    });
    
//    shopManagerMenuTreePanel.expandAll();
    
    return shopManagerMenuTreePanel;
}

var shopsTab = "shops-tab";
function showShops() {
	var tabPanel = Ext.getCmp("mainTabs");
	var comp = tabPanel.get(shopsTab);
	if (comp) {
		tabPanel.activate(shopsTab);
	} else {
		tabPanel.add(createShopsPanel()).show();
	}
}

function createCenterPanel() {
	var tabs = new Ext.TabPanel({
		id: 'mainTabs',
		region: 'center',
        split: false,
        collapsible: false,
        margins: '0 0 0 0',
        plain:true,
        frame: true,
        defaults:{autoScroll: true}
        ,
        items: [
        	createShopsPanel(),
			createShopDetailPanel()
        ]
    });
    
    return tabs;
}

function createShopsPanel() {
	
	var myData = [
        ['上海1号私藏菜','restaurant','吃饭的地方', '静安区', '南京西路1856号', '021-51501177']
    ];
    
	 // create the data store
    var store = new Ext.data.ArrayStore({
        fields: [
           {name: 'title'},
           {name: 'type'},
           {name: 'content'},
           {name: 'district'},
           {name: 'street'},
           {name: 'tel'}
        ]
    });

   
    
    var colModel = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults: {
            sortable: true // columns are not sortable by default           
        },
        columns: [
        	new Ext.grid.CheckboxSelectionModel ({singleSelect : false}), 
        {
			header: '名称',
			dataIndex: 'title',
			sortable: true,
			width: 220
		}, {
			header: '类型',
			dataIndex: 'type',
			sortable: true,
			width: 130
		}, {
			header: '简介',
			dataIndex: 'content',
			sortable: true,
			width: 70,
			align: 'right'
		}, {
			header: '区县',
			dataIndex: 'district',
			sortable: true,
			width: 95
        }, {
			header: '地址',
			dataIndex: 'street',
			sortable: true,
			width: 95
        }, {
			header: '电话',
			dataIndex: 'tel',
			sortable: true,
			width: 95
        }]
    });
    
    var grid = new Ext.grid.GridPanel({
    	id: shopsTab,
    	title: '商铺',
	    store: store,
	    colModel: colModel,
	    closable:true,
	    sm: new Ext.grid.CheckboxSelectionModel({singleSelect: false}),
	    frame: true,
	    
		tbar:[{
            text: '删除',
            iconCls: 'icon-delete'
        }],
        bbar: new Ext.PagingToolbar({
            pageSize: 25,
            store: store,
            displayInfo: true,
            displayMsg: '显示商铺 {0} - {1} of {2}',
            emptyMsg: "没有商铺"
        })
	});
	
    store.loadData(myData); 
    
    return grid;
}

function createShopDetailPanel() {
    
	var shopForm = new Ext.FormPanel({
		align:'center',
		flex:1,
		
		frame: true,
		
		border:false,
		deferredRender: false,
		plain: true,
        labelWidth: 40, // label settings here cascade unless overridden
        bodyStyle:'padding:5px',
        width: 350,
        height: 180,
        defaultType: 'textfield',
        items: [{
            fieldLabel: '名称',
            name: 'title',
            allowBlank:false,
            blankText: '请输入名称'
        },{
            fieldLabel: '类型',
            name: 'type',
            allowBlank:false,
            blankText: '请输入商铺类型'
        },{
            fieldLabel: '简介',
            name: 'content'
        },{
            fieldLabel: '区县',
            name: 'district',
            allowBlank:false,
            blankText: '请选择区县'
        },{
            fieldLabel: '地址',
            name: 'street',
            allowBlank:false,
            blankText: '请输入地址'
        },{
            fieldLabel: '电话',
            name: 'tel'
        }]
    });
    
    var shopDetail2Panel = new Ext.Panel({
    	align:'center',
		flex:1,
		
		border:false,
		deferredRender: false,
		plain:true,
        layout:'table',
        layoutConfig: {columns:2},
        bodyStyle:'padding:5px',
        items:[{
        	xtype: 'textfield',
        	disabled: true
        },{
            xtype: 'button',
            text: '选择位置',
            handler: function() {
            	Ext.Msg.alert('选择位置');
            }
        },{
            xtype: 'gmappanel',
            width: 200,
            colspan:2,
            zoomLevel: 13,
            gmapType: 'map',
//            mapConfOpts: ['enableScrollWheelZoom','enableDoubleClickZoom','enableDragging'],
//            mapControls: ['GSmallMapControl','GMapTypeControl','NonExistantControl'],
            setCenter: {
                geoCodeAddr: '4 Yawkey Way, Boston, MA, 02215-3409, USA',
                marker: {title: 'Fenway Park'}
            },
            markers: [{
                lat: 42.339641,
                lng: -71.094224,
                marker: {title: 'Boston Museum of Fine Arts'},
                listeners: {
                    click: function(e){
                        Ext.Msg.alert('Its fine', 'and its art.');
                    }
                }
            },{
                lat: 42.339419,
                lng: -71.09077,
                marker: {title: 'Northeastern University'}
            }]
        }]
    });
    
    var shopDetail = new Ext.Panel({
    	id: "shopId",
    	
        closable:true,
        
        title: '店铺',
        layout: {
            type:'vbox',
            padding:'5',
            align:'stretch'
        },
        defaults:{margins:'0 0 5 0'},
        items: [
        	shopForm,
        	shopDetail2Panel
        ]
    });
    
    return shopDetail;
	
}
