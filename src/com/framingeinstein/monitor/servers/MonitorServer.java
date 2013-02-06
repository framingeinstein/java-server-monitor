/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.framingeinstein.monitor.servers;


import com.framingeinstein.monitor.configuration.MonitorConfiguration;
import com.framingeinstein.monitor.configuration.Site;
import com.framingeinstein.monitor.interfaces.Listener;
import com.framingeinstein.monitor.report.Report;
import com.framingeinstein.monitor.report.SiteReport;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.NoSuchProviderException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.HeadMethod;
/**
 *
 * @author Framingeinstein
 */
public class MonitorServer extends Thread
{
    private boolean runnable = true;
    private long period = 1000 * 30;
   // private HashMap map;
    private List sites;
    private List reports;
    private WeakReference listenerRef;
    private MonitorConfiguration configuration;
    
    //private HealingTask healingTask;
    
    public MonitorServer(MonitorConfiguration config, List reports)
    {
        this(config.Interval, config.Sites, reports);
        this.configuration = config;
        this.runnable = this.configuration.Enabled;
        
    }   
    
    public MonitorServer(long period, List sites, List reports)
    {        
        this.period = period;
        this.sites = sites;
        this.reports = reports;     
        super.setName("MonitorServer@" + this.hashCode());
        
    }
    
    public void terminate()
    {
        this.runnable = false;
    }
    
    public boolean isRunnable()
    {
        return this.runnable;
    }

    @Override
    public void run() 
    {
        System.out.println("Monitor Server Running");
        while(this.runnable)
        {
           
            try 
            {
                Report report = new Report(this.configuration.Mail);
                                
		Iterator iterator = this.sites.iterator();
                
		while(iterator.hasNext())
		{
			
                        //SiteReport siteReport = new SiteReport();
                        Site site = (Site) iterator.next();
			//Map.Entry entry = (Map.Entry)iterator.next();
			String server = site.name;
			String path = site.path;
			HttpClient client = new HttpClient();
                        client.getParams().setParameter("http.socket.timeout", new Integer(this.configuration.ClientTimeout));
			//HttpMethod method = new GetMethod("http://" + server + ":80" + path);
                        String uri = server.matches("^https?://.*") ? server : "http://" + server;
                        
                        HttpMethod method = new HeadMethod(uri + path);
                        //method = new HeadMethod();
                        method.setFollowRedirects(true);
                        
			try 
                        {
                            long start = System.currentTimeMillis();
                            int statusCode = client.executeMethod(method);
                            long responceTime = System.currentTimeMillis() - start;                            
                            report.addSiteReport(new SiteReport(server, path, statusCode, new Date(), responceTime));
			} 
                        catch(java.net.UnknownHostException e)
                        {
                            report.addSiteReport(new SiteReport(server, path, HttpStatus.SC_BAD_GATEWAY, new Date(), this.configuration.ClientTimeout));
                        }
                        catch(java.net.SocketTimeoutException e)
                        {                            
                            report.addSiteReport(new SiteReport(server, path, HttpStatus.SC_REQUEST_TIMEOUT, new Date(), this.configuration.ClientTimeout));
                        }
                        catch (HttpException e) {
				//TODO Log this and sent error email.
				e.printStackTrace();
			} catch (IOException e) {
				//TODO Log this and sent error email.wswws
				e.printStackTrace();
			}
                        
			
		}
                report.sendMailOnError();
                this.update(report);
                this.reports.add(report);
                if(this.reports.size() > 20)
                {
                    this.reports.remove(0);
                }
                Thread.sleep(this.period);
            } 
            catch (NoSuchProviderException ex) {
                Logger.getLogger(MonitorServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (InterruptedException ex) 
            {
                Logger.getLogger(MonitorServer.class.getName()).log(Level.SEVERE, null, ex);
            }           
        }
        System.out.println("Monitor Server Has Stoped");
               
    }

    public void setListener(Listener listener)
    {
        this.listenerRef = new WeakReference(listener);
    }
    
    public Object getListener()
    {
        return this.listenerRef == null ? null : this.listenerRef.get();
    }    
    public void update(Report report)
    {
        Object listener = getListener();
        if(listener != null)
        {
            ((Listener)listener).update(report);
        }
    }
    /*
    private class HealingTask extends Thread
    {
        public HealingTask(Report report)
        {
            this.start();
        }
    }
    */
}
