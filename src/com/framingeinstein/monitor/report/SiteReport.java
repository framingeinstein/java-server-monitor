/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.framingeinstein.monitor.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.httpclient.*;
/**
 *
 * @author Jason Morgan
 */
public class SiteReport 
{

    private final String server;
    private final String path;
    private final int status;
    private final Date timestamp;
    private final long responsetime;
    
    private static ArrayList<Integer> errorConditions = new ArrayList<Integer>();
    
    static
    {
        errorConditions.add(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        errorConditions.add(HttpStatus.SC_REQUEST_TIMEOUT);
        errorConditions.add(HttpStatus.SC_BAD_GATEWAY);
        errorConditions.add(HttpStatus.SC_NOT_FOUND);
    }
    
    public SiteReport(String server, String path, int status, Date timestamp, long responsetime)
    {
        this.server = server;
        this.path = path;
        this.status = status;
        this.timestamp = timestamp;
        this.responsetime = responsetime;
        
    }
    
    public boolean hasError()
    {
        if(SiteReport.errorConditions.contains(getStatus()))
        {
            return true;
        }
        return false;
    }
    
    public int getStatus()
    {
        return this.status;
    }
    
    public String getStatusText()
    {
        return HttpStatus.getStatusText(this.status);
    }
    
    public String getServer()
    {
        return this.server;
    }
    public String getPath()
    {
        return this.path;
    }
    public Date getTimestamp()
    {
        return this.timestamp;
    }
    public long getResponsetime()
    {
        return this.responsetime;
    }
    
    @Override
    public String toString()
    {
        DateFormat format = SimpleDateFormat.getDateTimeInstance();
        return "<sitereport site=\""+this.server+"\" path=\""+this.path+"\" time=\""+format.format(timestamp)+"\" status=\""+this.status+ " " + getStatusText() + "\" responcetime=\""+this.responsetime+"\" />";
    }
    
}
