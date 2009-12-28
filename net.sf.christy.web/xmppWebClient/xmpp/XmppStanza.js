// start of XmlStanza
var XmlStanza = jClass.extend({
	init: function(){
		
	},
	
	toXml: function(){
		return null;
	}
});
// end of XmlStanza



// start of AbstractXmlStanza
var AbstractXmlStanza = XmlStanza.extend({
	init: function() {
	    this._super();
	},
	
	getStanzaId: function(){
        if (this.stanzaId != null
        	&& AbstractXmlStanza.ID_NOT_AVAILABLE == this.stanzaId)
        {
                return null;
        }

        if (this.stanzaId == null)
        {
                this.stanzaId = AbstractXmlStanza.nextID();
        }
        return this.stanzaId;
    },
    
    setStanzaId: function(stanzaId){
    	this.stanzaId = stanzaId;
    }
    

});

AbstractXmlStanza.ID_NOT_AVAILABLE = "ID_NOT_AVAILABLE"

AbstractXmlStanza.prefix = StringUtils.randomString(5) + "-";

AbstractXmlStanza.id = 0;

AbstractXmlStanza.nextID = function(){
	return AbstractXmlStanza.prefix + (AbstractXmlStanza.id++);
}

// end of AbstractXmlStanza



// start of PacketExtension

var PacketExtension = XmlStanza.extend({
	init: function() {
	    this._super();
	},
	
	getElementName: function(){
		return null;
	},
	
	getNamespace: function(){
		return null;
	}
});

// end of PacketExtension




// start of XmppError
var ErrorType = {
	WAIT: 'wait',
	CANCEL: 'cancel',
	MODIFY: 'modify',
	AUTH: 'auth',
	CONTINUE: 'continue'
}

var XmppError = XmlStanza.extend({
	init: function(code, type) {
	    this._super();
	    this.code = code;
	    this.type = type;
	    this.conditions = new Array();
	    this.packetExtensions = new Array();
	},
	
	setCode: function(code){
    	this.code = code;
	},
	
	getCode: function(){
		return this.code;
	},
	
	setType: function(type){
    	this.type = type;
	},
	
	getType: function(){
		return this.type;
	},
	
	setCondition: function(condition){
		this.condition = condition;
	},
	
	getCondition: function(){
		return this.condition;
	},
	
	addCondition: function(element, namespace){
		this.conditions[element] = namespace;
	},
	
	removeCondition: function(element, namespace){
		delete this.conditions[element];
	},
	
	getConditions: function(){
		return this.conditions;
	},
	
	setMessage: function(message){
		this.message = message;
	},
	
	getMessage: function(){
		return this.message;
	},
	
	addPacketExtension: function(extension){
		this.packetExtensions.push(extension);
	},
	
	removePacketExtension: function(extension){
		for (var i = 0; i < this.packetExtensions.length; ++i){
			if (this.packetExtensions[i] == exten){
				this.packetExtensions.splice(i,1);
			}
		}
	},
	
	getPacketExtensions: function(){
		return this.packetExtensions;
	},
	
	toXml: function(){
	    var xml = "";
	    xml += "<error code=\"" + this.code + "\"";
	    
	    if (this.type != null)
	    {
	    	xml += " type=\"";
	    	xml += this.type;
	    	xml += "\"";
	    }
	    xml += ">";
	    
	    
	    if (this.condition != null)
	    {
	    	xml += "<" + this.condition + 
	    			" xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/>";
	        for(key in this.conditions){
	        	xml += "<" + key + " xmlns=\"" + this.conditions[key] + "\"/>";
	        }

	    }
	    
	    if (this.message != null)
	    {
	    	xml += "<text xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\">";
			xml += StringUtils.escapeXml(this.message);
			xml += "</text>";
	    }
	    
	    for (var j = 0; j < this.packetExtensions.length; ++j){
	            xml += this.packetExtensions[j].toXml();
	    }
	    
		xml += "</error>";
	    return xml;
    }
	
});

// end of XmppError


