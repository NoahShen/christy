jingo.declare({
	require: [
	  "com.christy.web.clazz.JClass",
	  "com.christy.web.utils.StringUtils",
	  "com.christy.web.xmpp.JID",
	  "com.christy.web.Christy",
	  "com.christy.web.xmpp.XmppStanza",
	  "com.christy.web.parser.XmppParser",
	  "com.christy.web.utils.TimeUtils"
	],
	name: "com.christy.web.connectionmgr.XmppConnectionMgr",
	as: function() {
		var JClass = com.christy.web.clazz.JClass;
		var StringUtils = com.christy.web.utils.StringUtils;
		var XmppStanza = com.christy.web.xmpp.XmppStanza;
		var XmppParser = com.christy.web.parser.XmppParser;
		var requestUrl = com.christy.web.Christy.requestUrl;
		var TimeUtils = com.christy.web.utils.TimeUtils;
		
		com.christy.web.connectionmgr.XmppConnectionMgr = JClass.extend({
			init: function() {
				this.connections = new Array();
				this.bodyHandlers = new Array();
				this.listeners = new Array();
			},
			
			getRequestId: function() {
				if (this.requestId == null) {
					this.requestId = StringUtils.randomNumber(10, 1000);
				}
				return this.requestId++;
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
				var requestId = this.getRequestId();

				var body = new XmppStanza.Body();
				body.setAttribute("rid", requestId);
				body.setAttribute("hold", options.hold);
				body.setAttribute("to", options.to);
				body.setAttribute("route", options.route);
				body.setAttribute("ver", options.ver);
				body.setAttribute("wait", options.wait);
				body.setAttribute("ack", options.ack);

				var xmppConnectionMgrThis = this;
				
				this.handleBodyByRid({
					rid: body.getAttribute("rid"),
					handler: function(responsebody) {
						var domain = responsebody.getAttribute("from");
						if (domain == null || domain == "") {
							domain = body.getAttribute("to");
						}
						var streamName = responsebody.getAttribute("stream");
						var connection = new XmppConnectionMgr.XmppConnection(domain);
						connection.setStreamName(streamName);
						xmppConnectionMgrThis.connections.push(connection);
						
						var ConnectonEvent = XmppConnectionMgr.ConnectonEvent;
						var created = XmppConnectionMgr.ConnectionEventType.Created;
						var event = new ConnectonEvent(created,
																					TimeUtils.currentTimeMillis(),
																					connection,
																					null,
																					null,
																					null);
						xmppConnectionMgrThis.fireConnectionEvent(event);
					}
				});
				this.sendBody(body);
			},
			
			getStreamId: function() {
				return this.streamId;	
			},
			
			sendBody: function(body) {
				var xmppConnectionMgrThis = this;
				$.ajax({
					url: requestUrl,
					dataType: "xml",
					cache: false,
					type: "post",
					data: body.toXml(),
//					data: "<body content='text/xml;charset=utf-8' hold='1' rid='1573741820' to='jabbercn.org' ver='1.6' wait='60' ack='1' xml:lang='en' xmlns='http://jabber.org/protocol/httpbind'/>",
					processData: false,
					success: function(xml){
						var bodyElement = xml.documentElement;
						var parser = XmppParser.getInstance();
						var responseBody = parser.parseStanza(bodyElement);
						xmppConnectionMgrThis.handleBody(responseBody);
					}
				});
			},
			
			/**
			 * options = {
			 * 	rid: rid
			 * handler: handler function
			 * }
			 */
			handleBodyByRid: function(options) {
				this.bodyHandlers.push(options);
			},
			
			handleBody: function(body) {
				this.fireBodyHandler(body);
				// TODO handler Packet
			},
			
			fireBodyHandler: function(body) {
				for (var i = this.bodyHandlers.length - 1; i >= 0; --i){
					// TODO check rid
//					if (this.bodyHandlers[i].rid == body.getAttribute("rid")){
						this.bodyHandlers[i].handler(body);
						this.bodyHandlers.splice(i,1);
//					}
				}
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
        	
        	getConnectionByStreamName: function(streamName) {
                for (var i = 0; i < this.connections.length; ++i){
					if (streamName == this.connections[i].getStreamName()) {
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
						this.listeners[i].handler(event);
					}
				}
        	}
        	
		});
		
		var XmppConnectionMgr = com.christy.web.connectionmgr.XmppConnectionMgr;
		
		XmppConnectionMgr.getInstance = function() {
			if (XmppConnectionMgr.instance == null) {
				XmppConnectionMgr.instance = new XmppConnectionMgr();
			}
			return XmppConnectionMgr.instance;
		}
		
		XmppConnectionMgr.ConnectionEventType = {
			
			Closed: "Closed",
			
			Created: "Created",
			
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
		
		
		XmppConnectionMgr.XmppConnection = JClass.extend({

			init: function(domain) {
				this.domain = domain;
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