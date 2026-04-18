package br.com.vinheriaagnello.servlet;

import br.com.vinheriaagnello.config.FirebaseConfig;
import br.com.vinheriaagnello.config.DatabaseConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class AppStartupListener
 *
 */
@WebListener
public class AppStartupListener implements ServletContextListener {

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
        try {
            FirebaseConfig.init();
            DatabaseConfig.init();
            System.out.println("✅ Firebase and Database initialized.");
        } catch (Exception e) {
            throw new RuntimeException("Startup initialization failed", e);
        }
    }
	
}