// start of Packet
var Packet = AbstractXmlStanza.extend({
	init: function() {
		this._super();
		this.packetExtensions = new Array();
	},
	
	setTo: function(to){
		this.to = to;
	},
	
	getTo: function(){
		return this.to
	},
	
    setFrom: function(from){
    	this.from = from;
    },
    
	getFrom: function(){
    	return this.from;
    },
    
    setLanguage: function(language){
    	this.language = language;
    },
    
	getLanguage: function(){
    	return this.language;
    },
    
    setXmppError: function(xmppError){
		this.xmppError = xmppError;
	},
	
	getXmppError: function(){
		return this.xmppError
	},
	
	addPacketExtension: function(extension){
		this.packetExtensions.push(extension);
	},
	
	removePacketExtension: function(extension){
		for (var i = 0; i < this.packetExtensions.length; ++i){
			if (this.packetExtensions[i] == exten){
				this.packetExtensions.splice(i,1);
				break;
			}
		}
	},
	
	getPacketExtensions: function(){
		return this.packetExtensions;
	},
	
	getPacketExtension: function(elementName, namespace){
		for (var i = 0; i < this.packetExtensions.length; ++i){
			var extension = this.packetExtensions[i];
			if (extension.getElementName == elementName
				 && extension.getElementName == namespace){
				return extension;
			}
		}
	},
	
	getExtensionsXml: function(){
		var xml = "";
		
		for (var i = 0; i < this.packetExtensions.length; ++i){
			xml += this.packetExtensions[i].toXml();
		}
		return xml;
	}
	
});

// end of Packet


// start of Iq
var IqType = {
	GET: 'get',
	SET: 'set',
	ERROR: 'error',
	RESULT: 'result'
}

var Iq = Packet.extend({
	init: function(type) {
	    this._super();
	    this.type = type;
	},
	
	setType: function(type){
		this.type = type;
	},
	
	getType: function(){
		return this.type;
	},
	
	toXml: function(){
        var xml = "";
        xml += "<iq";
        
        if (this.getLanguage() != null){
        	xml += " xml:lang=\"" + this.getLanguage() + "\"";
        }
        
        if (this.getStanzaId() != null){
			xml += " id=\"" + this.getStanzaId() + "\"";
        }
        
        if (this.getTo() != null){
			xml += " to=\"" + this.getTo().toFullJID() + "\"";
        }
        
        if (this.getFrom() != null){
			xml += " from=\"" + this.getFrom().toFullJID() + "\"";
        }
        
        if (this.type == null){
        	xml += " type=\"get\">";
        } else {
            xml += " type=\"" + this.getType() + "\">";
        }

        var extensionXml = this.getExtensionsXml();
        if (extensionXml != null){
        	xml += extensionXml;
        }
        // Add the error sub-packet, if there is one.
        var error = this.getXmppError();
        
        if (error != null){
			xml += error.toXml();
        }
        xml += "</iq>";
        return xml;
    }
	
});

// end of Iq


// start of Presence

var PresenceType = {
	
	AVAILABLE: "available",
	
    UNAVAILABLE: "unavailable",

    SUBSCRIBE: "subscribe",

    SUBSCRIBED: "subscribed",

    UNSUBSCRIBE: "unsubscribe",

    UNSUBSCRIBED: "unsubscribed",

    PROBE: "probe",

    ERROR: "error"
	
}

var PresenceShow = {
	
	CHAT: "chat",

    AVAILABLE: "available",

    AWAY: "away",

    XA: "xa",

    DND: "dnd"

}

