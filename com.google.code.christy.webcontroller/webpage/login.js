Ext.onReady(function(){
    Ext.QuickTips.init();
 
	// Create a variable to hold our EXT Form Panel. 
	// Assign various config options as seen.	 
    var login = new Ext.FormPanel({ 
        labelWidth:80,
        url:'login.do', 
        frame:true, 
        title:'Login', 
        defaultType:'textfield',
		monitorValid:true,
		// Specific attributes for the text fields for username / password. 
		// The "name" attribute defines the name of variables sent to the server.
        items:[{ 
            fieldLabel:'Username', 
            name:'username', 
            allowBlank:false 
        },{ 
            fieldLabel:'Password', 
            name:'password', 
            inputType:'password', 
            allowBlank:false 
        }],
 
		// All the magic happens after the user clicks the button     
        buttons:[{ 
            text:'Login',
            formBind: true,	 
            // Function that fires when user clicks the button 
            handler:function(){ 
                login.getForm().submit({ 
                    method:'POST', 
                    waitTitle:'Connecting', 
                    waitMsg:'Loging in...',
 
			// Functions that fire (success or failure) when the server responds. 
			// The one that executes is determined by the 
			// response that comes from login.asp as seen below. The server would 
			// actually respond with valid JSON, 
			// something like: response.write "{ success: true}" or 
			// response.write "{ success: false, errors: { reason: 'Login failed. Try again.' }}" 
			// depending on the logic contained within your server script.
			// If a success occurs, the user is notified with an alert messagebox, 
			// and when they click "OK", they are redirected to whatever page
			// you define as redirect. 
 
                    success:function(){ 
						Ext.Msg.alert('Status', 'Login Successful!', function(btn, text){
						    Ext.getCmp("loginWin").close();
						    ScriptMgr.load({
								scripts: ['main.js'],
								callback: function() {
								
							  	}
							}); 
						});
					},
 
					// Failure function, see comment above re: success and failure. 
					// 如果登录失败，弹出对话框。 
 
                    failure:function(form, action){ 
                        if(action.failureType == 'server'){ 
                            obj = Ext.util.JSON.decode(action.response.responseText); 
                            Ext.Msg.alert('Login Failed!', obj.errors.reason); 
                        }else{ 
                            Ext.Msg.alert('Warning!', 'Authentication server is unreachable : ' + action.response.responseText); 
                        } 
                        login.getForm().reset(); 
                    } 
            	}); 
            } 
        }] 
	});
 
 
	// This just creates a window to wrap the login form. 
	// The login object is passed to the items collection.       
    var win = new Ext.Window({
		id:"loginWin",
        layout:'fit',
        width:300,
        height:150,
        closable: false,
        resizable: false,
        plain: true,
        border: false,
        items: [login]
	});
	win.show();
});

ScriptLoader = function() {
	this.timeout = 30;
    this.scripts = [];
    this.disableCaching = false;
    this.loadMask = null;
};

ScriptLoader.prototype = {
	showMask: function() {
    	if (!this.loadMask) {
        	this.loadMask = new Ext.LoadMask(Ext.getBody());
        	this.loadMask.show();
      	}
	},

    hideMask: function() {
		if (this.loadMask) {
			this.loadMask.hide();
			this.loadMask = null;
		}
    },

    processSuccess: function(response) {
		this.scripts[response.argument.url] = true;
		window.execScript ? window.execScript(response.responseText) : window.eval(response.responseText);
		if (response.argument.options.scripts.length == 0) {
			this.hideMask();
		}
		if (typeof response.argument.callback == 'function') {
    		response.argument.callback.call(response.argument.scope);
  		}
	},

	processFailure: function(response) {
	  	this.hideMask();
		Ext.MessageBox.show({title: 'Application Error', msg: 'Script library could not be loaded.', closable: false, icon: Ext.MessageBox.ERROR, minWidth: 200});
	  	setTimeout(function() { Ext.MessageBox.hide(); }, 3000);
	},

	load: function(url, callback) {
	  	var cfg, callerScope;
	  	if (typeof url == 'object') { // must be config object
		  	cfg = url;
		  	url = cfg.url;
		  	callback = callback || cfg.callback;
		  	callerScope = cfg.scope;
		  	if (typeof cfg.timeout != 'undefined') {
		    	this.timeout = cfg.timeout;
		  	}
		  	if (typeof cfg.disableCaching != 'undefined') {
		        this.disableCaching = cfg.disableCaching;
		    }
	  	}
	
	  	if (this.scripts[url]) {
	    	if (typeof callback == 'function') {
	      		callback.call(callerScope || window);
	    	}
	    	return null;
	  	}
	
	  	this.showMask();
	
	  	Ext.Ajax.request({
		  	url: url,
		  	success: this.processSuccess,
		  	failure: this.processFailure,
		   	scope: this,
		  	timeout: (this.timeout*1000),
		   	disableCaching: this.disableCaching,
		    argument: {
	      		'url': url,
				'scope': callerScope || window,
				'callback': callback,
				'options': cfg
		  	}
      	});
	   }
};

ScriptLoaderMgr = function() {
	this.loader = new ScriptLoader();

	this.load = function(o) {
		if (!Ext.isArray(o.scripts)) {
			o.scripts = [o.scripts];
		}

		o.url = o.scripts.shift();

		if (o.scripts.length == 0) {
			this.loader.load(o);
		} else {
			o.scope = this;
			this.loader.load(o, function() {
				this.load(o);
			});
      	}
    };
};

ScriptMgr = new ScriptLoaderMgr(); 

