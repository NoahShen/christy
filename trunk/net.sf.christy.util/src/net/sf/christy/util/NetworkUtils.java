package net.sf.christy.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author noah
 * 
 */
public class NetworkUtils
{
	private static String ip = null;
	
	public static String getLocalIP()
	{
		if (ip == null)
		{
			synchronized(NetworkUtils.class)
			{
				if (ip == null)
				{
					String os = System.getProperty("os.name");

					if (os != null)
					{
						if (os.startsWith("Windows"))
						{
							ip = windowsLocalIP();
						}
						else if (os.startsWith("Linux"))
						{
							ip = linuxLocalIP();
						}
					}
				}
			}
		}
		
		return ip;
	}

	private static String linuxLocalIP()
	{
		String ip = null;
		try
		{
			Enumeration<NetworkInterface> e1 = NetworkInterface.getNetworkInterfaces();
			while (e1.hasMoreElements())
			{
				NetworkInterface ni = e1.nextElement();
				String name = ni.getName();
//				if (!ni.getName().equals("wlan0"))
//				{
//					continue;
//				}
//				else 
				// 有线以太网
				if (name.equals("eth0"))
				{
					Enumeration<InetAddress> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements())
					{
						InetAddress ia = e2.nextElement();
						if (ia instanceof Inet4Address)
						{
							ip = ia.getHostAddress();
							break;
						}
					}
					
				}
				// 无线以太网
				else if (name.equals("wlan0"))
				{
					Enumeration<InetAddress> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements())
					{
						InetAddress ia = e2.nextElement();
						if (ia instanceof Inet4Address)
						{
							ip = ia.getHostAddress();
							break;
						}
					}
					
				}
				//本地环回
				else if (name.equals("lo"))
				{
					Enumeration<InetAddress> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements())
					{
						InetAddress ia = e2.nextElement();
						if (ia instanceof Inet4Address)
						{
							ip = ia.getHostAddress();
							break;
						}
					}
					
				}
//				else
//				{
//					Enumeration<InetAddress> e2 = ni.getInetAddresses();
//					while (e2.hasMoreElements())
//					{
//						InetAddress ia = e2.nextElement();
//						if (ia instanceof Inet6Address)
//							continue;
//						ip = ia.getHostAddress();
//					}
//					break;
//				}
			}
		}
		catch (SocketException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		return ip;

	}

	private static String windowsLocalIP()
	{
		String ip = null;

		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}
//
//	public static String getMACAddress()
//	{
//
//		String address = "";
//		String os = System.getProperty("os.name");
//		System.out.println(os);
//		if (os != null)
//		{
//			if (os.startsWith("Windows"))
//			{
//				try
//				{
//					ProcessBuilder pb = new ProcessBuilder("ipconfig", "/all");
//					Process p = pb.start();
//					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//					String line;
//					while ((line = br.readLine()) != null)
//					{
//						if (line.indexOf("Physical Address") != -1)
//						{
//							int index = line.indexOf(":");
//							address = line.substring(index + 1);
//							break;
//						}
//					}
//					br.close();
//					return address.trim();
//				}
//				catch (IOException e)
//				{
//
//				}
//			}
//			else if (os.startsWith("Linux"))
//			{
//				try
//				{
//					ProcessBuilder pb = new ProcessBuilder("ifconfig");
//					Process p = pb.start();
//					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//					String line;
//					while ((line = br.readLine()) != null)
//					{
//						int index = line.indexOf("硬件地址");
//						if (index != -1)
//						{
//							address = line.substring(index + 4);
//							break;
//						}
//					}
//					br.close();
//					return address.trim();
//				}
//				catch (IOException ex)
//				{
//					ex.printStackTrace();
//				}
//
//			}
//		}
//		return address;
//	}

}