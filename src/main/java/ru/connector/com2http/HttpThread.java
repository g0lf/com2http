package ru.connector.com2http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by g0lf on 02.10.2016.
 */
public class HttpThread extends Thread{

    private final static Logger log = LoggerFactory.getLogger(HttpThread.class);

    private HttpSender httpSender;
    private Deque<String> dataDeque;

    public HttpThread(HttpSender httpSender, Deque<String> _dataDeque) {
        super("http thread");
        this.httpSender = httpSender;
        this.dataDeque = _dataDeque;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                if (!dataDeque.isEmpty()) {
                    String data = dataDeque.pop();
                    log.trace("try to send data '{}'", data);
                    httpSender.send(data);
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


}
