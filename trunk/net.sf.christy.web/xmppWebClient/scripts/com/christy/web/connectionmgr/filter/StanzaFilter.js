jingo.declare({
	require: [
	  "com.christy.web.clazz.JClass",
	  "com.christy.web.xmpp.XmppStanza"
	],
	name: "com.christy.web.connectionmgr.filter.StanzaFilter",
	as: function() {
		var JClass = com.christy.web.clazz.JClass;
		var XmppStanza = com.christy.web.xmpp.XmppStanza;
		
		com.christy.web.connectionmgr.filter.StanzaFilter = JClass.extend({
			init: function() {
			},
			
			accept: function(connection, stanza) {
				return false;
			}
			
		});
		
		var StanzaFilter = com.christy.web.connectionmgr.filter.StanzaFilter;
		
		StanzaFilter.StanzaTypeFilter = StanzaFilter.extend({
			init: function(stanzaClass) {
				this.stanzaClass = stanzaClass;
			},
			
			accept: function(connection, stanza) {
				return stanza instanceof stanzaClass;
			}
			
		});
		
		StanzaFilter.PacketIdFilter = StanzaFilter.extend({
			init: function(packetId) {
				this.packetId = packetId;
			},
			
			accept: function(connection, stanza) {
				return (stanza instanceof XmppStanza.Packet)
							&& (stanza.getStanzaId() == this.packetId);
			}
			
		});
		
		StanzaFilter.OrFilter = StanzaFilter.extend({
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
	}
});