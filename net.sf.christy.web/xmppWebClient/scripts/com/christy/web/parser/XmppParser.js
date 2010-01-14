jingo.declare({
	require: [
	  "com.christy.web.clazz.JClass",
	  "com.christy.web.xmpp.XmppStanza"
	],
	name: "com.christy.web.parser.XmppParser",
	as: function() {
		var JClass = com.christy.web.clazz.JClass;
		var XmppStanza = com.christy.web.xmpp.XmppStanza;
		
		
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
				
				
				var chileNodes = bodyElement.childNodes;
				for (var i = 0; i < chileNodes.length; ++i) {
					var packetElement = chileNodes[i];
					var elementName = packetElement.nodeName;
					if ("features" == elementName
						|| "stream:features" == elementName) {
						body.addStanza(this.parseStreamFeature(packetElement));
					}
					else if ("success" == elementName) {
						body.addStanza(new XmppStanza.Success());
					}
					// TODO
				}
				
//				alert(body.toXml());
				return body;
			},
			
			parseStreamFeature: function(streamFeatureElement) {
				var streamFeature = new XmppStanza.StreamFeature();
				var chileNodes = streamFeatureElement.childNodes;
				for (var i = 0; i < chileNodes.length; ++i) {
					var featureElement = chileNodes[i];
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