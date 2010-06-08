(function(){
	var task = null;
	
	var viewport = new Ext.Viewport({
        layout: 'border',
        items: [
        {
            // lazily created panel (xtype:'panel' is default)
            region: 'north',
            id: 'north',
            split: true,
            height: 100,
            minSize: 100,
            maxSize: 200,
            collapsible: true,
            title: 'north',
            margins: '0 0 0 0'
        }, {
            region: 'west',
            id: 'west', // see Ext.getCmp() below
            title: 'West',
            split: true,
            width: 200,
            minSize: 175,
            maxSize: 400,
            collapsible: true,
            margins: '0 0 0 5',
            
            xtype: 'treepanel',
            rootVisible: false,
            dataUrl: 'controller.do?action=getModuleNodes',
	        root: {
	            nodeType: 'async',
	            text: 'Module Root',
	            draggable: false,
	            id: 'moduleRoot'
	        },
	        
	        listeners: {
	            'dblclick': function(n) {
	                if (n.attributes.ip) {
	                	var ip = n.attributes.ip;
	                	var port = n.attributes.port;
	                	var tabPanel = Ext.getCmp("tabs");
	                	var comp = tabPanel.get(n.attributes.text);
	                	if (comp) {
	                		tabPanel.activate(n.attributes.text);
	                	} else {
	                		tabPanel.add({
		                		id: n.attributes.text,
					            title: n.attributes.text,
					            html: n.attributes.text,
					            ip:ip,
					            port:port,
					            closable:true
					        }).show();
	                	}
	                	
	                }
	            }
	        }
        },
        // in this instance the TabPanel is not wrapped by another panel
        // since no title is needed, this Panel is added directly
        // as a Container
        new Ext.TabPanel({
        	id:'tabs',
            region: 'center', // a center region is ALWAYS required for border layout
            deferredRender: false,
            listeners: {
            	"add": function(thisContainer, component, index) {

            		if (task == null) {
            			task = {
						    run: function(){
						        alert("D");
						    },
						    interval: 3000
						}
						
						Ext.TaskMgr.start(task);
            		}
            	},
            	"remove": function(thisContainer, component ) {
            		var count = Ext.getCmp("tabs").items.getCount();
            		if (count == 0) {
            			alert(component.ip);
            			if (task) {
            				Ext.TaskMgr.stop(task);
            				task = null;
            			}
            			
            		}
            		
            	}
            }
        })]
    });
    
})();