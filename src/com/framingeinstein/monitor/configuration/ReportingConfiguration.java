/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.framingeinstein.monitor.configuration;

/**
 *
 * @author jmorgan
 */
public class ReportingConfiguration {
    public final boolean Enabled;
    public final int Port;
    
    public ReportingConfiguration(final boolean enabled, final int port)
    {
        this.Enabled = enabled;
        this.Port = port;
    }
    
}
