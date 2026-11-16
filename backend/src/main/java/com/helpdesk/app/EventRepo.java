package com.helpdesk.app;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EventRepo extends JpaRepository<TicketEvent,Long>{List<TicketEvent> findByTicketIdOrderByFechaDesc(Long ticketId);}