var Presence = Packet.extend({
	init: function(type) {
	    this._super();
	    this.type = type;
	},
	
	isAvailable: function(){
    	return this.type == PresenceType.AVAILABLE;
    },
    
    isAway: function(){
		return this.type == PresenceType.AVAILABLE
				&& (this.show == PresenceShow.AWAY 
						|| this.show == PresenceShow.XA
						|| this.show == PresenceShow.DND);
	},
	
	setType: function(type){
		this.type = type;
	},
	
	getType: function(){
		return this.type;
	},
	
	setUserStatus: function(userStatus){
		this.userStatus = userStatus
	},
	
	getUserStatus: function(){
		return this.userStatus;
	},
	
	setPriority: function(priority){
		if (priority < -128 || priority > 128){
        	throw new Error(0, 
        					"Priority value " + 
        					priority + 
        					" is not valid. Valid range is -128 through 128.");
        }
		
		this.priority = priority;
	},
	
	getPriority: function(){
		return this.priority;
	},
	
	setShow: function(show){
		this.show = show;
	},
	
	getShow: function(){
		return this.show;
	},
	
	toXml: function(){
        var xml = "";
        xml += "<presence";
        if (this.getLanguage() != null){
        	xml += " xml:lang=\"" + this.getLanguage() + "\"";
        }
        if (this.getStanzaId() != null){
			xml += " id=\"" + this.getStanzaId() + "\"";
        }
        if (this.getTo() != null){
        	xml += " to=\"" + this.getTo().toFullJID() + "\"";
        }
        if (this.getFrom() != null){
			xml += " from=\"" + this.getFrom().toFullJID() + "\"";
        }
        if (this.type != PresenceType.AVAILABLE){
			xml += " type=\"" + this.type + "\"";
        }
        xml += ">";
        if (this.getUserStatus() != null){
			xml += "<status>" + StringUtils.escapeXml(this.getUserStatus()) + "</status>";
        }
        if (this.priority != null){
			xml += "<priority>" + this.priority + "</priority>";
        }
        if (this.show != null && this.show != PresenceShow.AVAILABLE){
			xml += "<show>" + this.show + "</show>";
        }

		var extensionXml = this.getExtensionsXml();
        if (extensionXml != null){
        	xml += extensionXml;
        }

        // Add the error sub-packet, if there is one.
        var error = this.getXmppError();
        if (error != null){
			xml += error.toXml();
        }

        xml += "</presence>";

        return xml;
	}

});
// end of Presence

//start of Message
var MessageType = {
	
	NORMAL: "normal",

	CHAT: "chat",

	GROUPCHAT: "groupchat",

	HEADLINE: "headline",

	ERROR: "error"
	
}

var MessageBody = XmlStanza.extend({
	init: function(language, body) {
	    this._super();
	    this.language = language;
	    this.body = body;
	},
	
	setLanguage: function(language){
		this.language = language;
	},
	
	getLanguage: function(){
		return this.language;
	},
	
	setBody: function(body){
		this.body = body;
	},
	
	getBody: function(){
		return this.body;
	},
	
	toXml: function(){
		var xml = "";         
        xml += "<body xml:lang=\"" + this.language + "\">";
        xml += StringUtils.escapeXml(this.body);
        xml += "</body>";
        return xml;
		
	}
});


var MessageSubject = XmlStanza.extend({
	init: function(language, subject) {
	    this._super();
	    this.language = language;
	    this.subject = subject;
	},
	
	setLanguage: function(language){
		this.language = language;
	},
	
	getLanguage: function(){
		return this.language;
	},
	
	setSubject: function(subject){
		this.subject = subject;
	},
	
	getSubject: function(){
		return this.subject;
	},
	
	toXml: function(){
		var xml = "";         
        xml += "<subject xml:lang=\"" + this.language + "\">";
        xml += StringUtils.escapeXml(this.subject);
        xml += "</subject>";
        return xml;
		
	}
});

