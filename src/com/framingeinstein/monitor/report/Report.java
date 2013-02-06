/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.framingeinstein.monitor.report;

import com.framingeinstein.monitor.configuration.MailConfiguration;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import org.apache.commons.httpclient.*;
/**
 *
 * @author Framingeinstein
 */
public class Report {
    
    ArrayList<SiteReport> siteReports = new ArrayList<SiteReport>();
    private boolean hasError = false;
    private Date timestamp = new Date();
    private MailConfiguration mailConfig;
    private long totalResponceTime = 0;
    private long maxResponceTime = Integer.MIN_VALUE;
    private long minResponceTime = Integer.MAX_VALUE;
    private double avgResponceTime = 0;
    
    
    public Report(MailConfiguration mailConfig)
    {        
        this.mailConfig = mailConfig;
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        
    }
    
    public boolean addSiteReport(SiteReport siteReport)
    {
       
        this.hasError = this.hasError || siteReport.hasError();
        boolean result = siteReports.add(siteReport);
        
        this.totalResponceTime += siteReport.getResponsetime();
        this.avgResponceTime = this.totalResponceTime / this.siteReports.size();
        this.minResponceTime = (siteReport.getResponsetime() < this.minResponceTime) ? siteReport.getResponsetime(): this.minResponceTime;
        this.maxResponceTime = (siteReport.getResponsetime() > this.maxResponceTime) ? siteReport.getResponsetime(): this.maxResponceTime;
        
        return result;
    }
    
    public boolean HasError()
    {
        return this.hasError;
    }
    
    public String getDateString()
    {
         DateFormat format = SimpleDateFormat.getDateTimeInstance(); 
         return format.format(this.timestamp);
    }
     
    public String[][] getData()
    {
        Iterator iterator = this.siteReports.iterator();
        String [][] sites =  new String[this.siteReports.size()][4];
        int i = 0;
        while(iterator.hasNext())
        {
            DateFormat format = SimpleDateFormat.getDateTimeInstance();  
            SiteReport sr = (SiteReport) iterator.next();
            String [] info = {sr.getServer(), format.format(sr.getTimestamp()), "" + sr.getResponsetime() + " ms", sr.getStatus() + " " + sr.getStatusText()};
            sites[i] = info;
            i++;
        }
        return sites;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        Iterator iterator = this.siteReports.iterator();
        DateFormat format = SimpleDateFormat.getDateTimeInstance();        
        builder.append("\t<report date=\""+format.format(this.timestamp)+"\" minresponse=\""+this.minResponceTime+"\" maxresponse=\""+this.maxResponceTime+"\" avgresponse=\""+this.avgResponceTime+"\" haserrors=\""+this.HasError()+"\">\n");
        while(iterator.hasNext())
        {           
            builder.append("\t\t" + iterator.next().toString() + "\n");                       
        }
        builder.append("\t</report>\n");        
        return builder.toString();
    }
    
    public String renderErrors()
    {
         StringBuilder builder = new StringBuilder();
         Iterator iterator = this.siteReports.iterator();
         DateFormat format = SimpleDateFormat.getDateTimeInstance();
         while(iterator.hasNext())
         {
            SiteReport sr = (SiteReport) iterator.next();
            if(sr.hasError())
            {
                builder.append(sr.getServer() + sr.getPath() + " returned an error ("+sr.getStatusText()+") on "+format.format(sr.getTimestamp())+"\n\r" );
            }
         }
         return builder.toString();
    }
    
    public void sendMailOnError() throws NoSuchProviderException
    {   
        if((this.mailConfig != null && this.mailConfig.Server != null && this.mailConfig.Server.compareTo("") != 0) && this.HasError())
        {
            Properties props = new Properties();
            props.put("mail.smtp.host", this.mailConfig.Server);
            props.put("mail.from", "monitor@framingeinstein.com");
            
            props.put("mail.smtp.auth", "true");
            props.put("mail.debug", "true");
            props.put("mail.smtp.port", 465);
            props.put("mail.smtp.socketFactory.port", 465);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            
            if(this.mailConfig.Username != null && this.mailConfig.Username.compareTo("") != 0)
            {
                props.put("mail.user", this.mailConfig.Username);
                if(this.mailConfig.Password != null && this.mailConfig.Password.compareTo("") != 0)
                {
                    props.put("mail.password", this.mailConfig.Password);
                }
            }
            
            //props.get("")
            
            final Properties p = (Properties)props.clone();
            
            //Session session = Session.getInstance(props, null);
            //final Properties p = props.
            Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                 
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(p.get("mail.user").toString(), p.get("mail.password").toString());
            }
            });
            String text = "The following sites returned errors on " + getDateString() + "\r\n\r\n";
            text += renderErrors();
            
            Transport t = session.getTransport("smtp");
            try 
            {
                MimeMessage msg = new MimeMessage(session);
                msg.setFrom();
                msg.setRecipients(Message.RecipientType.TO, this.mailConfig.ErrorTo);
                msg.setSubject("Site Monitor Error Report");
                msg.setSentDate(new Date());
                msg.setText(text);
                
                
                t.connect(props.getProperty("mail.smtp.host"), (Integer)props.get("mail.smtp.port"), props.getProperty("mail.user"),props.getProperty("mail.password"));
                t.sendMessage(msg, msg.getAllRecipients());
                //Transport.send(msg);
            } 
            catch (MessagingException mex) 
            {
                System.out.println("send failed, exception: " + mex);
            }
            finally {
                System.out.println("send failed, exception: " + props.getProperty("mail.smtp.port"));
                try {
                    t.close();
                } catch (MessagingException ex) {
                    Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    
}
