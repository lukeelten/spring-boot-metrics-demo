package de.codecentric.metricsdemo;

import de.codecentric.metricsdemo.entity.Message;
import de.codecentric.metricsdemo.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class DemoService {

    private static Logger logger = LoggerFactory.getLogger(DemoService.class);

    private final MessageRepository repository;

    public DemoService(@Autowired MessageRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/message/{id}")
    public ResponseEntity<Message> loadMessage(@PathVariable("id") long id) {
        Optional<Message> msg = this.repository.findById(id);
        return ResponseEntity.of(msg);
    }

    @PostMapping("/message")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        if (message == null || !message.validate()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Message newMessage = this.repository.save(message);

            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(newMessage.getId())
                    .toUri();

            return ResponseEntity.created(uri).body(newMessage);
        } catch (IllegalArgumentException ex) {
            logger.error("Got exception", ex);
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

}
