package com.deepanshu.repository;

import com.deepanshu.modal.Pincode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PincodeRepository extends JpaRepository<Pincode, String> {
    // Add custom queries if needed
    Optional<Pincode> findByPincode(String pincode);
}