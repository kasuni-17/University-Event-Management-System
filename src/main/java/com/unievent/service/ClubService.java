package com.unievent.service;

import com.unievent.entity.Club;
import com.unievent.entity.User;
import com.unievent.repository.ClubRepository;
import com.unievent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private com.unievent.repository.ClubMembershipRepository clubMembershipRepository;

    public long getClubMemberCount(Long clubId) {
        return clubMembershipRepository.countByClubIdAndStatus(clubId, com.unievent.entity.ClubMembership.MembershipStatus.APPROVED);
    }

    public Club createClub(Club club) {
        club.setApprovalStatus(Club.ApprovalStatus.PENDING);
        club.setActive(false);
        return clubRepository.save(club);
    }

    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    public List<Club> getActiveClubs() {
        return clubRepository.findByActive(true);
    }

    public List<Club> getClubsByCategory(String category) {
        return clubRepository.findByCategory(category);
    }

    public Optional<Club> getClubById(Long id) {
        return clubRepository.findById(id);
    }

    public Club updateClub(Long id, Club clubDetails) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + id));

        club.setName(clubDetails.getName());
        club.setCategory(clubDetails.getCategory());
        club.setDescription(clubDetails.getDescription());
        club.setPresidentEmail(clubDetails.getPresidentEmail());
        club.setEmail(clubDetails.getEmail());
        club.setPersonalContactGmail(clubDetails.getPersonalContactGmail());
        club.setLogoUrl(clubDetails.getLogoUrl());
        club.setCoverUrl(clubDetails.getCoverUrl());

        return clubRepository.save(club);
    }

    public void deleteClub(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + id));
        // Soft delete
        club.setActive(false);
        clubRepository.save(club);
    }

    // ============ ADMIN APPROVAL METHODS ============

    /**
     * Get all pending clubs (newly registered, awaiting approval)
     */
    public List<Club> getPendingClubs() {
        return clubRepository.findByApprovalStatus(Club.ApprovalStatus.PENDING);
    }

    /**
     * Get all approved clubs
     */
    public List<Club> getApprovedClubs() {
        return clubRepository.findByApprovalStatusAndActive(Club.ApprovalStatus.APPROVED, true);
    }

    /**
     * Get clubs by approval status
     */
    public List<Club> getClubsByApprovalStatus(Club.ApprovalStatus status) {
        return clubRepository.findByApprovalStatus(status);
    }

    /**
     * Approve a club registration with email notification
     */
    public Club approveClub(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));

        club.setApprovalStatus(Club.ApprovalStatus.APPROVED);
        club.setActive(true);
        club.setApprovalDate(LocalDateTime.now());
        Club savedClub = clubRepository.save(club);

        // Send email notification to club admin
        if (club.getCreatedByUser() != null) {
            emailService.sendClubApprovalEmail(club.getCreatedByUser(), club, true, null);
        }

        return savedClub;
    }

    /**
     * Reject a club registration with email notification
     */
    public Club rejectClub(Long clubId, String rejectionReason) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));

        club.setApprovalStatus(Club.ApprovalStatus.REJECTED);
        club.setActive(false);
        club.setRejectionReason(rejectionReason);
        club.setApprovalDate(LocalDateTime.now());
        Club savedClub = clubRepository.save(club);

        // Send email notification to club admin
        if (club.getCreatedByUser() != null) {
            emailService.sendClubApprovalEmail(club.getCreatedByUser(), club, false, rejectionReason);
        }

        return savedClub;
    }

    public List<Club> getClubsByPresidentEmail(String email) {
        // Club admin can see all their clubs (PENDING, APPROVED, REJECTED)
        // They need to see pending clubs to check approval status
        return clubRepository.findByPresidentEmail(email);
    }

    /**
     * Get only approved clubs for student viewing
     */
    public List<Club> getApprovedClubsForStudents() {
        return clubRepository.findByApprovalStatusAndActive(Club.ApprovalStatus.APPROVED, true);
    }

    /**
     * Check if a club is approved
     */
    public boolean isClubApproved(Long clubId) {
        return clubRepository.findById(clubId)
                .map(c -> c.getApprovalStatus() == Club.ApprovalStatus.APPROVED && c.isActive())
                .orElse(false);
    }
}
