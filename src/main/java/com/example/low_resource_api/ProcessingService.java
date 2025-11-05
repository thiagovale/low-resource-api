package com.example.low_resource_api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ProcessingService {

    private static final Logger log = LoggerFactory.getLogger(ProcessingService.class);

    private static final int BATCH_SIZE = 1000;
    private static final int LINGER_MS = 25;

    private final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10000);

    public void processNumber(Integer number) {
        try {
            queue.put(number);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public ProcessingService() {

        List<Integer> batch = new ArrayList<>(BATCH_SIZE);

        Thread consumerThread = new Thread(() -> {
            while (true) {
                try {
                    batch.add(queue.take());

                    Thread.sleep(LINGER_MS);

                    queue.drainTo(batch, BATCH_SIZE - 1);

                    log.info("Processing batch of {} items.", batch.size());

                    batch.clear();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        consumerThread.setDaemon(true);
        consumerThread.start();
    }
}