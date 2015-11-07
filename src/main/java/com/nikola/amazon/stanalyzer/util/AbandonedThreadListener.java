package com.nikola.amazon.stanalyzer.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

@WebListener
public class AbandonedThreadListener implements ServletContextListener {
   public void contextDestroyed(ServletContextEvent arg0) {
      try { 
          AbandonedConnectionCleanupThread.shutdown();
      	} catch (InterruptedException e) {
      }
   }

@Override
public void contextInitialized(ServletContextEvent arg0) {
	
	}
	
}