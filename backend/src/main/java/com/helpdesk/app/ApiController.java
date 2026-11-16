package com.helpdesk.app;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
@RestController @RequestMapping("/api") public class ApiController {
 private final UserRepo users;private final TicketRepo tickets;private final EventRepo events;private final PasswordEncoder encoder;
 ApiController(UserRepo u,TicketRepo t,EventRepo e,PasswordEncoder p){users=u;tickets=t;events=e;encoder=p;}
 @GetMapping("/health") public Map<String,String> health(){return Map.of("status","ok");}
 @GetMapping("/me") public Map<String,Object> me(Authentication a){UserAccount u=current(a);return Map.of("id",u.id,"username",u.username,"nombre",u.nombre,"role",u.role);}
 @GetMapping("/technicians") public List<Map<String,Object>> technicians(){return users.findAll().stream().filter(u->u.role.equals("TECH")||u.role.equals("ADMIN")).map(u->Map.<String,Object>of("id",u.id,"nombre",u.nombre,"username",u.username,"role",u.role)).toList();}
 public record NewUser(@NotBlank String username,@NotBlank String nombre,@NotBlank @Size(min=8) String password,@NotBlank String role){}
 @PostMapping("/users") public Map<String,Object> addUser(@Valid @RequestBody NewUser x){if(!Set.of("ADMIN","TECH").contains(x.role()))throw bad("Rol no permitido");if(users.findByUsername(x.username()).isPresent())throw bad("Usuario ya existe");UserAccount u=users.save(new UserAccount(x.username(),encoder.encode(x.password()),x.nombre(),x.role()));return Map.of("id",u.id,"nombre",u.nombre,"role",u.role);}
 public record TicketInput(@NotBlank @Size(max=160) String titulo,@NotBlank @Size(max=3000) String descripcion,@NotBlank String categoria,@NotBlank String prioridad,@NotBlank String estado,@NotBlank String solicitante,Long tecnicoId){}
 @GetMapping("/tickets") public List<Ticket> all(){return tickets.findAll().stream().sorted(Comparator.comparing((Ticket t)->t.id).reversed()).toList();}
 @GetMapping("/tickets/{id}") public Ticket get(@PathVariable Long id){return ticket(id);}
 @PostMapping("/tickets") public Ticket create(@Valid @RequestBody TicketInput x,Authentication a){Ticket t=new Ticket();apply(t,x);Ticket saved=tickets.save(t);event(saved,a.getName(),"Ticket creado");return saved;}
 @PutMapping("/tickets/{id}") public Ticket update(@PathVariable Long id,@Valid @RequestBody TicketInput x,Authentication a){Ticket t=ticket(id);String old=t.estado;apply(t,x);Ticket saved=tickets.save(t);event(saved,a.getName(),"Ticket actualizado"+(old.equals(saved.estado)?"":"; estado: "+old+" → "+saved.estado));return saved;}
 @GetMapping("/tickets/{id}/events") public List<TicketEvent> history(@PathVariable Long id){ticket(id);return events.findByTicketIdOrderByFechaDesc(id);}
 public record Comment(@NotBlank @Size(max=2000) String mensaje){}
 @PostMapping("/tickets/{id}/events") public TicketEvent comment(@PathVariable Long id,@Valid @RequestBody Comment x,Authentication a){return event(ticket(id),a.getName(),x.mensaje());}
 @GetMapping("/dashboard") public Map<String,Object> dashboard(){List<Ticket> ts=tickets.findAll();Map<String,Long> estados=new LinkedHashMap<>();for(String s:List.of("ABIERTO","EN_PROCESO","RESUELTO","CERRADO"))estados.put(s,ts.stream().filter(t->s.equals(t.estado)).count());Map<String,Long> prioridades=new LinkedHashMap<>();for(String s:List.of("BAJA","MEDIA","ALTA","CRITICA"))prioridades.put(s,ts.stream().filter(t->s.equals(t.prioridad)).count());return Map.of("total",ts.size(),"estados",estados,"prioridades",prioridades);}
 private Ticket ticket(Long id){return tickets.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Ticket no encontrado"));}
 private UserAccount current(Authentication a){return users.findByUsername(a.getName()).orElseThrow();}
 private ResponseStatusException bad(String m){return new ResponseStatusException(HttpStatus.BAD_REQUEST,m);}
 private void apply(Ticket t,TicketInput x){if(!Set.of("HARDWARE","SOFTWARE","REDES","ACCESOS","OTROS").contains(x.categoria()))throw bad("Categoría inválida");if(!Set.of("BAJA","MEDIA","ALTA","CRITICA").contains(x.prioridad()))throw bad("Prioridad inválida");if(!Set.of("ABIERTO","EN_PROCESO","RESUELTO","CERRADO").contains(x.estado()))throw bad("Estado inválido");t.titulo=x.titulo().trim();t.descripcion=x.descripcion().trim();t.categoria=x.categoria();t.prioridad=x.prioridad();t.estado=x.estado();t.solicitante=x.solicitante().trim();t.tecnico=x.tecnicoId()==null?null:users.findById(x.tecnicoId()).orElseThrow(()->bad("Técnico no encontrado"));if(t.tecnico!=null&&!Set.of("TECH","ADMIN").contains(t.tecnico.role))throw bad("Usuario no es técnico");}
 private TicketEvent event(Ticket t,String autor,String mensaje){TicketEvent e=new TicketEvent();e.ticket=t;e.autor=autor;e.mensaje=mensaje;return events.save(e);}
}
