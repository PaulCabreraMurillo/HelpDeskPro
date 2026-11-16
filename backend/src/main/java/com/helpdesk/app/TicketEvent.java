package com.helpdesk.app;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="historial_ticket") public class TicketEvent {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) public Ticket ticket;
 @Column(nullable=false) public String autor;
 @Column(nullable=false,length=2000) public String mensaje;
 @Column(nullable=false) public Instant fecha=Instant.now();
}
