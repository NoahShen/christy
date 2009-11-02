// start of XmlStanza
var XmlStanza = jClass.extend({
	init: function(){
		
	},
	
	toXml: function(){
		
	},
	
	clone: function(){
		
	}
});
// end of XmlStanza



// start of AbstractXmlStanza
var AbstractXmlStanza = XmlStanza.extend({
	init: function() {
	    this._super();
	}
});

AbstractXmlStanza.ID_NOT_AVAILABLE = "ID_NOT_AVAILABLE"


// end of AbstractXmlStanza