package net.sf.christy.xmpp;

/**
 * failure
 * @author noah
 *
 */
public class Failure implements XMLStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1589020378658870995L;

	private String namespace;
	
	private Error error;
	
	public Failure()
	{
	}


	/**
	 * @param namespace
	 */
	public Failure(String namespace)
	{
		this.namespace = namespace;
	}


	/**
	 * @return the namespace
	 */
	public String getNamespace()
	{
		return namespace;
	}


	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}


	/**
	 * @return the error
	 */
	public Error getError()
	{
		return error;
	}


	/**
	 * @param error the error to set
	 */
	public void setError(Error error)
	{
		this.error = error;
	}

	@Override
	public String toXML()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<failure xmlns=\"").append(getNamespace()).append("\"");
		if (error != null)
		{
			buf.append(">");
			buf.append(error.toXML());
			buf.append("</failure>");
		}
		else
		{
			buf.append("\"/>");
		}
		
		return buf.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Failure failure = (Failure) super.clone();
		failure.setNamespace(this.namespace);
		if (this.error != null)
		{
			failure.setError((Error) this.error.clone());
		}
		
		return failure;
	}

	public static class Error implements XMLStanza
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -6524079435877817449L;

		public static Error aborted = new Error("aborted");
		
		public static Error incorrect_encoding = new Error("incorrect-encoding");
		
		public static Error invalid_authzid = new Error("invalid-authzid");
		
		public static Error invalid_mechanism = new Error("invalid-mechanism");
		
		public static Error mechanism_too_weak = new Error("mechanism-too-weak");
		
		public static Error not_authorized = new Error("not-authorized");
		
		public static Error temporary_auth_failure = new Error("temporary-auth-failure");
		
		private String error;

		/**
		 * @param aborted
		 * @param error
		 */
		private Error(String error)
		{
			this.error = error;
		}
		
		public String getError()
		{
			return error;
		}

		public static Error fromString(String error)
		{
			if (aborted.getError().equals(error))
			{
				return aborted;
			}
			else if (incorrect_encoding.getError().equals(error))
			{
				return incorrect_encoding;
			}
			else if (invalid_authzid.getError().equals(error))
			{
				return invalid_authzid;
			}
			else if (invalid_mechanism.getError().equals(error))
			{
				return invalid_mechanism;
			}
			else if (mechanism_too_weak.getError().equals(error))
			{
				return mechanism_too_weak;
			}
			else if (not_authorized.getError().equals(error))
			{
				return not_authorized;
			}
			else if (temporary_auth_failure.getError().equals(error))
			{
				return temporary_auth_failure;
			}
			
			return null;
		}
		
		@Override
		public String toXML()
		{
			return "<" + error + "/>";
		}

		@Override
		public Object clone() throws CloneNotSupportedException
		{
			Error error = (Error) super.clone();
			error.error = this.error;
			
			return error;
		}
		
		
		
	}
}
