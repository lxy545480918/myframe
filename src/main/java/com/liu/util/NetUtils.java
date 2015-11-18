
package com.liu.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class NetUtils {
    
    private static final Log logger = LogFactory.getLog(NetUtils.class);

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    private static final int RND_PORT_START = 30000;
    
    private static final int RND_PORT_RANGE = 10000;
    
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    
    public final static int IP_A_TYPE = 1;  
    public final static int IP_B_TYPE = 2;  
    public final static int IP_C_TYPE = 3;  
    public final static int IP_OTHER_TYPE = 4; 
    
    private static int[] IpATypeRange;  
    private static int[] IpBTypeRange;    
    private static int[] IpCTypeRange;   
    private static int DefaultIpAMask;  
    private static int DefaultIpBMask;  
    private static int DefaultIpCMask;
    
    static{  
    	IpATypeRange = new int[2];  
    	IpATypeRange[0] = getIpV4Value("1.0.0.1");  
    	IpATypeRange[1] = getIpV4Value("126.255.255.254");  
   	        
    	IpBTypeRange = new int[2];  
    	IpBTypeRange[0] = getIpV4Value("128.0.0.1");  
    	IpBTypeRange[1] = getIpV4Value("191.255.255.254");  
   	         
    	IpCTypeRange = new int[2];  
    	IpCTypeRange[0] = getIpV4Value("192.168.0.0");  
    	IpCTypeRange[1] = getIpV4Value("192.168.255.255");  
    	           
    	DefaultIpAMask = getIpV4Value("255.0.0.0");  
    	DefaultIpBMask = getIpV4Value("255.255.0.0");  
    	DefaultIpCMask = getIpV4Value("255.255.255.0");  
    }  

    
    public static int getRandomPort() {
        return RND_PORT_START + RANDOM.nextInt(RND_PORT_RANGE);
    }

    public static int getAvailablePort() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket();
            ss.bind(null);
            return ss.getLocalPort();
        } catch (IOException e) {
            return getRandomPort();
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static final int MIN_PORT = 0;
    
    private static final int MAX_PORT = 65535;
    
    public static boolean isInvalidPort(int port){
        return port > MIN_PORT || port <= MAX_PORT;
    }

    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");
    public static boolean isValidAddress(String address){
    	return ADDRESS_PATTERN.matcher(address).matches();
    }

    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");
    public static boolean isLocalHost(String host) {
        return host != null 
                && (LOCAL_IP_PATTERN.matcher(host).matches() 
                        || host.equalsIgnoreCase("localhost"));
    }
    
    private static final Pattern IPV4_REGEX = Pattern.compile("((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})"); 
    public static boolean isValidIpV4(String address){
    	return IPV4_REGEX.matcher(address).matches();
    }

    public static boolean isAnyHost(String host) {
        return "0.0.0.0".equals(host);
    }
    
    public static boolean isInvalidLocalHost(String host) {
        return host == null 
        			|| host.length() == 0
                    || host.equalsIgnoreCase("localhost")
                    || host.equals("0.0.0.0")
                    || (LOCAL_IP_PATTERN.matcher(host).matches());
    }
    
    public static boolean isValidLocalHost(String host) {
    	return ! isInvalidLocalHost(host);
    }

    public static InetSocketAddress getLocalSocketAddress(String host, int port) {
        return isInvalidLocalHost(host) ? 
        		new InetSocketAddress(port) : new InetSocketAddress(host, port);
    }

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress())
            return false;
        String name = address.getHostAddress();
        return (name != null 
                && ! ANYHOST.equals(name)
                && ! LOCALHOST.equals(name) 
                && IP_PATTERN.matcher(name).matches());
    }
    
    public static String getLocalHost(){
        InetAddress address = getLocalAddress();
        return address == null ? LOCALHOST : address.getHostAddress();
    }
    
    public static String filterLocalHost(String host) {
    	if (NetUtils.isInvalidLocalHost(host)) {
    		return NetUtils.getLocalHost();
    	}
    	return host;
    }
    
    private static volatile InetAddress LOCAL_ADDRESS = null;


    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null)
            return LOCAL_ADDRESS;
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }
    
    public static String getLogHost() {
        InetAddress address = LOCAL_ADDRESS;
        return address == null ? LOCALHOST : address.getHostAddress();
    }
    
    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address)) {
                                        return address;
                                    }
                                } catch (Throwable e) {
                                    logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }
    
    private static final Map<String, String> hostNameCache = new LRUCache<String, String>(1000);

    public static String getHostName(String address) {
    	try {
    		int i = address.indexOf(':');
    		if (i > -1) {
    			address = address.substring(0, i);
    		}
    		String hostname = hostNameCache.get(address);
    		if (hostname != null && hostname.length() > 0) {
    			return hostname;
    		}
    		InetAddress inetAddress = InetAddress.getByName(address);
    		if (inetAddress != null) {
    			hostname = inetAddress.getHostName();
    			hostNameCache.put(address, hostname);
    			return hostname;
    		}
		} catch (Throwable e) {
			// ignore
		}
		return address;
    }
    
    /**
     * @param hostName
     * @return ip address or hostName if UnknownHostException 
     */
    public static String getIpByHost(String hostName) {
        try{
            return InetAddress.getByName(hostName).getHostAddress();
        }catch (UnknownHostException e) {
            return hostName;
        }
    }

    public static String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }
    
    public static InetSocketAddress toAddress(String address) {
        int i = address.indexOf(':');
        String host;
        int port;
        if (i > -1) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
            port = 0;
        }
        return new InetSocketAddress(host, port);
    }
    
    public static String toURL(String protocol, String host, int port, String path) {
		StringBuilder sb = new StringBuilder();
		sb.append(protocol).append("://");
		sb.append(host).append(':').append(port);
		if( path.charAt(0) != '/' )
			sb.append('/');
		sb.append(path);
		return sb.toString();
	}
    
    public static String qureyRemoteMacAddr(String ip){
    	try {
			UdpGetClientMacAddr queryer = new UdpGetClientMacAddr(ip);
			return queryer.getRemoteMacAddr();
		} catch (Exception e) {
			return null;
		}	
    }
    
    public static byte[] getIpV4Bytes(String ipOrMask){  
        try{  
              String[] addrs = ipOrMask.split("\\.");  
               int length = addrs.length;  
               byte[] addr = new byte[length];  
               for (int index = 0; index < length; index++){  
                   addr[index] = (byte) (Integer.parseInt(addrs[index]) & 0xff);  
               }  
              return addr;  
        }  
        catch (Exception e){}  
        return new byte[4];  
    }  
    
    public static int getIpV4Value(String ipOrMask){  
         byte[] addr = getIpV4Bytes(ipOrMask);  
         int address1  = addr[3] & 0xFF;  
         address1 |= ((addr[2] << 8) & 0xFF00);  
         address1 |= ((addr[1] << 16) & 0xFF0000);  
         address1 |= ((addr[0] << 24) & 0xFF000000);  
         return address1;  
     }
    
    public static String trans2IpV4Str(byte[] ipBytes){    
        return (ipBytes[0] & 0xff) + "." + (ipBytes[1] & 0xff) + "." + (ipBytes[2] & 0xff) + "." + (ipBytes[3] & 0xff);  
    }
    
    public static String trans2IpStr(int ipValue){
    	return ((ipValue >> 24) & 0xff) + "." + ((ipValue >> 16) & 0xff) + "." + ((ipValue >> 8) & 0xff) + "." + (ipValue & 0xff);  
    }
    
    public static String getDefaultMaskStr(String anyIp){  
    	return trans2IpStr(getDefaultMaskValue(anyIp));  
    }
    
    public static int getDefaultMaskValue(String anyIpV4) {  
    	int checkIpType = checkIpV4Type(anyIpV4);  
    	int maskValue = 0;  
    	switch (checkIpType){  
    		case IP_C_TYPE:  
    			maskValue = DefaultIpCMask;  
    			break;  
    		case IP_B_TYPE:  
    			maskValue = DefaultIpBMask;  
    			break;  
    		case IP_A_TYPE:  
    			maskValue = DefaultIpAMask;  
    			break;  
    		default:  
    			maskValue = DefaultIpCMask;  
    	}  
    	return maskValue;  
    }  
    
    public static int checkIpV4Type(String ipV4){  
    	int inValue = getIpV4Value(ipV4);  
    	if(inValue >= IpCTypeRange[0] && inValue <= IpCTypeRange[1]){  
    		return IP_C_TYPE;  
    	}  
    	else if(inValue >= IpBTypeRange[0] && inValue <= IpBTypeRange[1]){  
    		return IP_B_TYPE;  
    	}  
    	else if(inValue >= IpATypeRange[0] && inValue <= IpATypeRange[1]){
    		return IP_A_TYPE;  
    	}  
    	return IP_OTHER_TYPE;  
    }
    
    
    public static boolean checkSameSegment(String ip1,String ip2, int mask){
           if(!isValidIpV4(ip1)){  
               return false;  
           }  
           if(!isValidIpV4(ip2)){  
        	   return false;  
           }
           int ipValue1 = getIpV4Value(ip1);  
           int ipValue2 = getIpV4Value(ip2);  
           return (mask & ipValue1) == (mask & ipValue2);  
    }
    
    public static boolean checkSameSegmentByDefault(String ip1,String ip2){  
    	int mask = getDefaultMaskValue(ip1); 
    	return checkSameSegment(ip1,ip2,mask);  
    }
    
    public static void main(String[] args){
    	//System.out.println(NetUtils.qureyRemoteMacAddr("172.16.11.114"));
    	String ip1="172.16.1.2";
    	String ip2="172.16.1.5";
    	
    	System.out.println(checkSameSegmentByDefault(ip1, ip2));
    }
}

