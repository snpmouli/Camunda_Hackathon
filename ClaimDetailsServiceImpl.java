package com.Practice.Hackathon.service;

import com.Practice.Hackathon.Repository.*;
import com.Practice.Hackathon.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClaimDetailsServiceImpl {

    @Autowired
    private ClaimDetailsRepo timeEntryRepository;
    @Autowired
    private PolicyDetailsRepo policyDetailsRepo;
    @Autowired
    private ClaimStatusRepo repo;
    @Autowired
    private ApproverDetailsRepo approverDetailsRepo;
    @Autowired
    private VehicleClaimAllDetailsRepo vehicleClaimAllDetailsRepo;

    public ClaimDetails saveCardDetails(ClaimDetails claimDetails) {
        claimDetails.setDate(LocalDate.now());
        return timeEntryRepository.save(claimDetails);
    }

    public List<ClaimDetails> getAllClaimDetails() {
        return timeEntryRepository.findAll();
    }
    public String deleteClaimDetails(Long claimId) {
        if (timeEntryRepository.existsById(claimId)) {
            timeEntryRepository.deleteById(claimId);
            return "ClaimDetails with ID " + claimId + " has been deleted.";
        } else {
            return "ClaimDetails with ID " + claimId + " not found.";
        }
    }
    public List<PolicyDetails> getAllPolicyDetails() {
        return policyDetailsRepo.findAll();
    }
    public ClaimResponse saveClaimStatus(ClaimResponse claimResponse) {
        return repo.save(claimResponse);
    }
    public List<ClaimResponse> getAllClaimStatus() {
        return repo.findAll();
    }
    public List<ApproverDetails> getApproverDetails() {
        return approverDetailsRepo.findAll();
    }
    public VehicleClaimAllDetails saveVehicleClaimAllDetails(VehicleClaimAllDetails claimDetails) {
        return vehicleClaimAllDetailsRepo.save(claimDetails);
    }

    public List<VehicleClaimAllDetails> getVehicleClaimAllDetails() {
        return vehicleClaimAllDetailsRepo.findAll();
    }


}
