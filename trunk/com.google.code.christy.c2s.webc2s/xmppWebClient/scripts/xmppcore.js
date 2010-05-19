Christy = {};
Christy.requestUrl = "webclient/JHB.do";
Christy.loginTimeout = 30 * 1000; // 30 sec
Christy.intervalTime = 1 * 1000; //1 sec
////////////start of JID

JID = jClass.extend({
    init: function(node, domain, resource) {
        this.node = node;
        this.domain = domain;
        this.resource = resource;
        
        if (node != null){
        	this.nodePreped = node.toLowerCase();
        }
        
        if (domain == null){
        	throw new Error(0, "domain must not be null");
        }
        
		this.domainPreped = domain.toLowerCase();
		
		if (resource != null){
			this.resourcePreped = resource;
		}
    },
    
    getNode: function(){
    	return this.node;
    },
    
    getDomain: function(){
    	return this.domain;
    },
    
    getResource: function(){
    	return this.resource;
    },
    
    toBareJID: function(){
    	var bareJID = "";
        if (this.node != null)
        {
                bareJID += (this.node + "@");
        }
        bareJID += this.domain;
        return bareJID;
    },
    
    toFullJID: function(){
    	var fullJID = "";
        if (this.node != null){
                fullJID += (this.node + "@");
        }
        
        fullJID += this.domain;
        
        if (this.resource != null){
        	fullJID += ("/" + this.resource);
        }
        return fullJID;
    },
    
    toPrepedBareJID: function(){
    	var prepedBareJID = "";
        if (this.nodePreped != null)
        {
                prepedBareJID += (this.nodePreped + "@");
        }
        prepedBareJID += this.domainPreped;
        return prepedBareJID;
    },
    
    toPrepedFullJID: function(){
    	var prepedFullJID = "";
        if (this.nodePreped != null){
                prepedFullJID += (this.nodePreped + "@");
        }
        
        prepedFullJID += this.domainPreped;
        
        if (this.resourcePreped != null){
        	prepedFullJID += ("/" + this.resourcePreped);
                }
                return prepedFullJID;
	},
            
    equals: function(other){
    	if (this == other)
                return true;
        if (other == null)
                return false;
        if (!(other instanceof JID))
                return false;
        
        var prepedFullJID = this.toPrepedFullJID();
        if (prepedFullJID != other.toPrepedFullJID()){
        	return false;
        }
        return true;
    	
    },
    
    equalsWithBareJid: function(other){
    	if (this == other)
                return true;
        if (other == null)
                return false;
        if (!(other instanceof JID))
                return false;
        
        var prepedFullJID = this.toPrepedBareJID();
        if (prepedFullJID != other.toPrepedBareJID()){
        	return false;
        }
        return true;
    }

	
});

JID.createJID = function(jid){
	if (jid == null){
		throw new Error(0, "jid must not be null");
	}
	
	var node, domain, resource;
	
	var atIndex = jid.indexOf("@");
	var slashIndex = jid.indexOf("/");
	
	// Node
    if (atIndex > 0) {
    	node = jid.substring(0, atIndex);
    }

    // Domain
    if (atIndex + 1 > jid.length) {
    	throw new Error(0, "JID with empty domain not valid");
    }
    if (atIndex < 0) {
	    if (slashIndex > 0) {
	    	domain = jid.substring(0, slashIndex);
	    } else {
	    	domain = jid;
	    }
    } else {
	    if (slashIndex > 0) {
	    	domain = jid.substring(atIndex + 1, slashIndex);
	    } else {
	    	domain = jid.substring(atIndex + 1);
	    }
    }

    // Resource
    if (slashIndex + 1 > jid.length || slashIndex < 0) {
    	resource = null;
    } else {
		resource = jid.substring(slashIndex + 1);
    }
    
    return new JID(node, domain, resource);
	
};
		

////////////end of JID



// start of XmlStanza
XmlStanza = jClass.extend({
	init: function(){
		
	},
	
	toXml: function(){
		return null;
	}
});
// end of XmlStanza

