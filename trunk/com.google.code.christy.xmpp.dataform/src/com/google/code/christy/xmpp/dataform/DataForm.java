package com.google.code.christy.xmpp.dataform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.XmlStanza;


/**
 * @author noah
 * 
 */
public class DataForm implements PacketExtension
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5425888111130592472L;

	public static final String ELEMENTNAME = "x";

	public static final String NAMESPACE = "jabber:x:data";

	private Type type;

	private String title;

	private Set<String> instructions = new HashSet<String>();

	private ReportedData reportedData;

	private List<Item> items = new ArrayList<Item>();

	private List<FormField> fields = new ArrayList<FormField>();

	public DataForm()
	{
	}


	@Override
	public String getElementName()
	{
		return ELEMENTNAME;
	}

	@Override
	public String getNamespace()
	{
		return NAMESPACE;
	}

	public Type getType()
	{
		return type;
	}

	public String getTitle()
	{
		return title;
	}

	public ReportedData getReportedData()
	{
		return reportedData;
	}

	public void setType(Type type)
	{
		this.type = type;
	}
	/**
	 * Sets the description of the data. It is similar to the title on a
	 * web page or an X window. You can put a <title/> on either a form to
	 * fill out, or a set of data results.
	 * 
	 * @param title
	 *                  description of the data.
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	public void addInstruction(String instruction)
	{
		synchronized (instruction)
		{
			this.instructions.add(instruction);
		}
	}
	
	public void removeInstruction(String instruction)
	{
		this.instructions.remove(instruction);
	}
	
	public void removeAllInstructions()
	{
		this.instructions.clear();
	}
	

	public Collection<String> getInstructions()
	{
		synchronized (instructions)
		{
			return Collections.unmodifiableList(new ArrayList<String>(instructions));
		}
	}
	/**
	 * Sets the fields that will be returned from a search.
	 * 
	 * @param reportedData
	 *                  the fields that will be returned from a search.
	 */
	public void setReportedData(ReportedData reportedData)
	{
		this.reportedData = reportedData;
	}

	/**
	 * Adds a new field as part of the form.
	 * 
	 * @param field
	 *                  the field to add to the form.
	 */
	public void addField(FormField field)
	{
		synchronized (fields)
		{
			fields.add(field);
		}
	}

	public void removeField(FormField field)
	{
		synchronized (fields)
		{
			fields.remove(field);
		}
	}
	
	public void removeAllField()
	{
		synchronized (fields)
		{
			fields.clear();
		}
	}
	/**
	 * Returns an Iterator for the fields that are part of the form.
	 * 
	 * @return an Iterator for the fields that are part of the form.
	 */
	public Collection<FormField> getFields()
	{
		synchronized (fields)
		{
			return Collections.unmodifiableList(new ArrayList<FormField>(fields));
		}
	}
	/**
	 * Adds a new item returned from a search.
	 * 
	 * @param item
	 *                  the item returned from a search.
	 */
	public void addItem(Item item)
	{
		synchronized (items)
		{
			items.add(item);
		}
	}

	public void removeItem(Item item)
	{
		synchronized (items)
		{
			items.remove(item);
		}
	}
	
	public void removeAllItems()
	{
		synchronized (items)
		{
			items.clear();
		}
	}
	
	public Collection<Item> getItems()
	{
		synchronized (items)
		{
			return Collections.unmodifiableList(new ArrayList<Item>(items));
		}
	}


	@Override
	public String toXml()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<")
			.append(getElementName())
				.append(" xmlns=\"")
					.append(getNamespace())
						.append("\" type=\"" + getType().name() + "\">");
		if (getTitle() != null)
		{
			buf.append("<title>").append(getTitle()).append("</title>");
		}
		for (Iterator<String> it = getInstructions().iterator(); it.hasNext();)
		{
			buf.append("<instructions>").append(it.next()).append("</instructions>");
		}

		if (getReportedData() != null)
		{
			buf.append(getReportedData().toXml());
		}

		for (Iterator<Item> i = getItems().iterator(); i.hasNext();)
		{
			Item item = i.next();
			buf.append(item.toXml());
		}
		// Loop through all the form fields and append them to the
		// string buffer
		for (Iterator<FormField> i = getFields().iterator(); i.hasNext();)
		{
			FormField field = i.next();
			buf.append(field.toXml());
		}
		buf.append("</").append(getElementName()).append(">");
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		DataForm dataForm = (DataForm) super.clone();
		dataForm.type = this.type;
		dataForm.title = this.title;
		
		dataForm.instructions = new HashSet<String>();
		for (String instruction : this.instructions)
		{
			dataForm.instructions.add(instruction);
		}
		
		if (this.reportedData != null)
		{
			dataForm.reportedData = (ReportedData) this.reportedData.clone();
		}
		
		dataForm.items = new ArrayList<Item>();
		for (Item item : this.items)
		{
			dataForm.items.add((Item) item.clone());
		}
		
		dataForm.fields = new ArrayList<FormField>();
		for (FormField formField : this.fields)
		{
			dataForm.fields.add((FormField) formField.clone());
		}
		return dataForm;
	}

	public enum Type
	{
		form,
		
		submit,
		
		cancel,
		
		result
	}
	
	public static class ReportedData implements XmlStanza
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1060723949361249234L;
		
		private List<FormField> fields = new ArrayList<FormField>();

		public ReportedData()
		{
		}

		/**
		 * Adds a new field as part of the form.
		 * 
		 * @param field
		 *                  the field to add to the form.
		 */
		public void addField(FormField field)
		{
			synchronized (fields)
			{
				fields.add(field);
			}
		}

		public void removeField(FormField field)
		{
			synchronized (fields)
			{
				fields.remove(field);
			}
		}
		
		public void removeAllField()
		{
			synchronized (fields)
			{
				fields.clear();
			}
		}
		
		/**
		 * Returns an Iterator for the fields that are part of the form.
		 * 
		 * @return an Iterator for the fields that are part of the form.
		 */
		public Collection<FormField> getFields()
		{
			synchronized (fields)
			{
				return Collections.unmodifiableList(new ArrayList<FormField>(fields));
			}
		}

		public String toXml()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("<reported>");
			
			for (Iterator<FormField> i = getFields().iterator(); i.hasNext();)
			{
				FormField field = i.next();
				buf.append(field.toXml());
			}
			buf.append("</reported>");
			return buf.toString();
		}

		@Override
		public Object clone() throws CloneNotSupportedException
		{
			ReportedData data = (ReportedData) super.clone();
			data.fields = new ArrayList<FormField>();
			for (FormField field : this.fields)
			{
				data.fields.add((FormField) field.clone());
			}
			data.fields = new ArrayList<FormField>();
			
			return data;
		}
		
		
	}

	public static class Item implements XmlStanza
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7043985136795686132L;
		
		private List<FormField> fields = new ArrayList<FormField>();

		public Item()
		{
		}

		/**
		 * Adds a new field as part of the form.
		 * 
		 * @param field
		 *                  the field to add to the form.
		 */
		public void addField(FormField field)
		{
			synchronized (fields)
			{
				fields.add(field);
			}
		}

		public void removeField(FormField field)
		{
			synchronized (fields)
			{
				fields.remove(field);
			}
		}
		
		public void removeAllField()
		{
			synchronized (fields)
			{
				fields.clear();
			}
		}
		
		/**
		 * Returns an Iterator for the fields that are part of the form.
		 * 
		 * @return an Iterator for the fields that are part of the form.
		 */
		public Collection<FormField> getFields()
		{
			synchronized (fields)
			{
				return Collections.unmodifiableList(new ArrayList<FormField>(fields));
			}
		}
		public String toXml()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("<item>");
			for (Iterator<FormField> i = getFields().iterator(); i.hasNext();)
			{
				FormField field = i.next();
				buf.append(field.toXml());
			}
			buf.append("</item>");
			return buf.toString();
		}

		@Override
		public Object clone() throws CloneNotSupportedException
		{
			Item item = (Item) super.clone();
			item.fields = new ArrayList<FormField>();
			for (FormField field : this.fields)
			{
				item.fields.add((FormField) field.clone());
			}
			return item;
		}
		
		
	}

}