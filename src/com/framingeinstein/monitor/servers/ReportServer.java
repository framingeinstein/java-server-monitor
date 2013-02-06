/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.framingeinstein.monitor.servers;

import com.framingeinstein.monitor.configuration.ReportingConfiguration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Framingeinstein
 */
public class ReportServer extends Thread
{
    private int port;
    private boolean runnable = true;
    private List report;
    private ServerSocket server;
    private ReportingConfiguration configuration;
    private ExecutorService executor = Executors.newCachedThreadPool();
    public ReportServer(ReportingConfiguration config, List report) throws IOException
    {
        this(config.Port, report);
        this.configuration = config;
        this.runnable = true;
        if(this.runnable)
        {
            this.server = new ServerSocket(this.port);
            //System.out.println("SO_TIMEOUT :: " + this.server.getSoTimeout());
            
        }
    }
    private ReportServer(int port,List report) throws IOException
    {
        this.port = port;
        this.report = report;
        super.setName("ReportServer@" + this.hashCode());
        super.setPriority(Thread.MIN_PRIORITY);
    }

    public void terminate()
    {
        this.runnable = false;
        try {            
            this.server.close();
        } catch (IOException ex) {
            System.out.println("Report Server Has Stoped");
        }
              
    }
  
    
    public boolean isRunnable()
    {
        return this.runnable;       
    }
   
    
    @Override
    public void run() 
    {
        System.out.println("Report Server Running");
        while(this.runnable)
        {   try 
            { 
                executor.submit(new ReportTask(server.accept(), this.report));
            } 
            catch (IOException ex) 
            {            
                /*Logger.getLogger(ReportServer.class.getName()).log(Level.SEVERE, null, ex); */                   
            }
        }    
        System.out.println("Report Server Has Stoped");
    }
    
    private class ReportTask implements Runnable
    {
        private Socket socket;
        private List reports;
        
        public ReportTask(Socket socket, List reports) throws SocketException
        {   
            this.socket = socket;
            this.socket.setSoLinger(true, 4);
            this.socket.setTcpNoDelay(true);            
            //System.out.println("SO_Linger :: " + this.socket.getSoLinger());
            this.reports = Collections.unmodifiableList(reports);
            //super.setName("ReportTask@" + this.hashCode());
            //super.setPriority(Thread.MAX_PRIORITY);            
        }
        
        @Override
        public void run()
        {
           
            PrintWriter out = null;
            BufferedReader reader;
            try 
            {
                //socket.shutdownInput();
                out = new PrintWriter(this.socket.getOutputStream(), true);               
                reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                //this.socket.getInputStream().
                String linein = "";
                int i = 0;
                /*
                for(linein = reader.readLine(); linein.compareTo("") != 0;linein = reader.readLine(),i++)
                {
                    if(i > 45)
                    {
                        break;
                    }
                }
                */
                while((linein = reader.readLine()).compareTo("") != 0)
                {
                    System.out.println(linein);
                    //if(linein.startsWith("GET:"))
                }
                
                StringBuilder builder = new StringBuilder();
                StringBuilder headers = new StringBuilder();                
                
                //headers.append("HTTP/1.1 200 OK \r\n");
                //headers.append("Content-Type: text/xml \r\n");
                //headers.append("Connection: close \r\n");                
                out.print("HTTP/1.0 200 OK\r\n");
                out.print("Content-Type: text/xml\r\n");
                //out.print("Connection: close\r\n");
                
                //pout.print(headers.toString());
                
                builder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n");
                builder.append("<reports>\r\n");
                for(i = reports.size() -1; i >= 0; i--)
                {
                    builder.append(reports.get(i).toString());
                }
                builder.append("</reports>\r\n");
                /*
                while(iterator.hasNext())
                {
                    builder.append(iterator.next().toString());
                    
                }
                */
                //headers.append("Content-Length: " + builder.toString().length() + "\r\n");
                out.print("Content-Length: " + builder.toString().length() + "\r\n");
                out.print("\r\n");
                out.flush();
                out.print(builder.toString());               
                out.flush();
                out.close();   
                reader.close();
                socket.close();
            } 
            catch (IOException ex) 
            {                             
                Logger.getLogger(ReportServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally
            {
                try 
                {   
                    if(!socket.isClosed())
                    {
                        socket.close();
                    }
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(ReportServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
           
           
        }
    }
    
}
