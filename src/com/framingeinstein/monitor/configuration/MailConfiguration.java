/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.framingeinstein.monitor.configuration;

/**
 *
 * @author jmorgan
 */
public class MailConfiguration 
{
    public final String Server, Username, Password, ErrorTo;
    
    public MailConfiguration(final String server, final String username, final String password, final String errorto)            
    {
        this.Server = server;
        this.Username = username;
        this.Password = password;
        this.ErrorTo = errorto;
    }  
    
    /*
      * <mail>
            <server>sbs-srv-01.Congruent.local</server>
            <username>jmorgan</username>
            <password>jmor282g</password>
        </mail>
     * */
    
    
}
