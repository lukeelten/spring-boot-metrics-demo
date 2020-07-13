package de.codecentric.metricsdemo.entity;

import de.codecentric.metricsdemo.util.Strings;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String sender;
    private String recipient;

    private String message;

    public Message(String sender, String reciepient, String message) {
        this.sender = sender;
        this.recipient = reciepient;
        this.message = message;
    }

    public Message() {}

    public Long getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public boolean validate() {
        return !Strings.isEmpty(this.sender) && !Strings.isEmpty(this.recipient) && !Strings.isEmpty(this.message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message1 = (Message) o;
        return Objects.equals(sender, message1.sender) &&
                Objects.equals(recipient, message1.recipient) &&
                Objects.equals(message, message1.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, recipient, message);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
