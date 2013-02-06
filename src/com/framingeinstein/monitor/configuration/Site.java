/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.framingeinstein.monitor.configuration;

/**
 *
 * @author jmorgan
 */
public class Site
{
    public String name;
    public String path;
    public boolean heal;
    public Site(String name, String path)
    {
        this(name, path, true);
    }
    public Site (String name, String path, boolean heal)
    {
        this.name = name;
        this.path = path;
        this.heal = heal;
    }
}