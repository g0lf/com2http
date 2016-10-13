package ru.connector.com2http;

import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

/**
 * Created by n_erohin on 13.10.2016.
 */
public class WindowsServiceWrapper implements WrapperListener {
    @Override
    public Integer start(String[] strings) {
        com2http com2httpApp = new com2http(strings);
        Thread thread = new Thread(com2httpApp);
        thread.start();
        return null;
    }

    @Override
    public int stop(int i) {
        return 0;
    }

    @Override
    public void controlEvent(int i) {
        if ((i == WrapperManager.WRAPPER_CTRL_LOGOFF_EVENT)
                && (WrapperManager.isLaunchedAsService() || WrapperManager.isIgnoreUserLogoffs())) {
            // Ignore
        } else {
            WrapperManager.stop(0);
            // Will not get here.
        }
    }

    public static void main(String[] args) {
        // Start the application.  If the JVM was launched from the native
        //  Wrapper then the application will wait for the native Wrapper to
        //  call the application's start method.  Otherwise the start method
        //  will be called immediately.
        WrapperManager.start(new WindowsServiceWrapper(), args);
    }

}
