package br.com.vinheriaagnello.servlet;

import br.com.vinheriaagnello.config.FirebaseConfig;
import br.com.vinheriaagnello.config.DatabaseConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application Lifecycle Listener implementation class AppStartupListener
 *
 */
@WebListener
public class AppStartupListener implements ServletContextListener {

	private static final Logger LOGGER = Logger.getLogger(AppStartupListener.class.getName());

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
        try {
            FirebaseConfig.init();
            LOGGER.info("Firebase initialized.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Firebase failed to initialize; login is unavailable.", e);
        }
        try {
            DatabaseConfig.init();
            LOGGER.info("Database initialized.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Database failed to initialize; pages that need it are unavailable.", e);
        }
    }
	
}
