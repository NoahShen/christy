package com.google.code.christy.sm.userfavoriteshop;

import java.util.List;

import com.google.code.christy.sm.OnlineUser;
import com.google.code.christy.sm.SmHandler;
import com.google.code.christy.sm.SmManager;
import com.google.code.christy.sm.UserResource;
import com.google.code.christy.sm.userfavoriteshop.UserFavoriteShopExtension.ShopItem;
import com.google.code.christy.xmpp.Iq;
import com.google.code.christy.xmpp.Packet;
import com.google.code.christy.xmpp.XmppError;
import com.google.code.christy.xmpp.resultsetmgr.ResultSetExtension;

public class UserFavoriteShopHandler implements SmHandler
{
	private UserFavoriteShopDbHelperTracker userFavoriteShopDbHelperTracker;
	
	
	public UserFavoriteShopHandler(UserFavoriteShopDbHelperTracker userFavoriteShopDbHelperTracker)
	{
		super();
		this.userFavoriteShopDbHelperTracker = userFavoriteShopDbHelperTracker;
	}

	@Override
	public boolean accept(SmManager smManager, OnlineUser onlineUser, Packet packet)
	{
		return (packet instanceof Iq) 
			&& packet.containExtension(UserFavoriteShopExtension.ELEMENTNAME, UserFavoriteShopExtension.NAMESPACE);
	}

	@Override
	public UserResource[] checkResource(SmManager smManager, OnlineUser onlineUser)
	{
		return new UserResource[]{null};
	}

	@Override
	public void handleClientPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		Iq iq = (Iq) packet;
		UserFavoriteShopExtension userFavoriteShopExtension = 
			(UserFavoriteShopExtension) iq.getExtension(UserFavoriteShopExtension.ELEMENTNAME, UserFavoriteShopExtension.NAMESPACE);
		
		UserFavoriteShopDbHelper userFavoriteShopDbHelper = userFavoriteShopDbHelperTracker.getUserFavoriteShopDbHelper();
		if (userFavoriteShopDbHelper == null)
		{
			try
			{
				Iq iqError = (Iq) iq.clone();
				iqError.setType(Iq.Type.error);
				iqError.setTo(userResource.getFullJid());
				iqError.setFrom(null);
				iqError.setError(new XmppError(XmppError.Condition.internal_server_error));
				userResource.sendToSelfClient(iqError);
			}
			catch (CloneNotSupportedException e2)
			{
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			return;
		}
		String username = onlineUser.getNode();
		Iq.Type type = iq.getType();
		if (type == Iq.Type.set)
		{
			List<ShopItem> items = userFavoriteShopExtension.getShopItems();
			try
			{
				for (ShopItem item : items)
				{
					if (item.getAction() == UserFavoriteShopExtension.ShopAction.add)
					{
						userFavoriteShopDbHelper.addFavoriteShop(username, item.getShopId());
					}
					else if (item.getAction() == UserFavoriteShopExtension.ShopAction.remove)
					{
						userFavoriteShopDbHelper.removeFavoriteShop(username, item.getShopId());
					}
				}
				
				Iq iqResponse = new Iq(Iq.Type.result);
				iqResponse.setStanzaId(iq.getStanzaId());
				iqResponse.setTo(userResource.getFullJid());
				
				userResource.sendToSelfClient(iqResponse);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				try
				{
					Iq iqError = (Iq) iq.clone();
					iqError.setType(Iq.Type.error);
					iqError.setTo(userResource.getFullJid());
					iqError.setFrom(null);
					iqError.setError(new XmppError(XmppError.Condition.internal_server_error));
					userResource.sendToSelfClient(iqError);
				}
				catch (CloneNotSupportedException e2)
				{
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
			}
			
			
		}
		else if (type == Iq.Type.get)
		{
			
			try
			{
				int startIndex = 0;
				int max = 10;
				ResultSetExtension rsx = userFavoriteShopExtension.getResultSetExtension();
				if (rsx != null)
				{
					if (rsx.getIndex() != Integer.MIN_VALUE)
					{
						startIndex = rsx.getIndex();
					}
					
					if (rsx.getMax() != Integer.MIN_VALUE)
					{
						max = rsx.getMax();
					}
				}
				UserFavoriteShopExtension extension = new UserFavoriteShopExtension();
				
				UserFavoriteShopEntity[] entities = userFavoriteShopDbHelper.getAllFavoriteShop(username, startIndex, max);
				for (UserFavoriteShopEntity entity :  entities)
				{
					ShopItem item = new ShopItem(entity.getShopId());
					item.setShopName(entity.getShopName());
					item.setStreet(entity.getStreet());
					item.setTel(entity.getTel());
					extension.addShopItem(item);
				}
				
				if (rsx != null)
				{
					ResultSetExtension responseRsx = new ResultSetExtension();
					responseRsx.setFirstIndex(startIndex);
					if (entities.length != 0)
					{
						UserFavoriteShopEntity first = entities[0];
						UserFavoriteShopEntity last = entities[entities.length - 1];
						responseRsx.setFirst(String.valueOf(first.getId()));
						responseRsx.setLast(String.valueOf(last.getId()));
					}
					int count = userFavoriteShopDbHelper.getAllFavoriteShopCount(username);
					responseRsx.setCount(count);
					extension.setResultSetExtension(responseRsx);
				}
				
				Iq iqResponse = new Iq(Iq.Type.result);
				iqResponse.setStanzaId(iq.getStanzaId());
				iqResponse.setTo(userResource.getFullJid());
				
				iqResponse.addExtension(extension);
				
				userResource.sendToSelfClient(iqResponse);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				try
				{
					Iq iqError = (Iq) iq.clone();
					iqError.setType(Iq.Type.error);
					iqError.setTo(userResource.getFullJid());
					iqError.setFrom(null);
					iqError.setError(new XmppError(XmppError.Condition.internal_server_error));
					userResource.sendToSelfClient(iqError);
				}
				catch (CloneNotSupportedException e2)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		
		
		
	}

	@Override
	public void handleOtherUserPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void userResourceAdded(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void userResourceAvailable(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void userResourceRemoved(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		// TODO Auto-generated method stub

	}

}