class UdpGetClientMacAddr {
    private String         remoteAddr;
    private int            remotePort = 137;
    private byte[]         buffer      = new byte[1024];
    private DatagramSocket ds          = null;

    public UdpGetClientMacAddr(String strAddr) throws Exception {
        remoteAddr = strAddr;
        ds = new DatagramSocket();
    }

    protected final DatagramPacket send(final byte[] bytes) throws IOException {
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(remoteAddr), remotePort);
        ds.send(dp);
        return dp;
    }

    protected final DatagramPacket receive() throws Exception {
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
        ds.receive(dp);
        return dp;
    }

    protected byte[] GetQueryCmd() throws Exception {
        byte[] t_ns = new byte[50];
        t_ns[0] = 0x00;
        t_ns[1] = 0x00;
        t_ns[2] = 0x00;
        t_ns[3] = 0x10;
        t_ns[4] = 0x00;
        t_ns[5] = 0x01;
        t_ns[6] = 0x00;
        t_ns[7] = 0x00;
        t_ns[8] = 0x00;
        t_ns[9] = 0x00;
        t_ns[10] = 0x00;
        t_ns[11] = 0x00;
        t_ns[12] = 0x20;
        t_ns[13] = 0x43;
        t_ns[14] = 0x4B;

        for (int i = 15; i < 45; i++) {
            t_ns[i] = 0x41;
        }

        t_ns[45] = 0x00;
        t_ns[46] = 0x00;
        t_ns[47] = 0x21;
        t_ns[48] = 0x00;
        t_ns[49] = 0x01;
        return t_ns;
    }

    
    protected final String getMacAddr(byte[] brevdata) throws Exception {
        int i = brevdata[56] * 18 + 56;
        String sAddr = "";
        StringBuffer sb = new StringBuffer(17);
        for (int j = 1; j < 7; j++) {
            sAddr = Integer.toHexString(0xFF & brevdata[i + j]);
            if (sAddr.length() < 2) {
                sb.append(0);
            }
            sb.append(sAddr.toUpperCase());
            if (j < 6) sb.append(':');
        }
        return sb.toString();
    }

    public final void close() {
        try {
            ds.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public final String getRemoteMacAddr() throws Exception {
        byte[] bqcmd = GetQueryCmd();
        send(bqcmd);
        DatagramPacket dp = receive();
        String smac = getMacAddr(dp.getData());
        close();
        return smac;
    }
   
}

