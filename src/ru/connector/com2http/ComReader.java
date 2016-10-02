package ru.connector.com2http;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Deque;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by g0lf on 02.10.2016.
 */
public class ComReader {

    private final static Logger log = LoggerFactory.getLogger(com2http.class);

    private SerialPort serialPort;
    private ConcurrentLinkedDeque<String> _deque = new ConcurrentLinkedDeque<>();

    public ComReader(String comName, int baudrate, int databits, int stopbits, int parity) throws SerialPortException {
        serialPort = new SerialPort(comName);
        try {
            serialPort.openPort();
            serialPort.setParams(baudrate, databits, stopbits, parity);
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
        } catch (SerialPortException spe) {
            log.error("can't not initialize com reader");
            throw spe;
        }
    }

    public void closeReader() {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            log.error("error while close port", e);
        }
    }

    private class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    //Получаем ответ от устройства, обрабатываем данные и т.д.
                    String data = serialPort.readString(event.getEventValue());
                    log.trace("Got string {}", data);
                    _deque.push(data);
                    //И снова отправляем запрос
                    serialPort.writeString(data);
                } catch (SerialPortException ex) {
                    log.error("error while reading from port", ex);
                }
            }
        }
    }

    public Deque<String> getDataDeque(){
        return _deque;
    }

}
