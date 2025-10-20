package org.example.rentalsytsem.repository;

import org.example.rentalsytsem.entity.booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface bookingrepository extends JpaRepository<booking, Long> {
	List<booking> findByUserId(Long userId);
	List<booking> findByVehicleId(Long vehicleId);
	
	@Query("SELECT b FROM booking b WHERE b.user.username = :username")
	List<booking> findByUsername(@Param("username") String username);
}

