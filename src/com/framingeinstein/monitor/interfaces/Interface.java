/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.framingeinstein.monitor.interfaces;

import com.framingeinstein.monitor.SiteMonitor;
import com.framingeinstein.monitor.report.Report;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
/**
 *
 * @author jmorgan
 */
public class Interface extends JFrame implements Listener
{

    private SiteMonitor monitor;
    private JButton startMonitorServer = new JButton("Start Monitor");    
    private JButton stopMonitorServer = new JButton("Stop Monitor");
    private JButton startReportServer = new JButton("Start Reporter");    
    private JButton stopReportServer = new JButton("Stop Reporter");
    private JPanel panel = new JPanel();
    //private JTable table;
    
    private static Color ok = new Color(50,255,50,128);
    private static Color error = new Color(255,50,50,128);
    
    private void initGUI()
    {
        
        boolean show = monitor.getMonitorServer() == null;
        startMonitorServer.setSize(150, 75);
        startMonitorServer.setVisible(show);  
        panel.add(startMonitorServer);        
        stopMonitorServer.setSize(150, 75);
        show = monitor.getMonitorServer() != null;
        stopMonitorServer.setVisible(show);
        panel.add(stopMonitorServer);        
        startReportServer.setSize(150, 75);
        show = monitor.getReportServer() == null;
        startReportServer.setVisible(show);        
        panel.add(startReportServer);
        stopReportServer.setSize(150, 75); 
        show = monitor.getReportServer() != null;
        stopReportServer.setVisible(show);
        panel.add(stopReportServer);
        
        //table = new JTable();
        this.add(panel);
    }
    
    public Interface(SiteMonitor monitor) throws InstantiationException
    {
        super("Site Monitor");
        this.monitor = monitor;  
        if(this.monitor.getMonitorServer() != null)
        {
            this.monitor.getMonitorServer().setListener(this);
        }
        //this.monitor.startGUI(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startMonitorServer.addActionListener(new StartMonitorCommand());
        stopMonitorServer.addActionListener(new StopMonitorCommand());
        startReportServer.addActionListener(new StartReporterCommand());
        stopReportServer.addActionListener(new StopReporterCommand());     
        initGUI();
        this.pack();
        //this.setTitle("Server Monitor");
        this.setName("Server Monitor");
        this.setVisible(true);
    }
    
    
    public void setMonitorStarted(boolean started)
    {
        startMonitorServer.setVisible(!started);  
        stopMonitorServer.setVisible(started);        
    }
    
    public void setReportingStarted(boolean started)
    {
        startReportServer.setVisible(!started);  
        stopReportServer.setVisible(started);
    }
    
    private class StopMonitorCommand implements ActionListener
    {
        public void actionPerformed(ActionEvent e) {
            monitor.stopMonitorServer();  
            //((JButton)e.getSource()).setVisible(false);
            //startMonitorServer.setVisible(true);
        }
    
    }
    
    private class StopReporterCommand implements ActionListener
    {
        public void actionPerformed(ActionEvent e) {
            monitor.stopReportServer();
            //((JButton)e.getSource()).setVisible(false);
            //startReportServer.setVisible(true);
        }    
    }
    
     private class StartMonitorCommand implements ActionListener
    {
        public void actionPerformed(ActionEvent e) {
            monitor.startMonitorServer();
            //((JButton)e.getSource()).setVisible(false);
            //stopMonitorServer.setVisible(true);
        }
    
    }
    
    private class StartReporterCommand implements ActionListener
    {
        public void actionPerformed(ActionEvent e) {
            try 
            {
                monitor.startReportServer();
                //((JButton)e.getSource()).setVisible(false);
                //stopReportServer.setVisible(true);
            }
            catch (IOException ex) 
            {
                Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        }
    }

    public void update(Report report) {
        if(report.HasError())
        {
            this.panel.setBackground(error);
        }
        else
        {
            this.panel.setBackground(ok);
        }
        
        //this.panel.remove(this.table);
        //String [] columns = {"Site", "Timestamp", "Responce Time", "Status"};
        //this.table = new JTable(report.getData(), columns);
        
        //this.table.setSize(this.panel.getSize());
          
        //this.panel.add(this.table);
        //this.setSize(this.getWidth(), 250 + this.table.getHeight());
        /*
        if(this.getWidth() < this.table.getWidth())
        {
            this.setSize(this.table.getWidth() + 20, this.getHeight());
        }
        */
        //this.table.setVisible(true);    
        //this.set
        
    }
    
    
    
    
    
    
}
