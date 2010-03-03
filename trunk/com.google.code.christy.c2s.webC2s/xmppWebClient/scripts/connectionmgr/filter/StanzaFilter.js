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
