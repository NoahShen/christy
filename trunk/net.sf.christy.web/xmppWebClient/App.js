$(document).ready(function() {
	var jid1 = new JID("Node", "Domain", "resource");
	var jid2 = new JID("node", "domain", "Resource");
//	alert(jid1.toPrepedFullJID());
//	
//	alert(jid1 instanceof JID);

//	alert(jid1.equalsWithBareJid(jid2));
//	alert(jid1.toPrepedFullJID());
//	alert(jid2.toPrepedFullJID());

	var jid3 = JID.createJID("Noah@example.com/resource");
	alert(jid3.toPrepedFullJID());
	
	alert(StringUtils.hash("123", "md5"));
	alert(StringUtils.hash("123", "sha1"));
});