XmppConnectionMgr = jClass.extend({
	init: function() {
		this.connections = new Array();
		this.bodyHandlers = new Array();
		this.listeners = new Array();
		this.bodyMessagQueue = new Array();
		this.requestingCount = 0;
		this.connectionErrorCount = 0;
		this.keyGenerater = new KeyGenerater();
	},
	
	processRequest: function(clearBody) {
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
		
		if (clearBody) {
			while (aThis.bodyMessagQueue.length != 0) {
				var bodyMessage = aThis.bodyMessagQueue.shift();
				aThis.execAjaxRequest(bodyMessage);
			}
			return;
		}
		if (aThis.bodyMessagQueue.length > 0) {
			var bodyMessage = aThis.bodyMessagQueue.shift();
			aThis.execAjaxRequest(bodyMessage);
		} else {
			if (aThis.requestingCount < aThis.hold) {
				aThis.sendBody(new Body());
			}
		}
	},
	
	execAjaxRequest: function(bodyMessage) {
		var aThis = XmppConnectionMgr.instance;
		// TODO
//		if (window.console) {
//			window.console.log("sending:" + bodyMessage.toXml());
//		}
		
		++aThis.requestingCount;
		$.ajax({
			url: Christy.requestUrl,
			dataType: "xml",
			cache: false,
			type: "post",
			data: bodyMessage.toXml(),
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
						eventType: ConnectionEventType.Error,
						when: TimeUtils.currentTimeMillis(),
						error: errorThrown
					}
					aThis.fireConnectionEvent(event);
					return;
				}
				var requestId = aThis.getRequestId();
				bodyMessage.setAttribute("rid", requestId);
				var key = aThis.keyGenerater.getKey();
				if (key != null) {
					bodyMessage.setAttribute("key", key);
				}
				if (aThis.keyGenerater.isKeyEmpty()) {
					bodyMessage.setAttribute("newkey", aThis.keyGenerater.getNewKey());
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
		var body = new Body();
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
		

		var aThis = this;
		
		this.handleBodyByRid({
			rid: body.getAttribute("rid"),
			handler: function(responsebody) {
				var type = responsebody.getAttribute("type");
				if (type == "terminate") {
					var event = {
						eventType: ConnectionEventType.Error,
						when: TimeUtils.currentTimeMillis(),
						connection: connection,
						condition: responsebody.getAttribute("condition")
					}
					aThis.fireConnectionEvent(event);
					aThis.removeConnectionByStreamName(null, responsebody);
					return;
				}
				
				var domain = responsebody.getAttribute("from");
				if (domain == null || domain == "") {
					domain = body.getAttribute("to");
				}
				
				var hold = responsebody.getAttribute("hold");
				if (hold != null) {
					aThis.hold = hold;
				}
				
				var streamId = responsebody.getAttribute("sid");
				aThis.streamId = streamId;
				
				var streamName = responsebody.getAttribute("stream");
				if (streamName != null) {
					var conn = aThis.getConnectionByStreamName(streamName);
					if (conn != null) {
						return;
					}
				} else {
					if (aThis.connections.length > 0) {
						return;
					}
				}
				var connection = new XmppConnection(domain);
				connection.setStreamName(streamName);

				aThis.connections.push(connection);
				
				var stanzas = responsebody.getStanzas();
				var stanza = stanzas[0];
				if (stanza instanceof StreamFeature) {
					var streamFeature = stanza;
					var mechanisms = streamFeature.getMechanisms();
					connection.setAllowedMechanisms(mechanisms);
				}

				var event = {
					eventType: ConnectionEventType.Created,
					when: TimeUtils.currentTimeMillis(),
					connection: connection
				}
				aThis.fireConnectionEvent(event);
			}
		});
		this.sendBody(body);
		if (this.intervalId == null) {
			this.intervalId = setInterval(this.processRequest, 1000);
		}
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
		if (body.getAttribute("type") == "terminate") {
			var streamName = body.getAttribute("stream");
			this.removeConnectionByStreamName(streamName, body);
			return;
		}
		this.fireBodyHandler(body);
		this.handleStanza(body);
	},
	
	handleStanza: function(body) {
		var streamName = body.getAttribute("stream");
		var connection = this.getConnectionByStreamName(streamName);
		if (connection == null) {
			if (this.connections.length == 1) {
				connection = this.connections[0];
			} else if (this.connections.length == 0)  {
				// no connection exist
				return;
			}else {
				var event = {
					eventType: ConnectionEventType.Error,
					when: TimeUtils.currentTimeMillis(),
					error: new Error("unknow connection")
				}
				this.fireConnectionEvent(event);
				return;
			}
		}
		
		var stanzas = body.getStanzas();
		var stanzaReceived = ConnectionEventType.StanzaReceived;
		for (var i = 0; i < stanzas.length; ++i){
			var stanza = stanzas[i];
			if (stanza instanceof Packet) {
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
			else if (stanza instanceof StreamFeature) {
				if (stanza.containFeature(IqBind.ELEMENTNAME, IqBind.NAMESPACE)) {
					connection.bindResource();
				}
			}
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

	getConnectionByJid: function(jid){
        for (var i = 0; i < this.connections.length; ++i){
			if (jid.equals(this.connections[i].getJid())) {
				return this.connections[i];
			}
		}
        return null;
	},
	
	removeConnectionByStreamName: function(streamName, body) {
		for (var i = 0; i < this.connections.length; ++i){
			var conn = this.connections[i];
			if (streamName == null
				|| streamName == conn.getStreamName()) {
				conn.closed = true;
				conn.authenticated = false;
				conn.sessionBinded = false;
				conn.resourceBinded = false;
				
				this.connections.splice(i,1);
				var event = {
					eventType: ConnectionEventType.ConnectionClosed,
					when: TimeUtils.currentTimeMillis(),
					connection: conn,
					body: body
				}
				this.fireConnectionEvent(event);
				break;
			}
		}
		
		if (this.connections.length == 0) {
			clearInterval(this.intervalId);
			this.processRequest(true);
			this.intervalId = null;
		}
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

XmppConnectionMgr.getInstance = function() {
	if (XmppConnectionMgr.instance == null) {
		XmppConnectionMgr.instance = new XmppConnectionMgr();
	}
	return XmppConnectionMgr.instance;
}


KeyGenerater = jClass.extend({
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

ConnectionEventType = {
	
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
	
	OtherResourceStatusChanged: "OtherResourceStatusChanged",
	
	ContactStatusChanged: "ContactStatusChanged",
	
	ContactSubscribeMe: "ContactSubscribeMe",
	
	ContactSubscribed: "ContactSubscribed",
	
	ContactUnsubscribeMe: "ContactUnsubscribeMe",
	
	ContactUnsubscribed: "ContactUnsubscribed",
	
	ChatCreated: "ChatCreated",
	
	MessageReceived: "MessageReceived",
	
	ChatResourceChanged: "ChatResourceChanged",
	
	ConnectionClosed: "ConnectionClosed"
}		

XmppConnection = jClass.extend({

	init: function(domain) {
		this.domain = domain;
		this.allowedMechanisms = new Array();
		this.handlers = new Array();
		this.contacts = new Array();
		this.otherResources = new Array();
		this.chats = new Array();
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

		var body = new Body();
		var auth = new Auth();
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
					var eventType = ConnectionEventType.SaslFailed;
					if (stanzas[0] instanceof Success) {
						eventType = ConnectionEventType.SaslSuccessful;
						connectionThis.authenticated = true;
						
						// TODO Do not need it in new Protocal
//						connectionThis.bindResource();
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
		var iq = new Iq(IqType.SET);
		iq.setTo(new JID(null, this.getDomain(), null));
		var iqBind = new IqBind();
		if (this.resource == null) {
			this.resource = "Christy";
		}
		iqBind.setResource(this.resource);
		iq.addPacketExtension(iqBind);
		
		var id = iq.getStanzaId();
		
		var connectionMgr = XmppConnectionMgr.getInstance();
		var connectionThis = this;
		this.handleStanza({
			filter: new PacketIdFilter(id),
			handler: function(iqResponse) {
				var eventType = ConnectionEventType.BindResourceFailed;
				if (iqResponse.getType() == IqType.RESULT) {
					eventType = ConnectionEventType.ResourceBinded;
					var iqBindResponse = iqResponse.getPacketExtension(IqBind.ELEMENTNAME, IqBind.NAMESPACE);
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
				if (eventType == ConnectionEventType.ResourceBinded) {
					connectionThis.bindSession();
				}
			}
		});
		// TODO Do not need second arg in new Protocal
		this.sendStanza(iq, true);
	},
	
	bindSession: function() {
		
		var iq = new Iq(IqType.SET);
		iq.setTo(new JID(null, this.getDomain(), null));
		var iqSession = new IqSession();
		iq.addPacketExtension(iqSession);
		
		var id = iq.getStanzaId();
		
		var connectionMgr = XmppConnectionMgr.getInstance();
		var connectionThis = this;
		this.handleStanza({
			filter: new PacketIdFilter(id),
			handler: function(iqResponse) {
				
				var eventType = ConnectionEventType.BindSessionFailed;
				if (iqResponse.getType() == IqType.RESULT) {
					eventType = ConnectionEventType.SessionBinded;
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
		var iq = new Iq(IqType.GET);
		iq.addPacketExtension(new IqRoster());
		
		this.sendStanza(iq);
	},
	
	packetHandler: function(packet) {
		if (packet instanceof Iq) {
			if (packet.getPacketExtension(IqRoster.ELEMENTNAME, IqRoster.NAMESPACE) != null) {
				this.handleRoster(packet);
			}
		}
		else if (packet instanceof Presence) {
			var presence = packet;
			var type = presence.getType();
			var from = presence.getFrom();
			if (type == PresenceType.AVAILABLE
				|| type == PresenceType.UNAVAILABLE) {
				if (from.equalsWithBareJid(this.jid)) {
					if (from.getResource() != this.jid.getResource()) {
						this.handleOtherResource(from.getResource(), presence);
					}
				} else {
					this.handleStatusChanged(presence);
				}

			} else {
				var eventType = null;
				if (type == PresenceType.SUBSCRIBE) {
					eventType = ConnectionEventType.ContactSubscribeMe;
				}
				else if (type == PresenceType.SUBSCRIBED) {
					eventType = ConnectionEventType.ContactSubscribed;
				}
				else if (type == PresenceType.UNSUBSCRIBE) {
					eventType = ConnectionEventType.ContactUnsubscribeMe;
				}
				else if (type == PresenceType.UNSUBSCRIBED) {
					eventType = ConnectionEventType.ContactUnsubscribed;
				}
				
				if (eventType != null) {
					var event = {
							eventType: eventType,
							when: TimeUtils.currentTimeMillis(),
							connection: this,
							stanza: presence
					}
					var connectionMgr = XmppConnectionMgr.getInstance();
					connectionMgr.fireConnectionEvent(event);
				}
			}
		} else if (packet instanceof Message) {
			
			var message = packet;
			var type = message.getType();
			if (type == MessageType.CHAT) {
				this.handleChatMessage(packet);
			}
		}
		
		for (var i =  0; i < this.handlers.length; ++i) {
			if (this.handlers[i].filter.accept(this, packet)) {
				this.handlers[i].handler(packet);
				this.handlers.splice(i,1);
			}
		}
	},
	
	handleChatMessage: function(message) {
		var from = message.getFrom();
		var bareJID = new JID(from.getNode(), from.getDomain(), null);
		var resource = from.getResource();

		var chat = this.getChat(bareJID);
		if (chat == null) {
			chat = this.createChat(bareJID, resource);
		}

		
		var messageReceived = ConnectionEventType.MessageReceived;
		var event = {
				eventType: messageReceived,
				when: TimeUtils.currentTimeMillis(),
				connection: this,
				stanza: message,
				chat: chat
		}
		var connectionMgr = XmppConnectionMgr.getInstance();
		connectionMgr.fireConnectionEvent(event);

		if (chat.chatResource != resource) {
			chat.chatResource = resource;
			var chatResourceChanged = ConnectionEventType.ChatResourceChanged;
			var event = {
					eventType: chatResourceChanged,
					when: TimeUtils.currentTimeMillis(),
					connection: this,
					stanza: message,
					chat: chat
			}
			connectionMgr.fireConnectionEvent(event);
		}
	},
	
	sendChatText: function(chat, text) {
		var message = new Message(MessageType.CHAT);
		message.setTo(new JID(chat.bareJID.getNode(), chat.bareJID.getDomain(), chat.chatResource));
		message.setBody(text);
		this.sendStanza(message);
	},
	
	createChat: function(bareJID, chatResource) {
		var chat = this.getChat(bareJID);
		if (chat == null) {
			chat = {
				bareJID: bareJID,
				chatResource: chatResource
			}
			this.chats.push(chat);
			
			var chatCreated = ConnectionEventType.ChatCreated;
			var event = {
					eventType: chatCreated,
					when: TimeUtils.currentTimeMillis(),
					connection: this,
					chat: chat
			}
			var connectionMgr = XmppConnectionMgr.getInstance();
			connectionMgr.fireConnectionEvent(event);
		}
		return chat;
	},
	
	getChat: function(jid) {
		for (var i =  0; i < this.chats.length; ++i) {
			if (this.chats[i].bareJID.equalsWithBareJid(jid)) {
				return this.chats[i];
			}
		}
		return null;
	},
	
	handleStatusChanged: function(presence) {
		var jid = presence.getFrom();
		var bareJID = JID.createJID(jid.toBareJID());
		var resource = jid.getResource();
		
		var contact = this.getContact(bareJID);
		
		if (contact == null) {
			return;
		}

		var userResource = contact.getResource(resource);
		if (userResource == null) {
			userResource = {
				resource: resource
			}
			contact.addResource(userResource);
		}

		userResource.oldPresence = userResource.currentPresence;
		userResource.currentPresence = presence;
		
		if (presence.getType() == PresenceType.UNAVAILABLE) {
			contact.removeResource(resource);
		}
		
		//TOO 
		if (window.console) {
			window.console.log(jid.toFullJID() + " status changed");
		}
		
		var contactStatusChanged = ConnectionEventType.ContactStatusChanged;
		var event = {
				eventType: contactStatusChanged,
				when: TimeUtils.currentTimeMillis(),
				connection: this,
				stanza: presence,
				contact: contact,
				resource: userResource
		}
		var connectionMgr = XmppConnectionMgr.getInstance();
		connectionMgr.fireConnectionEvent(event);
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
		
		if (PresenceType.UNAVAILABLE == presence.getType()) {
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
		
		var otherResourceStatusChanged = ConnectionEventType.OtherResourceStatusChanged;
		var event = {
				eventType: otherResourceStatusChanged,
				when: TimeUtils.currentTimeMillis(),
				connection: this,
				stanza: presence,
				otherResource: otherResource
		}
		var connectionMgr = XmppConnectionMgr.getInstance();
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
		var iqRoster = iq.getPacketExtension(IqRoster.ELEMENTNAME, IqRoster.NAMESPACE);
		var items = iqRoster.getRosterItems();
		for (var i = 0; i < items.length; ++i) {
			var rosterItem = items[i];
			var subscription = rosterItem.getSubscription();
			var jid = rosterItem.getJid();
			if (subscription == IqRosterSubscription.remove) {
				this.removeContact(jid);
			} else {
				var contact = this.getContact(jid);
				if (contact == null) {
					contact = new XmppContact(rosterItem);
					this.contacts.push(contact);
				}
				contact.setRosterItem(rosterItem);
				
				//TODO 
				if (window.console) {
					window.console.log(jid.toFullJID() + " has been updated");
				}
				
				var event = {
					eventType: ConnectionEventType.ContactUpdated,
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
					eventType: ConnectionEventType.ContactRemoved,
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
	
	isClosed: function() {
		return this.closed;
	},
	
	close: function(unavailablePresence) {
		var body = new Body();
		body.setAttribute("type", "terminate");
		
		if (this.streamName != null) {
			body.setAttribute("stream", this.streamName);
		}
		
		if (unavailablePresence != null) {
			body.addStanza(unavailablePresence);
		}
		var connectionMgr = XmppConnectionMgr.getInstance();
		connectionMgr.sendBody(body);
		connectionMgr.removeConnectionByStreamName(this.streamName);
	},
	
	sendStanza: function(stanza) {
		if (this.isClosed()) {
			throw new Error("connection has been closed");
		}
		
		var body = new Body();
						
		// TODO Do not need it in new Protocal
//		if (arguments[1]) {
//			body.setAttribute("xmpp:restart", "true");
//		}
		
		
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

XmppContact = jClass.extend({

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
	
});