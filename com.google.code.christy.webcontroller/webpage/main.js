(function(){
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
            layout: {
                type: 'accordion',
                animate: true
            },
            items: [{
                title: 'Navigation',
                html: 'Navigation',
                border: false,
                iconCls: 'nav' // see the HEAD section for style used
            }, {
                title: 'Settings',
                html: '<p>Some settings in here.</p>',
                border: false,
                iconCls: 'settings'
            }]
        },
        // in this instance the TabPanel is not wrapped by another panel
        // since no title is needed, this Panel is added directly
        // as a Container
        new Ext.TabPanel({
            region: 'center', // a center region is ALWAYS required for border layout
            deferredRender: false,
            activeTab: 0,     // first tab initially active
            items: [{
                title: 'Close Me',
                closable: true,
                autoScroll: true
            }, {
                title: 'Center Panel',
                autoScroll: true
            }]
        })]
    });
    
})();