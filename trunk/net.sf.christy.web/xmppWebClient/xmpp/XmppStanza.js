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
			xml += this.message;
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
			xml += "<status>" + this.getUserStatus() + "</status>";
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
