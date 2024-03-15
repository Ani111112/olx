package com.olx.OlxBackend.repository;


import com.olx.OlxBackend.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long>{
    ApplicationUser findByEmailId(String emailId);
    ApplicationUser findByPhoneNumber(String mobileNumber);
}