var Message = Packet.extend({
	init: function(type) {
	    this._super();
	    this.type = type;
	    this.bodies = new Array();
	    this.subjects = new Array();
	},
	
	setType: function(type){
		this.type = type;
	},
	
	getType: function(){
		return this.type;
	},
	
	setThread: function(thread){
		this.thread = thread;
	},
	
	getThread: function(){
		return this.thread;
	},
	
	setBody: function(body){
		this.body = body;
	},
	
	getBody: function(){
		return this.body;
	},
	
	addBody: function(body){
		this.bodies.push(body);
	},
	
	removeBody: function(body){
		if (body instanceof MessageBody){
			for (var i = 0; i < this.bodies.length; ++i){
				if (this.bodies[i] == body){
					this.bodies.splice(i,1);
					break;
				}
			}
		} else if (body instanceof String){
			for (var i = 0; i < this.bodies.length; ++i){
				if (this.bodies[i].getLanguage() == body){
					this.bodies.splice(i,1);
					break;
				}
			}
		}
		
	},
	
	getBodies: function(){
		return this.bodies;
	},
	
	setSubject: function(subject){
		this.subject = subject;
	},
	
	getSubject: function(){
		return this.subject;
	},
	
	addSubject: function(subject){
		this.subjects.push(subject);
	},
	
	removeSubject: function(subject){
		if (subject instanceof MessageSubject){
			for (var i = 0; i < this.subjects.length; ++i){
				if (this.subjects[i] == subject){
					this.subjects.splice(i,1);
					break;
				}
			}
		} else if (subject instanceof String){
			for (var i = 0; i < this.subjects.length; ++i){
				if (this.subjects[i].getLanguage() == body){
					this.subjects.splice(i,1);
					break;
				}
			}
		}
		
	},
	
	getSubjects: function(){
		return this.subjects;
	},
	
	toXml: function(){
		var xml = "";
        xml += "<message";
        if (this.getLanguage() != null){
			xml += " xml:lang=\"" + this.getLanguage() + "\"";
        }
        if (this.getStanzaId() != null){
			xml += " id=\"" + this.getStanzaId() + "\"";
        }
        if (this.getTo() != null){
			xml += " to=\"" + StringUtils.escapeXml(this.getTo().toFullJID()) + "\"";
        }
        if (this.getFrom() != null){
			xml += " from=\"" + this.getFrom().toFullJID() + "\"";
        }
        if (this.type != MessageType.NORMAL){
			xml += " type=\"" + this.type + "\"";
        }
        xml += ">";
        
        if (this.subject != null){
			xml += "<subject>" + StringUtils.escapeXml(this.subject) + "</subject>";
        }
        
        for (var i = 0; i < this.subjects.length; ++i){
			xml += this.subjects[i].toXml();
		}
        
        // Add the body in the default language
        if (this.getBody() != null){
			xml += "<body>" + StringUtils.escapeXml(this.getBody()) + "</body>";
        }

        for (var i = 0; i < this.bodies.length; ++i){
			xml += this.bodies[i].toXml();
		}

        if (this.thread != null){
			xml += "<thread>" + this.thread + "</thread>";
        }

		var extensionXml = this.getExtensionsXml();
        if (extensionXml != null){
        	xml += extensionXml;
        }

        // Add the error sub-packet, if there is one.
        var error = this.getXmppError();
        if (error != null){
			xml += error.toXml();
        }
        xml += "</message>";
        return xml;
		
	}
	
});
// end of Message

// start of Auth
var Auth = XmlStanza.extend({
	init: function(){
		this._super();
	},
	
	getMechanism: function(){
		return this.mechanism;
	},

	setMechanism: function(mechanism){
		this.mechanism = mechanism;
	},
	
	getContent: function(){
		return this.content;
	},
	
	setContent: function(content){
		this.content = content;
	},
	
	toXml: function(){
		var xml = "";
		
		xml += "<auth";
		if (this.getMechanism() != null){
			xml += " mechanism=\"" + this.getMechanism() + "\"";
		}
		xml += " xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"";
		if (this.getContent() != null){
			xml += ">" + this.getContent() + "</auth>";
		} else {
			xml += "/>";
		}
		
		
		return xml;
	}
});
// end of Auth

// start of Challenge
var Challenge = XmlStanza.extend({
	init: function(content){
		this._super();
		this.content = content;
	},
	
	getContent: function(){
		return this.content;
	},

	setContent: function(content){
		this.content = content;
	},
	
	toXml: function(){
		var xml = "";
		
		xml += "<challenge xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">";
		if (this.getContent() != null){
			xml += this.getContent();
		}
		xml += "</challenge>";
		
		
		return xml;
	}
});
// end of Challenge

// start of Failure
var FailureError = {
	aborted: "aborted",
	incorrect_encoding: "incorrect-encoding",
	invalid_authzid: "invalid-authzid",
	invalid_mechanism: "invalid-mechanism",
	mechanism_too_weak: "mechanism-too-weak",
	not_authorized: "not-authorized",
	temporary_auth_failure: "temporary-auth-failure"
}
var Failure = XmlStanza.extend({
	init: function(namespace){
		this._super();
		this.namespace = namespace;
	},
	
	getNamespace: function(){
		return this.namespace;
	},

	setNamespace: function(namespace){
		this.namespace = namespace;
	},

	getError: function(){
		return this.error;
	},
	
	setError: function(error){
		this.error = error;
	},
	
	toXml: function(){
		var xml = "";
		xml += "<failure xmlns=\"" + this.getNamespace() + "\"";
		if (this.getError() != null){
			xml += ">" + this.getError() + "</failure>";
		} else {
			buf.append("/>");
		}
		
		return xml;
	}
});

Failure.SASL_FAILURE_NS = "urn:ietf:params:xml:ns:xmpp-sasl";
Failure.TLS_FAILURE_NS = "urn:ietf:params:xml:ns:xmpp-tls";

