package com.helpdesk.app;
import jakarta.persistence.*;
@Entity @Table(name="usuarios") public class UserAccount {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,unique=true) public String username;
 @Column(nullable=false) public String passwordHash;
 @Column(nullable=false) public String nombre;
 @Column(nullable=false) public String role;
 public UserAccount(){}
 public UserAccount(String username,String passwordHash,String nombre,String role){this.username=username;this.passwordHash=passwordHash;this.nombre=nombre;this.role=role;}
}
