package org.example.rentalsytsem.controller;

import org.example.rentalsytsem.entity.vehicle;
import org.example.rentalsytsem.repository.vehiclerepository;
import org.example.rentalsytsem.service.vehicleservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/vehicles")
public class vehicleController {

    private final vehicleservice service;

    @Autowired
    public vehicleController(vehicleservice vehicleservice) {
        this.service = vehicleservice;
    }

    // Create
    @PostMapping
    public ResponseEntity<vehicle> create(@RequestBody vehicle V) {
        vehicle saved = service.createVehicle(V);
        return ResponseEntity.ok(saved);
    }

    // Read all
    @GetMapping
    public ResponseEntity<List<vehicle>> getAllVehicles(@RequestParam(value = "status", required = false) String status) {
        if (status != null) return ResponseEntity.ok(service.getAllVehicles().stream().filter(v -> status.equalsIgnoreCase(v.getStatus())).toList());
        return ResponseEntity.ok(service.getAllVehicles());
    }

    // Read one
    @GetMapping("/{id}")
    public ResponseEntity<vehicle> getVehicleById(@PathVariable Long id) {
        return service.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<vehicle> updateVehicle(@PathVariable Long id, @RequestBody vehicle v) {
        vehicle updated = service.updateVehicle(id, v);
        return ResponseEntity.ok(updated);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable Long id) {
        try {
            service.deleteVehicle(id);
            return ResponseEntity.ok("Vehicle with ID " + id + " deleted successfully");
        } catch (Exception e) {
            e.printStackTrace(); // This will log the full stack trace
            return ResponseEntity.status(500).body("Failed to delete vehicle: " + e.getMessage());
        }
    }

    // Submit vehicle as a user (status = PENDING)
    @PostMapping("/submit")
    public ResponseEntity<vehicle> submitVehicle(@RequestBody vehicle v) {
        if (v.getStatus() == null) v.setStatus("PENDING");
        vehicle saved = service.createVehicle(v);
        return ResponseEntity.ok(saved);
    }

    // Admin: approve vehicle
    @PostMapping("/{id}/approve")
    public ResponseEntity<vehicle> approveVehicle(@PathVariable Long id) {
        vehicle v = service.setStatus(id, "APPROVED");
        return ResponseEntity.ok(v);
    }

    // Admin: reject vehicle
    @PostMapping("/{id}/reject")
    public ResponseEntity<vehicle> rejectVehicle(@PathVariable Long id) {
        vehicle v = service.setStatus(id, "REJECTED");
        return ResponseEntity.ok(v);
    }

}
 