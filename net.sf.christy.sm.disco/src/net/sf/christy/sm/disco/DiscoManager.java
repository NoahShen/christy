package net.sf.christy.sm.disco;


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