// end of Failure



// start of IqBind
var IqBind = PacketExtension.extend({
	init: function(){
		this._super();
	},
	
	getElementName: function(){
		return IqBind.ELEMENTNAME;
	},
	
	getNamespace: function(){
		return IqBind.NAMESPACE;
	},
	
	getResource: function(){
		return this.resource;
	},

	setResource: function(resource){
		this.resource = resource;
	},

	getJid: function(){
		return this.jid;
	},
	
	setJid: function(jid){
		this.jid = jid;
	},
	
	toXml: function(){
		var xml = "";
		xml += "<" + this.getElementName() + " xmlns=\"" + this.getNamespace() + "\">";
		if (this.getResource() != null){
			xml += "<resource>" + this.getResource() + "</resource>";
		}
		if (this.getJid() != null){
			xml += "<jid>" + this.getJid().toFullJID() + "</jid>";
		}
		xml += "</" +  this.getElementName() + ">";
		return xml;
	}
});
IqBind.ELEMENTNAME = "bind";
IqBind.NAMESPACE = "urn:ietf:params:xml:ns:xmpp-bind";

// end of IqBind

// start of IqRoster

var IqRosterAsk = {
	subscribe: "subscribe",
	unsubscribe: "unsubscribe"
}

var IqRosterSubscription = {
	none: "none",
	to: "to",
	from: "from",
	both: "both",
	remove: "remove"
}

var IqRosterItem = XmlStanza.extend({
	init: function(jid, rosterName) {
	    this._super();
	    this.jid = jid;
	    this.rosterName = rosterName;
	    this.groups = new Array();
	},
	
	getJid: function(){
		return this.jid;
	},
	
	getRosterName: function(){
			return this.rosterName;
	},
	
	setRosterName: function(rosterName){
		this.rosterName = rosterName;
	},
	
	getSubscription: function(){
		return this.subscription;
	},
	
	setSubscription: function(subscription){
		this.subscription = subscription;
	},
	
	getAsk: function(){
		return this.ask;
	},
	
	setAsk: function(ask){
		this.ask = ask;
	},
	
	getGroups: function(){
		return this.groups;
	},
	
	containGroup: function(group){
		for (var i = 0; i < this.groups.length; ++i){
			if (this.groups[i] == group){
				return true;
			}
		}
		return false;
	},
	
	addGroup: function(group){
		if (this.groups != null){
			this.groups.push(group);
		}
	},
	
	removeGroupName: function(group){
		for (var i = 0; i < this.groups.length; ++i){
			if (this.groups[i] == group){
				this.groups.splice(i,1);
			}
		}
	},
	
	toXml: function(){
		var xml = "";
		xml += "<item jid=\"" + this.jid.toBareJID() + "\"";
		if (this.getRosterName() != null){
			xml += " name=\"" + StringUtils.escapeXml(this.getRosterName()) + "\"";
		}
		
		if (this.getSubscription() != null){
			xml += " subscription=\"" + this.getSubscription() + "\"";
		}
		if (this.getAsk() != null){
			xml += " ask=\"" + this.getAsk() + "\"";
		}
		xml += ">";
		for (var i = 0; i < this.groups.length; ++i){
			xml += "<group>" + StringUtils.escapeXml(this.groups[i]) + "</group>";
		}
		xml += "</item>";
		return xml;
	}
});

var IqRoster = PacketExtension.extend({
	init: function(){
		this._super();
		this.rosterItems = new Array();
	},
	
	getElementName: function(){
		return IqRoster.ELEMENTNAME;
	},
	
	getNamespace: function(){
		return IqRoster.NAMESPACE;
	},
	
	addRosterItem: function(rosterItem){
		this.rosterItems.push(rosterItem);
	},
	
	getRosterItemCount: function(){
		return this.rosterItems.length;
	},
	
	getRosterItems: function(){
		return this.rosterItems;
	},
	
	getRosterItem: function(jid){
		for (var i = 0; i < this.rosterItems.length; ++i){
				if (this.rosterItems[i].getJid().equalsWithBareJid(jid)){
					return this.rosterItems[i];
				}
		}
		return null;
	},
	
	containRosterItem: function(jid)
	{
		for (var i = 0; i < this.rosterItems.length; ++i){
				if (this.rosterItems[i].getJid().equalsWithBareJid(jid)){
					return true;
				}
		}
		return false;
	},
	
	
	toXml: function(){
		var xml = "";
		
		xml += "<" + this.getElementName() + " xmlns=\"" + this.getNamespace() + "\">";
		for (var i = 0; i < this.rosterItems.length; ++i){
				xml += this.rosterItems[i].toXml();
		}
		
		xml += "</" +  this.getElementName() + ">";
		return xml;
	}
});
IqRoster.ELEMENTNAME = "query";
IqRoster.NAMESPACE = "jabber:iq:roster";

