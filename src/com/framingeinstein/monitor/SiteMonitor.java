/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.framingeinstein.monitor;


import com.framingeinstein.monitor.configuration.Configuration;
import com.framingeinstein.monitor.interfaces.Interface;
import com.framingeinstein.monitor.report.Report;
import com.framingeinstein.monitor.servers.MonitorServer;
import com.framingeinstein.monitor.servers.ReportServer;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import org.jdom.JDOMException;

/**
 *
 * @author Framingeinstein
 */

public class SiteMonitor
{
    private ReportServer reportServer;
    private MonitorServer monitorServer;
    private List report = new ArrayList<Report>();
    private int port;
    private ServerSocket messageListener;
    private Interface GUI = null;
    //private HashMap<String, String> servers;
    private Configuration config;
    private File configFile;
    
    public SiteMonitor(int port, File config) throws JDOMException, IOException
    {
        this.port = port;
        this.report = (List) Collections.synchronizedList(this.report);
        if(config != null)
        {
            this.config = new Configuration(config);
            this.configFile = config;
            
        }
        /*
        this.servers = new HashMap<String, String>();
        this.servers.put("www.congruentmedia.com", "/");
        this.servers.put("www.postpastdue.com", "/");
        this.servers.put("www.hpbexpo.com", "/");
        this.servers.put("postpastdue.cm-dev.com", "/content/error/throwerror.cfm");
        */
    }
    
    public synchronized void startMonitorServer()
    {
        if(this.monitorServer != null)
        {
            stopMonitorServer();
        }
        this.monitorServer = new MonitorServer(this.config.getMonitorConfig(), report);
        if(this.GUI != null)
        {
            this.monitorServer.setListener(GUI);
            this.GUI.setMonitorStarted(true);
        }
        this.monitorServer.start();
       
    }
    public synchronized void startReportServer() throws IOException
    {
         if(this.reportServer != null)
        {
            stopReportServer();
        }
        this.reportServer = new ReportServer(this.config.getReportingConfig(), report);
        this.reportServer.start();
        if(this.GUI != null)
        {
            this.GUI.setReportingStarted(true);
        }
    }
    
