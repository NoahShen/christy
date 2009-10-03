package net.sf.christy.router.impl.consistentHashingImpl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.christy.router.BindTask;
import net.sf.christy.router.BindTaskListener;
import net.sf.christy.router.ResourceBinder;

/**
 * 
 * @author noah
 *
 */
public class ConsistentHashingBindTask implements BindTask
{
	
	private String jidNode;
	
	private String resource;
	
	private String bindedResource;
	
	private boolean completed = false;
	
	private ResourceBinder resourceBinder;
	
	private List<BindTaskListener> bindTaskListeners = new CopyOnWriteArrayList<BindTaskListener>();
	
	
	/**
	 * @param jidNode
	 * @param resource
	 * @param resourceBinder
	 */
	public ConsistentHashingBindTask(String jidNode, String resource, ResourceBinder resourceBinder)
	{
		this.jidNode = jidNode;
		this.resource = resource;
		this.resourceBinder = resourceBinder;
	}

	@Override
	public void addBindTaskListener(BindTaskListener listener)
	{
		bindTaskListeners.add(listener);
	}

	@Override
	public BindTaskListener[] getBindTaskListeners()
	{
		return bindTaskListeners.toArray(new BindTaskListener[]{});
	}

	@Override
	public String getBindedResouce()
	{
		return bindedResource;
	}

	@Override
	public String getJidNode()
	{
		return jidNode;
	}

	@Override
	public String getResource()
	{
		return resource;
	}

	@Override
	public ResourceBinder getResourceBinder()
	{
		return resourceBinder;
	}

	@Override
	public boolean isCompleted()
	{
		return completed;
	}

	@Override
	public void removeBindTaskListener(BindTaskListener listener)
	{
		bindTaskListeners.remove(listener);
	}

}
