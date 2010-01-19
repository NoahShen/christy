jingo.declare({
	require: [
	  "com.christy.web.clazz.JClass",
	  "com.christy.web.xmpp.XmppStanza",
	  "com.christy.web.xmpp.JID"
	],
	name: "com.christy.web.parser.XmppParser",
	as: function() {
		var JClass = com.christy.web.clazz.JClass;
		var XmppStanza = com.christy.web.xmpp.XmppStanza;
		var JID = com.christy.web.xmpp.JID;
		
		
		com.christy.web.parser.XmppParser = JClass.extend({
			init: function() {
				this.extensionParsers = new Array();
			},
			
			
			parseStanza: function(bodyElement) {
				
				if ("body" != bodyElement.nodeName) {
					throw new Error("bad xml");
				}
				
				var body = new XmppStanza.Body();
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
						body.addStanza(new XmppStanza.Success());
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
				type = (type == null) ? XmppStanza.MessageType.NORMAL : type;
				
				var message = new XmppStanza.Message(type);
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
							message.addBody(new XmppStanza.MessageBody(lang, messageExtensionElem.firstChild.nodeValue));
						} else {
							message.setBody(messageExtensionElem.firstChild.nodeValue);
						}
						
					} else if ("subject" == elementName) {
						var lang = messageExtensionElem.getAttribute("xml:lang");
						if (lang != null) {
							message.addSubject(new XmppStanza.MessageSubject(lang, messageExtensionElem.firstChild.nodeValue));
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
				type = (type == null) ? XmppStanza.PresenceType.AVAILABLE : type;
				
				var presence = new XmppStanza.Presence(type);
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
					if (window.console) {
						window.console.log("ExtensionParser parse extension complete:" + packetExtension.toXml());
					}
					return packetExtension;
					
				} else {
					//TOO 
					if (window.console) {
						window.console.log("can not get [" + elementName + " " + namespace + "]ExtensionParser");
					}
					
					var unknownExtension = new XmppStanza.UnknownExtension(extensionElem);
					
					//TOO 
					if (window.console) {
						window.console.log("parseUnknownExtension complete:" + unknownExtension.toXml());
					}

					return unknownExtension;
				}
			},
			
			parseIq: function(iqElement)  {
				var to = iqElement.getAttribute("to");
				var from = iqElement.getAttribute("from");
				var lang = iqElement.getAttribute("xml:lang");
				var id = iqElement.getAttribute("id");
				var type = iqElement.getAttribute("type");
				
				var iq = new XmppStanza.Iq(type);
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
					if (XmppStanza.IqBind.ELEMENTNAME == elementName) {
						iq.addPacketExtension(this.parseIqBind(iqExtensionElem));
					} else if (XmppStanza.IqSession.ELEMENTNAME == elementName) {
						iq.addPacketExtension(new XmppStanza.IqSession());
					} else if (XmppStanza.IqRoster.ELEMENTNAME == elementName
								&& XmppStanza.IqRoster.NAMESPACE == namespace) {
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
				var iqRoster = new XmppStanza.IqRoster();
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
				var iqRosterItem = new XmppStanza.IqRosterItem(JID.createJID(jidStr), nickname);
				iqRosterItem.setSubscription(subscription);
				iqRosterItem.setAsk(ask);
				
				var childNodes = rosterItemElem.childNodes;
				for (var i = 0; i < childNodes.length; ++i) {
					var childElem = childNodes[i];
					var elementName = childElem.nodeName;
					if ("group" == elementName) {
						iqRosterItem.addGroup(childElem.firstChild == null ? null : childElem.firstChild.nodeValue);
					}
				}

				return iqRosterItem;
			},
			
			parseIqBind: function(iqBindElement) {
				var iqBind = new XmppStanza.IqBind();
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
				var streamFeature = new XmppStanza.StreamFeature();
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
						var streamFeatureFeature = new XmppStanza.StreamFeatureFeature(elementName, namespace);
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
				
				var error = new XmppStanza.XmppError(code, type);
				
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
		
		var XmppParser = com.christy.web.parser.XmppParser;
		
		XmppParser.getInstance = function() {
			if (XmppParser.instance == null) {
				XmppParser.instance = new XmppParser();
			}
			return XmppParser.instance;
		}
		
		XmppParser.ExtensionParser = JClass.extend({
			init: function() {
				this._super();
			},
			
			getElementName: function() {
				
			},
			
			getNamespace: function() {
				
			},
			
			parseExtension: function(xmppParser, xmlElement) {
				
			}
		});
	}
});