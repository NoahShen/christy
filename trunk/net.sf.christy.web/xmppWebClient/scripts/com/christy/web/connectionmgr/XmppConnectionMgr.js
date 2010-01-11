jingo.declare({
	require: [
	  "com.christy.web.clazz.JClass",
	  "com.christy.web.xmpp.JID"
	],
	name: "com.christy.web.connectionmgr.XmppConnectionMgr",
	as: function() {
		var JClass = com.christy.web.clazz.JClass;
		var StringUtils = com.christy.web.utils.StringUtils;
		
		var requestUrl = com.christy.web.Christy.requestUrl;
		
		com.christy.web.connectionmgr.XmppConnectionMgr = JClass.extend({
			init: function() {
				this.connections = new Array();
				this.listeners = new Array();
			},
			
			getInstance: function() {
				if (this.instance == null) {
					this.instance = new XmppConnectionMgr();
				}
				return this.instance;
			},
			
			/**
			 * options = {
			 * 		hold: hold
			 * 		to: to
			 * 		route： route
			 * 		ver: ver
			 * 		wait: wait
			 * 		ack: ack
			 * }
			 */
			requestCreateConnection: function(options) {
				var Body = com.christy.web.connectionmgr.XmppConnectionMgr.Body;
				var body = new Body()
				
				$.ajax({
					url: requestUrl,
					cache: false,
					type: "post",
					data: "<body content='text/xml; charset=utf-8' hold='1' rid='1573741820' to='jabbercn.org' ver='1.6' wait='60' ack='1' xml:lang='en' xmlns='http://jabber.org/protocol/httpbind'/>",
					processData: false,
					success: function(data){
						alert(data);
					}
				});
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
			init: function(eventType, when, connection, stanza, errorThrown, attachment) {
				this.eventType = eventType;
				this.when = when;
				this.connection = connection;
				this.stanza = stanza;
				this.errorThrown = errorThrown;
				this.attachment = attachment;
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
			},
			
			getAttachment: function() {
				return this.attachment;
			}
		});
		
		XmppConnectionMgr.Body = XmlStanza.extend({
			init: function() {
			    this._super();
			},
			
			setRequestId: function(requestId) {
				this.requestId = requestId;
			},
			
			getRequestId: function() {
				return this.requestId;
			},
			
			setHold: function(hold) {
				this.hold = hold;
			},
			
			getHold: function() {
				return this.hold;
			},
			
			setTo: function(to) {
				this.to = to;
			},
			
			getTo: function() {
				return this.to;
			},
			
			setRoute: function(route) {
				this.route = route;
			},
			
			getRoute: function() {
				return this.route;
			},
			
			setVer: function(ver) {
				this.ver = ver;
			},
			
			getVer: function() {
				return this.ver;
			},
			
			setWait: function(wait) {
				this.wait = wait;
			},
			
			getWait: function() {
				return this.wait;
			},
			
			setAck: function(ack) {
				this.ack = ack;
			},
			
			getAck: function() {
				return this.ack;
			},
			
			setStanza: function(stanza) {
		        this.stanza = stanza;
		    },
		    
		    getStanza: function() {
		    	return this.stanzaId;
		    },
		    
		    toXml: function() {
		    	var xml = "";
		    	
		    	xml += "<body content=\"text/xml; charset=utf-8\"";
		    	if (this.getRequestId() != null) {
		    		xml += " rid=\"" + this.getRequestId() + "\"";
		    	}
		    	
		    	if (this.getHold() != null) {
		    		xml += " hold=\"" + this.getHold() + "\"";
		    	}
		    	
		    	if (this.getTo() != null) {
		    		xml += " to=\"" + this.getTo() + "\"";
		    	}
		    	
		    	if (this.getRoute() != null) {
		    		xml += " route=\"" + this.getRoute() + "\"";
		    	}
		    	
		    	if (this.getVer() != null) {
		    		xml += " ver=\"" + this.getVer() + "\"";
		    	}
		    	
		    	if (this.getWait() != null) {
		    		xml += " wait=\"" + this.getWait() + "\"";
		    	}
		    	
		    	if (this.getAck() != null) {
		    		xml += " ack=\"" + this.getAck() + "\"";
		    	}
		    	
		    	if (this.getStanza() != null) {
		    		xml += ">";
		    		xml += this.getStanza().toXml();
		    	} else {
		    		xml += " />";
		    	}
		    	
		    	return xml;
		    }
		    
		
		});
		
		XmppConnectionMgr.XmppConnection = JClass.extend({

			init: function(domain, route) {
				this.domain = domain;
				this.route = route;
				this.handlers = new Array();
			},
			
			getJid: function() {
				return this.owner.getJid();
			},
			
			setStreamName: function(streamName) {
				this.streamName = streamName;
			},
			
			getStreamName: function() {
				return this.streamName;
			},
			
			getDomain: function() {
				return this.domain;
			},
			
			setDomain: function(domain) {
				if (this.isConnected()) {
					throw new Error("The connection has connected");
				}
				this.domain = domain;
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