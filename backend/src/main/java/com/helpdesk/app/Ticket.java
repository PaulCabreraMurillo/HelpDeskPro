package com.helpdesk.app;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="tickets") public class Ticket {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,length=160) public String titulo;
 @Column(nullable=false,length=3000) public String descripcion;
 @Column(nullable=false) public String categoria="SOFTWARE";
 @Column(nullable=false) public String prioridad="MEDIA";
 @Column(nullable=false) public String estado="ABIERTO";
 @Column(nullable=false) public String solicitante;
 @ManyToOne public UserAccount tecnico;
 @Column(nullable=false,updatable=false) public Instant creadoEn=Instant.now();
 @Column(nullable=false) public Instant actualizadoEn=Instant.now();
 @PreUpdate public void updated(){actualizadoEn=Instant.now();}
}
