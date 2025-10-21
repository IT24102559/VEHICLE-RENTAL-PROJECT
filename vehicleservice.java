package org.example.rentalsytsem.service;

import org.example.rentalsytsem.entity.booking;
import org.example.rentalsytsem.entity.vehicle;
import org.example.rentalsytsem.entity.paymentSlip;
import org.example.rentalsytsem.entity.payment;
import org.example.rentalsytsem.repository.bookingrepository;
import org.example.rentalsytsem.repository.vehiclerepository;
import org.example.rentalsytsem.repository.paymentSlipRepository;
import org.example.rentalsytsem.repository.paymentrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class vehicleservice {

    private final vehiclerepository vehiclerepository;
    private final bookingrepository bookingrepository;
    private final paymentSlipRepository paymentSlipRepository;
    private final paymentrepository paymentrepository;

    @Autowired
    public vehicleservice(vehiclerepository vehiclerepository, bookingrepository bookingrepository,
                          paymentSlipRepository paymentSlipRepository, paymentrepository paymentrepository) {
        this.vehiclerepository = vehiclerepository;
        this.bookingrepository = bookingrepository;
        this.paymentSlipRepository = paymentSlipRepository;
        this.paymentrepository = paymentrepository;
    }

    public  vehicle createVehicle(vehicle v) {
        return vehiclerepository.save(v);
    }

    public List<vehicle> getAllVehicles() {
        return vehiclerepository.findAll();
    }

    public Optional<vehicle> getVehicleById(Long id) {
        return vehiclerepository.findById(id);
    }
    public vehicle updateVehicle(Long id, vehicle updateVehicle) {
        Optional<vehicle> existing = vehiclerepository.findById(id);
        if (existing.isPresent()) {
            vehicle ex = existing.get();
            ex.setBrandName(updateVehicle.getBrandName());
            ex.setModelName(updateVehicle.getModelName());
            ex.setVehicleType(updateVehicle.getVehicleType());
            ex.setAvailable(updateVehicle.isAvailable());
            ex.setRentalPricePerDay(updateVehicle.getRentalPricePerDay());
            ex.setImageUrl(updateVehicle.getImageUrl());
            ex.setStatus(updateVehicle.getStatus()); // Add this line to update status
            return vehiclerepository.save(ex);
        } else {
            // if not found, set id and save as new
            updateVehicle.setId(id);
            return vehiclerepository.save(updateVehicle);
        }
    }
    @Transactional
    public void deleteVehicle(Long id) {
        System.out.println("Attempting to delete vehicle with ID: " + id);
        
        // Check if vehicle exists
        Optional<vehicle> vehicleToDelete = vehiclerepository.findById(id);
        if (vehicleToDelete.isEmpty()) {
            throw new RuntimeException("Vehicle with ID " + id + " not found");
        }
        
        // Get all bookings associated with this vehicle
        List<booking> relatedBookings = bookingrepository.findByVehicleId(id);
        System.out.println("Found " + relatedBookings.size() + " related bookings for vehicle ID: " + id);
        
        if (!relatedBookings.isEmpty()) {
            // For each booking, delete payment slips and payments first
            for (booking b : relatedBookings) {
                Long bookingId = b.getId();
                System.out.println("Processing booking ID: " + bookingId);
                
                // Delete payment slips associated with this booking
                List<paymentSlip> paymentSlips = paymentSlipRepository.findByBookingId(bookingId);
                if (!paymentSlips.isEmpty()) {
                    System.out.println("Deleting " + paymentSlips.size() + " payment slip(s) for booking ID: " + bookingId);
                    for (paymentSlip slip : paymentSlips) {
                        // Delete file from disk
                        try {
                            if (slip.getFilePath() != null) {
                                Path filePath = Paths.get(slip.getFilePath());
                                Files.deleteIfExists(filePath);
                                System.out.println("Deleted payment slip file: " + slip.getFilePath());
                            }
                        } catch (IOException e) {
                            System.err.println("Error deleting payment slip file: " + e.getMessage());
                        }
                    }
                    paymentSlipRepository.deleteAll(paymentSlips);
                }
                
                // Delete payments associated with this booking
                List<payment> payments = paymentrepository.findByBookingId(bookingId);
                if (!payments.isEmpty()) {
                    System.out.println("Deleting " + payments.size() + " payment(s) for booking ID: " + bookingId);
                    paymentrepository.deleteAll(payments);
                }
                
                // Now delete the booking
                System.out.println("Deleting booking ID: " + bookingId);
                bookingrepository.deleteById(bookingId);
            }
            System.out.println("All related bookings and associated data deleted successfully");
        }
        
        // Finally delete the vehicle
        System.out.println("Deleting vehicle with ID: " + id);
        vehiclerepository.deleteById(id);
        System.out.println("Vehicle deleted successfully");
    }

        // Set status (ADMIN action)
        public vehicle setStatus(Long id, String status) {
            Optional<vehicle> existing = vehiclerepository.findById(id);
            if (existing.isPresent()) {
                vehicle v = existing.get();
                v.setStatus(status);
                return vehiclerepository.save(v);
            }
            throw new RuntimeException("Vehicle not found");
        }

        // Find vehicles with PENDING status
        public List<vehicle> findPending() {
            return vehiclerepository.findAll().stream().filter(v -> "PENDING".equalsIgnoreCase(v.getStatus())).toList();
        }
}