// end of IqRoster



// start of IqSession
var IqSession = PacketExtension.extend({
	init: function(){
		this._super();
	},
	
	getElementName: function(){
		return IqSession.ELEMENTNAME;
	},
	
	getNamespace: function(){
		return IqSession.NAMESPACE;
	},
	
	toXml: function(){
		var xml = "";
		xml += "<" + this.getElementName() + " xmlns=\"" + this.getNamespace() + "\"/>";
		return xml;
	}
});
IqSession.ELEMENTNAME = "session";
IqSession.NAMESPACE = "urn:ietf:params:xml:ns:xmpp-session";

// end of IqSession


// start of PrivacyItem
var PrivacyItemSubscription = {
	both: "both",
	
	to: "to",
	
	from: "from",
	
	none: "none"
}

var PrivacyItemType = {
			/**
		 * JID being analyzed should belong to a roster group of the
		 * list's owner.
		 */
		group: "group",
		/**
		 * JID being analyzed should have a resource match, domain
		 * match or bare JID match.
		 */
		jid: "jid",
		/**
		 * JID being analyzed should belong to a contact present in
		 * the owner's roster with the specified subscription
		 * status.
		 */
		subscription: "subscription"
}

var PrivacyItem = PacketExtension.extend({
	init: function(type, value, action, order){
		this._super();
		this.setType(type);
		this.setValue(value);
		this.setAction(action);
		this.setOrder(order);
	},
	
	getType: function(){
		return this.type;
	},
	
	setType: function(type){
		this.type = type;
	},
	
	getValue: function(){
		return this.value;
	},
	
	setValue: function(value){
		this.value = value;
	},
	
	isAction: function(){
		return this.action == null ? false : this.action;
	},

	setAction: function(action){
		this.action = action;
	},
	
	getOrder: function(){
		return this.order;
	},

	setOrder: function(order){
		this.order = order;
	},
	
	 isFilterIQ: function(){
		return this.filterIQ;
	},
	
	setFilterIQ: function(filterIQ){
		this.filterIQ = filterIQ;
	},

	isFilterMessage: function(){
		return this.filterMessage;
	},
	
	setFilterMessage: function(filterMessage){
		this.filterMessage = filterMessage;
	},
	
	isFilterPresence_in: function(){
		return this.filterPresence_in;
	},
	
	setFilterPresence_in: function(filterPresence_in){
		this.filterPresence_in = filterPresence_in;
	},
	
	isFilterPresence_out: function(){
		return this.filterPresence_out;
	},
	
	setFilterPresence_out: function(filterPresence_out){
		this.filterPresence_out = filterPresence_out;
	},
	
	isFilterEverything: function(){
		return !(this.isFilterIQ() || this.isFilterMessage() || this.isFilterPresence_in() || this.isFilterPresence_out());
	},
	
	isFilterEmpty: function(){
		return !this.isFilterIQ() && !this.isFilterMessage() && !this.isFilterPresence_in() && !this.isFilterPresence_out();
	},
	
	toXml: function(){
		var xml = "";
		xml += "<item";
		if (this.isAction()){
			xml += " action=\"allow\"";
		} else {
			xml += " action=\"deny\"";
		}
		xml += " order=\"" + this.getOrder() + "\"";
		if (this.getType() != null){
			xml += " type=\"" + this.getType() + "\"";
		}
		if (this.getValue() != null){
			xml += " value=\"" + StringUtils.escapeXml(this.getValue()) + "\"";
		}
		if (this.isFilterEverything()){
			xml += "/>";
		} else {
			xml += ">";
			if (this.isFilterIQ()){
				xml += "<iq/>";
			}
			if (this.isFilterMessage()){
				xml += "<message/>";
			}
			if (this.isFilterPresence_in()){
				xml += "<presence-in/>";
			}
			if (this.isFilterPresence_out()){
				xml += "<presence-out/>";
			}
			xml += "</item>";
		}
		return xml;
	}
});

