$(document).ready(function() {
	var jid1 = new JID("Node1", "Domain", "resource");
	var jid2 = new JID("node2", "domain", "Resource");
//	alert(jid1.toPrepedFullJID());
	
//	alert(jid1 instanceof JID);

//	alert(jid1.equalsWithBareJid(jid2));
//	alert(jid1.toPrepedFullJID());
//	alert(jid2.toPrepedFullJID());
//
	var jid3 = JID.createJID("Noah3@example.com/resource");
//	alert(jid3.toPrepedFullJID());
//	
//	alert(StringUtils.hash("123", "md5"));
//	alert(StringUtils.hash("123", "sha1"));

//	alert(StringUtils.randomString(5));
	
	var innerHtml = "";
//	for (var i = 0; i < 10; ++i){
//		innerHtml += AbstractXmlStanza.nextID() + "<br/>";
//	}

//	var abstractXmlStanza = new AbstractXmlStanza();
//	abstractXmlStanza.setStanzaId("dfsd");
//	innerHtml += abstractXmlStanza.getStanzaId() + "\n";
	
	
	var error = new XmppError(500, ErrorType.CANCEL);
	error.setCondition("bad-request");
	error.setMessage("messag");
	error.addCondition("e", "ns");
//	innerHtml += error.toXml() + "\n";
	
	var iq = new Iq(IqType.RESULT);
	iq.setLanguage("en");
	iq.setFrom(JID.createJID("Noah@example.com/res"));
	iq.setTo(JID.createJID("Noah2@example.com/res"));
//	innerHtml += iq.toXml() + "\n";
	
	var presence = new Presence(PresenceType.AVAILABLE);
	presence.setShow(PresenceShow.DND);
	presence.setUserStatus("status");
	presence.setPriority(1);
	presence.setXmppError(error)
//	innerHtml += presence.toXml() + "\n";
	
	var message = new Message(MessageType.CHAT);
	message.setFrom(JID.createJID("Noah@example.com/res"));
	message.setTo(JID.createJID("Noah2@example.com/res"));
	message.setThread("thread");
	message.setBody("body<>&'\"");
	message.setSubject("subject");
	message.addBody(new MessageBody("zh-CN", "你好"));
	message.addSubject(new MessageSubject("zh-CN", "主题"));
//	innerHtml += message.toXml() + "\n";
	
	var auth = new Auth();
	auth.setMechanism("setMechanism");
	auth.setContent("setContent");
//	innerHtml += auth.toXml() + "\n";
	
	var challenge = new Challenge("content");
//	innerHtml += challenge.toXml() + "\n";
	
	var failure = new Failure(Failure.SASL_FAILURE_NS);
	failure.setError(FailureError.invalid_authzid);
//	innerHtml += failure.toXml() + "\n";


	var iqBind = new IqBind();
	iqBind.setResource("resource");
	iqBind.setJid(jid3);
//	iq.addPacketExtension(iqBind);
//	innerHtml += iq.toXml() + "\n";
	
	var iqRosterItem = new IqRosterItem(jid3, "Noah");
	iqRosterItem.setAsk(IqRosterAsk.subscribe);
	iqRosterItem.setSubscription(IqRosterSubscription.both);
	iqRosterItem.addGroup("group1");
	
	var iqRoster = new IqRoster();
	iqRoster.addRosterItem(iqRosterItem);
	iq.addPacketExtension(iqRoster);
	innerHtml += iq.toXml() + "\n";
	
	$("#testId1").text(innerHtml);
});