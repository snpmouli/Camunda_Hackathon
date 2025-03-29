package com.Practice.Hackathon.jobWorker;

import com.Practice.Hackathon.Repository.ClaimDetailsRepo;
import com.Practice.Hackathon.Repository.ClaimStatusRepo;
import com.Practice.Hackathon.Repository.PolicyDetailsRepo;
import com.Practice.Hackathon.model.*;
import com.Practice.Hackathon.service.ApproverSendEmail;
import com.Practice.Hackathon.service.ClaimDetailsServiceImpl;

import com.Practice.Hackathon.service.SendEmail;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ClaimDetailsJobWorker {
    @Autowired
    private ClaimDetailsServiceImpl claimDetailsService;
    @Autowired
    private SendEmail sendEmaildetails;
    @Autowired
    private ApproverSendEmail approverSendEmail;
    @Autowired
    private ClaimDetailsRepo timeEntryRepository;
    @Autowired
    private PolicyDetailsRepo policyDetailsRepo;
    @Autowired
    private ClaimStatusRepo repo;
    int flag=0;

    @JobWorker(type = "PolicyCheck", autoComplete = true)
    public Map<String, Boolean> getPolicyCheck(JobClient client, ActivatedJob job) {
        System.out.println("*****Service Task For Policy Details*****");
        List<ClaimDetails> claimDetails = claimDetailsService.getAllClaimDetails();
        List<PolicyDetails> policyDetails = claimDetailsService.getAllPolicyDetails();
        List<VehicleClaimAllDetails> vehicleClaimAll = claimDetailsService.getVehicleClaimAllDetails();
        ClaimResponse claimResponse = new ClaimResponse();
        VehicleClaimAllDetails vehicleClaimAllDetails = new VehicleClaimAllDetails();
        Long claim_id = Long.valueOf(0);
        boolean validPolicy = false;
        for (ClaimDetails claimDetails1 : claimDetails) {
            for (PolicyDetails policy : policyDetails) {
                    if (claimDetails1.getPolicyNumber().equals(policy.getPolicyNumber())) {
                        claim_id = claimDetails1.getClaim_Id();
                        validPolicy = true;
                        break;
                    } else {
                        claimResponse.setClaimId(claimDetails1.getClaim_Id());
                        claimResponse.setClaimStatus("Rejected");
                        claimDetailsService.saveClaimStatus(claimResponse);
                        if (claimDetails1.getClaim_Id().equals(claimResponse.getClaimId()))
                        {
                        for (VehicleClaimAllDetails vehicleClaimAllDet : vehicleClaimAll) {
                            if (flag==0){
                              setVehicleClaimDetails(claimDetails1, claimResponse);
                              flag=1;
                            }
                            else break;
                        }
                    }
                }

            }
            }

            System.out.println(validPolicy);
            return Map.of("validPolicy", validPolicy);

    }
    // Checking Duplicate Claim Details
    @JobWorker(type = "DuplicateCheck", autoComplete = true)
    public Map<String, Boolean> getDuplicateCheck(JobClient client, ActivatedJob job) {
        List<ClaimDetails> claimDetails = claimDetailsService.getAllClaimDetails();
        List<PolicyDetails> policyDetails = claimDetailsService.getAllPolicyDetails();
        List<ClaimResponse> claimResponses = claimDetailsService.getAllClaimStatus();
        List<VehicleClaimAllDetails> vehicleClaimAll = claimDetailsService.getVehicleClaimAllDetails();

        ClaimResponse claimResponse = new ClaimResponse();
        boolean validDuplicateClaim=false;
        Map<String, Boolean> result=new HashMap<>();
        for (ClaimDetails claimDetails1 : claimDetails) {
            for (PolicyDetails policy : policyDetails) {
                    if ((claimDetails1.getPolicyNumber().equals(policy.getPolicyNumber()))
                            && (claimDetails1.getDriver_License_Numer().equals(policy.getDriver_License_Numer()))
                            && (claimDetails1.getVehicle_Model().equals(policy.getVehicle_Model())))
                    {
                        validDuplicateClaim = true;
                        claimResponse.setClaimId(claimDetails1.getClaim_Id());
                        claimResponse.setClaimStatus("Rejected");
                        claimDetailsService.saveClaimStatus(claimResponse);
                        if (claimDetails1.getClaim_Id().equals(claimResponse.getClaimId()))
                        {
                            for (VehicleClaimAllDetails vehicleClaimAllDet : vehicleClaimAll) {
                                if (flag==0){
                                    setVehicleClaimDetails(claimDetails1, claimResponse);
                                    flag=1;
                                }
                                else break;
                            }
                        }

                        break;
                    }

                }
            }
        result.put("validDuplicateClaim", validDuplicateClaim);
        System.out.println(validDuplicateClaim);
        return result;
    }
    // Checking Duplicate Claim Details
    @JobWorker(type = "CreditabilyCheck", autoComplete = true)
    public Map<String, Boolean> getCreditabilityCheck(JobClient client, ActivatedJob job) {
        List<ClaimDetails> claimDetails = claimDetailsService.getAllClaimDetails();
        List<PolicyDetails> policyDetails = claimDetailsService.getAllPolicyDetails();
        List<ClaimResponse> claimResponses = claimDetailsService.getAllClaimStatus();
        List<VehicleClaimAllDetails> vehicleClaimAll = claimDetailsService.getVehicleClaimAllDetails();

        ClaimResponse claimResponse = new ClaimResponse();
        boolean validSSN=false;
        for (ClaimDetails claimDetails1 : claimDetails) {
            for (PolicyDetails policy : policyDetails) {
                    if ((claimDetails1.getSSN_Number().equals(policy.getSSN_Number()))) {
                        System.out.println("Creditability check");
                        validSSN = true;
                        break;
                    } else {
                        claimResponse.setClaimId(claimDetails1.getClaim_Id());
                        claimResponse.setClaimStatus("Rejected");
                        claimDetailsService.saveClaimStatus(claimResponse);
                        if (claimDetails1.getClaim_Id().equals(claimResponse.getClaimId()))
                        {
                            for (VehicleClaimAllDetails vehicleClaimAllDet : vehicleClaimAll) {
                                if (flag==0){
                                    setVehicleClaimDetails(claimDetails1, claimResponse);
                                    flag=1;
                                }
                                else break;
                            }
                        }

                            break;
                        }

                }
            }
        System.out.println(validSSN);
        return Map.of("validSSN", validSSN);
    }
    @JobWorker(type = "sendMail", autoComplete = true)
    public void sendMail(JobClient client, ActivatedJob job) {
        List<ClaimDetails> claimDetails = claimDetailsService.getAllClaimDetails();
        sendEmaildetails.mail();
       System.out.println("*****Sent mail*****");
    }
    @JobWorker(type = "sendMailToApprover", autoComplete = true)
    public void sendDetailsToApprover(JobClient client, ActivatedJob job) {
        List<ClaimDetails> claimDetails = claimDetailsService.getAllClaimDetails();
        List<ApproverDetails> approverDetails = claimDetailsService.getApproverDetails();
        Map<String, Object> variables = job.getVariablesAsMap();
//         // Extract variables from the process
         String role=(String) variables.get("role");
      //  String role;
          System.out.println(role);
        for (ClaimDetails claimDetails1 : claimDetails) {
            for (ApproverDetails policy : approverDetails) {
                if (claimDetails1.getClaim_Amount() >= 0 && claimDetails1.getClaim_Amount() <= 5000) {
                    role="Senior_Manager";
                } else if (claimDetails1.getClaim_Amount() >= 5001 && claimDetails1.getClaim_Amount() <= 10000) {
                    role="General_Manager";
                } else if (claimDetails1.getClaim_Amount() >= 10001 && 10000 <= 50000) {
                    role="Director";
                } else if (claimDetails1.getClaim_Amount() > 50001) {
                    role="Sr.Director";
                }
                }
            }
        approverSendEmail.mail(role);
        System.out.println("*****Sent mail to Approver*****");
    }
    @JobWorker(type = "claimStatus", autoComplete = true)
    public void getClaimStatus(JobClient client, ActivatedJob job) {
        System.out.println("*****Service Task For claim status Details*****");
        List<ClaimDetails> claimDetails = claimDetailsService.getAllClaimDetails();
        List<PolicyDetails> policyDetails = claimDetailsService.getAllPolicyDetails();
        List<VehicleClaimAllDetails> vehicleClaimAll = claimDetailsService.getVehicleClaimAllDetails();
        Map<String, Object> variables = job.getVariablesAsMap();
        // Extract variables from the process
        for (ClaimDetails claimDetails1 : claimDetails) {
            for (PolicyDetails policy : policyDetails) {
                if ((claimDetails1.getSSN_Number().equals(policy.getSSN_Number()))) {
                    Long Claim_Id = claimDetails1.getClaim_Id();

                    String claimStatus = (String) variables.get("claimStatus");
                    ClaimResponse claim = new ClaimResponse();
                    claim.setClaimId(Claim_Id);
                    claim.setClaimStatus(claimStatus);
                    claimDetailsService.saveClaimStatus(claim);
                }
            }
        }
        sendEmaildetails.mail();

    }

    public void setVehicleClaimDetails(ClaimDetails claimDetails, ClaimResponse claimResponse) {
        // Create an instance of VehicleClaimAllDetails

            VehicleClaimAllDetails vehicleClaimAllDetails = new VehicleClaimAllDetails();

            // Set all values from ClaimDetails to VehicleClaimAllDetails
            vehicleClaimAllDetails.setClaim_Id(claimDetails.getClaim_Id());
            vehicleClaimAllDetails.setDate(claimDetails.getDate());
            vehicleClaimAllDetails.setPolicyNumber(claimDetails.getPolicyNumber());
            vehicleClaimAllDetails.setName(claimDetails.getName());
            vehicleClaimAllDetails.setEmail(claimDetails.getEmail());
            vehicleClaimAllDetails.setAddress(claimDetails.getAddress());
            vehicleClaimAllDetails.setPhoneNumber(claimDetails.getPhoneNumber());
            vehicleClaimAllDetails.setAverage_Km_Run_Per_year(claimDetails.getAverage_Km_Run_Per_year());
            vehicleClaimAllDetails.setClaim_Amount(claimDetails.getClaim_Amount());
            vehicleClaimAllDetails.setDriver_License_Numer(claimDetails.getDriver_License_Numer());
            vehicleClaimAllDetails.setDriver_Name(claimDetails.getDriver_Name());
            vehicleClaimAllDetails.setIs_Vehicle_Under_Loan(claimDetails.isIs_Vehicle_Under_Loan());
            vehicleClaimAllDetails.setMilage_Run(claimDetails.getMilage_Run());
            vehicleClaimAllDetails.setPrevious_Claim_Numbers(claimDetails.getPrevious_Claim_Numbers());
            vehicleClaimAllDetails.setVehicle_Manufacture(claimDetails.getVehicle_Manufacture());
            vehicleClaimAllDetails.setVehicle_Model(claimDetails.getVehicle_Model());
            vehicleClaimAllDetails.setVehicle_Usage(claimDetails.getVehicle_Usage());
            vehicleClaimAllDetails.setSSN_Number(claimDetails.getSSN_Number());

            // Set claim status from ClaimResponse
            vehicleClaimAllDetails.setClaimStatus(claimResponse.getClaimStatus());

            // Set role if available (optional field, can be null)
            //vehicleClaimAllDetails.setRole(claimResponse.getRole());  // Ensure `ClaimResponse` contains a `role` field

            // After setting all details, you can save the VehicleClaimAllDetails object to the database
            claimDetailsService.saveVehicleClaimAllDetails(vehicleClaimAllDetails);

            System.out.println("Vehicle Claim Details saved: " + vehicleClaimAllDetails);
    }
}