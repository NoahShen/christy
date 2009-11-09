package net.sf.christy.sm;

import net.sf.christy.xmpp.Packet;

public interface PacketHandler
{

        /**
         * Tests whether or not the specified packet should pass the filter.
         * 
         * @param userResource
         * @param packet
         * @return true if and only if <tt>packet</tt> passes the filter.
         */
        public boolean accept(UserResource userResource, Packet packet);
        
        /**
         * 
         * @param userResource
         * @param packet
         */
        public void handlePacket(UserResource userResource, Packet packet);

}
