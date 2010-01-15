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
					var elementName = packetElement.nodeName;
					if ("features" == elementName
						|| "stream:features" == elementName) {
						body.addStanza(this.parseStreamFeature(packetElement));
					}
					else if ("success" == elementName) {
						body.addStanza(new XmppStanza.Success());
					}
					else if ("iq" == elementName) {
						body.addStanza(this.parseIq(packetElement));
					}
					// TODO
				}
				
//				alert(body.toXml());
				return body;
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
					var elementName = iqExtensionElem.nodeName;
					if ("bind" == elementName) {
						iq.addPacketExtension(this.parseIqBind(iqExtensionElem));
					}
				}
				return iq;
			},
			
			parseIqBind: function(iqBindElement) {
				var iqBind = new XmppStanza.IqBind();
				var childNodes = iqBindElement.childNodes;
				for (var i = 0; i < childNodes.length; ++i) {
					var bindChild = childNodes[i];
					var elementName = bindChild.nodeName;
					if ("resource" == elementName) {
						iqBind.setResource(bindChild.firstChild.nodeValue);
					} else if ("jid" == elementName) {
						iqBind.setJid(JID.createJID(bindChild.firstChild.nodeValue));
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