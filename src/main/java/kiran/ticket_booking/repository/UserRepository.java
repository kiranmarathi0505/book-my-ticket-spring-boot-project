package kiran.ticket_booking.repository;

import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties.Apiversion.Use;
import org.springframework.data.jpa.repository.JpaRepository;

import kiran.ticket_booking.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
