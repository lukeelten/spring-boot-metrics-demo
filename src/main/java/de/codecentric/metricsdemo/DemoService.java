package de.codecentric.metricsdemo;

import de.codecentric.metricsdemo.entity.Message;
import de.codecentric.metricsdemo.repository.MessageRepository;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.MeterFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.Random;

@RestController
public class DemoService {

    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

    private final MessageRepository repository;
    private final MeterRegistry metrics;

    private final Counter creationCounter;
    private final Counter errorCounter;



    public DemoService(@Autowired MessageRepository repository, @Autowired MeterRegistry registry) {
        this.repository = repository;
        this.metrics = registry;

        this.creationCounter = this.metrics.counter("messages_created_successful", Tags.of("class", DemoService.class.getSimpleName(), "entity", Message.class.getSimpleName()));
        this.errorCounter = this.metrics.counter("messages_created_error", Tags.of("class", DemoService.class.getSimpleName(), "entity", Message.class.getSimpleName()));
    }

    @GetMapping("/message/{id}")
    public ResponseEntity<Message> loadMessage(@PathVariable("id") long id) {
        Optional<Message> msg = this.repository.findById(id);
        return ResponseEntity.of(msg);
    }

    @PostMapping("/message")
    @Timed(description = "Duration of message creation")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        if (message == null || !message.validate()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Message newMessage = this.createMessageTimed(message);

            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(newMessage.getId())
                    .toUri();

            this.creationCounter.increment();
            return ResponseEntity.created(uri).body(newMessage);
        } catch (IllegalArgumentException ex) {
            logger.error("Got exception", ex);
            this.errorCounter.increment();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/message/{id}")
    public ResponseEntity<Object> deleteMessage(@PathVariable("id") long id) {
        try {
            this.repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            logger.error("Got exception", ex);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<Iterable<Message>> listMessages() {
        Iterable<Message> messages = this.repository.findAll();
        return ResponseEntity.ok(messages);
    }

    private Message createMessageTimed(Message msg) {
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        long sleepBefore = (rand.nextLong() % 500);
        long sleepAfter = (rand.nextLong() % 500);

        try {
            Thread.sleep(sleepBefore);
            Message newMessage = this.repository.save(msg);
            Thread.sleep(sleepAfter);

            return newMessage;
        } catch (InterruptedException ex) {
            logger.error("Interrupted", ex);

            return null;
        }
    }

}
