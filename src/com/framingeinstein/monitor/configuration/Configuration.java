/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.framingeinstein.monitor.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author jmorgan
 */
public class Configuration {
    private MonitorConfiguration monitorConfiguration;
    private ReportingConfiguration reportingConfiguration;
    
    public MonitorConfiguration getMonitorConfig()
    {
        return monitorConfiguration;
    }
    
    public ReportingConfiguration getReportingConfig()
    {
        return reportingConfiguration;
    }
    
    public Configuration(File config) throws JDOMException, IOException
    {
       SAXBuilder oBuilder = new SAXBuilder();
       Document oDoc = oBuilder.build(config);
       Element root = oDoc.getRootElement();
       List children = root.getChildren();
       Iterator iterator = children.iterator();
       while(iterator.hasNext())
       {
            Element e = (Element)iterator.next();
            if(e.getName().compareTo("reporting") == 0)
            {
                 setReporting(e);
            }
            else if(e.getName().compareTo("monitoring") == 0)
            {
                setMonitoring(e);
            }           
           
       }
    }
    
    private void setReporting(Element e)
    {
        boolean enabled = Boolean.valueOf(e.getAttributeValue("enabled")).booleanValue();
        String sport = e.getAttributeValue("port");        
        int port = Integer.valueOf(sport).intValue();
        this.reportingConfiguration = new ReportingConfiguration(enabled, port);
    }
    private void setMonitoring(Element e)
    {
        boolean enabled = Boolean.valueOf(e.getAttributeValue("enabled")).booleanValue();
        long interval = Long.valueOf(e.getAttributeValue("interval")).longValue() * 1000;
        int timeout = Integer.valueOf(e.getAttributeValue("timeout")).intValue() * 1000;
        List children = e.getChildren();
        Iterator iterator = children.iterator();
        MailConfiguration mail = null;
        ArrayList sites = null;
        while(iterator.hasNext())
        {
            Element element = (Element)iterator.next();
            if(element.getName().compareTo("mail") == 0)
            {
                mail = getMail(element);                
            }
            else if (element.getName().compareTo("sites") == 0)
            {
                sites = getSites(element);
            }
        }
        this.monitorConfiguration = new MonitorConfiguration(enabled, interval, mail, sites, timeout);
    }
    private MailConfiguration getMail(Element el)
    {   
        String server = el.getAttributeValue("server");
        String username = el.getAttributeValue("username");
        String password = el.getAttributeValue("password");
        String errorto = el.getAttributeValue("errorto");
        return new MailConfiguration(server, username, password, errorto);
    }
    private ArrayList getSites(Element el)
    {
        ArrayList sitelist = new ArrayList();
        List sites = el.getChildren();
        Iterator iterator = sites.iterator();
        while(iterator.hasNext())
        {
            Element site = (Element) iterator.next();
            sitelist.add(new Site(site.getAttributeValue("name"), site.getAttributeValue("path")));
            //siteMap.put(site.getAttributeValue("name"), site.getAttributeValue("path"));
        }
        return sitelist;
    }
       
}
