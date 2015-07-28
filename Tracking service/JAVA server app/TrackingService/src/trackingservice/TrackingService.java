/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trackingservice;
import java.net.DatagramPacket;
import java.net.DatagramSocket; 
import java.lang.Runnable;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mukesh
 */
public class TrackingService implements Runnable{

    ServerSocket server;
    String[] array;
    int udpport = 6000;
    DatagramSocket dsocket;
    DatagramPacket packet;
    Socket socket;
    int i=0;

    public TrackingService(int size) {
        array = new String[size];
        
        try {
            dsocket = new DatagramSocket(udpport);
            server = new ServerSocket();
            //
//        String[] array={"Raj","loc",
//                        "Sachin","loc",
//                        "Virat","loc",
//                        "Rahul","loc"};
        } catch (Exception ex) {
            Logger.getLogger(TrackingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        // Declaring a rinnable object
        TrackingService object = new TrackingService(8);
        
        Thread udp = new Thread(object,"UDP");
        Thread tcp = new Thread(object,"TCP");
        
        tcp.setPriority(Thread.MAX_PRIORITY);
        udp.setPriority(Thread.MIN_PRIORITY);
        
        tcp.start();
        udp.start();

        
    }
    
    // Thread Running....
    public void run(){
        int temp=0;
        int j=0,l=0;
        
        
        try{
           
            if (Thread.currentThread().getName().equalsIgnoreCase("UDP")) {
                // Create a socket to listen on the port.
                    System.out.println("UDP"+l);
                    dsocket.setBroadcast(true);
                    byte[] udpData = new byte[256];
                    packet = new DatagramPacket(udpData, udpData.length);
                     // Create a socket to listen on the port.    
                    l++;
            }
            
            if (Thread.currentThread().getName().equalsIgnoreCase("TCP")) {
                
                System.out.println("TCP"+l);
                l++;
                //Bind to a local address
                server.bind(new InetSocketAddress(8000));
                if (server.isBound()) {
                    System.out.println("Bind Successful");
                }    
            }
            
        // the thread cond. running ....
        while(true){
            
            try{
            
                if (Thread.currentThread().getName().equalsIgnoreCase("TCP")) {
                    System.out.println("tcp thread running");
                    
                    // create Socket
                    socket = server.accept();
                    
                    // client --> server commnication
                    InputStream is = socket.getInputStream();
                    System.out.println(""+is);
                    OutputStream os = socket.getOutputStream();
                    byte[] buff = new byte[50];
                    is.read(buff);
                    
                   // System.out.println("buff");                    
                    String rawUser = new String(buff, "US-ASCII");
                    String[] d = rawUser.split("//");
                    String user = d[0];
                    System.out.println("Request is for user : "+user); 
                    for(int k=0;k<array.length;k+=2){
                        System.out.println("comparing "+user+" and "+array[k]);
                        if(user.equalsIgnoreCase(array[k])){
                            System.out.println("Sending location of : "+array[k]);
                            //k++;
                            System.out.println("location : "+array[k+1]);
                            String userL = array[k+1]+"//";
                            byte[] userLoca = userL.getBytes("US-ASCII");
                            os.write(userLoca);
                        }
                     }
                    os.close();
                    is.close();
                    
                }
            
            
            
                if (Thread.currentThread().getName().equalsIgnoreCase("UDP")) {
                    System.out.println("udp thread running");
                    dsocket.receive(packet);
                    byte[] dataReceived = packet.getData();                    
                      // Convert the contents to a string, and display them
                     String msg = new String(dataReceived, "US-ASCII");
                     String[] v = msg.split("//");
                     String user = v[0];
                     String loc = v[1];

                     System.out.println(user+" : "
                         + loc);

                     for (j = 0; j < array.length; j+=2) {
                         if (user.equalsIgnoreCase(array[j])) {
                             System.out.println("Location of "+array[j]+
                                     " changed to "+loc);
                             array[i+1] =loc; 
                             temp++;
                         }
                     }
                     if (temp==0) {
                        array[i]=user;
                        array[i+1]=loc;
                        i+=2;
                    }
                    temp=0;
                }
            
            }catch(Exception ee){
                ee.printStackTrace();
            }
//            System.out.println("Thread Running "+Thread.currentThread().getName()+i);
//            i++;
            
            try{
                Thread.sleep(10000);
            }catch(Exception ee){
                ee.printStackTrace();
            }
        }
        
     }catch(Exception ee){
            ee.printStackTrace();
        }    
    }
    
}
