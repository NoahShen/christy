package com.google.code.christy.sm.disco;

import com.google.code.christy.xmpp.disco.DiscoInfoExtension;
import com.google.code.christy.xmpp.disco.DiscoItemsExtension;


/**
 * @author noah
 *
 */
public interface DiscoManager
{       

        /**
         * 
         * @param node
         * @return
         */
        public DiscoInfoExtension getDiscoInfo(String node);
        
        /**
         * 
         * @param node
         * @return
         */
        public DiscoItemsExtension getDiscoItems(String node);
}