package kiran.ticket_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kiran.ticket_booking.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	boolean existsByMobile(Long string);
	
	User findByEmail(String eamil);
	
	
}