    public synchronized void stopMonitorServer()
    {   if(monitorServer != null)
        {
            monitorServer.terminate();
            monitorServer = null;
        }
        
        if(this.GUI != null)
        {
            this.GUI.setMonitorStarted(false);
        }
    }
    public synchronized void stopReportServer()
    {
        if(reportServer != null)
        {
            reportServer.terminate();
            reportServer = null;
        }
        if(this.GUI != null)
        {
            this.GUI.setReportingStarted(false);
        }
    }
    public synchronized void start()
    {
       
        if(this.config.getMonitorConfig().Enabled)
        {
            startMonitorServer();       
        }
        if(this.config.getReportingConfig().Enabled)
        {
            try {
                startReportServer();
            } 
            catch (IOException ex) {
                //Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch(NullPointerException ex)
            {
                //Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
       
    }
    public synchronized void stop()
    {   
        try 
        {
            stopReportServer();
        }
        catch(NullPointerException ex)
        {
            //Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        stopMonitorServer();
        
                
    }
    
       
    public synchronized ReportServer getReportServer()
    {
        return this.reportServer;
    }
    public synchronized MonitorServer getMonitorServer()
    {
        return this.monitorServer;
    }
    
    public synchronized void startGUI()
    {
        if(GUI == null)
        {
            try {
                
                this.GUI = new Interface(this);
            } catch (InstantiationException ex) {
                Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            this.GUI.setVisible(true);
        }
    }
    
    public synchronized void stopGUI()
    {
        if(GUI != null)
        {
            this.GUI.setVisible(false);           
        }
    }
    
    public synchronized boolean canRun(ArrayList args)
    {
        try 
        {
            messageListener = new ServerSocket(55999);
            MessageListenTask task = new MessageListenTask();
            task.start();
        } 
        catch (IOException ex) 
        {
            Socket socket = null;
            ObjectOutputStream out = null;
            try {
                socket = new Socket("localhost", 55999);
                out = new ObjectOutputStream(socket.getOutputStream());
                //Send args to Monitor
                out.writeObject(args);
            } catch (UnknownHostException ex1) {
                Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (IOException ex1) {
                Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex1);
            }
            finally
            {
                if(out != null)
                {
                    try {
                        out.close();
                    } catch (IOException ex1) {
                        Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
           
           return false;
        }
        return true;
        
    }
    
    
    private synchronized void setGUI(Interface gui)
    {
        this.GUI = gui;
    }
    
    
    public static void main(String [] args) throws Exception
    {
        
        File config = null;
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(args));
        int cpos = list.indexOf("-c");
        if(cpos > -1 && list.size() - 1 >= cpos + 1)
        {
            config = new File((String)list.get(cpos + 1));           
        }
        
       
        /*
         * 
         * /Users/jason/Documents/projects/java/cfmon/src/com/congruentmedia/monitor/configuration/config.xml
         */
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Server Monitor");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        
        SiteMonitor monitor = new SiteMonitor(41001, config);     
        
        if(!monitor.canRun(list))
        {
            System.exit(1);
        }
        else
        {
            if(config == null || !config.exists())
            {
                throw new Exception("The configuration file specified does not exist.");
            }
        }
                
        int ipos = list.indexOf("-i");
        
        if(ipos > -1)
        {
            monitor.setGUI(new Interface(monitor));         
        }
        else
        {
            monitor.start();
        }
        
        
    }
    
    private synchronized void reload()
    {
        try 
        {
            this.config = new Configuration(this.configFile);
            stopMonitorServer();
            stopReportServer();
                    
            startMonitorServer();
            startReportServer();
        } catch (JDOMException ex) {
            Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private class MessageListenTask extends Thread
    {
        private boolean runnable = true;
        public MessageListenTask()
        {
            super();
            super.setName("MessageListenTask@" + this.hashCode());
        }
                
        public void terminate()
        {
            try {
                messageListener.close();
            } catch (IOException ex) {
                Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        @Override
        public void run()
        {
            while(this.runnable)
            {
                try {
                    Socket sock = messageListener.accept();
                    MessageRecievedTask task = new MessageRecievedTask(sock);
                    task.start();
                } catch (IOException ex) {
                    this.runnable = false;
                    Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
    }
    
    private class MessageRecievedTask extends Thread            
    {
        private Socket sock;
        public MessageRecievedTask(Socket socket)
        {
            this.sock = socket;
            super.setName("MessageRecievedTask@" + this.hashCode());
        }
        @Override
        public void run()
        {
            ObjectInputStream in = null;
            try 
            {
                System.out.println("Recieved command from another process.");
                in = new ObjectInputStream(sock.getInputStream());
                ArrayList list = (ArrayList) in.readObject();
                if(list.contains("-c"))
                {
                    int cpos = list.indexOf("-c");
                    if(cpos > -1 && list.size() - 1 >= cpos + 1)
                    {
                        configFile = new File((String)list.get(cpos + 1));           
                        reload();
                    }
                }
                if(list.contains("-kill"))
                {
                    System.out.println("Shuting down");
                    System.exit(0);
                }
                if(list.contains("-i"))
                {
                    System.out.println("Launching GUI");
                    startGUI();
                }
                if(list.contains("-reload"))
                {
                    reload();
                }
                if(list.contains("-x"))
                {
                    stopGUI();
                }
                
                
                //System.out.println(Arrays.deepToString(list.toArray()));
            }

            catch (IOException ex) 
            {
                Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }            catch (ClassNotFoundException ex) 
            {
                Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally 
            {
                try 
                {
                    in.close();
                }
                catch (IOException ex) 
                {
                    Logger.getLogger(SiteMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
