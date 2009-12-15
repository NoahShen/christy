package net.sf.christy.sm;

import net.sf.christy.xmpp.Packet;

public interface SmHandler
{

        /**
         * Tests whether or not the specified packet should pass the filter.
         * 
         * @param smManager
         * @param onlineUser
         * @param userResource
         * @param packet
         * @return true if and only if <tt>packet</tt> passes the filter.
         */
        public boolean accept(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet);
        
        /**
         * handle packet from user sent
         * @param smManager
         * @param onlineUser
         * @param userResource
         * @param packet
         */
        public void handleClientPacket(SmManager smManager,OnlineUser onlineUser, UserResource userResource, Packet packet);
        
        /**
         * handle packet from other user sent
         * @param smManager
         * @param onlineUser
         * @param userResource
         * @param packet
         */
        public void handleOtherUserPacket(SmManager smManager,OnlineUser onlineUser, UserResource userResource, Packet packet);

        /**
         * 
         * @param smManager
         * @param onlineUser
         * @param userResource
         */
        public void userResourceAdded(SmManager smManager,OnlineUser onlineUser, UserResource userResource);
        
        /**
         * 
         * @param smManager
         * @param onlineUser
         * @param userResource
         */
        public void userResourceRemoved(SmManager smManager,OnlineUser onlineUser, UserResource userResource);
        
        /**
         * 
         * @param smManager
         * @param onlineUser
         * @param userResource
         */
        public void userResourceAvailable(SmManager smManager,OnlineUser onlineUser, UserResource userResource);
}
