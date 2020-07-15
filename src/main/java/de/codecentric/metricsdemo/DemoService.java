package de.codecentric.metricsdemo;

import de.codecentric.metricsdemo.entity.Message;
import de.codecentric.metricsdemo.repository.MessageRepository;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.MeterFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
public class DemoService {

    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

    private final MessageRepository repository;
    private final MeterRegistry registry;

    private List<Message> list;

    private Counter messagesCreated;
    private Counter messagesError;
    private Timer timer;

    @Autowired
    public DemoService(MessageRepository repository, MeterRegistry registry) {
        this.repository = repository;
        this.registry = registry;

        this.messagesCreated = this.registry.counter("messages_created", Tags.of("type", "incoming"));
        this.messagesError = this.registry.counter("messages_error", Tags.of("type", "incoming"));
        this.list = new ArrayList<>();

        this.registry.gaugeCollectionSize("message_buffer_size", Tags.of("type", "buffered"), list);

        timer = this.registry.timer("messages_created_duration", Tags.of("type", "incoming"));
    }

    @GetMapping("/message/{id}")
    public ResponseEntity<Message> loadMessage(@PathVariable("id") long id) {
        Optional<Message> msg = this.repository.findById(id);
        return ResponseEntity.of(msg);
    }

    @PostMapping("/message")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        return timer.record(() -> {
            if (message == null || !message.validate()) {
                return ResponseEntity.badRequest().build();
            }

            try {
                Message newMessage = this.repository.save(message);
                this.messagesCreated.increment();
                list.add(newMessage);

                URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(newMessage.getId())
                        .toUri();

                return ResponseEntity.created(uri).body(newMessage);
            } catch (IllegalArgumentException ex) {
                logger.error("Got exception", ex);
                this.messagesError.increment();
                return ResponseEntity.badRequest().build();
            }
        });
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

}
