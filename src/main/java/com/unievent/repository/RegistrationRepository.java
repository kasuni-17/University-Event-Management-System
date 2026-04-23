package com.unievent.repository;

import com.unievent.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByEventId(Long eventId);

    List<Registration> findByStudentId(Long studentId);

    Optional<Registration> findByStudentIdAndEventId(Long studentId, Long eventId);

    List<Registration> findByStudentIdAndStatus(Long studentId, Registration.RegistrationStatus status);

    @Modifying
    @Transactional
    @Query("DELETE FROM Registration r WHERE r.event.id = ?1")
    void deleteByEventId(Long eventId);
}
