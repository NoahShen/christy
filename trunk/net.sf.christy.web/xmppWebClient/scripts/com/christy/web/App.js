$(document).ready(function() {	
	jingo.anonymous({
		require: [
		  "com.christy.web.clazz.JClass",
		  "com.christy.web.utils.StringUtils",
		  "com.christy.web.xmpp.JID",
		  "com.christy.web.xmpp.XmppStanza",
		  "com.christy.web.connectionmgr.XmppConnectionMgr"
		],
		exec: function() {
			var JClass = com.christy.web.clazz.JClass;
			var StringUtils = com.christy.web.utils.StringUtils;
			var JID = com.christy.web.xmpp.JID;
			var XmppStanza = com.christy.web.xmpp.XmppStanza;
			
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
//				innerHtml += XmppStanza.AbstractXmlStanza.nextID() + "<br/>";
//			}
//		
//			var abstractXmlStanza = new AbstractXmlStanza();
//			abstractXmlStanza.setStanzaId("dfsd");
//			innerHtml += abstractXmlStanza.getStanzaId() + "\n";
//			
//			
			var XmppError = XmppStanza.XmppError;
			var ErrorType = XmppStanza.ErrorType;
			var error = new XmppError(500, ErrorType.CANCEL);
//			error.setCondition("bad-request");
			error.setMessage("messag");
			error.addCondition("e", "ns");
//			innerHtml += error.toXml() + "\n";
			
			var Iq = XmppStanza.Iq;
			var IqType = XmppStanza.IqType;
			var iq = new Iq(IqType.RESULT);
			iq.setLanguage("en");
			iq.setFrom(JID.createJID("Noah@example.com/res"));
			iq.setTo(JID.createJID("Noah2@example.com/res"));
//			innerHtml += iq.toXml() + "\n";
			
			var Presence = XmppStanza.Presence;
			var PresenceType = XmppStanza.PresenceType;
			var PresenceShow = XmppStanza.PresenceShow;
			var presence = new Presence(PresenceType.AVAILABLE);
			presence.setShow(PresenceShow.DND);
			presence.setUserStatus("status");
			presence.setPriority(1);
			presence.setXmppError(error)
//			innerHtml += presence.toXml() + "\n";
			
			var Message = XmppStanza.Message;
			var MessageType = XmppStanza.MessageType;
			var MessageBody = XmppStanza.MessageBody;
			var MessageSubject = XmppStanza.MessageSubject;
			var message = new Message(MessageType.CHAT);
			message.setFrom(JID.createJID("Noah@example.com/res"));
			message.setTo(JID.createJID("Noah2@example.com/res"));
			message.setThread("thread");
			message.setBody("body<>&'\"");
			message.setSubject("subject");
			message.addBody(new MessageBody("zh-CN", "你好"));
			message.addSubject(new MessageSubject("zh-CN", "主题"));
//			innerHtml += message.toXml() + "\n";
			
			var Auth = XmppStanza.Auth;
			var auth = new Auth();
			auth.setMechanism("setMechanism");
			auth.setContent("setContent");
//			innerHtml += auth.toXml() + "\n";
			
			var Challenge = XmppStanza.Challenge;
			var challenge = new Challenge("content");
//			innerHtml += challenge.toXml() + "\n";
			
			var Failure = XmppStanza.Failure;
			var FailureError = XmppStanza.FailureError;
			var failure = new Failure(Failure.SASL_FAILURE_NS);
			failure.setError(FailureError.invalid_authzid);
//			innerHtml += failure.toXml() + "\n";
		
			var IqBind = XmppStanza.IqBind;
			var iqBind = new IqBind();
			iqBind.setResource("resource");
			iqBind.setJid(jid3);
			iq.addPacketExtension(iqBind);
//			innerHtml += iq.toXml() + "\n";
			
			var IqRosterItem = XmppStanza.IqRosterItem;
			var IqRosterAsk = XmppStanza.IqRosterAsk;
			var IqRosterSubscription = XmppStanza.IqRosterSubscription;
			var iqRosterItem = new IqRosterItem(jid3, "Noah");
			iqRosterItem.setAsk(IqRosterAsk.subscribe);
			iqRosterItem.setSubscription(IqRosterSubscription.both);
			iqRosterItem.addGroup("group1");
	
			var IqRoster = XmppStanza.IqRoster;
			var iqRoster = new IqRoster();
			iqRoster.addRosterItem(iqRosterItem);
			iq.addPacketExtension(iqRoster);
//			innerHtml += iq.toXml() + "\n";
		
			var IqSession = XmppStanza.IqSession;
			var iqSession = new IqSession();
			iq.addPacketExtension(iqSession);
//			innerHtml += iq.toXml() + "\n";
			
			var PrivacyItem = XmppStanza.PrivacyItem;
			var PrivacyItemType = XmppStanza.PrivacyItemType;
			var privacyItem = new PrivacyItem(PrivacyItemType.jid, "Noah@example.com", true, 1);
			privacyItem.setFilterIQ(true);
			privacyItem.setFilterMessage(true);
			privacyItem.setFilterPresence_in(true);
			privacyItem.setFilterPresence_out(true);
//			innerHtml += privacyItem.toXml() + "\n";
			
			var PrivacyList = XmppStanza.PrivacyList;
			var privacyList = new PrivacyList("listName");
			privacyList.addItem(privacyItem);
//			innerHtml += privacyList.toXml() + "\n";
			
			var Privacy = XmppStanza.Privacy;
			var privacy = new Privacy();
			privacy.addPrivacyList(privacyList);
			iq.addPacketExtension(privacy);
//			innerHtml += iq.toXml() + "\n";
		
			var Proceed = XmppStanza.Proceed;
			var proceed = new Proceed();
//			innerHtml += proceed.toXml() + "\n";
			
			var Response = XmppStanza.Response;
			var response = new Response("content");
//			innerHtml += response.toXml() + "\n";
			
			var StartTls = XmppStanza.StartTls;
			var startTls = new StartTls();
//			innerHtml += startTls.toXml() + "\n";
		
			var Stream = XmppStanza.Stream;
			var stream = new Stream();
			stream.setFrom(jid1);
			stream.setTo(jid2);
//			innerHtml += stream.toXml() + "\n";
			
			var StreamError = XmppStanza.StreamError;
			var StreamErrorCondition = XmppStanza.StreamErrorCondition;
			var streamError = new StreamError(StreamErrorCondition.bad_format);
			streamError.setText("text", "lang");
			streamError.addApplicationCondition("e", "ns");
//			innerHtml += streamError.toXml() + "\n";
			
			var StreamFeature = XmppStanza.StreamFeature;
			var streamFeature = new StreamFeature();
			streamFeature.addFeature("e", "ns", true);
			streamFeature.addMechanism("mechanism");
			streamFeature.addCompressionMethod("compression");
//			innerHtml += streamFeature.toXml() + "\n";
			
			var Success = XmppStanza.Success;
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
			var XmppConnectionMgr = com.christy.web.connectionmgr.XmppConnectionMgr;
			
			var connectionMgr = XmppConnectionMgr.getInstance();
			connectionMgr.requestCreateConnection({
				hold: 1,
				to: "jabber.org",
//				to: "gmail.com",
				ack: "1",
				ver: "1.6",
				wait: "60"
			});
			
			var ConnectionEventType = XmppConnectionMgr.ConnectionEventType;
			connectionMgr.addConnectionListener(ConnectionEventType.Created, function(event){
//				alert("created");
//				alert(event.getConnection().getDomain());
//				alert(event.getWhen());
//				alert(event.getConnection().getAllowedMechanisms());
				var conn = event.connection;
				conn.login("NoahShen", "159357", "resource", new XmppStanza.Presence(XmppStanza.PresenceShow.AVAILABLE));
			});
			
			connectionMgr.addConnectionListener(ConnectionEventType.Error, function(event){
				alert("Error");
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
			$("#testId1").text(innerHtml);
		}
	});

});