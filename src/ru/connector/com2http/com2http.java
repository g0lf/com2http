package ru.connector.com2http;


import jssc.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by g0lf on 01.10.2016.
 */
public class com2http implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(com2http.class);

    private String[] args;

    public com2http(String[] args) {
        this.args = args;
    }

    public void init() throws Exception {
        log.info("com2http started");

        log.info("try to read settings...");
        Settings.loadSettings(args[0]);

        log.info("try to create reader...");
        ComReader comReader = new ComReader(
                Settings.getParam(Settings.COM_NAME, "COM1"),
                Settings.getIntParam(Settings.COM_BAUDRATE, SerialPort.BAUDRATE_9600),
                Settings.getIntParam(Settings.COM_DATABIT, SerialPort.DATABITS_8),
                Settings.getIntParam(Settings.COM_STOPBIT, SerialPort.STOPBITS_1),
                Settings.getIntParam(Settings.COM_PARITY, SerialPort.PARITY_NONE)
        );

        log.info("create http sender...");
        HttpSender httpSender = new HttpSender();
        log.info("create send thread...");
        HttpThread httpThread = new HttpThread(httpSender, comReader.getDataDeque());
        httpThread.start();

        Thread.sleep(1000 * 60);
        comReader.closeReader();
        httpThread.interrupt();

    }

    @Override
    public void run() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
