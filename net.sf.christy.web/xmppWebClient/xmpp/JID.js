var JID = jClass.extend({
            init: function(node, domain, resource) {
                this.node = node;
                this.domain = domain;
                this.resource = resource;
                
                if (node != null){
                	this.nodePreped = node.toLowerCase();
                }
                
                if (domain == null){
                	throw new Error(0, "domain must not be null");
                }
                
        		this.domainPreped = domain.toLowerCase();
        		
        		if (resource != null){
        			this.resourcePreped = resource;
        		}
            },
            
            getNode: function(){
            	return this.node;
            },
            
            getDomain: function(){
            	return this.domain;
            },
            
            getResource: function(){
            	return this.resource;
            },
            
            toBareJID: function(){
            	var bareJID = "";
                if (this.node != null)
                {
                        bareJID += (this.node + "@");
                }
                bareJID += this.domain;
                return bareJID;
            },
            
            toFullJID: function(){
            	var fullJID = "";
                if (this.node != null){
                        fullJID += (this.node + "@");
                }
                
                fullJID += this.domain;
                
                if (this.resource != null){
                	fullJID += ("/" + this.resource);
                }
                return fullJID;
            },
            
            toPrepedBareJID: function(){
            	var prepedBareJID = "";
                if (this.nodePreped != null)
                {
                        prepedBareJID += (this.nodePreped + "@");
                }
                prepedBareJID += this.domainPreped;
                return prepedBareJID;
            },
            
            toPrepedFullJID: function(){
            	var prepedFullJID = "";
                if (this.nodePreped != null){
                        prepedFullJID += (this.nodePreped + "@");
                }
                
                prepedFullJID += this.domainPreped;
                
                if (this.resourcePreped != null){
                	prepedFullJID += ("/" + this.resourcePreped);
                }
                return prepedFullJID;
            },
            
            equals: function(other){
            	if (this == other)
                        return true;
                if (other == null)
                        return false;
                if (!(other instanceof JID))
                        return false;
                
                var prepedFullJID = this.toPrepedFullJID();
                if (prepedFullJID != other.toPrepedFullJID()){
                	return false;
                }
                return true;
            	
            },
            
            equalsWithBareJid: function(other){
            	if (this == other)
                        return true;
                if (other == null)
                        return false;
                if (!(other instanceof JID))
                        return false;
                
                var prepedFullJID = this.toPrepedBareJID();
                if (prepedFullJID != other.toPrepedBareJID()){
                	return false;
                }
                return true;
            }

   
});

JID.createJID = function(jid){
	if (jid == null){
		throw new Error(0, "jid must not be null");
	}
	
	var node, domain, resource;
	
	var atIndex = jid.indexOf("@");
	var slashIndex = jid.indexOf("/");
	
	// Node
    if (atIndex > 0)
    {
            node = jid.substring(0, atIndex);
    }

    // Domain
    if (atIndex + 1 > jid.length)
    {
    	throw new Error(0, "JID with empty domain not valid");
    }
    if (atIndex < 0)
    {
            if (slashIndex > 0)
            {
                    domain = jid.substring(0, slashIndex);
            }
            else
            {
                    domain = jid;
            }
    }
    else
    {
            if (slashIndex > 0)
            {
                    domain = jid.substring(atIndex + 1, slashIndex);
            }
            else
            {
                    domain = jid.substring(atIndex + 1);
            }
    }

    // Resource
    if (slashIndex + 1 > jid.length || slashIndex < 0)
    {
            resource = null;
    }
    else
    {
            resource = jid.substring(slashIndex + 1);
    }
    
    return new JID(node, domain, resource);
	
};

