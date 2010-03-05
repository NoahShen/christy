$(document).ready(function() {	
	
	var jid1 = new JID("Node1", "Domain", "resource");
	var jid2 = new JID("node2", "domain", "Resource");
	
//			alert(jid1.toPrepedFullJID());
	
//			alert(jid1 instanceof JID);
//		
//		//	alert(jid1.equalsWithBareJid(jid2));
//		//	alert(jid1.toPrepedFullJID());
//		//	alert(jid2.toPrepedFullJID());
//		//
	var jid3 = JID.createJID("Noah3@example.com/resource");
//		//	alert(jid3.toPrepedFullJID());
//		//	
//		//	alert(StringUtils.hash("123", "md5"));
//		//	alert(StringUtils.hash("123", "sha1"));
//		
//			alert(StringUtils.randomString(5));
	
	var innerHtml = "";
//			for (var i = 0; i < 10; ++i){
//				innerHtml += AbstractXmlStanza.nextID() + "<br/>";
//			}
//		
//			var abstractXmlStanza = new AbstractXmlStanza();
//			abstractXmlStanza.setStanzaId("dfsd");
//			innerHtml += abstractXmlStanza.getStanzaId() + "\n";
//			
//			

	var error = new XmppError(500, ErrorType.CANCEL);
//			error.setCondition("bad-request");
	error.setMessage("messag");
	error.addCondition("e", "ns");
//			innerHtml += error.toXml() + "\n";
	
	var iq = new Iq(IqType.RESULT);
	iq.setLanguage("en");
	iq.setFrom(JID.createJID("Noah@example.com/res"));
	iq.setTo(JID.createJID("Noah2@example.com/res"));
//			innerHtml += iq.toXml() + "\n";
	
	var presence = new Presence(PresenceType.AVAILABLE);
	presence.setShow(PresenceShow.DND);
	presence.setUserStatus("status");
	presence.setPriority(1);
	presence.setXmppError(error)
//			innerHtml += presence.toXml() + "\n";

	var message = new Message(MessageType.CHAT);
	message.setFrom(JID.createJID("Noah@example.com/res"));
	message.setTo(JID.createJID("Noah2@example.com/res"));
	message.setThread("thread");
	message.setBody("body<>&'\"");
	message.setSubject("subject");
	message.addBody(new MessageBody("zh-CN", "你好"));
	message.addSubject(new MessageSubject("zh-CN", "主题"));
//			innerHtml += message.toXml() + "\n";
	
	var auth = new Auth();
	auth.setMechanism("setMechanism");
	auth.setContent("setContent");
//			innerHtml += auth.toXml() + "\n";
	

	var challenge = new Challenge("content");
//			innerHtml += challenge.toXml() + "\n";
	
	var failure = new Failure(Failure.SASL_FAILURE_NS);
	failure.setError(FailureError.invalid_authzid);
//			innerHtml += failure.toXml() + "\n";

	var iqBind = new IqBind();
	iqBind.setResource("resource");
	iqBind.setJid(jid3);
	iq.addPacketExtension(iqBind);
//			innerHtml += iq.toXml() + "\n";
	
	var iqRosterItem = new IqRosterItem(jid3, "Noah");
	iqRosterItem.setAsk(IqRosterAsk.subscribe);
	iqRosterItem.setSubscription(IqRosterSubscription.both);
	iqRosterItem.addGroup("group1");

	var iqRoster = new IqRoster();
	iqRoster.addRosterItem(iqRosterItem);
	iq.addPacketExtension(iqRoster);
//			innerHtml += iq.toXml() + "\n";

	var iqSession = new IqSession();
	iq.addPacketExtension(iqSession);
//			innerHtml += iq.toXml() + "\n";
	
	var privacyItem = new PrivacyItem(PrivacyItemType.jid, "Noah@example.com", true, 1);
	privacyItem.setFilterIQ(true);
	privacyItem.setFilterMessage(true);
	privacyItem.setFilterPresence_in(true);
	privacyItem.setFilterPresence_out(true);
//			innerHtml += privacyItem.toXml() + "\n";
	
	var privacyList = new PrivacyList("listName");
	privacyList.addItem(privacyItem);
//			innerHtml += privacyList.toXml() + "\n";
	
	var privacy = new Privacy();
	privacy.addPrivacyList(privacyList);
	iq.addPacketExtension(privacy);
//			innerHtml += iq.toXml() + "\n";

	var proceed = new Proceed();
//			innerHtml += proceed.toXml() + "\n";
	
	var response = new Response("content");
//			innerHtml += response.toXml() + "\n";
	
	var startTls = new StartTls();
//			innerHtml += startTls.toXml() + "\n";

	var stream = new Stream();
	stream.setFrom(jid1);
	stream.setTo(jid2);
//			innerHtml += stream.toXml() + "\n";
	

	var streamError = new StreamError(StreamErrorCondition.bad_format);
	streamError.setText("text", "lang");
	streamError.addApplicationCondition("e", "ns");
//			innerHtml += streamError.toXml() + "\n";
	
	var streamFeature = new StreamFeature();
	streamFeature.addFeature("e", "ns", true);
	streamFeature.addMechanism("mechanism");
	streamFeature.addCompressionMethod("compression");
//			innerHtml += streamFeature.toXml() + "\n";
	
	var success = new Success("Success");
	innerHtml += success.toXml() + "\n";

	
//			$.ajax({
//			  url: "webclient/JHB.do",
//			  cache: false,
//			  async: false,
//			  type: "post",
//			  data: "<body content='text/xml; charset=utf-8' hold='1' rid='1573741820' to='jabbercn.org' ver='1.6' wait='60' ack='1' xml:lang='en' xmlns='http://jabber.org/protocol/httpbind'/>",
//			  processData: false,
//			  success: function(data){
//			    alert(data);
//			  }
//			});

//			alert(StringUtils.hash("bfb06a6f113cd6fd3838ab9d300fdb4fe3da2f7d", "SHA-1"));
	
	var connectionMgr = XmppConnectionMgr.getInstance();
	connectionMgr.requestCreateConnection({
		hold: 1,
		to: "jabber.org",
//				to: "gmail.com",
		ack: "1",
		ver: "1.6",
		wait: "60"
	});
	
	connectionMgr.addConnectionListener(ConnectionEventType.Created, function(event){
//				alert("created");
//				alert(event.getConnection().getDomain());
//				alert(event.getWhen());
//				alert(event.getConnection().getAllowedMechanisms());
		var conn = event.connection;
		conn.login("NoahShen", "159357", "resource", new Presence(PresenceShow.AVAILABLE));
	});
	
	connectionMgr.addConnectionListener(ConnectionEventType.Error, function(event){
		alert("Error");
		alert(event.error);
	});
	
	connectionMgr.addConnectionListener(ConnectionEventType.StanzaReceived, function(event){
//				alert(event.getStanza().toXml());
	});
	connectionMgr.addConnectionListener(ConnectionEventType.SaslSuccessful, function(event){
//				alert("SaslSuccessful");
	});
//			connectionMgr.addConnectionListener(ConnectionEventType.ResourceBinded, function(event){
//				alert("ResourceBinded");
//			});
	
	connectionMgr.addConnectionListener(ConnectionEventType.SessionBinded, function(event){
		alert("SessionBinded");
//				event.connection.close();
	});
	
//			connectionMgr.addConnectionListener(ConnectionEventType.OtherResourceStatusChanged, function(event){
//				alert("OtherResourceStatusChanged");
//			});
//			
//			connectionMgr.addConnectionListener(ConnectionEventType.ContactStatusChanged, function(event){
//				alert("ContactStatusChanged");
//			});
	
	connectionMgr.addConnectionListener(ConnectionEventType.ChatCreated, function(event){
		alert("ChatCreated");
	});
	
	connectionMgr.addConnectionListener(ConnectionEventType.MessageReceived, function(event){
		alert("MessageReceived");
		event.connection.sendChatText(event.chat, "echo:" + event.stanza.getBody());
	});
	
	connectionMgr.addConnectionListener(ConnectionEventType.ChatResourceChanged, function(event){
		alert("ChatResourceChanged");
	});
	connectionMgr.addConnectionListener(ConnectionEventType.ConnectionClosed, function(event){
		alert("ConnectionClosed");
	});
	$("#testId1").text(innerHtml);


});