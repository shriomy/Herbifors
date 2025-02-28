package com.example.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) {
        System.out.println("OSGi Bundle Started: Smart Greenhouse System");
    }

    @Override
    public void stop(BundleContext context) {
        System.out.println("OSGi Bundle Stopped.");
    }
}
