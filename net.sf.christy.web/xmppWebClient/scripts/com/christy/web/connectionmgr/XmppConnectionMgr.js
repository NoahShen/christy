jingo.declare({
	require: [
	  "com.christy.web.clazz.JClass",
	  "com.christy.web.utils.StringUtils",
	  "com.christy.web.xmpp.JID",
	  "com.christy.web.Christy",
	  "com.christy.web.xmpp.XmppStanza",
	  "com.christy.web.parser.XmppParser",
	  "com.christy.web.utils.TimeUtils",
	  "com.christy.web.connectionmgr.filter.StanzaFilter"
	],
	name: "com.christy.web.connectionmgr.XmppConnectionMgr",
	as: function() {
		var JClass = com.christy.web.clazz.JClass;
		var JID = com.christy.web.xmpp.JID;
		var StringUtils = com.christy.web.utils.StringUtils;
		var XmppStanza = com.christy.web.xmpp.XmppStanza;
		var XmppParser = com.christy.web.parser.XmppParser;
		var requestUrl = com.christy.web.Christy.requestUrl;
		var TimeUtils = com.christy.web.utils.TimeUtils;
		var StanzaFilter = com.christy.web.connectionmgr.filter.StanzaFilter;
		
		com.christy.web.connectionmgr.XmppConnectionMgr = JClass.extend({
			init: function() {
				this.connections = new Array();
				this.bodyHandlers = new Array();
				this.listeners = new Array();
				this.bodyMessagQueue = new Array();
				this.intervalId = setInterval(this.processRequest, 1000);
				this.requestingCount = 0;
				this.connectionErrorCount = 0;
				this.keyGenerater = new XmppConnectionMgr.KeyGenerater();
			},
			
			processRequest: function() {
				var aThis = XmppConnectionMgr.instance;
				if (aThis == null) {
					return;
				}
				
				// TODO
//				if (window.console) {
//					window.console.log("bodyMessagQueue.length:" + aThis.bodyMessagQueue.length);
//					window.console.log("aThis.requestingCount:" + aThis.requestingCount);
//					window.console.log("aThis.hold:" + aThis.hold);
//				}
				
				if (aThis.bodyMessagQueue.length > 0) {
					var bodyMessage = aThis.bodyMessagQueue.shift();
					aThis.execAjaxRequest(bodyMessage);
				} else {
					if (aThis.requestingCount < aThis.hold) {
						aThis.sendBody(new XmppStanza.Body());
					}
				}
			},
			
			execAjaxRequest: function(bodyMessage) {
				var aThis = XmppConnectionMgr.instance;
				// TODO
				if (window.console) {
					window.console.log("sending:" + bodyMessage.toXml());
				}
				
				++aThis.requestingCount;
				$.ajax({
					url: requestUrl,
					dataType: "xml",
					cache: false,
					type: "post",
					data: bodyMessage.toXml(),
//					data: "<body content='text/xml;charset=utf-8' hold='1' rid='1573741820' to='jabbercn.org' ver='1.6' wait='60' ack='1' xml:lang='en' xmlns='http://jabber.org/protocol/httpbind'/>",
					processData: false,
					success: function(xml){
						aThis.connectionErrorCount == 0;
						var bodyElement = xml.documentElement;
						var parser = XmppParser.getInstance();
						var responseBody = parser.parseStanza(bodyElement);
						aThis.handleBody(responseBody);
					},
					error: function (xmlHttpRequest, textStatus, errorThrown) {
						++aThis.connectionErrorCount;
						// TODO
						if (window.console) {
							window.console.log("connection error, count:" + aThis.connectionErrorCount);
						}
						
						if (aThis.connectionErrorCount > 5) {
							clearInterval(aThis.intervalId);
							var event = {
								eventType: XmppConnectionMgr.ConnectionEventType.Error,
								when: TimeUtils.currentTimeMillis(),
								error: errorThrown
							}
							aThis.fireConnectionEvent(event);
							return;
						}
					    aThis.bodyMessagQueue.unshift(bodyMessage);
					},
					complete: function(xmlHttpRequest, textStatus) {
						--aThis.requestingCount;
					}
				});
				
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

				var firstConn = (this.connections.length == 0);
				var body = new XmppStanza.Body();
				if (firstConn) {
					body.setAttribute("content", "text/xml;charset=utf-8");
					body.setAttribute("hold", options.hold);
					this.hold = options.hold;
					body.setAttribute("ver", options.ver);
					body.setAttribute("wait", options.wait);
					body.setAttribute("ack", options.ack);
				}
				
				body.setAttribute("rid", requestId);
				
				body.setAttribute("to", options.to);
				body.setAttribute("route", options.route);
				

				var xmppConnectionMgrThis = this;
				
				this.handleBodyByRid({
					rid: body.getAttribute("rid"),
					handler: function(responsebody) {
						var domain = responsebody.getAttribute("from");
						if (domain == null || domain == "") {
							domain = body.getAttribute("to");
						}
						
						var hold = responsebody.getAttribute("hold");
						if (hold != null) {
							this.hold = hold;
						}
						
						var streamId = responsebody.getAttribute("sid");
						xmppConnectionMgrThis.streamId = streamId;
						
						var streamName = responsebody.getAttribute("stream");
						if (streamName != null) {
							var conn = xmppConnectionMgrThis.getConnectionByStreamName(streamName);
							if (conn != null) {
								return;
							}
						} else {
							if (xmppConnectionMgrThis.connections.length > 0) {
								return;
							}
						}
						var connection = new XmppConnectionMgr.XmppConnection(domain);
						connection.setStreamName(streamName);
						
						
						
						xmppConnectionMgrThis.connections.push(connection);
						
						var stanzas = responsebody.getStanzas();
						var stanza = stanzas[0];
						if (stanza instanceof XmppStanza.StreamFeature) {
							var streamFeature = stanza;
							var mechanisms = streamFeature.getMechanisms();
							connection.setAllowedMechanisms(mechanisms);
						}

						var event = {
							eventType: XmppConnectionMgr.ConnectionEventType.Created,
							when: TimeUtils.currentTimeMillis(),
							connection: connection
						}
						xmppConnectionMgrThis.fireConnectionEvent(event);
					}
				});
				this.sendBody(body);
			},
			
			getStreamId: function() {
				return this.streamId;	
			},
			
			sendBody: function(body) {
				var streamId = body.getAttribute("sid");
				if (streamId == null) {
					body.setAttribute("sid", this.streamId);
				}
				
				var rid = body.getAttribute("rid");
				if (rid == null) {
					body.setAttribute("rid", this.getRequestId());
				}
				var key = this.keyGenerater.getKey();
				if (key != null) {
					body.setAttribute("key", key);
				}
				if (this.keyGenerater.isKeyEmpty()) {
					body.setAttribute("newkey", this.keyGenerater.getNewKey());
				}
				
				this.bodyMessagQueue.push(body);
			},
			
			/**
			 * options = {
			 * 	rid: rid
			 * 	handler: handler function
			 * }
			 */
			handleBodyByRid: function(options) {
				this.bodyHandlers.push(options);
			},
			
			handleBody: function(body) {
				this.fireBodyHandler(body);
				this.handleStanza(body);
			},
			
			handleStanza: function(body) {
				var streamName = body.getAttribute("stream");
				var connection = this.getConnectionByStreamName(streamName);
				if (connection == null) {
					if (this.connections.length == 1) {
						connection = this.connections[0];
					} else {
						var event = {
							eventType: XmppConnectionMgr.ConnectionEventType.Error,
							when: TimeUtils.currentTimeMillis(),
							error: new Error("unknow connection")
						}
						this.fireConnectionEvent(event);
						return;
					}
				}
				
				var stanzas = body.getStanzas();
				var stanzaReceived = XmppConnectionMgr.ConnectionEventType.StanzaReceived;
				for (var i = 0; i < stanzas.length; ++i){
					var stanza = stanzas[i];
					if (stanza instanceof XmppStanza.Packet) {
						var event = {
							eventType: stanzaReceived,
							when: TimeUtils.currentTimeMillis(),
							connection: connection,
							stanza: stanza
						}
						connection.packetHandler(stanza);
						this.fireConnectionEvent(event);
						
					}
					// TODO Do need it in new Protocal
//					else if (stanza instanceof XmppStanza.StreamFeature) {
//						if (stanza.containFeature(XmppStanza.IqBind.ELEMENTNAME, XmppStanza.IqBind.NAMESPACE)) {
//							connection.bindResource();
//						}
//					}
				}
			},
			
			fireBodyHandler: function(body) {
				for (var i = this.bodyHandlers.length - 1; i >= 0; --i){
					// TODO check rid
//					if (this.bodyHandlers[i].rid == body.getAttribute("ack")){
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
					if (this.listeners[i].eventType == event.eventType){
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
		
		
		XmppConnectionMgr.KeyGenerater = JClass.extend({
			init: function(){
				this.keyLength = 10;
				this.keySequence = new Array();
			},
			
			getKey: function() {
				return this.keySequence.pop();
			},
			
			isKeyEmpty: function() {
				return this.keySequence.length == 0;
			},
			
			getNewKey: function() {
				this.keySequence = new Array();
				var seed = StringUtils.randomNumber(1000, 10000);
				for (var i = 0; i < this.keyLength; ++i) {
					var key = StringUtils.hash(
										(i == 0) ? seed.toString() : this.keySequence[i - 1], 
										"SHA1");
					this.keySequence.push(key);
				}
				return this.getKey();
			}
		});
		
		XmppConnectionMgr.ConnectionEventType = {
			
			Closed: "Closed",
			
			Created: "Created",
			
			StreamError: "StreamError",
			
			Error: "Error",
			
			SaslSuccessful: "SaslSuccessful",
			
			SaslFailed: "SaslFailed",
			
			SessionBinded: "SessionBinded",
			
			BindSessionFailed: "BindSessionFailed",
			
			ResourceBinded: "ResourceBinded",
			
			BindResourceFailed: "BindResourceFailed",
			
			StanzaReceived: "StanzaReceived",
			
			StanzaSended: "StanzaSended",
			
			ContactRemoved: "ContactRemoved",
			
			ContactUpdated: "ContactUpdated",
			
			OtherResourceStatusChanged: "OtherResourceStatusChanged"
		}		
		
		XmppConnectionMgr.XmppConnection = JClass.extend({

			init: function(domain) {
				this.domain = domain;
				this.allowedMechanisms = new Array();
				this.handlers = new Array();
				this.contacts = new Array();
				this.otherResources = new Array();
			},
			
			setAllowedMechanisms: function(allowedMechanisms) {
				this.allowedMechanisms = allowedMechanisms;
			},
			
			getAllowedMechanisms: function() {
				return this.allowedMechanisms;
			},
			
			getJid: function() {
				return this.jid;
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
				if (username == null || password == null) {
					throw new Error("username or password is null");
				}
				this.username = username;
				this.password = password;
				this.resource = resource;
				this.initPresence = initPresence;

				var body = new XmppStanza.Body();
				var auth = new XmppStanza.Auth();
				auth.setMechanism("PLAIN");
				var content = StringUtils.base64encode((String.fromCharCode(0) + this.username + String.fromCharCode(0) + this.password));
				auth.setContent(content);
				body.addStanza(auth);
				
				var connectionMgr = XmppConnectionMgr.getInstance();
				var requestId = connectionMgr.getRequestId();
				body.setAttribute("rid", requestId);
				
				var connectionThis = this;
				connectionMgr.handleBodyByRid({
					rid: requestId,
					handler: function(responsebody) {
						var stanzas = responsebody.getStanzas();
						if (stanzas.length > 0 ){
							var eventType = XmppConnectionMgr.ConnectionEventType.SaslFailed;
							if (stanzas[0] instanceof XmppStanza.Success) {
								eventType = XmppConnectionMgr.ConnectionEventType.SaslSuccessful;
								connectionThis.authenticated = true;
								
								// TODO Do not need it in new Protocal
								connectionThis.bindResource();
							}
							var event = {
									eventType: eventType,
									when: TimeUtils.currentTimeMillis(),
									connection: connectionThis,
									stanza: stanzas[0]
							}
							connectionMgr.fireConnectionEvent(event);
						}
					}
				});
				connectionMgr.sendBody(body);
			},
			
			
			bindResource: function() {
				var iq = new XmppStanza.Iq(XmppStanza.IqType.SET);
				iq.setTo(new JID(null, this.getDomain(), null));
				var iqBind = new XmppStanza.IqBind();
				if (this.resource == null) {
					this.resource = "Christy";
				}
				iqBind.setResource(this.resource);
				iq.addPacketExtension(iqBind);
				
				var id = iq.getStanzaId();
				
				var connectionMgr = XmppConnectionMgr.getInstance();
				var connectionThis = this;
				this.handleStanza({
					filter: new StanzaFilter.PacketIdFilter(id),
					handler: function(iqResponse) {
						var eventType = XmppConnectionMgr.ConnectionEventType.BindResourceFailed;
						if (iqResponse.getType() == XmppStanza.IqType.RESULT) {
							eventType = XmppConnectionMgr.ConnectionEventType.ResourceBinded;
							var iqBindResponse = iqResponse.getPacketExtension(XmppStanza.IqBind.ELEMENTNAME, XmppStanza.IqBind.NAMESPACE);
							var jid = iqBindResponse.getJid();
							connectionThis.jid = jid;
							connectionThis.resource = jid.getResource();
							this.resourceBinded = true;
						}												
						var event = {
								eventType: eventType,
								when: TimeUtils.currentTimeMillis(),
								connection: connectionThis,
								stanza: iqResponse
						}
		
						connectionMgr.fireConnectionEvent(event);
						if (eventType == XmppConnectionMgr.ConnectionEventType.ResourceBinded) {
							connectionThis.bindSession();
						}
					}
				});
				// TODO Do not need second arg in new Protocal
				this.sendStanza(iq, true);
			},
			
			bindSession: function() {
				
				var iq = new XmppStanza.Iq(XmppStanza.IqType.SET);
				iq.setTo(new JID(null, this.getDomain(), null));
				var iqSession = new XmppStanza.IqSession();
				iq.addPacketExtension(iqSession);
				
				var id = iq.getStanzaId();
				
				var connectionMgr = XmppConnectionMgr.getInstance();
				var connectionThis = this;
				this.handleStanza({
					filter: new StanzaFilter.PacketIdFilter(id),
					handler: function(iqResponse) {
						
						var eventType = XmppConnectionMgr.ConnectionEventType.BindSessionFailed;
						if (iqResponse.getType() == XmppStanza.IqType.RESULT) {
							eventType = XmppConnectionMgr.ConnectionEventType.SessionBinded;
							this.sessionBinded = true;
							connectionThis.queryRoster();
							if (connectionThis.initPresence) {
								connectionThis.sendStanza(connectionThis.initPresence);
							}
						}
						var event = {
								eventType: eventType,
								when: TimeUtils.currentTimeMillis(),
								connection: connectionThis,
								stanza: iqResponse
						}
						connectionMgr.fireConnectionEvent(event);
					}
				});
				this.sendStanza(iq);
			},
			
			queryRoster: function() {
				var IqRoster = XmppStanza.IqRoster;
				var iq = new XmppStanza.Iq(XmppStanza.IqType.GET);
				iq.addPacketExtension(new XmppStanza.IqRoster());
				
				this.sendStanza(iq);
			},
			
			packetHandler: function(packet) {
				var IqRoster = XmppStanza.IqRoster;
				if (packet instanceof XmppStanza.Iq) {
					if (packet.getPacketExtension(IqRoster.ELEMENTNAME, IqRoster.NAMESPACE) != null) {
						this.handleRoster(packet);
					}
				}
				else if (packet instanceof XmppStanza.Presence) {
					var presence = packet;
					var type = presence.getType();
					var from = presence.getFrom();
					if (type == XmppStanza.PresenceType.AVAILABLE
						|| type == XmppStanza.PresenceType.UNAVAILABLE) {
						if (from.equalsWithBareJid(this.jid)) {
							if (from.getResource() != this.jid.getResource()) {
								this.handleOtherResource(from.getResource(), presence);
							}
						}
					} 
				}
				
				for (var i =  0; i < this.handlers.length; ++i) {
					if (this.handlers[i].filter.accept(this, packet)) {
						this.handlers[i].handler(packet);
						this.handlers.splice(i,1);
					}
				}
			},
			
			handleOtherResource: function(resource, presence) {
				var otherResource = this.getOtherResource(resource);
				if (otherResource == null) {
					otherResource = {
						resource: resource
					}
					this.otherResources.push(otherResource);
				}
				otherResource.oldPresence = otherResource.currentPresence;
				otherResource.currentPresence = presence;
				
				if (XmppStanza.PresenceType.UNAVAILABLE == presence.getType()) {
					for (var i =  0; i < this.otherResources.length; ++i) {
						if (this.otherResources[i].resource == resource) {
							this.otherResources.splice(i,1);
						}
					}
				}
				
				//TODO 
				if (window.console) {
					window.console.log("other resouce(" + resource + ") changed");
				}
				
				var otherResourceStatusChanged = XmppConnectionMgr.ConnectionEventType.OtherResourceStatusChanged;
				var event = {
						eventType: otherResourceStatusChanged,
						when: TimeUtils.currentTimeMillis(),
						connection: this,
						stanza: presence,
						otherResource: otherResource
				}
				connectionMgr.fireConnectionEvent(event);
			},
			
			getOtherResource: function(resource) {
				for (var i =  0; i < this.otherResources.length; ++i) {
					if (this.otherResources[i].resource == resource) {
						return this.otherResources[i];
					}
				}
				return null;
			},
			
			handleRoster: function(iq) {
				var IqRoster = XmppStanza.IqRoster;
				var iqRoster = iq.getPacketExtension(IqRoster.ELEMENTNAME, IqRoster.NAMESPACE);
				var items = iqRoster.getRosterItems();
				for (var i = 0; i < items.length; ++i) {
					var rosterItem = items[i];
					var subscription = rosterItem.getSubscription();
					var jid = rosterItem.getJid();
					if (subscription == XmppStanza.IqRosterSubscription.remove) {
						this.removeContact(jid);
					} else {
						var contact = this.getContact(jid);
						if (contact == null) {
							contact = new XmppConnectionMgr.XmppContact(rosterItem);
							this.contacts.push(contact);
						}
						contact.setRosterItem(rosterItem);
						
						//TODO 
						if (window.console) {
							window.console.log(jid.toFullJID() + " has been updated");
						}
						
						var event = {
							eventType: XmppConnectionMgr.ConnectionEventType.ContactUpdated,
							when: TimeUtils.currentTimeMillis(),
							connection: this,
							contact: contact
						}
						var connectionMgr = XmppConnectionMgr.getInstance();
						connectionMgr.fireConnectionEvent(event);
					}
				}
			},
			
			getContact: function(jid) {
				for (var i =  0; i < this.contacts.length; ++i){
					var contact = this.contacts[i];
					if (contact.getBareJid().equalsWithBareJid(jid)){
						return contact;
					}
				}
				return null; 
			},
			
			removeContact: function(jid) {
				for (var i =  0; i < this.contacts.length; ++i){
					var contact = this.contacts[i];
					if (contact.getBareJid().equalsWithBareJid(jid)){
						this.contacts.splice(i,1);
						
						//TODO 
						if (window.console) {
							window.console.log(jid.toFullJID() + " has been removed");
						}
						
						var event = {
							eventType: XmppConnectionMgr.ConnectionEventType.ContactRemoved,
							when: TimeUtils.currentTimeMillis(),
							connection: this,
							contact: contact
						}
						var connectionMgr = XmppConnectionMgr.getInstance();
						connectionMgr.fireConnectionEvent(event);
						break;
					}
				}
			},
			
			isAuthenticated: function() {
				return this.authenticated;
			},
			
			close: function(unavailablePresence) {
				// TODO
			},
			
			sendStanza: function(stanza) {
				var body = new XmppStanza.Body();
								
				// TODO Do not need it in new Protocal
				if (arguments[1]) {
					body.setAttribute("xmpp:restart", "true");
				}
				
				
				if (this.streamName != null) {
					body.setAttribute("stream", streamName);
				}
				body.addStanza(stanza);
				var connectionMgr = XmppConnectionMgr.getInstance();
				connectionMgr.sendBody(body);
			},
			
			/*
			 * handler = {
			 * 	filter： filter,
			 * 	handler： function(stanza) {
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
			}
		});
		
		XmppConnectionMgr.XmppContact = JClass.extend({

			init: function(rosterItem) {
				this.rosterItem = rosterItem;
				this.userResources = {};
			},
			
			getBareJid: function() {
				return this.rosterItem.getJid();
			},
			
			getNickname: function() {
				return this.rosterItem.setRosterName();
			},
			
			getGroups: function() {
				return this.rosterItem.getGroups();
			},
			
			addResource: function(userResource) {
				this.userResources[userResource.resource] = userResource;
			},
			
			removeResource: function(resource) {
				delete this.userResources[resource];
			},
			
			getResource: function(resource) {
				return this.userResources[resource];
			},
			
			getResources: function() {
				var res = new Array();
				for (var resource in this.userResources ) {
					res.push(this.userResources[resource]);
				}
				return res;
			},
			
			isResourceAvailable: function() {
				return this.getResources().length != 0;
			},
			
			setRosterItem: function(rosterItem) {
				this.rosterItem = rosterItem;
			},
			
			getRosterItem: function() {
				return this.rosterItem;
			},
			
			getMaxPriorityResource: function() {
				var maxRes = null;
				var currentPriority = Number.MIN_VALUE;
				
				for (var resource in this.userResources) {
					var userResource = this.userResources[resource];
					var presence = userResource.currentPresence;
					var resPriority = presence.getPriority();
					if (resPriority >= currentPriority) {
						maxRes = userResource;
						currentPriority = resPriority;
					}
				}
				return maxRes;
			}
			
		});	}
});