package ru.connector.com2http;


import jssc.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by g0lf on 01.10.2016.
 */
public class com2http {

    private final static Logger log = LoggerFactory.getLogger(com2http.class);

    public static void main(String[] args) throws Exception {
        log.info("com2http started");

        log.info("try to read settings...");
        Settings settings = new Settings(args[0]);

        log.info("try to create reader...");
        ComReader comReader = new ComReader(
                settings.getParam(Settings.COM_NAME, "COM1"),
                settings.getIntParam(Settings.COM_BAUDRATE, SerialPort.BAUDRATE_9600),
                settings.getIntParam(Settings.COM_DATABIT, SerialPort.DATABITS_8),
                settings.getIntParam(Settings.COM_STOPBIT, SerialPort.STOPBITS_1),
                settings.getIntParam(Settings.COM_PARITY, SerialPort.PARITY_NONE)
        );

        log.info("create http sender...");
        HttpSender httpSender = new HttpSender(settings.getParam(Settings.HTTP_URL, ""));
        log.info("create send thread...");
        HttpThread httpThread = new HttpThread(httpSender, comReader.getDataDeque());
        httpThread.start();

        Thread.sleep(1000 * 60);
        comReader.closeReader();
        httpThread.interrupt();

    }

}
