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
				//this._super();
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
				
				// TODO
				alert(bodyElement.nodeName);
				alert(bodyElement.getAttribute("sid"));
				alert(body.toXml());
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
		
		com.christy.web.parser.XmppParser.getInstance = function() {
			if (XmppParser.instance == null) {
				XmppParser.instance = new XmppParser();
			}
			return XmppParser.instance;
		}
		
		com.christy.web.parser.XmppParser.ExtensionParser = JClass.extend({
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