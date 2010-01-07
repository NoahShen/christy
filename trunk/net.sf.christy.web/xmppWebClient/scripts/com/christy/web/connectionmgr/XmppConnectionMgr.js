jingo.declare({
	require: [
	  "com.christy.web.clazz.JClass"
	],
	name: 'com.christy.web.connectionmgr.XmppConnectionMgr',
	as: function() {
		var JClass = com.christy.web.clazz.JClass;
		
		com.christy.web.connectionmgr.XmppConnectionMgr = JClass.extend({
			init: function() {
				this.connections = new Array();
				this.listeners = new Array();
			},
			
			createConnection: function(options) {
				var XmppConnection = com.christy.web.connectionmgr.XmppConnectionMgr.XmppConnection;
				var connection = new XmppConnection(options);
				this.connections.push(connection);
				return connection;
			},
			
			getAllConnections: function() {
                return this.connections;
        	},

			getConnection: function(jid){
                for (var i = 0; i < this.connections.length; ++i){
					if (jid.equals(this.connections[i].getJid())) {
						return this.connections[i];
					}
				}
                return null;
        	},
        	
        	addConnectionListener: function(eventType, listenerFunc) {
        		// wrapper
        		this.listeners.push({
        			eventType: eventType,
        			handler: listenerFunc
        		})
        	},
        	
        	removeConnectionListener: function(eventType, listenerFunc) {
        		for (var i = 0; i < this.listeners.length; ++i){
					if (this.listeners[i].eventType == eventType
						&& this.listeners[i].handler == listenerFunc){
						this.listeners.splice(i,1);
					}
				}
        	},
        	
        	getConnectionListener: function() {
        		return this.listeners;
        	},
        	
        	fireConnectionEvent: function(event) {
        		for (var i = 0; i < this.listeners.length; ++i){
					if (this.listeners[i].eventType == event.getEventType()){
						this.listeners.handler(event);
					}
				}
        	}
        	
			
		});
		var XmppConnectionMgr = com.christy.web.connectionmgr.XmppConnectionMgr;
		
		XmppConnectionMgr.ConnectionEventType = {
			
			Closed: "Closed",
			
			Created: "Created",
			
			Connected: "Connected",
			
			StreamError: "StreamError",
			
			Exception: "Exception",
			
			SaslSuccessful: "SaslSuccessful",
			
			SaslFailed: "SaslFailed",
			
			SessionBinded: "SessionBinded",
			
			ResourceBinded: "ResourceBinded"
		}
		
		XmppConnectionMgr.ConnectonEvent = JClass.extend({
			init: function(eventType, when, connection, stanza, errorThrown) {
				this.eventType = eventType;
				this.when = when;
				this.connection = connection;
				this.stanza = stanza;
				this.errorThrown = errorThrown;
			},
			
			getEventType: function() {
				return this.eventType;
			},
			
			getWhen: function() {
				return this.when;
			},
			
			getConnection: function() {
				return this.connection;
			},
			
			getStanza: function() {
				return this.stanza;
			},
			
			getErrorThrown: function() {
				return this.errorThrown;
			}
		});
		
		XmppConnectionMgr.XmppConnection = JClass.extend({
			/**
			 * options = {
			 * 	domain: domain,
			 * 	route: route,
			 * 	ack: ack,
			 * 	wait: wait,
			 * 	rid: rid
			 * }
			 */
			init: function(options) {
				this.options = options;
				this.handlers = new Array();
			},
			
			getJid: function() {
				return this.owner.getJid();
			},
			
			getDomain: function() {
				return this.options.domain;
			},
			
			login: function(username, password, resource, initPresence) {
				// TODO 
			},
			
			isConnected: function() {
				return this.connected;
			},
			
			isAuthenticated: function() {
				return this.authenticated;
			},
			
			close: function(unavailablePresence) {
				// TODO
			},
			
			sendStanza: function(stanza) {
				// TODO
			},
			
			/*
			 * handler = {
			 * 	filter： filter,
			 * 	timeout: timeout,
			 * 	handlerFunction： function(stanza) {
			 * 	}
			 * }
			 * handler will be invoked only once
			 */ 
			handleStanza: function(handler) {
				this.handlers.push(handler);
			},
			
			isSessionBinded: function() {
				return this.sessionBinded;
			},
			
			isResourceBinded: function() {
				return this.resourceBinded;
			},
			
			connect: function() {
				// TODO
			},
			
			getConnectionId: function() {
				return this.connectionId;
			},
			
			getLastActiveTime: function() {
				return this.lastActiveTime;
			},
			
			getLastReceiveTime: function() {
				return this.lastReceiveTime;
			},
			
			getLastSendTime: function() {
				return this.lastSendTime;
			},
			
			getOwner: function() {
				return this.owner;
			},
			
			getContactManager: function() {
				return this.contactManager;
			},
			
			getPrivacyManager: function() {
				return this.privacyManager;
			}
		});
		
	}
});