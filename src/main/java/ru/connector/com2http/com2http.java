package ru.connector.com2http;


import jssc.SerialPort;
import jssc.SerialPortList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by g0lf on 01.10.2016.
 */
public class com2http {

    private final static Logger log = LoggerFactory.getLogger(com2http.class);

    public static void main(String[] args) throws Exception {
        log.info("com2http started");

        log.info("List of serial ports: " + String.join(",", SerialPortList.getPortNames()));

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

}
