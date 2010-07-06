Ext.onReady(function(){
    Ext.QuickTips.init();
 	Ext.BLANK_IMAGE_URL = "/lib/resources/images/default/s.gif";
 	
	// Create a variable to hold our EXT Form Panel. 
	// Assign various config options as seen.	 
    var login = new Ext.FormPanel({
        labelWidth:80,
        width: 350,
        url:'login.do', 
        frame:true, 
        title:'登陆', 
        defaultType:'textfield',
		monitorValid:true,
		// Specific attributes for the text fields for username / password. 
		// The "name" attribute defines the name of variables sent to the server.
        items:[{ 
            fieldLabel:'用户名',
            id: 'username',
            name:'username', 
            allowBlank:false 
        },{ 
            fieldLabel:'密码', 
            name:'password', 
            inputType:'password', 
            allowBlank:false 
        }],
 
		// All the magic happens after the user clicks the button     
        buttons:[{
        	id:'login',
            text:'登陆',
            formBind: true,	 
            // Function that fires when user clicks the button 
            handler:function(){ 
                login.getForm().submit({ 
                    method:'POST', 
                    waitTitle:'登陆', 
                    waitMsg:'正在登陆...', 
                    success:function(){ 
						Ext.Msg.alert('success', "Login Success"); 
					},
 
                    failure:function(form, action){ 
                        if(action.failureType == 'server'){ 
                            obj = Ext.util.JSON.decode(action.response.responseText); 
                            Ext.Msg.alert('Login Failed!', obj.errors.reason); 
                        }else{ 
                            Ext.Msg.alert('错误', '无法连接服务器！'); 
                        } 
                        login.getForm().reset(); 
                    } 
            	}); 
            } 
        }] 
	});
 	login.render("login");
	
});