// end of PrivacyItem

// start of PrivacyList
var PrivacyList = XmlStanza.extend({
	init: function(listName){
		this._super();
		this.listName = listName;
		this.items = new Array();
	},
	
	isActiveList: function(){
		return this.isActiveList;
	},
	
	setActiveList: function(isActiveList){
		this.isActiveList = isActiveList;
	},
	
	isDefaultList: function(){
		return this.isDefaultList;
	},
	
	setDefaultList: function(isDefaultList){
		this.isDefaultList = isDefaultList;
	},
	
	getListName: function(){
		return this.listName;
	},
	
	setListName: function(listName){
		this.listName = listName;
	},
	
	addItem: function(item){
		this.items.push(item);
	},
	
	removeItem: function(item){
		for (var i = 0; i < this.items.length; ++i){
				if (this.items[i] == item){
					this.items.splice(i,1);
					break;
				}
		}
	},
	
	getItem: function(order){
		for (var i = 0; i < this.items.length; ++i){
				if (this.items[i].getOrder() == order){
					return this.items[i];
				}
		}
		return null;
	},
	
	getItems: function(){
		return this.items;
	},
	
	toXml: function(){
		var xml = "";
		xml += "<list";
		if (this.getListName() != null){
			xml += " name=\"" + this.getListName() + "\"";
		}
		if (this.items.length == 0){
			xml += "/>";
		} else {
			xml += ">";
			for (var i = 0; i < this.items.length; ++i){
					xml += this.items[i].toXml();
			}
	
			xml += "</list>";
		}
		
		return xml;
	}
});

// end of PrivacyList

// start of Privacy
var Privacy = PacketExtension.extend({
	init: function(){
		this._super();
		this.privacyLists = new Array();
	},
	
	getElementName: function(){
		return Privacy.ELEMENTNAME;
	},
	
	getNamespace: function(){
		return Privacy.NAMESPACE;
	},
	
	getActiveName: function(){
		return this.activeName;
	},
	
	setActiveName: function(activeName){
		this.activeName = activeName;
	},
	
	getDefaultName: function(){
		return this.defaultName;
	},
	
	setDefaultName: function(defaultName){
		this.defaultName = defaultName;
	},
	
	addPrivacyList: function(privacyList){
		this.privacyLists.push(privacyList);
	},
	
	removePrivacyList: function(listName){
		for (var i = 0; i < this.privacyLists.length; ++i){
				if (this.privacyLists[i].getListName() == listName){
					this.privacyLists.splice(i,1);
				}
		}
	},
	
	getPrivacyList: function(listName){
		for (var i = 0; i < this.privacyLists.length; ++i){
				if (this.privacyLists[i].getListName() == listName){
					return this.privacyLists[i];
				}
		}
		return null;
	},
	
	getPrivacyLists: function(){
		return this.privacyLists;
	},
	
	isDeclineActiveList: function(){
		return this.declineActiveList;
	},
	
	setDeclineActiveList: function(declineActiveList){
		this.declineActiveList = declineActiveList;
	},
	
	isDeclineDefaultList: function(){
		return this.declineDefaultList;
	},
	
	setDeclineDefaultList: function(declineDefaultList){
		this.declineDefaultList = declineDefaultList;
	},
	
	toXml: function(){
		var xml = "";
		xml += "<" + this.getElementName() + " xmlns=\"" + this.getNamespace() + "\">";

		// Add the active tag
		if (this.isDeclineActiveList()){
			xml += "<active/>";
		} else {
			if (this.getActiveName() != null){
				xml += "<active name=\"" + this.getActiveName() + "\"/>";
			}
		}
		// Add the default tag
		if (this.isDeclineDefaultList()){
			xml += "<default/>";
		} else {
			if (this.getDefaultName() != null){
				xml += "<default name=\"" + this.getDefaultName() + "\"/>";
			}
		}
		
		for (var i = 0; i < this.privacyLists.length; ++i){
				xml += this.privacyLists[i].toXml();
		}
		
		xml += "</" + this.getElementName() + ">";
		return xml;
	}
});
Privacy.ELEMENTNAME = "query";
Privacy.NAMESPACE = "jabber:iq:privacy";

// end of Privacy
