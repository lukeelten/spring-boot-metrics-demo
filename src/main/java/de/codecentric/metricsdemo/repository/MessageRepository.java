package de.codecentric.metricsdemo.repository;

import de.codecentric.metricsdemo.entity.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {
}
