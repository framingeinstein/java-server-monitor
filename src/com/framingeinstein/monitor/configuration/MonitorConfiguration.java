/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.framingeinstein.monitor.configuration;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author jmorgan
 */
public class MonitorConfiguration 
{
    public final boolean Enabled;
    public final long Interval;
    public final MailConfiguration Mail;
    public final List Sites;
    public final int ClientTimeout;
    
    public MonitorConfiguration(final boolean enabled, final long interval, final MailConfiguration mail, final List sites, final int timeout)
    {
        this.Enabled = enabled;
        this.Interval = interval;
        this.Mail = mail;
        this.Sites = sites;
        this.ClientTimeout = timeout;
        
        
    }   
        /*
        <enabled>true</enabled>
        <interval>20</interval><!-- seconds -->
        <mail>
            <server>sbs-srv-01.Congruent.local</server>
            <username>jmorgan</username>
            <password>jmor282g</password>
        </mail>
        <sites>
            <site name="www.congruentmedia.com" path="/" />
            <site name="www.hpbexpo.com" path="/" />
            <site name="www.postpastdue.com" path="/" />
        </sites>
        */
}