// start of AbstractXmlStanza
AbstractXmlStanza = XmlStanza.extend({
	init: function() {
	    this._super();
	},
	
	getStanzaId: function(){
        if (this.stanzaId != null
        	&& AbstractXmlStanza.ID_NOT_AVAILABLE == this.stanzaId) {
                return null;
        }

        if (this.stanzaId == null) {
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

PacketExtension = XmlStanza.extend({
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

// start of UnknownExtension
UnknownExtension = PacketExtension.extend({
	init: function(xmlElement) {
	    this._super();
	    if (xmlElement) {
	    	this.xmlElement = xmlElement;
		    this.elementName = xmlElement.nodeName;
		    this.namespace = xmlElement.getAttribute("xmlns");
	    }
	    
	},
	
	setElementName: function(elementName) {
		this.elementName = elementName;
	},
	
	getElementName: function() {
		return this.elementName;
	},
	
	setNamespace: function(namespace) {
		this.namespace = namespace;
	},
	
	getNamespace: function() {
		return this.namespace;
	},
	
	generateXml: function(element) {
		var xml = "";
		if (element.nodeType == 1) {
			xml += "<" + element.nodeName;
			var attributes = element.attributes;
			for (var i = 0; i < attributes.length; ++i) {
				var attribute = attributes[i];
				xml += " " + attribute.nodeName + "=\"" + attribute.nodeValue + "\"";
				
			}
			xml += ">";
			var childNodes = element.childNodes;
			for (var i = 0; i < childNodes.length; ++i) {
				var childNode = childNodes[i];
				if (childNode.nodeType == 1) {
					xml += this.generateXml(childNode);
				} else if (childNode.nodeType == 3) {
					xml += childNode.nodeValue;
				}
			}
			
			xml += "</" + element.nodeName + ">";
		}
		return xml;
	},
	
	toXml: function() {
		if (this.xmlElement == null) {
			var xml = "";
			xml += "<" + this.getElementName() + " xmlns=\"" + this.getNamespace() + "\"/>";
			return xml;
		}
		return this.generateXml(this.xmlElement);
	}
});
// end of UnknownExtension


// start of XmppError
ErrorType = {
	WAIT: 'wait',
	CANCEL: 'cancel',
	MODIFY: 'modify',
	AUTH: 'auth',
	CONTINUE: 'continue'
}

XmppError = XmlStanza.extend({
	init: function(code, type) {
	    this._super();
	    this.code = code;
	    this.type = type;
	    this.conditions = new Array();
	    this.packetExtensions = new Array();
	},
	
	setCode: function(code) {
    	this.code = code;
	},
	
	getCode: function(){
		return this.code;
	},
	
	setType: function(type) {
    	this.type = type;
	},
	
	getType: function() {
		return this.type;
	},
	
	addCondition: function(element, namespace) {
		this.conditions.push({
			element: element,
			namespace: namespace
		});
	},
	
	removeCondition: function(element, namespace) {
		for (var i = 0; i < this.conditions.length; ++i) {
			if (this.conditions[i].element == element
				&& this.conditions[i].namespace == namespace) {
				this.conditions.splice(i,1);
			}
		}
	},
	
	getConditions: function() {
		return this.conditions;
	},
	
	setMessage: function(message) {
		this.message = message;
	},
	
	getMessage: function() {
		return this.message;
	},
	
	addPacketExtension: function(extension) {
		this.packetExtensions.push(extension);
	},
	
	removePacketExtension: function(extension) {
		for (var i = 0; i < this.packetExtensions.length; ++i) {
			if (this.packetExtensions[i] == exten) {
				this.packetExtensions.splice(i,1);
			}
		}
	},
	
	getPacketExtensions: function() {
		return this.packetExtensions;
	},
	
	toXml: function() {
	    var xml = "";
	    xml += "<error code=\"" + this.code + "\"";
	    
	    if (this.type != null) {
	    	xml += " type=\"";
	    	xml += this.type;
	    	xml += "\"";
	    }
	    xml += ">";
	    
	    for (var i = 0; i < this.conditions.length; ++i) {
			xml += "<" + this.conditions[i].element + 
				" xmlns=\"" + this.conditions[i].namespace + "\"/>";
		}
	    
	    if (this.message != null) {
	    	xml += "<text xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\">";
			xml += StringUtils.escapeXml(this.message);
			xml += "</text>";
	    }
	    
	    for (var j = 0; j < this.packetExtensions.length; ++j) {
	            xml += this.packetExtensions[j].toXml();
	    }
	    
		xml += "</error>";
	    return xml;
    }
	
});

// end of XmppError


// start of Packet
Packet = AbstractXmlStanza.extend({
	init: function() {
		this._super();
		this.packetExtensions = new Array();
	},
	
	setTo: function(to) {
		this.to = to;
	},
	
	getTo: function() {
		return this.to
	},
	
    setFrom: function(from) {
    	this.from = from;
    },
    
	getFrom: function() {
    	return this.from;
    },
    
    setLanguage: function(language) {
    	this.language = language;
    },
    
	getLanguage: function() {
    	return this.language;
    },
    
    setXmppError: function(xmppError) {
		this.xmppError = xmppError;
	},
	
	getXmppError: function() {
		return this.xmppError
	},
	
	addPacketExtension: function(extension) {
		this.packetExtensions.push(extension);
	},
	
	removePacketExtension: function(elementName, namespace) {
		for (var i = 0; i < this.packetExtensions.length; ++i) {
			var extension = this.packetExtensions[i];
			if (extension.getElementName() == elementName
				 && extension.getNamespace() == namespace) {
				this.packetExtensions.splice(i,1);
				break;
			}
		}
	},
	
	getPacketExtensions: function() {
		return this.packetExtensions;
	},
	
	getPacketExtension: function(elementName, namespace) {
		for (var i = 0; i < this.packetExtensions.length; ++i) {
			var extension = this.packetExtensions[i];
			if (extension.getElementName() == elementName
				 && extension.getNamespace() == namespace) {
				return extension;
			}
		}
		return null;
	},
	
	getExtensionsXml: function() {
		var xml = "";
		
		for (var i = 0; i < this.packetExtensions.length; ++i) {
			xml += this.packetExtensions[i].toXml();
		}
		return xml;
	}
	
});

// end of Packet

// start of Iq
IqType = {
	GET: 'get',
	SET: 'set',
	ERROR: 'error',
	RESULT: 'result'
}

Iq = Packet.extend({
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
PresenceType = {
	
	AVAILABLE: "available",
	
    UNAVAILABLE: "unavailable",

    SUBSCRIBE: "subscribe",

    SUBSCRIBED: "subscribed",

    UNSUBSCRIBE: "unsubscribe",

    UNSUBSCRIBED: "unsubscribed",

    PROBE: "probe",

    ERROR: "error"
	
}

PresenceShow = {
	
	CHAT: "chat",

    AVAILABLE: "available",

    AWAY: "away",

    XA: "xa",

    DND: "dnd"

}
Presence = Packet.extend({
	init: function(type) {
	    this._super();
	    this.type = type;
	    this.priority = Number.MIN_VALUE;
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
        if (this.priority != Number.MIN_VALUE){
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
MessageType = {
	
	NORMAL: "normal",

	CHAT: "chat",

	GROUPCHAT: "groupchat",

	HEADLINE: "headline",

	ERROR: "error"
	
}

MessageBody = XmlStanza.extend({
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

MessageSubject = XmlStanza.extend({
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

Message = Packet.extend({
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
Auth = XmlStanza.extend({
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
Challenge = XmlStanza.extend({
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
FailureError = {
	aborted: "aborted",
	incorrect_encoding: "incorrect-encoding",
	invalid_authzid: "invalid-authzid",
	invalid_mechanism: "invalid-mechanism",
	mechanism_too_weak: "mechanism-too-weak",
	not_authorized: "not-authorized",
	temporary_auth_failure: "temporary-auth-failure"
}

Failure = XmlStanza.extend({
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
IqBind = PacketExtension.extend({
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

IqRosterAsk = {
	subscribe: "subscribe",
	unsubscribe: "unsubscribe"
}

IqRosterSubscription = {
	none: "none",
	to: "to",
	from: "from",
	both: "both",
	remove: "remove"
}

IqRosterItem = XmlStanza.extend({
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
		if (this.groups != null && group && group != ""){
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


IqRoster = PacketExtension.extend({
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
	
	containRosterItem: function(jid){
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
IqSession = PacketExtension.extend({
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
PrivacyItemSubscription = {
	
	both: "both",
	
	to: "to",
	
	from: "from",
	
	none: "none"
}

PrivacyItemType = {
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


PrivacyItem = PacketExtension.extend({
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
PrivacyList = XmlStanza.extend({
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
Privacy = PacketExtension.extend({
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


// start of Proceed
Proceed = XmlStanza.extend({
	init: function(){
		this._super();
	},
	
	toXml: function(){
		return "<proceed xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>";
	}
});
// end of Proceed


// start of Response
Response = XmlStanza.extend({
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
		xml += "<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"";
		if (this.getContent() != null){
			xml += ">" + this.getContent() + "</response>";
		} else {
			 xml += "/>";
		}

		return xml;
	}
});
// end of Response

// start of StartTls
StartTls = XmlStanza.extend({
	init: function(){
		this._super();
	},
	
	toXml: function(){
		return "<starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>";
	}
});
// end of StartTls


// start of Stream
Stream = AbstractXmlStanza.extend({
	init: function(){
		this._super();
		this.version = "1.0";
	},
	
	getTo: function(){
		return this.to;
	},
	
	setTo: function(to){
		this.to = to;
	},
	
	getFrom: function(){
		return this.from;
	},
	
	setFrom: function(from){
		this.from = from;
	},
	
	getVersion: function(){
		return this.version;
	},
	
	setVersion: function(version){
		this.version = version;
	},
	
	getLang: function(){
		return this.lang;
	},
	
	setLang: function(lang){
		this.lang = lang;
	},
	
	toXml: function(){
		var xml = "";
		xml += "<stream:stream";

		if (this.getStanzaId() != null){
			xml += " id=\"" + this.getStanzaId() + "\"";
		}
		if (this.getTo() != null){
			xml += " to=\"" + this.getTo().toFullJID() + "\"";
		}
		if (this.getFrom() != null){
			xml += " from=\"" + this.getFrom().toFullJID() + "\"";
		}
		if (this.getVersion() != null){
			xml += " version=\"" + this.getVersion() + "\"";
		}
		if (this.getLang() != null){
			xml += " xml:lang=\"" + this.getLang() + "\"";
		}
		
		var streamNs = Stream.JABBER_CLIENT_NAMESPACE;
		var clientNs = Stream.JABBER_CLIENT_NAMESPACE;
		xml += " xmlns:stream=\"" + streamNs + "\" xmlns=\"" + clientNs + "\"";
		xml += ">";
		return xml;
	}
});
	
Stream.JABBER_CLIENT_NAMESPACE = "jabber:client";
Stream.STREAM_NAMESPACE = "http://etherx.jabber.org/streams";

// end of Stream

// start of StreamError
StreamErrorCondition = {
		/**
		 * The entity has sent XML that cannot be processed; this
		 * error MAY be used instead of the more specific
		 * XML-related errors, such as
		 * &lt;bad-namespace-prefix/&gt;, &lt;invalid-xml/&gt;,
		 * &lt;restricted-xml/&gt;, &lt;unsupported-encoding/&gt;,
		 * and &lt;xml-not-well-formed/&gt;, although the more
		 * specific errors are preferred.
		 */
		bad_format: "bad-format",

		/**
		 * The entity has sent a namespace prefix that is
		 * unsupported, or has sent no namespace prefix on an
		 * element that requires such a prefix.
		 */
		bad_namespace_prefix: "bad-namespace-prefix",

		/**
		 * The server is closing the active stream for this entity
		 * because a new stream has been initiated that conflicts
		 * with the existing stream.
		 */
		conflict: "conflict",

		/**
		 * The entity has not generated any traffic over the stream
		 * for some period of time (configurable according to a
		 * local service policy).
		 */
		connection_timeout: "connection-timeout",

		/**
		 * The value of the 'to' attribute provided by the
		 * initiating entity in the stream header corresponds to a
		 * hostname that is no longer hosted by the server.
		 */
		host_gone: "host-gone",

		/**
		 * The value of the 'to' attribute provided by the
		 * initiating entity in the stream header does not
		 * correspond to a hostname that is hosted by the server.
		 */
		host_unknown: "host-unknown",

		/**
		 * A stanza sent between two servers lacks a 'to' or 'from'
		 * attribute (or the attribute has no value).
		 */
		improper_addressing: "improper-addressing",

		/**
		 * The server has experienced a misconfiguration or an
		 * otherwise-undefined internal error that prevents it from
		 * servicing the stream.
		 */
		internal_server_error: "internal-server-error",

		/**
		 * The JID or hostname provided in a 'from' address does not
		 * match an authorized JID or validated domain negotiated
		 * between servers via SASL or dialback, or between a client
		 * and a server via authentication and resource binding.
		 */
		invalid_from: "invalid-from",

		/**
		 * The stream ID or dialback ID is invalid or does not match
		 * an ID previously provided.
		 */
		invalid_id: "invalid-id",

		/**
		 * the streams namespace name is something other than
		 * "http://etherx.jabber.org/streams" or the dialback
		 * namespace name is something other than
		 * "jabber:server:dialback".
		 */
		invalid_namespace: "invalid-namespace",

		/**
		 * The entity has sent invalid XML over the stream to a
		 * server that performs validation.
		 */
		invalid_xml: "invalid-xml",

		/**
		 * The entity has attempted to send data before the stream
		 * has been authenticated, or otherwise is not authorized to
		 * perform an action related to stream negotiation; the
		 * receiving entity MUST NOT process the offending stanza
		 * before sending the stream error.
		 */
		not_authorized: "not-authorized",

		/**
		 * The entity has violated some local service policy; the
		 * server MAY choose to specify the policy in the <text/>
		 * element or an application-specific condition element.
		 */
		policy_violation: "policy-violation",

		/**
		 * The server is unable to properly connect to a remote
		 * entity that is required for authentication or
		 * authorization.
		 */
		remote_connection_failed: "remote-connection-failed",

		/**
		 * The server lacks the system resources necessary to
		 * service the stream.
		 */
		resource_constraint: "resource-constraint",

		/**
		 * The entity has attempted to send restricted XML features
		 * such as a comment, processing instruction, DTD, entity
		 * reference, or unescaped character.
		 */
		restricted_xml: "restricted-xml",

		/**
		 * The server will not provide service to the initiating
		 * entity but is redirecting traffic to another host; the
		 * server SHOULD specify the alternate hostname or IP
		 * address (which MUST be a valid domain identifier) as the
		 * XML character data of the &lt;see-other-host/&gt;
		 * element.
		 */
		see_other_host: "see-other-host",

		/**
		 * The server is being shut down and all active streams are
		 * being closed.
		 */
		system_shutdown: "system-shutdown",

		/**
		 * The error condition is not one of those defined by the
		 * other conditions in this list; this error condition
		 * SHOULD be used only in conjunction with an
		 * application-specific condition.
		 */
		undefined_condition: "undefined-condition",

		/**
		 * The initiating entity has encoded the stream in an
		 * encoding that is not supported by the server.
		 */
		unsupported_encoding: "unsupported-encoding",

		/**
		 * The initiating entity has sent a first-level child of the
		 * stream that is not supported by the server.
		 */
		unsupported_stanza_type: "unsupported-stanza-type",

		/**
		 * the value of the 'version' attribute provided by the
		 * initiating entity in the stream header specifies a
		 * version of XMPP that is not supported by the server; the
		 * server MAY specify the version(s) it supports in the
		 * &lt;text/&gt; element.
		 */
		unsupported_version: "unsupported-version",

		/**
		 * The initiating entity has sent XML that is not
		 * well-formed.
		 */
		xml_not_well_formed: "xml-not-well-formed"

}


StreamErrorAppCondition = XmlStanza.extend({
	init: function(elementName, namespace){
		this._super();
		this.elementName = elementName;
		this.namespace = namespace;
	},
	
	getElementName: function(){
		return this.elementName;
	},
	
	getNamespace: function(){
		return this.namespace;
	},
	
	toXml: function(){
		var xml = "";
		xml += "<" + this.getElementName() + " xmlns=\"" + this.getNamespace() + "\"/>";
		return xml;
	}
});


StreamErrorAppErrorText = XmlStanza.extend({
	init: function(text){
		this._super();
		this.text = text;
	},
	
	getLang: function(){
		return this.lang;
	},
	
	setLang: function(lang){
		this.lang = lang;
	},
	
	getText: function(){
		return this.text;
	},
	
	setText: function(text){
		this.text = text;
	},
	
	toXml: function(){
		var xml = "";
		xml += "<text xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"";
		if (this.getLang() != null){
			xml += " xml:lang=\"" + this.getLang() + "\"";
		}
		xml += ">" + this.getText() + "</text>";
		
		return xml;
	}
});

StreamError = XmlStanza.extend({
	init: function(condition){
		this._super();
		this.condition = condition;
		this.applicationConditions = new Array();
	},

	getCondition: function(){
		return this.condition;
	},
	
	setCondition: function(condition){
		this.condition = condition;
	},
	
	getText: function(){
		return this.text;
	},
	
	setText: function(text){
		this.text = text;
	},
	
	setText: function(textStr, lang){
		this.text = new StreamErrorAppErrorText(textStr);
		if (lang != null){
			this.text.setLang(lang);
		}
	},
	
	addApplicationCondition: function(elementName, namespace){
		var appCondition = new StreamErrorAppCondition(elementName, namespace);
		return this.applicationConditions.push(appCondition);
	},
	
	removeAppCondition: function(elementName, namespace){
		for (var i = 0; i < this.applicationConditions.length; ++i){
				if (this.applicationConditions[i].getElementName() == elementName
					&& this.applicationConditions[i].getNamespace() == namespace){
					this.applicationConditions.splice(i,1);
					break;
				}
		}
	},
	
	getAppConditions: function(){
		return this.applicationConditions;
	},
	
	toXml: function(){
		var xml = "";
		xml += "<stream:error>";
		if (this.getCondition() != null){
			xml += "<" + this.getCondition() + " xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>";
		}
		var text = this.getText();
		if (text != null){
			xml += text.toXml();
		}
		if (this.applicationConditions.length != 0){
			for (var i = 0; i < this.applicationConditions.length; ++i){			
				xml += this.applicationConditions[i].toXml();
			}
		}
		xml += "</stream:error>";
		return xml;
	}
});

// end of StreamError

// end of StreamFeature
StreamFeatureFeature = XmlStanza.extend({
	init: function(elementName, namespace){
		this._super();
		this.elementName = elementName;
		this.namespace = namespace;
	},
	
	getElementName: function(){
		return this.elementName;
	},
	
	getNamespace: function(){
		return this.namespace;
	},
	
	isRequired: function(){
		return this.required;
	},
	
	setRequired: function(required){
		this.required = required;
	},
	
	toXml: function(){
		var xml = "";
		xml += "<" + this.getElementName() + " xmlns=\"" + this.getNamespace() + "\"";
		
		if (this.isRequired()){
			xml += "><required/></" + this.getElementName() + ">";
		} else {
			xml += "/>";
		}
		
		return xml;
	}
});

StreamFeature = XmlStanza.extend({
	init: function(){
		this._super();
		this.mechanisms = new Array();
		this.compressionMethods = new Array();
		this.features = new Array();
	},
	
	addFeature: function(elementName, namespace, required){
		var feature = new StreamFeatureFeature(elementName, namespace);
		feature.setRequired(required);
		this.addStreamFeatureFeature(feature);
	},
	
	addStreamFeatureFeature: function(feature){
		this.features.push(feature);
	},
	
	removeFeature: function(elementName, namespace){
		for (var i = 0; i < this.features.length; ++i){
			if (this.features[i].getElementName() == elementName
				&& this.features[i].getNamespace() == namespace){
				this.features.splice(i,1);
				break;
			}
		}
	},
	
	removeFeature: function(feature){
		this.removeFeature(feature.getElementName(), feature.getNamespace());
	},
	
	getFeatures: function(){
		return this.features;
	},
	
	containFeature: function(elementName, namespace) {
		for (var i = 0; i < this.features.length; ++i){
			if (this.features[i].getElementName() == elementName
				&& this.features[i].getNamespace() == namespace){
				return true;
			}
		}
		return false;
	},
	
	addMechanism: function(mechanism){
		if (mechanism != null){
			this.mechanisms.push(mechanism);
		}
	},
	
	addMechanismCollection: function(mechanisms){
		this.mechanisms = this.mechanisms.concat(mechanisms);
	},
	
	removeMechanism: function(mechanism){
		for (var i = 0; i < this.mechanisms.length; ++i){
			if (this.mechanisms[i] == mechanism){
				this.mechanisms.splice(i,1);
				break;
			}
		}
	},
	
	removeAllMechanism: function(){
		this.mechanisms = new Array();
	},
	
	getMechanisms: function(){
		return this.mechanisms;
	},
	
	getCompressionMethod: function(){
		return this.compressionMethods;
	},
	
	addCompressionMethod: function(compressionMethod){
		if (compressionMethod != null){
			this.compressionMethods.push(compressionMethod);
		}
	},

	
	toXml: function(){
		var xml = "";
		xml += "<stream:features>";
		
		for (var i = 0; i < this.features.length; ++i){
			xml += this.features[i].toXml();
		}
		
		if (this.compressionMethods.length != 0)
		{
			xml += "<compression xmlns=\"http://jabber.org/features/compress\">";
			for (var i = 0; i < this.compressionMethods.length; ++i){
				xml += "<method>" + this.compressionMethods[i] + "</method>";
			}
			xml += "</compression>";
		}
		if (this.mechanisms.length != 0){
			xml += "<mechanisms xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>";
			for (var i = 0; i < this.mechanisms.length; ++i){
				xml += "<mechanism>" + this.mechanisms[i] + "</mechanism>";
			}
			xml += "</mechanisms>";
		}
		
		xml += "</stream:features>";
		return xml;
	}
});

// end of StreamFeature


// start of Success
Success = XmlStanza.extend({
	init: function(successData){
		this._super();
		this.successData = successData;
	},
	
	getSuccessData: function(){
		return this.successData;
	},
	
	setSuccessData: function(successData){
		this.successData = successData;
	},
	
	toXml: function(){
		var xml = "";

		xml += "<success xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"";
		
		if (this.getSuccessData() != null){
			xml += ">" + this.getSuccessData() + "</success>";
		} else {
			xml += "/>";
		}
		
		return xml;
	}
});
// end of Success

// start of Body
Body = XmlStanza.extend({
	init: function() {
	    this._super();
	    this.attributes = {};
	    this.stanzas = new Array();
	},
	
	setAttribute: function(key, value) {
		this.attributes[key] = value;
	},
	
	getAttribute: function(key) {
		return this.attributes[key];
	},
	
	getAttributes: function() {
		return this.attributes;
	},
	
	removeAttribute: function(key) {
		delete this.attributes[key];
	},
	
	removeAllAttributes: function() {
		this.attributes = {};
	},
	
	addStanza: function(stanza) {
        this.stanzas.push(stanza);
    },
    
    removeStanza: function(stanza) {
    	for (var i = 0; i < this.stanzas.length; ++i){
			if (this.stanzas[i] == stanza){
				this.stanzas.splice(i,1);
				break;
			}
		}
    },
    
    getStanzas: function() {
    	return this.stanzas;
    },
    
    toXml: function() {
    	var xml = "";
    	
    	xml += "<body";
    	
    	var containNamespace = false;
    	for ( var key in this.attributes ) {
    		if (key == "xmlns") {
    			containNamespace = true;
    		}
    		var value = this.attributes[key];
    		if (value != null) {
    			xml += " " + key + "=\"" + value + "\"";
    		}
    	}

		if (!containNamespace) {
			xml += " xmlns=\"http://jabber.org/protocol/httpbind\"";
		}
    	
    	if (this.stanzas.length > 0) {
			xml += ">";
    		for (var i = 0; i < this.stanzas.length; ++i){
				xml += this.stanzas[i].toXml();
			}
			
			xml += "</body>";
    	} else {
    		xml += " />";
    	}
    	
    	return xml;
    }
    
});
// end of Body

// start of XmppParser
XmppParser = jClass.extend({
	init: function() {
		this.extensionParsers = new Array();
	},
	
	
	parseStanza: function(bodyElement) {
		
		if ("body" != bodyElement.nodeName) {
			throw new Error("bad xml");
		}
		
		var body = new Body();
		var attributes = bodyElement.attributes;
		for (var i = 0; i < attributes.length; ++i) {
			body.setAttribute(attributes[i].nodeName, attributes[i].nodeValue);
		}
		
		
		var childNodes = bodyElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var packetElement = childNodes[i];
			if (packetElement.nodeType != 1) {
				continue;
			}
			
			var elementName = packetElement.nodeName;
			if ("features" == elementName
				|| "stream:features" == elementName) {
				body.addStanza(this.parseStreamFeature(packetElement));
			} else if ("success" == elementName) {
				body.addStanza(new Success());
			} else if ("iq" == elementName) {
				body.addStanza(this.parseIq(packetElement));
			} else if ("presence" == elementName) {
				body.addStanza(this.parsePresence(packetElement));
			} else if ("message" == elementName) {
				body.addStanza(this.parseMessage(packetElement));
			}
			
			// TODO
		}
		
//				alert(body.toXml());
		return body;
	},
	
	parseMessage: function(messageElement) {
		var to = messageElement.getAttribute("to");
		var from = messageElement.getAttribute("from");
		var lang = messageElement.getAttribute("xml:lang");
		var id = messageElement.getAttribute("id");
		var type = messageElement.getAttribute("type");
		type = (type == null) ? MessageType.NORMAL : type;
		
		var message = new Message(type);
		message.setTo(to == null ? null : JID.createJID(to));
		message.setFrom(from == null ? null : JID.createJID(from));
		message.setLanguage(lang);
		message.setStanzaId(id);
		
		var childNodes = messageElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var messageExtensionElem = childNodes[i];
			if (messageExtensionElem.nodeType != 1) {
				continue;
			}
			var elementName = messageExtensionElem.nodeName;
			var namespace = messageExtensionElem.getAttribute("xmlns");
			if ("body" == elementName) {
				var lang = messageExtensionElem.getAttribute("xml:lang");
				if (lang != null) {
					message.addBody(new MessageBody(lang, messageExtensionElem.firstChild.nodeValue));
				} else {
					message.setBody(messageExtensionElem.firstChild.nodeValue);
				}
				
			} else if ("subject" == elementName) {
				var lang = messageExtensionElem.getAttribute("xml:lang");
				if (lang != null) {
					message.addSubject(new MessageSubject(lang, messageExtensionElem.firstChild.nodeValue));
				} else {
					message.setSubject(messageExtensionElem.firstChild.nodeValue);
				}
			} else if ("thread" == elementName) {
				message.setThread(messageExtensionElem.firstChild == null ? null : messageExtensionElem.firstChild.nodeValue);
			} else if ("error" == elementName) {
				message.setXmppError(this.parseError(messageExtensionElem));
			} else {
				message.addPacketExtension(this.parseExtension(messageExtensionElem));
			}
		}
		
		return message;
	},
	
	parsePresence: function(presenceElement) {
		var to = presenceElement.getAttribute("to");
		var from = presenceElement.getAttribute("from");
		var lang = presenceElement.getAttribute("xml:lang");
		var id = presenceElement.getAttribute("id");
		var type = presenceElement.getAttribute("type");
		type = (type == null) ? PresenceType.AVAILABLE : type;
		
		var presence = new Presence(type);
		presence.setTo(to == null ? null : JID.createJID(to));
		presence.setFrom(from == null ? null : JID.createJID(from));
		presence.setLanguage(lang);
		presence.setStanzaId(id);
		
		var childNodes = presenceElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var presenceExtensionElem = childNodes[i];
			if (presenceExtensionElem.nodeType != 1) {
				continue;
			}
			var elementName = presenceExtensionElem.nodeName;
			var namespace = presenceExtensionElem.getAttribute("xmlns");
			if ("status" == elementName) {
				presence.setUserStatus(presenceExtensionElem.firstChild == null ? null : presenceExtensionElem.firstChild.nodeValue);
			} else if ("show" == elementName) {
				presence.setShow(presenceExtensionElem.firstChild == null ? null : presenceExtensionElem.firstChild.nodeValue);
			} else if ("priority" == elementName) {
				presence.setPriority(presenceExtensionElem.firstChild == null ? null : presenceExtensionElem.firstChild.nodeValue);
			} else if ("error" == elementName) {
				presence.setXmppError(this.parseError(presenceExtensionElem));
			} else {
				presence.addPacketExtension(this.parseExtension(presenceExtensionElem));
			}
		}
		
		return presence;
	},
	
	parseExtension: function(extensionElem) {
		var elementName = extensionElem.nodeName;
		var namespace = extensionElem.getAttribute("xmlns");
		var extensionParser = this.getExtensionParser(elementName, namespace);
		if (extensionParser != null) {
			//TOO 
			if (window.console) {
				window.console.log("get [" + elementName + " " + namespace + "]ExtensionParser");
			}
			
			var packetExtension = extensionParser.parseExtension(this, extensionElem);
			
			//TOO 
//			if (window.console) {
//				window.console.log("ExtensionParser parse extension complete:" + packetExtension.toXml());
//			}
			return packetExtension;
			
		} else {
			//TOO 
			if (window.console) {
				window.console.log("can not get [" + elementName + " " + namespace + "]ExtensionParser");
			}
			
			var unknownExtension = new UnknownExtension(extensionElem);
			
			//TOO 
//			if (window.console) {
//				window.console.log("parseUnknownExtension complete:" + unknownExtension.toXml());
//			}

			return unknownExtension;
		}
	},
	
	parseIq: function(iqElement)  {
		var to = iqElement.getAttribute("to");
		var from = iqElement.getAttribute("from");
		var lang = iqElement.getAttribute("xml:lang");
		var id = iqElement.getAttribute("id");
		var type = iqElement.getAttribute("type");
		
		var iq = new Iq(type);
		iq.setTo(to == null ? null : JID.createJID(to));
		iq.setFrom(from == null ? null : JID.createJID(from));
		iq.setLanguage(lang);
		iq.setStanzaId(id);
		
		var childNodes = iqElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var iqExtensionElem = childNodes[i];
			if (iqExtensionElem.nodeType != 1) {
				continue;
			}
			var elementName = iqExtensionElem.nodeName;
			var namespace = iqExtensionElem.getAttribute("xmlns");
			if (IqBind.ELEMENTNAME == elementName) {
				iq.addPacketExtension(this.parseIqBind(iqExtensionElem));
			} else if (IqSession.ELEMENTNAME == elementName) {
				iq.addPacketExtension(new IqSession());
			} else if (IqRoster.ELEMENTNAME == elementName
						&& IqRoster.NAMESPACE == namespace) {
				iq.addPacketExtension(this.parseIqRoster(iqExtensionElem));
			} else if ("error" == elementName) {
				iq.setXmppError(this.parseError(iqExtensionElem));
			} else {
				iq.addPacketExtension(this.parseExtension(iqExtensionElem));
			}
		}
		return iq;
	},
	
	parseIqRoster: function(iqRosterElement) {
		var iqRoster = new IqRoster();
		var childNodes = iqRosterElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var rosterItemElem = childNodes[i];
			var elementName = rosterItemElem.nodeName;
			if ("item" == elementName) {
				iqRoster.addRosterItem(this.parseIqRosterItem(rosterItemElem));
			}
		}
		return iqRoster;
	},
	
	parseIqRosterItem: function(rosterItemElem) {
		var jidStr = rosterItemElem.getAttribute("jid");
		var nickname = rosterItemElem.getAttribute("name");
		var subscription = rosterItemElem.getAttribute("subscription");
		var ask = rosterItemElem.getAttribute("ask");
		var iqRosterItem = new IqRosterItem(JID.createJID(jidStr), nickname);
		iqRosterItem.setSubscription(subscription);
		iqRosterItem.setAsk(ask);
		
		var childNodes = rosterItemElem.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var childElem = childNodes[i];
			var elementName = childElem.nodeName;
			if ("group" == elementName) {
				if (childElem.firstChild != null ) {
					iqRosterItem.addGroup(childElem.firstChild.nodeValue);
				}
				
			}
		}

		return iqRosterItem;
	},
	
	parseIqBind: function(iqBindElement) {
		var iqBind = new IqBind();
		var childNodes = iqBindElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var bindChild = childNodes[i];
			var elementName = bindChild.nodeName;
			if ("resource" == elementName) {
				iqBind.setResource(bindChild.firstChild == null ? null : bindChild.firstChild.nodeValue);
			} else if ("jid" == elementName) {
				iqBind.setJid(JID.createJID(bindChild.firstChild == null ? null : bindChild.firstChild.nodeValue));
			}
		}
		return iqBind;
	},
	
	parseStreamFeature: function(streamFeatureElement) {
		var streamFeature = new StreamFeature();
		var childNodes = streamFeatureElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var featureElement = childNodes[i];
			var elementName = featureElement.nodeName;
			if ("mechanisms" == elementName) {
				var mechanisms = featureElement.childNodes;
				for (var j = 0; j < mechanisms.length; ++j) {
					streamFeature.addMechanism(mechanisms[j].firstChild.nodeValue);
				}
			} else if ("compression" == elementName) {
				var methods = featureElement.childNodes;
				for (var j = 0; j < methods.length; ++j) {
					streamFeature.addCompressionMethod(methods[j].firstChild.nodeValue);
				}
			} else {
				var namespace = featureElement.getAttribute("xmlns");
				var streamFeatureFeature = new StreamFeatureFeature(elementName, namespace);
				if (featureElement.firstChild != null 
					&& "required" == featureElement.firstChild.nodeName) {
					streamFeatureFeature.setRequired(true);
				}
				streamFeature.addStreamFeatureFeature(streamFeatureFeature);
			}
		}
		
		return streamFeature;
	},
	
	parseError: function(errorElement) {
		var code = errorElement.getAttribute("code");
		var type = errorElement.getAttribute("type");
		
		var error = new XmppError(code, type);
		
		var childNodes = errorElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var errorConditionElem = childNodes[i];
			var elementName = errorConditionElem.nodeName;
			var namespace = errorConditionElem.getAttribute("xmlns");
			if ("text" == elementName) {
				error.setMessage(errorConditionElem.firstChild.nodeValue);
			} else {
				error.addCondition(elementName, namespace);
			}
		}
		return error;
	},
	
	addExtensionParser: function(extensionParser) {
		this.extensionParsers.push(extensionParser);
	},
	
	removeExtensionParser: function(extensionParser) {
		for (var i = 0; i < this.extensionParsers.length; ++i){
			if (this.extensionParsers[i] == extensionParser){
				this.extensionParsers.splice(i,1);
			}
		}
	},
	
	getExtensionParser: function(elementName, namespace) {
		for (var i = 0; i < this.extensionParsers.length; ++i){
			if (this.extensionParsers[i].getElementName() == elementName
				&& this.extensionParsers[i].getNamespace() == namespace){
				return this.extensionParsers[i];
			}
		}
		return null;
	}
});

XmppParser.getInstance = function() {
	if (XmppParser.instance == null) {
		XmppParser.instance = new XmppParser();
	}
	return XmppParser.instance;
}

XmppParser.ExtensionParser = jClass.extend({
	init: function() {
	},
	
	getElementName: function() {
		
	},
	
	getNamespace: function() {
		
	},
	
	parseExtension: function(xmppParser, xmlElement) {
		
	}
});

// end of XmppParser

// start of StanzaFilter
StanzaFilter = jClass.extend({
	init: function() {
	},
	
	accept: function(connection, stanza) {
		return false;
	}
	
});

StanzaTypeFilter = StanzaFilter.extend({
	init: function(stanzaClass) {
		this.stanzaClass = stanzaClass;
	},
	
	accept: function(connection, stanza) {
		return stanza instanceof stanzaClass;
	}
	
});

PacketIdFilter = StanzaFilter.extend({
	init: function(packetId) {
		this.packetId = packetId;
	},
	
	accept: function(connection, stanza) {
		return (stanza instanceof Packet)
					&& (stanza.getStanzaId() == this.packetId);
	}
	
});

OrFilter = StanzaFilter.extend({
	init: function(newfilters) {
		this.filters = new Array();
		for (vari = 0; i < newfilters.length; ++i){
			this.filters.push(newfilters[i]);
		}
	},
	
	accept: function(connection, stanza) {
		for (vari = 0; i < newfilters.length; ++i){
			if (this.filters[i].accept(connection, stanza)) {
				return true;
			}
		}
	}
	
});

// end of StanzaFilter


// start of XmppConnectionMgr
XmppConnectionMgr = jClass.extend({
	init: function() {
		this.connections = new Array();
		this.bodyHandlers = new Array();
		this.listeners = new Array();
		this.bodyMessagQueue = new Array();
		this.requestingCount = 0;
		this.connectionErrorCount = 0;
		this.keyGenerater = new KeyGenerater();
		this.working = false;
		this.hold = 1;
	},
	
	isWorking: function(){
		return this.working;
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
		
		if (clearBody === true) {
			while (aThis.bodyMessagQueue.length != 0) {
				if (aThis.requestingCount >= aThis.hold + 1) {
					continue;
				}
				var bodyMessage = aThis.bodyMessagQueue.shift();
				aThis.execAjaxRequest(bodyMessage);
			}
			return;
		}
		
		if (aThis.requestingCount >= aThis.hold + 1) {
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
			contentType: "application/x-www-form-urlencoded; charset=UTF-8",
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
					aThis.removeAllConnections();
					var event = {
						eventType: ConnectionEventType.Error,
						when: TimeUtils.currentTimeMillis(),
						error: errorThrown
					}
					aThis.fireConnectionEvent(event);
					aThis.working = false;
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
			this.intervalId = setInterval(this.processRequest, Christy.intervalTime);
			this.working = true;
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
			if (this.bodyHandlers[i].rid == body.getAttribute("ack")){
				this.bodyHandlers[i].handler(body);
				this.bodyHandlers.splice(i,1);
			}
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
	
	removeAllConnections: function(){
		for (var i = 0; i < this.connections.length; ++i){
			var conn = this.connections[i];
			conn.closed = true;
			conn.authenticated = false;
			conn.sessionBinded = false;
			conn.resourceBinded = false;
			
			this.connections.splice(i,1);
			var event = {
				eventType: ConnectionEventType.ConnectionClosed,
				when: TimeUtils.currentTimeMillis(),
				connection: conn
			}
			this.fireConnectionEvent(event);
		}
		
		if (this.connections.length == 0) {
			clearInterval(this.intervalId);
			this.processRequest(true);
			this.intervalId = null;
			this.streamId = null;
			this.working = false;
		}
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
			this.streamId = null;
			this.working = false;
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
	
	addConnectionListener: function(eventTypes, listenerFunc) {
		// wrapper
		var types = eventTypes
		if (!isArray(eventTypes)) {
			types = [eventTypes];
		}
		this.listeners.push({
			eventTypes: types,
			handler: listenerFunc
		})
	},
	
	removeConnectionListener: function(listenerFunc) {
		for (var i = 0; i < this.listeners.length; ++i){
			if (this.listeners[i].handler == listenerFunc){
				this.listeners.splice(i,1);
			}
		}
	},
	
	getConnectionListener: function() {
		return this.listeners;
	},
	
	fireConnectionEvent: function(event) {
		for (var i = 0; i < this.listeners.length; ++i){
			if (this.listeners[i].eventTypes.contains(event.eventType)){
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

	ConnectionClosed: "ConnectionClosed",
	
	Created: "Created",
	
	StreamError: "StreamError",
	
	Error: "Error",
	
	Timeout: "Timeout",
	
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
	
	ChatRemoved: "ChatRemoved",
	
	MessageReceived: "MessageReceived",
	
	ChatResourceChanged: "ChatResourceChanged"
	
}		

XmppConnection = jClass.extend({

	init: function(domain) {
		this.domain = domain;
		this.allowedMechanisms = new Array();
		this.handlers = new Array();
		this.contacts = new Array();
		this.otherResources = new Array();
		this.chats = new Array();
		this.oldPresence = null;
		this.currentPresence = null;
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
			timeout: Christy.loginTimeout,
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
			},
			timeoutHandler: function() {
				var event = {
					eventType: ConnectionEventType.BindResourceFailed,
					when: TimeUtils.currentTimeMillis(),
					connection: connectionThis,
					reason: "timeout"
				}
				var connectionMgr = XmppConnectionMgr.getInstance();
				connectionMgr.fireConnectionEvent(event);
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
			timeout: Christy.loginTimeout,
			handler: function(iqResponse) {
				
				var eventType = ConnectionEventType.BindSessionFailed;
				if (iqResponse.getType() == IqType.RESULT) {
					eventType = ConnectionEventType.SessionBinded;
					this.sessionBinded = true;
//					connectionThis.queryRoster();
//					if (connectionThis.initPresence) {
//						connectionThis.sendStanza(connectionThis.initPresence);
//					}
				}
				var event = {
						eventType: eventType,
						when: TimeUtils.currentTimeMillis(),
						connection: connectionThis,
						stanza: iqResponse
				}
				connectionMgr.fireConnectionEvent(event);
			},
			
			timeoutHandler: function() {
				var event = {
					eventType: ConnectionEventType.BindSessionFailed,
					when: TimeUtils.currentTimeMillis(),
					connection: connectionThis,
					reason: "timeout"
				}
				var connectionMgr = XmppConnectionMgr.getInstance();
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
	
	changeStatus: function(presence) {
		this.oldPresence = this.currentPresence;
		this.currentPresence = presence;
		this.sendStanza(presence);
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
	
	getChat: function(jid, create) {
		for (var i =  0; i < this.chats.length; ++i) {
			if (this.chats[i].bareJID.equalsWithBareJid(jid)) {
				return this.chats[i];
			}
		}
		
		if (create) {
			return this.createChat(jid, null);
		}
		return null;
	},
	
	removeChat: function(jid) {
		for (var i =  0; i < this.chats.length; ++i) {
			if (this.chats[i].bareJID.equalsWithBareJid(jid)) {
				var chat = this.chats[i];
				this.chats.splice(i,1);
				
				var chatRemoved = ConnectionEventType.ChatRemoved;
				var event = {
						eventType: chatRemoved,
						when: TimeUtils.currentTimeMillis(),
						connection: this,
						chat: chat
				}
				var connectionMgr = XmppConnectionMgr.getInstance();
				connectionMgr.fireConnectionEvent(event);
			}
		}
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
			/**
			 * Resource {
			 * 	resource: resource
			 * 	oldPresence: oldPresence
			 * 	currentPresence: currentPresence
			 * }
			 */
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
		if (handler.timeout && handler.timeoutHandler) {
			var aThis = this;
			setTimeout(function(){
				for (var i =  0; i < aThis.handlers.length; ++i) {
					if (aThis.handlers[i] == handler) {
						aThis.handlers.splice(i,1);
						handler.timeoutHandler();
						return;
					}
				}
			}, handler.timeout);
		}
		
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
		return this.rosterItem.getRosterName();
	},
	
	getShowName: function() {
		var showName = this.getNickname();
		if (showName == null) {
			showName = this.getBareJid().toBareJID();
		}
		return showName;
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


// end of XmppConnectionMgr


// start of IqVCard
IqVCard = PacketExtension.extend({
	init: function(){
		this._super();
		this.workPhones = new Array();
		this.workAddress = new Array();
		this.homePhones = new Array();
		this.homeAddress = new Array();
	},
	
	getElementName: function(){
		return IqVCard.ELEMENTNAME;
	},
	
	getNamespace: function(){
		return IqVCard.NAMESPACE;
	},
	
	/**
	 * @return the organizationName
	 */
	getOrganizationName: function() {
		return this.organizationName;
	},

	/**
	 * @param organizationName
	 *                  the organizationName to set
	 */
	setOrganizationName: function(organizationName) {
		this.organizationName = organizationName;
	},

	/**
	 * @return the organizationUnit
	 */
	getOrganizationUnit: function() {
		return this.organizationUnit;
	},

	/**
	 * @param organizationUnit
	 *                  the organizationUnit to set
	 */
	setOrganizationUnit: function(organizationUnit) {
		this.organizationUnit = organizationUnit;
	},

	/**
	 * @return the title
	 */
	getTitle: function() {
		return this.title;
	},

	/**
	 * @param title
	 *                  the title to set
	 */
	setTitle: function(title) {
		this.title = title;
	},

	/**
	 * @return the role
	 */
	getRole: function() {
		return this.role;
	},

	/**
	 * @param role
	 *                  the role to set
	 */
	setRole: function(role) {
		this.role = role;
	},

	/**
	 * @return the familyName
	 */
	getFamilyName: function() {
		return this.familyName;
	},

	/**
	 * @param familyName
	 *                  the familyName to set
	 */
	setFamilyName: function(familyName) {
		this.familyName = familyName;
	},

	/**
	 * @return the givenName
	 */
	getGivenName: function() {
		return this.givenName;
	},

	/**
	 * @param givenName
	 *                  the givenName to set
	 */
	setGivenName: function(givenName) {
		this.givenName = givenName;
	},

	/**
	 * @return the middleName
	 */
	getMiddleName: function() {
		return this.middleName;
	},

	/**
	 * @param middleName
	 *                  the middleName to set
	 */
	setMiddleName: function(middleName) {
		this.middleName = middleName;
	},

	setFullName: function(fullName) {
		this.fullName = fullName;
	},

	getFullName: function() {
		return this.fullName;
	},

	/**
	 * @return the nickName
	 */
	getNickName: function() {
		return this.nickName;
	},

	/**
	 * @param nickName
	 *                  the nickName to set
	 */
	setNickName: function(nickName) {
		this.nickName = nickName;
	},

	/**
	 * @return the url
	 */
	getUrl: function() {
		return this.url;
	},

	/**
	 * @param url
	 *                  the url to set
	 */
	setUrl: function(url) {
		this.url = url;
	},

	/**
	 * @return the birthday
	 */
	getBirthday: function() {
		return this.birthday;
	},

	/**
	 * @param birthday
	 *                  the birthday to set
	 */
	setBirthday: function(birthday) {
		this.birthday = birthday;
	},

	/**
	 * Set work phone number
	 * 
	 * @param phoneType
	 *                  one of VOICE, FAX, PAGER, MSG, CELL, VIDEO, BBS,
	 *                  MODEM, ISDN, PCS, PREF
	 * @param phoneNum
	 *                  phone number
	 */
	setWorkPhone: function(phoneType, phoneNum) {
		this.workPhones.push({phoneType: phoneType, phoneNum: phoneNum});
	},

	/**
	 * Get work phone number
	 * 
	 * @param phoneType
	 *                  one of VOICE, FAX, PAGER, MSG, CELL, VIDEO, BBS,
	 *                  MODEM, ISDN, PCS, PREF
	 */
	getWorkPhone: function(phoneType) {
		for (var i =  0; i < this.workPhones.length; ++i){
			var workPhone = this.workPhones[i];
			if (workPhone.phoneType == phoneType){
				return workPhone.phoneNum;
			}
		}
		return null;
	},

	/**
	 * Get work address field
	 * 
	 * @param addrField
	 *                  one of POSTAL, PARCEL, (DOM | INTL), PREF, POBOX,
	 *                  EXTADR, STREET, LOCALITY, REGION, PCODE, CTRY
	 */
	getWorkAddress: function(addrField) {
		for (var i =  0; i < this.workAddress.length; ++i){
			var workAddr = this.workAddress[i];
			if (workAddr.addrField == addrField){
				return workAddr.value;
			}
		}
		return null;
	},

	/**
	 * Set work address field
	 * 
	 * @param addrField
	 *                  one of POSTAL, PARCEL, (DOM | INTL), PREF, POBOX,
	 *                  EXTADR, STREET, LOCALITY, REGION, PCODE, CTRY
	 */
	setWorkAddress: function(addrField, value) {
		this.workAddress.push({addrField: addrField, value: value});
	},

	/**
	 * Set home phone number
	 * 
	 * @param phoneType
	 *                  one of VOICE, FAX, PAGER, MSG, CELL, VIDEO, BBS,
	 *                  MODEM, ISDN, PCS, PREF
	 * @param phoneNum
	 *                  phone number
	 */
	setHomePhone: function(phoneType, phoneNum) {
		this.homePhones.push({phoneType: phoneType, phoneNum: phoneNum});
	},

	/**
	 * Get home phone number
	 * 
	 * @param phoneType
	 *                  one of VOICE, FAX, PAGER, MSG, CELL, VIDEO, BBS,
	 *                  MODEM, ISDN, PCS, PREF
	 */
	getHomePhone: function(phoneType) {
		for (var i =  0; i < this.homePhones.length; ++i){
			var homePhone = this.homePhones[i];
			if (homePhone.phoneType == phoneType){
				return homePhone.phoneNum;
			}
		}
		return null;
	},

	/**
	 * Get home address field
	 * 
	 * @param addrField
	 *                  one of POSTAL, PARCEL, (DOM | INTL), PREF, POBOX,
	 *                  EXTADR, STREET, LOCALITY, REGION, PCODE, CTRY
	 */
	getHomeAddress: function(addrField) {
		for (var i =  0; i < this.homeAddress.length; ++i){
			var homeAddr = this.homeAddress[i];
			if (homeAddr.addrField == addrField){
				return homeAddr.value;
			}
		}
		return null;
	},

	/**
	 * Set home address field
	 * 
	 * @param addrField
	 *                  one of POSTAL, PARCEL, (DOM | INTL), PREF, POBOX,
	 *                  EXTADR, STREET, LOCALITY, REGION, PCODE, CTRY
	 */
	setHomeAddress: function(addrField, value) {
		this.homeAddress.push({addrField: addrField, value: value});
	},

	/**
	 * @return the email
	 */
	getEmail: function() {
		return this.email;
	},

	/**
	 * @param email
	 *                  the email to set
	 */
	setEmail: function(email) {
		this.email = email;
	},

	/**
	 * @return the jabberID
	 */
	getJabberID: function() {
		return this.jabberID;
	},

	/**
	 * @param jabberID
	 *                  the jabberID to set
	 */
	setJabberID: function(jabberID) {
		this.jabberID = jabberID;
	},

	/**
	 * @return the description
	 */
	getDescription: function() {
		return this.description;
	},

	/**
	 * @param description
	 *                  the description to set
	 */
	setDescription: function(description) {
		this.description = description;
	},

	/**
	 * @return the photoType
	 */
	getPhotoType: function() {
		return this.photoType;
	},

	/**
	 * @param photoType
	 *                  the photoType to set
	 */
	setPhotoType: function(photoType) {
		this.photoType = photoType;
	},

	/**
	 * @return the photoBinval
	 */
	getPhotoBinval: function() {
		return this.photoBinval;
	},

	/**
	 * @param photoBinval
	 *                  the photoBinval to set
	 */
	setPhotoBinval: function(photoBinval) {
		this.photoBinval = photoBinval;
	},

	hasPhoto: function() {
		return this.getPhotoType() != null
				 || this.getPhotoBinval() != null;
	},

	hasOrg: function() {
		return this.getOrganizationName() != null
				 || this.getOrganizationUnit() != null;
	},

	hasNameField: function() {
		return this.familyName != null
				 || this.givenName != null 
				 || this.middleName != null;
	},

	
	toXml: function() {
		var xml = "";
		xml += ("<" + this.getElementName() + " " + "xmlns=\"" + this.getNamespace() + "\">");
		if (this.getFullName() != null) {
			xml += "<FN>" + this.getFullName() + "</FN>";
		}
		if (this.hasNameField()) {
			xml += "<N>";
			if (this.getFamilyName() != null) {
				xml += "<FAMILY>" + StringUtils.escapeXml(this.getFamilyName()) + "</FAMILY>";
			}
			if (this.getGivenName() != null) {
				xml += "<GIVEN>" + StringUtils.escapeXml(this.getGivenName()) + "</GIVEN>";
			}
			if (this.getMiddleName() != null) {
				xml += "<MIDDLE>" + StringUtils.escapeXml(this.getMiddleName()) + "</MIDDLE>";
			}
			xml += "</N>";
		}
		if (this.getNickName() != null) {
			xml += "<NICKNAME>" + StringUtils.escapeXml(this.getNickName()) + "</NICKNAME>";
		}
		if (this.getUrl() != null) {
			xml += "<URL>" + this.getUrl() + "</URL>";
		}
		if (this.getBirthday() != null) {
			xml += "<BDAY>" + this.getBirthday() + "</BDAY>";
		}
		if (this.hasOrg()) {
			xml += "<ORG>";
			if (this.getOrganizationName() != null) {
				xml += "<ORGNAME>" + StringUtils.escapeXml(this.getOrganizationName()) + "</ORGNAME>";
			}
			if (this.getOrganizationUnit() != null) {
				xml += "<ORGUNIT>" + StringUtils.escapeXml(this.getOrganizationUnit()) + "</ORGUNIT>";
			}
			xml += "</ORG>";
		}
		if (this.getTitle() != null) {
			xml += "<TITLE>" + this.getTitle() + "</TITLE>";
		}
		if (this.getRole() != null) {
			xml += "<ROLE>" + this.getRole() + "</ROLE>";
		}
		if (this.workPhones.length != 0) {
			for (var i =  0; i < this.workPhones.length; ++i){
				var workPhone = this.workPhones[i];
				xml += " <TEL>" + "<WORK/><" + workPhone.phoneType + "/><NUMBER>" + workPhone.phoneNum + "</NUMBER></TEL>";
			}
			
		}
		if (this.workAddress.length != 0) {
			xml += "<ADR><WORD/>";
			
			for (var i =  0; i < this.workAddress.length; ++i){
				var workAddr = this.workAddress[i];
				xml += "<" + workAddr.addrField + ">" + StringUtils.escapeXml(workAddr.value) + "</" + workAddr.addrField + ">";
			}
			xml += "</ADR>";
		}
		
		
		if (this.homePhones.length != 0) {
			for (var i =  0; i < this.homePhones.length; ++i) {
				var homePhone = this.homePhones[i];
				xml += " <TEL>" + "<HOME/><" + homePhone.phoneType + "/><NUMBER>" + homePhone.phoneNum + "</NUMBER></TEL>";
			}
		}
		if (this.homeAddress.length != 0) {
			
			xml += "<ADR><HOME/>";
			
			for (var i =  0; i < this.homeAddress.length; ++i) {
				var homeAddr = this.homeAddress[i];
				xml += "<" + homeAddr.addrField + ">" + StringUtils.escapeXml(homeAddr.value) + "</" + homeAddr.addrField + ">";
			}
			xml += "</ADR>";
		}
		
		if (this.getEmail() != null) {
			xml += "<EMAIL><INTERNET/><PREF/><USERID>" + this.getEmail() + "</USERID></EMAIL>";
		}
		if (this.getJabberID() != null) {
			xml += "<JABBERID>" + this.getJabberID() + "</JABBERID>";
		}
		if (this.getDescription() != null) {
			xml += "<DESC>" + StringUtils.escapeXml(this.getDescription()) + "</DESC>";
		}
		if (this.hasPhoto()) {
			xml += "<PHOTO>";
			if (this.getPhotoType() != null) {
				xml += "<TYPE>" + StringUtils.escapeXml(this.getPhotoType()) + "</TYPE>";
			}
			if (this.getPhotoBinval() != null) {
				xml += "<BINVAL>" + StringUtils.escapeXml(this.getPhotoBinval()) + "</BINVAL>";
			}
			xml += "</PHOTO>";

		}

		xml += "</vCard>";

		return xml;
	}
});
IqVCard.ELEMENTNAME = "vCard";
IqVCard.NAMESPACE = "vcard-temp";

// end of IqVCard


// start of VCardExtensionParser

VCardExtensionParser = XmppParser.ExtensionParser.extend({
	init: function() {
		this._super();
	},
	
	getElementName: function() {
		return VCardExtensionParser.ELEMENTNAME
	},
	
	getNamespace: function() {
		return VCardExtensionParser.NAMESPACE;
	},
	
	parseExtension: function(xmppParser, xmlElement) {
		var vCard = new IqVCard();
		var childNodes = xmlElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var childEle = childNodes[i];
			// ELEMENT_NODE
			if (childEle.nodeType == 1) {
				var elementName = childEle.nodeName;
			
				if ("FN" == elementName) {
					vCard.setFullName(childEle.firstChild.nodeValue);
				} else if ("N" == elementName) {
					this.parseN(vCard, childEle);
				} else if ("NICKNAME" == elementName) {
					vCard.setNickName(childEle.firstChild.nodeValue);
				} else if ("URL" == elementName) {
					vCard.setUrl(childEle.firstChild.nodeValue);
				} else if ("BDAY" == elementName) {
					vCard.setBirthday(childEle.firstChild.nodeValue);
				} else if ("ORG" == elementName) {
					this.parseORG(vCard, childEle);
				} else if ("TITLE" == elementName) {
					vCard.setTitle(childEle.firstChild.nodeValue);
				} else if ("ROLE" == elementName) {
					vCard.setRole(childEle.firstChild.nodeValue);
				} else if ("TEL" == elementName) {
					this.parseTEL(vCard, childEle);
				} else if ("ADR" == elementName) {
					this.parseADR(vCard, childEle);
				} else if ("EMAIL" == elementName) {
					this.parseEMAIL(vCard, childEle);
				} else if ("JABBERID" == elementName) {
					vCard.setJabberID(childEle.firstChild.nodeValue);
				} else if ("DESC" == elementName) {
					vCard.setDescription(childEle.firstChild.nodeValue);
				} else if ("PHOTO" == elementName) {
					this.parsePHOTO(vCard, childEle);
				}
			}
			
		}
		
		return vCard;
	},
	
	parseN: function(card, childEle) {
		for (var i = 0; i < childEle.length; ++i) {
			var childEle2 = childEle[i];
			if (childEle2.nodeType == 1) {
				var elementName = childEle2.nodeName;
				if ("FAMILY" == elementName) {
					card.setFamilyName(childEle2.firstChild.nodeValue);
				} else if ("GIVEN" == elementName) {
					card.setGivenName(childEle2.firstChild.nodeValue);
				} else if ("MIDDLE" == elementName) {
					card.setMiddleName(childEle2.firstChild.nodeValue);
				}
			}
		}
	},
	
	parseORG: function(card, childEle) {
		var childNodes = childEle.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var childEle2 = childNodes[i];
			if (childEle2.nodeType == 1) {
				var elementName = childEle2.nodeName;
				if ("ORGNAME" == elementName) {
					card.setOrganizationName(childEle2.firstChild.nodeValue);
				} else if ("ORGUNIT".equals(elementName)) {
					card.setOrganizationUnit(childEle2.firstChild.nodeValue);
				}
				
			}
		}
	},
	
	parseTEL: function(card, childEle) {
		var phoneType = null;
		var childNodes = childEle.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var childEle2 = childNodes[i];
			if (childEle2.nodeType == 1) {
				var elementName = childEle2.nodeName;
				if ("WORK" == elementName) {
					work = true;
					var phoneTypeEle = childEle2.nextSibling;
					phoneType = phoneTypeEle.nodeName;
				}
				else if ("NUMBER" == elementName) {
					if (phoneType != null) {
						if (work) {
							card.setWorkPhone(phoneType, childEle2.firstChild.nodeValue);
						} else {
							card.setHomePhone(phoneType, childEle2.firstChild.nodeValue);
						}
					}
				}
			}
		}
	},
	
	parseADR: function(card, childEle) {
		var childNodes = childEle.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var childEle2 = childNodes[i];
			if (childEle2.nodeType == 1) {
				var elementName = childEle2.nodeName;
				if ("WORK" == elementName) {
					work = true;
				} else {
					if (work) {
						card.setWorkAddress(elementName, childEle2.firstChild.nodeValue);
					} else {
						card.setHomeAddress(elementName, childEle2.firstChild.nodeValue);
					}
				}
			}
		}
		
	},
	
	parseEMAIL: function(card, childEle) {
		var childNodes = childEle.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var childEle2 = childNodes[i];
			if (childEle2.nodeType == 1) {
				var elementName = childEle2.nodeName;
				if ("USERID" == elementName) {
					card.setEmail(childEle2.firstChild.nodeValue);
				}
			}
		}
	},
	
	parsePHOTO: function(card, childEle) {
		var childNodes = childEle.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var childEle2 = childNodes[i];
			if (childEle2.nodeType == 1) {
				var elementName = childEle2.nodeName;
				if ("TYPE" == elementName) {
					card.setPhotoType(childEle2.firstChild.nodeValue);
				} else if ("BINVAL" == elementName) {
					// Firefox XML node 4k limit,
					var s = "";
					for(var j = 0; j < childEle2.childNodes.length; ++j) {
						s += new String(childEle2.childNodes.item(j).nodeValue);
					} 
					card.setPhotoBinval(s);
				}
			}
		}
		
	}
});

VCardExtensionParser.ELEMENTNAME = "vCard";
VCardExtensionParser.NAMESPACE = "vcard-temp";

// end of VCardExtensionParser

var parser = XmppParser.getInstance();
parser.addExtensionParser(new VCardExtensionParser());



// start of IqPrivateXml
IqPrivateXml = PacketExtension.extend({
	init: function(){
	},
	
	getElementName: function(){
		return IqPrivateXml.ELEMENTNAME;
	},
	
	getNamespace: function(){
		return IqPrivateXml.NAMESPACE;
	},
	
	/**
	 * @return the unknownPacketExtension
	 */
	getPacketExtension: function() {
		return this.packetExtension;
	},

	setPacketExtension: function(packetExtension) {
		this.packetExtension = packetExtension;
	},
	
	toXml: function() {
		var xml = "";
		xml += ("<" + this.getElementName() + " " + "xmlns=\"" + this.getNamespace() + "\"");
		if (this.packetExtension != null) {
			xml += ">";
			xml += this.packetExtension.toXml();
			xml += "</" + this.getElementName() + ">";
		} else {
			xml += "/>";
		}
		return xml;
	}
});

IqPrivateXml.ELEMENTNAME = "query";
IqPrivateXml.NAMESPACE = "jabber:iq:private";

// end of IqPrivateXml


// start of IqPrivateXmlParser

IqPrivateXmlParser = XmppParser.ExtensionParser.extend({
	init: function() {
	},
	
	getElementName: function() {
		return IqPrivateXmlParser.ELEMENTNAME
	},
	
	getNamespace: function() {
		return IqPrivateXmlParser.NAMESPACE;
	},
	
	parseExtension: function(xmppParser, xmlElement) {
		var privateXml = new IqPrivateXml();
		var childNodes = xmlElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var childEle = childNodes[i];
			// ELEMENT_NODE
			if (childEle.nodeType == 1) {
				var elementName = childEle.nodeName;
				var namespace = childEle.getAttribute("xmlns");
				var extensionParser = xmppParser.getExtensionParser(elementName, namespace);
				var packetExtension = null;
				if (extensionParser != null) {
					packetExtension = extensionParser.parseExtension(xmppParser, childEle);					
				} else {
					packetExtension = new UnknownExtension(childEle);
				}
				privateXml.setPacketExtension(packetExtension);
			}
			
		}
		
		return privateXml;
	}
});

IqPrivateXmlParser.ELEMENTNAME = "query";
IqPrivateXmlParser.NAMESPACE = "jabber:iq:private";
// end of IqPrivateXmlParser

var parser = XmppParser.getInstance();
parser.addExtensionParser(new IqPrivateXmlParser());




// start of ResultSetExtension
ResultSetExtension = PacketExtension.extend({
	init: function() {
	},
	
	getFirst: function() {
		return this.first;
	},
	
	getFirstIndex: function() {
		return this.firstIndex;
	},
	
	getLast: function() {
		return this.last;
	},
	
	getAfter: function() {
		return this.after;
	},
	
	getMax: function() {
		return this.max;
	},
	
	getCount: function() {
		return this.count;
	},

	setFirst: function(first) {
		this.first = first;
	},

	setFirstIndex: function(firstIndex) {
		this.firstIndex = firstIndex;
	},

	setLast: function(last) {
		this.last = last;
	},

	setAfter: function(after) {
		this.after = after;
	},

	setMax: function(max) {
		this.max = max;
	},

	setCount: function(count) {
		this.count = count;
	},

	getIndex: function() {
		return this.index;
	},

	setIndex: function(index) {
		this.index = index;
	},
	
	getElementName: function() {
		return ResultSetExtension.ELEMENTNAME;
	},
	
	getNamespace: function() {
		return ResultSetExtension.NAMESPACE;
	},
	
	toXml: function() {
		var xml = "";
		xml += "<" + this.getElementName() + " " + "xmlns=\"" + this.getNamespace() + "\">";
		
		if (this.getFirst()) {
			xml += "<first";
			if (this.firstIndex != null) {
				xml += " index=\"" + this.getFirstIndex() + "\">";
			} else {
				xml += ">";
			}
			xml += this.getFirst() + "</first>";
		}
		
		if (this.getLast()) {
			xml += "<last>" + this.getLast() + "</last>";
		}
		
		if (this.getMax() != null) {
			xml += "<max>" + this.getMax() + "</max>";
		}
		
		if (this.getAfter()) {
			xml += "<after>" + this.getAfter() + "</after>";
		}
		
		if (this.getCount() != null) {
			xml += "<count>" + this.getCount() + "</count>";
		}
		
		if (this.getIndex() != null) {
			xml += "<index>" + this.getIndex() + "</index>";
		}
		
		xml += "</" + this.getElementName() + ">";
		return xml;
	}
});

ResultSetExtension.ELEMENTNAME = "set";
ResultSetExtension.NAMESPACE = "http://jabber.org/protocol/rsme";

// end of ResultSetExtension


// start of ResultSetExtensionParser
ResultSetExtensionParser = XmppParser.ExtensionParser.extend({
	init: function() {
	},
	
	getElementName: function() {
		return ResultSetExtensionParser.ELEMENTNAME
	},
	
	getNamespace: function() {
		return ResultSetExtensionParser.NAMESPACE;
	},
	
	parseExtension: function(xmppParser, xmlElement) {
		var resultSetExtension = new ResultSetExtension();
		var childNodes = xmlElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var childEle = childNodes[i];
			// ELEMENT_NODE
			if (childEle.nodeType == 1) {
				var elementName = childEle.nodeName;
				if ("first" == elementName) {
					var index = childEle.getAttribute("index");
					resultSetExtension.setFirstIndex(index);
					resultSetExtension.setFirst(childEle.firstChild.nodeValue);
				} else if ("last" == elementName) {
					resultSetExtension.setLast(childEle.firstChild.nodeValue);
				} else if ("after" == elementName) {
					resultSetExtension.setAfter(childEle.firstChild.nodeValue);
				} else if ("max" == elementName) {
					resultSetExtension.setMax(childEle.firstChild.nodeValue);
				} else if ("count" == elementName) {
					resultSetExtension.setCount(childEle.firstChild.nodeValue);
				} else if ("index" == elementName) {
					resultSetExtension.setIndex(childEle.firstChild.nodeValue);
				}
			}
		}
		
		return resultSetExtension;
	}
});

ResultSetExtensionParser.ELEMENTNAME = "set";
ResultSetExtensionParser.NAMESPACE = "http://jabber.org/protocol/rsme";
// end of ResultSetExtensionParser

var parser = XmppParser.getInstance();
parser.addExtensionParser(new ResultSetExtensionParser());




//// start of IqUserFavoriteShop
//
//ShopItem = jClass.extend({
//	init: function(shopId){
//		this.shopId = shopId;
//	},
//
//	getShopId: function() {
//		return this.shopId;
//	},
//	
//	getAction: function() {
//		return this.action;
//	},
//	
//	setAction: function(action) {
//		this.action = action;
//	},
//
//	getShopName: function() {
//		return this.shopName;
//	},
//
//	setShopName: function(shopName) {
//		this.shopName = shopName;
//	},
//
//	getStreet: function() {
//		return this.street;
//	},
//	
//	setStreet: function(street) {
//		this.street = street;
//	},
//	
//	getTel: function() {
//		return this.tel;
//	},
//
//	setTel: function(tel) {
//		this.tel = tel;
//	},
//		
//	toXml: function(){
//		var xml = "";
//		xml += "<shop id=\"" + this.getShopId() + "\"";
//		if (this.getAction() != null) {
//			xml +=  " action=\"" + this.getAction() + "\"";
//		}
//		xml += ">";
//		
//		if (this.getShopName() != null) {
//			xml += "<name>" + this.getShopName() + "</name>";
//		}
//		
//		if (this.getStreet() != null) {
//			xml += "<street>" + this.getStreet() + "</street>";
//		}
//		
//		if (this.getTel() != null) {
//			xml += "<tel>" + this.getStreet() + "</tel>";
//		}
//		
//		xml += "</shop>";
//		return xml;
//	}
//});
//
//IqUserFavoriteShop = PacketExtension.extend({
//	init: function(){
//		this.shopItems = new Array();
//	},
//	
//	getElementName: function(){
//		return IqUserFavoriteShop.ELEMENTNAME;
//	},
//	
//	getNamespace: function(){
//		return IqUserFavoriteShop.NAMESPACE;
//	},
//	
//	addShopItem: function(item) {
//		this.shopItems.push(item);
//	},
//	
//	removeShopItem: function(item) {
//		for (var i =  0; i < this.shopItems.length; ++i) {
//			if (this.shopItems[i] == item) {
//				this.shopItems.splice(i,1);
//			}
//		}
//	},
//	
//	getShopItems: function() {
//		return this.shopItems;
//	},
//	
//	getResultSetExtension: function() {
//		return this.resultSetExtension;
//	},
//
//	setResultSetExtension: function(resultSetExtension) {
//		this.resultSetExtension = resultSetExtension;
//	},
//	
//	toXml: function() {
//		var xml = "";
//		xml += "<" + this.getElementName() + " xmlns=\"" + this.getNamespace() + "\"";
//		
//		if (this.shopItems.length == 0 && this.resultSetExtension == null) {
//			xml += "/>";
//			
//		} else {
//			xml += ">";
//
//			for (var i = 0; i < this.shopItems.length; ++i){			
//				xml += this.shopItems[i].toXml();
//			}
//			
//			if (this.resultSetExtension) {
//				xml += this.resultSetExtension.toXml();
//			}
//			
//			xml += "</" + this.getElementName() + ">";
//		}
//		
//		
//		return xml;
//	}
//});
//
//IqUserFavoriteShop.ELEMENTNAME = "shops";
//IqUserFavoriteShop.NAMESPACE = "christy:shop:user:favoriteshop";
//
//// end of IqUserFavoriteShop
//
//
//// start of IqUserFavoriteShopParser
//
//IqUserFavoriteShopParser = XmppParser.ExtensionParser.extend({
//	init: function() {
//	},
//	
//	getElementName: function() {
//		return IqUserFavoriteShopParser.ELEMENTNAME
//	},
//	
//	getNamespace: function() {
//		return IqUserFavoriteShopParser.NAMESPACE;
//	},
//	
//	parseExtension: function(xmppParser, xmlElement) {
//		var userFavoriteShop = new IqUserFavoriteShop();
//		var childNodes = xmlElement.childNodes;
//		for (var i = 0; i < childNodes.length; ++i) {
//			var childEle = childNodes[i];
//			// ELEMENT_NODE
//			if (childEle.nodeType == 1) {
//				var elementName = childEle.nodeName;
//				if ("shop" == elementName) {
//					var shopItem = this.parseShopItem(childEle);
//					userFavoriteShop.addShopItem(shopItem);
//				} else {
//					var extensionParser = xmppParser.getExtensionParser(ResultSetExtension.ELEMENTNAME, ResultSetExtension.NAMESPACE);
//					if (extensionParser) {
//						var resultSetX = extensionParser.parseExtension(xmppParser, childEle);
//						userFavoriteShop.setResultSetExtension(resultSetX);
//					}
//				}
//			}
//		}
//		return userFavoriteShop;
//	},
//	
//	parseShopItem: function(shopItemElem) {
//		var shopId = shopItemElem.getAttribute("id");
//		var action = shopItemElem.getAttribute("action");
//		var shopItem = new ShopItem(shopId);
//		shopItem.setAction(action);
//		var childNodes = shopItemElem.childNodes;
//		for (var i = 0; i < childNodes.length; ++i) {
//			var childEle = childNodes[i];
//			// ELEMENT_NODE
//			if (childEle.nodeType == 1) {
//				var elementName = childEle.nodeName;
//				if ("name" == elementName) {
//					shopItem.setShopName(childEle.firstChild.nodeValue);
//				} else if ("street" == elementName) {
//					shopItem.setStreet(childEle.firstChild.nodeValue);
//				} else if ("tel" == elementName) {
//					shopItem.setTel(childEle.firstChild.nodeValue);
//				}
//			}
//		}
//		return shopItem;
//	}
//});
//
//IqUserFavoriteShopParser.ELEMENTNAME = "shops";
//IqUserFavoriteShopParser.NAMESPACE = "christy:shop:user:favoriteshop";
//// end of IqUserFavoriteShopParser
//
//var parser = XmppParser.getInstance();
//parser.addExtensionParser(new IqUserFavoriteShopParser());




// start of GeoLoc
GeoLocType = {
	UTM: "UTM",
	LATLON: "LATLON"
};
GeoLocExtension = PacketExtension.extend({
	init: function(){
	},
	
	getElementName: function(){
		return GeoLocExtension.ELEMENTNAME;
	},
	
	getNamespace: function(){
		return GeoLocExtension.NAMESPACE;
	},
	
	getType: function() {
		return this.type;
	},
	
	setType: function(type) {
		this.type = type;
	},
	
	setEasting: function(easting) {
		this.easting = easting;
	},
	
	getEasting: function() {
		return this.easting;
	},
	
	setNorthing: function(northing) {
		this.northing = northing;
	},
	
	getNorthing: function() {
		return this.northing;
	},
	
	setLat: function(lat) {
		this.lat = lat;
	},
	
	getLat: function() {
		return this.lat;
	},
	
	setLon: function(lon) {
		this.lon = lon;
	},
	
	getLon: function() {
		return this.lon;
	},
	
	toXml: function() {
		var xml = "";
		xml += "<" + this.getElementName() + " xmlns=\"" + this.getNamespace() + "\">";
		if (this.getType() == GeoLocType.UTM) {
			xml += "<utm easting=\"" + this.getEasting() + "\" northing=\"" + this.getNorthing() + "\"/>";
		} else if (this.getType() == GeoLocType.LATLON) {
			xml += "<latlon lat=\"" + this.getLat() + "\" lon=\"" + this.getLon() + "\"/>";
		}
		
		xml += "</" + this.getElementName() + ">";
		
		return xml;
	}
});

GeoLocExtension.ELEMENTNAME = "geoloc";
GeoLocExtension.NAMESPACE = "christy:geo:loc";

// end of GeoLocExtension


// start of GeoLocExtensionParser

GeoLocExtensionParser = XmppParser.ExtensionParser.extend({
	init: function() {
	},
	
	getElementName: function() {
		return GeoLocExtensionParser.ELEMENTNAME
	},
	
	getNamespace: function() {
		return GeoLocExtensionParser.NAMESPACE;
	},
	
	parseExtension: function(xmppParser, xmlElement) {
		var geoLocExtension = new GeoLocExtension();
		var childNodes = xmlElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var childEle = childNodes[i];
			// ELEMENT_NODE
			if (childEle.nodeType == 1) {
				var elementName = childEle.nodeName;
				if ("utm" == elementName) {
					var easting = childEle.getAttribute("easting");
					var northing = childEle.getAttribute("northing");
					geoLocExtension.setType(GeoLocType.UTM);
					geoLocExtension.setEasting(easting);
					geoLocExtension.setNorthing(northing);
				} else if ("latlon" == elementName) {
					var lat = childEle.getAttribute("lat");
					var lon = childEle.getAttribute("lon");
					geoLocExtension.setType(GeoLocType.LATLON);
					geoLocExtension.setLat(lat);
					geoLocExtension.setLon(lon);
				}
			}
		}
		return geoLocExtension;
	}
});


GeoLocExtensionParser.ELEMENTNAME = "geoloc";
GeoLocExtensionParser.NAMESPACE = "christy:geo:loc";

// end of GeoLocExtensionParser

var parser = XmppParser.getInstance();
parser.addExtensionParser(new GeoLocExtensionParser());










// start of Preferences

PreferencesExtension = PacketExtension.extend({
	init: function() {
		this.preferences = {};
	},
	
	getElementName: function(){
		return PreferencesExtension.ELEMENTNAME;
	},
	
	getNamespace: function(){
		return PreferencesExtension.NAMESPACE;
	},
	
	setPreference: function(preferenceName, preferenceValue) {
		this.preferences[preferenceName] = preferenceValue;
	},
	
	getPreference: function(preferenceName) {
		return this.preferences[preferenceName];
	},
	
	getAllPreferences: function() {
		return this.preferences;
	},
	
	removePreference: function(preferenceName) {
		delete this.preferences[preferenceName];
	},
	
	toXml: function() {
		var xml = "";
		xml += "<" + this.getElementName() + " xmlns=\"" + this.getNamespace() + "\">";
		
		for (var key in this.preferences) {
			xml += "<item name='" + key + "'>" + this.preferences[key] + "</item>";
		}
		
		xml += "</" + this.getElementName() + ">";
		
		return xml;
	}
});

PreferencesExtension.ELEMENTNAME = "preference";
PreferencesExtension.NAMESPACE = "christy:user:preference";

// end of GeoLocExtension


// start of GeoLocExtensionParser

PreferencesExtensionParser = XmppParser.ExtensionParser.extend({
	init: function() {
	},
	
	getElementName: function() {
		return PreferencesExtensionParser.ELEMENTNAME
	},
	
	getNamespace: function() {
		return PreferencesExtensionParser.NAMESPACE;
	},
	
	parseExtension: function(xmppParser, xmlElement) {
		var preferencesExtension = new PreferencesExtension();
		var childNodes = xmlElement.childNodes;
		for (var i = 0; i < childNodes.length; ++i) {
			var childEle = childNodes[i];
			// ELEMENT_NODE
			if (childEle.nodeType == 1) {
				var elementName = childEle.nodeName;
				if ("item" == elementName) {
					var prefeName = childEle.getAttribute("name");
					preferencesExtension.setPreference(prefeName, childEle.firstChild.nodeValue);
				}
			}
		}
		return preferencesExtension;
	}
});


PreferencesExtensionParser.ELEMENTNAME = "preference";
PreferencesExtensionParser.NAMESPACE = "christy:user:preference";

// end of Preferences

var parser = XmppParser.getInstance();
parser.addExtensionParser(new PreferencesExtensionParser());
