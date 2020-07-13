package de.codecentric.metricsdemo.repository;

import de.codecentric.metricsdemo.entity.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends CrudRepository<Message, Long> {

//    Boolean delete(Long id);
//    Message create(Message msg);
}
