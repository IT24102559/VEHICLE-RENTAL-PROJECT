package org.example.rentalsytsem.controller;

import java.util.List;

import org.example.rentalsytsem.entity.booking;
import org.example.rentalsytsem.service.bookingservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/bookings")
public class bookingcontroller {

    private final bookingservice service;

    @Autowired
    public bookingcontroller(bookingservice service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<booking> create(@RequestBody booking b) {
        booking saved = service.createBooking(b);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<booking>> getAll() {
        return ResponseEntity.ok(service.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<booking> getById(@PathVariable Long id) {
        return service.getBookingById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get bookings by username
    @GetMapping("/user/{username}")
    public ResponseEntity<List<booking>> getBookingsByUser(@PathVariable String username) {
        List<booking> userBookings = service.getBookingsByUser(username);
        return ResponseEntity.ok(userBookings);
    }

    // Update booking (dates, pickup) - user can update before it's cancelled or payment approved
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody booking updated) {
        try {
            booking existingBooking = service.getBookingById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
            
            // Check if booking payment has been approved - prevent edits if approved
            if ("SUCCESSFUL".equals(existingBooking.getStatus()) || 
                "APPROVED".equals(existingBooking.getStatus()) ||
                "PAID".equals(existingBooking.getStatus())) {
                return ResponseEntity.status(403)
                    .body("{\"error\": \"Cannot edit booking after payment has been approved\"}");
            }
            
            booking b = service.updateBooking(id, updated);
            return ResponseEntity.ok(b);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Cancel booking
    // Cancel booking (POST to /api/bookings/{id}/cancel)
    @PostMapping("/{id}/cancel")
    public ResponseEntity<booking> cancelBooking(@PathVariable Long id) {
        try {
            booking b = service.cancelBooking(id);
            return ResponseEntity.ok(b);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete booking (DELETE /api/bookings/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        try {
            service.deleteBooking(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Admin: approve booking
    @PostMapping("/{id}/approve")
    public ResponseEntity<booking> approveBooking(@PathVariable Long id) {
        try {
            booking b = service.getBookingById(id).orElseThrow(() -> new RuntimeException("Booking not found"));
            b.setStatus("APPROVED");
            booking updated = service.updateBooking(id, b);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Admin: reject booking
    @PostMapping("/{id}/reject")
    public ResponseEntity<booking> rejectBooking(@PathVariable Long id) {
        try {
            booking b = service.getBookingById(id).orElseThrow(() -> new RuntimeException("Booking not found"));
            b.setStatus("REJECTED");
            booking updated = service.updateBooking(id, b);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Mark booking as paid after successful payment
    @PostMapping("/{id}/paid")
    public ResponseEntity<booking> markBookingAsPaid(@PathVariable Long id) {
        try {
            booking b = service.getBookingById(id).orElseThrow(() -> new RuntimeException("Booking not found"));
            b.setStatus("PAID");
            booking updated = service.updateBooking(id, b);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

