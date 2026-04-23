package com.unievent.service;

import com.unievent.entity.Club;
import com.unievent.entity.Event;
import com.unievent.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * EmailService — production-ready async email notifications.
 *
 * All public methods are @Async so SMTP delays/failures never block
 * the HTTP request thread. Each method retries up to 3 times with a
 * 2-second back-off before giving up and logging the failure.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${app.name:UniEvent}")
    private String appName;

    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    // ==================== PUBLIC TRIGGER METHODS ====================

    /** User Registration — Club Admin approval/rejection */
    @Async
    public void sendClubAdminApprovalEmail(User user, boolean approved, String rejectionReason) {
        System.out.println("[EMAIL] >>> TRIGGER: sendClubAdminApprovalEmail | user=" + safeEmail(user) + " | approved=" + approved);
        if (!hasValidPersonalEmail(user)) return;

        String subject = approved
                ? "UniEvent: Your Club Admin Account Has Been Approved"
                : "UniEvent: Club Admin Application Status Update";
        String status      = approved ? "APPROVED" : "REJECTED";
        String statusColor = approved ? "#16a34a" : "#dc2626";
        String reason      = resolveReason(approved, rejectionReason,
                "Your application to become a Club Administrator has been successfully verified and approved.",
                "Your application did not meet our current institutional requirements.");

        String html = buildUserApprovalEmailTemplate(user.getName(), "Club Administrator", status, statusColor, reason, approved);
        sendWithRetry(user.getPersonalEmail(), subject, html, "sendClubAdminApprovalEmail");
    }

    /** User Registration — Student approval/rejection */
    @Async
    public void sendStudentApprovalEmail(User user, boolean approved, String rejectionReason) {
        System.out.println("[EMAIL] >>> TRIGGER: sendStudentApprovalEmail | user=" + safeEmail(user) + " | approved=" + approved);
        if (!hasValidPersonalEmail(user)) return;

        String subject = approved
                ? "UniEvent: Your Student Account Is Now Active"
                : "UniEvent: Student Registration Status Update";
        String status      = approved ? "APPROVED" : "REJECTED";
        String statusColor = approved ? "#16a34a" : "#dc2626";
        String reason      = resolveReason(approved, rejectionReason,
                "Your student account has been successfully verified and activated.",
                "Your student registration could not be processed at this time.");

        String html = buildUserApprovalEmailTemplate(user.getName(), "Student", status, statusColor, reason, approved);
        sendWithRetry(user.getPersonalEmail(), subject, html, "sendStudentApprovalEmail");
    }

    /** Club Registration — approval/rejection */
    @Async
    public void sendClubApprovalEmail(User clubAdmin, Club club, boolean approved, String rejectionReason) {
        System.out.println("[EMAIL] >>> TRIGGER: sendClubApprovalEmail | club=" + safeClubName(club) + " | admin=" + safeEmail(clubAdmin) + " | approved=" + approved);
        if (!hasValidPersonalEmail(clubAdmin)) return;

        String subject = approved
                ? "UniEvent: \"" + club.getName() + "\" Has Been Approved"
                : "UniEvent: Club Registration Update — " + club.getName();
        String status      = approved ? "APPROVED" : "REJECTED";
        String statusColor = approved ? "#16a34a" : "#dc2626";
        String reason      = resolveReason(approved, rejectionReason,
                "Your new club registration has been reviewed and officially approved. Students can now discover and join your club.",
                "Your club registration was not approved at this time. Please review the details below.");

        String html = buildClubApprovalEmailTemplate(clubAdmin.getName(), club.getName(), status, statusColor, reason, approved);
        sendWithRetry(clubAdmin.getPersonalEmail(), subject, html, "sendClubApprovalEmail");
    }

    /** Event Approval — approval/rejection */
    @Async
    public void sendEventApprovalEmail(User eventCreator, Event event, Club club, boolean approved, String rejectionReason) {
        System.out.println("[EMAIL] >>> TRIGGER: sendEventApprovalEmail | event=" + safeEventTitle(event) + " | creator=" + safeEmail(eventCreator) + " | approved=" + approved);
        if (!hasValidPersonalEmail(eventCreator)) return;

        String subject = approved
                ? "UniEvent: Event \"" + event.getTitle() + "\" Approved"
                : "UniEvent: Event Submission Update — " + event.getTitle();
        String status      = approved ? "APPROVED" : "REJECTED";
        String statusColor = approved ? "#16a34a" : "#dc2626";
        String reason      = resolveReason(approved, rejectionReason,
                "Your event submission has been reviewed and officially approved for publication. Students can begin registering.",
                "Your event submission was not approved. Please review the feedback below and resubmit with the requested changes.");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        String eventDateTime = (event.getStartTime() != null) ? event.getStartTime().format(fmt) : "TBD";
        String clubName      = (club != null) ? club.getName() : "Your Club";

        String html = buildEventApprovalEmailTemplate(
                eventCreator.getName(), event.getTitle(), clubName, eventDateTime,
                status, statusColor, reason, approved);
        sendWithRetry(eventCreator.getPersonalEmail(), subject, html, "sendEventApprovalEmail");
    }

    /** Event Registration Confirmation — sent to student */
    @Async
    public void sendEventRegistrationConfirmationEmail(User student, Event event) {
        System.out.println("[EMAIL] >>> TRIGGER: sendEventRegistrationConfirmationEmail | student=" + safeEmail(student) + " | event=" + safeEventTitle(event));
        if (!hasValidPersonalEmail(student)) return;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        String eventDateTime = (event.getStartTime() != null) ? event.getStartTime().format(fmt) : "TBD";
        String clubName      = (event.getOrganizer() != null) ? event.getOrganizer().getName() : "UniEvent";

        String html = buildEventRegistrationConfirmationTemplate(
                student.getName(), event.getTitle(), clubName, eventDateTime,
                event.getLocation() != null ? event.getLocation() : "To be announced");
        sendWithRetry(student.getPersonalEmail(), "Event Registration Confirmed — " + event.getTitle(), html, "sendEventRegistrationConfirmationEmail");
    }

    /** Club Membership Confirmation — sent to student after joining a club */
    @Async
    public void sendClubMembershipConfirmationEmail(User student, Club club) {
        System.out.println("[EMAIL] >>> TRIGGER: sendClubMembershipConfirmationEmail | student=" + safeEmail(student) + " | club=" + safeClubName(club));
        if (!hasValidPersonalEmail(student)) return;

        String html = buildClubMembershipConfirmationTemplate(student.getName(), club.getName(), club.getDescription());
        sendWithRetry(student.getPersonalEmail(), "Welcome to " + club.getName() + " — Membership Confirmed!", html, "sendClubMembershipConfirmationEmail");
    }

    /** OTP Verification Email — sent to student email for registration verification */
    // Temporarily removed @Async to debug connection issues
    public void sendOtpEmail(String toEmail, String otp) {
        System.out.println("[EMAIL] >>> TRIGGER: sendOtpEmail | to=" + toEmail);
        String subject = "UniEvent: Your Verification Code";
        String html = buildOtpEmailTemplate(otp);
        sendWithRetry(toEmail, subject, html, "sendOtpEmail");
    }

    // ==================== CORE SEND + RETRY ====================

    /**
     * Sends email with up to 3 attempts (2-second back-off between retries).
     * Runs on the async thread pool — never blocks the HTTP request.
     */
    private void sendWithRetry(String to, String subject, String htmlContent, String context) {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                System.out.println("[EMAIL] Attempt " + attempt + "/" + maxAttempts + " | to=" + to + " | context=" + context);
                sendEmail(to, subject, htmlContent);
                System.out.println("[EMAIL] SUCCESS | to=" + to + " | context=" + context);
                return;
            } catch (Exception e) {
                System.err.println("[EMAIL] ATTEMPT " + attempt + " FAILED | context=" + context + " | error=" + e.getMessage());
                if (attempt < maxAttempts) {
                    try { Thread.sleep(2000L * attempt); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                } else {
                    System.err.println("[EMAIL] ALL RETRIES EXHAUSTED | to=" + to + " | context=" + context);
                    e.printStackTrace();
                }
            }
        }
    }

    /** Actual SMTP send with a friendly display name to improve deliverability. */
    private void sendEmail(String to, String subject, String htmlContent) throws Exception {
        System.out.println("[EMAIL] Preparing MimeMessage for: " + to);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Set the 'From' address with a friendly display name (e.g., "UniEvent Portal")
        String displayName = appName + " Portal";
        helper.setFrom(new InternetAddress(mailFrom, displayName, "UTF-8"));

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        System.out.println("[EMAIL] Dispatching to SMTP server...");
        mailSender.send(message);
        System.out.println("[EMAIL] Successfully sent to: " + to);
    }

    // ==================== GUARD HELPERS ====================

    private boolean hasValidPersonalEmail(User user) {
        if (user == null) {
            System.err.println("[EMAIL] SKIPPED — user object is null");
            return false;
        }
        String email = user.getPersonalEmail();
        if (email == null || email.trim().isEmpty()) {
            System.err.println("[EMAIL] SKIPPED — personalEmail is null/blank for user id=" + user.getId() + " (" + user.getEmail() + ")");
            return false;
        }
        return true;
    }

    private String resolveReason(boolean approved, String provided, String defaultApproved, String defaultRejected) {
        if (provided != null && !provided.trim().isEmpty()) return provided;
        return approved ? defaultApproved : defaultRejected;
    }

    private String safeEmail(User u)      { return u != null ? u.getEmail() : "null"; }
    private String safeClubName(Club c)   { return c != null ? c.getName()  : "null"; }
    private String safeEventTitle(Event e){ return e != null ? e.getTitle() : "null"; }

    // ==================== EMAIL TEMPLATES ====================

    private String buildUserApprovalEmailTemplate(String userName, String roleName,
            String status, String statusColor, String reason, boolean approved) {
        String actionSection = approved
                ? "<p style='margin:16px 0;color:#475569;'>You now have full access to your dashboard and all platform features. Welcome to the community!</p>"
                + "<a href='" + appUrl + "/login.html' style='display:inline-block;background:#21417f;color:#fff;padding:14px 32px;text-decoration:none;border-radius:10px;font-weight:700;font-size:14px;margin-top:8px;'>Access Your Account</a>"
                : "<p style='margin:16px 0;color:#475569;'>If you believe this decision was made in error, please contact the SLIIT administration office with supporting documents.</p>";

        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1.0'></head>"
                + "<body style='font-family:-apple-system,BlinkMacSystemFont,Inter,sans-serif;background:#f1f5f9;margin:0;padding:40px 20px;color:#1e293b;'>"
                + "<div style='max-width:600px;margin:0 auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);border:1px solid #e2e8f0;'>"
                + "<div style='background:#21417f;padding:36px 24px;text-align:center;'>"
                + "<h1 style='margin:0;color:#fff;font-size:22px;font-weight:800;'>" + appName + " Portal</h1>"
                + "<p style='margin:8px 0 0;color:#bfdbfe;font-size:13px;'>Official Account Notification</p></div>"
                + "<div style='padding:40px 32px;'>"
                + "<p style='margin:0 0 16px;font-size:16px;'>Hello <strong>" + userName + "</strong>,</p>"
                + "<p style='margin:0 0 24px;color:#475569;'>Your <strong>" + roleName + "</strong> registration has been reviewed by our administration team.</p>"
                + "<div style='background:#f8fafc;border-radius:12px;padding:24px;border-left:4px solid " + statusColor + ";margin:0 0 24px;'>"
                + "<span style='display:block;color:" + statusColor + ";font-weight:800;text-transform:uppercase;font-size:13px;letter-spacing:0.08em;margin-bottom:10px;'>" + status + "</span>"
                + "<p style='margin:0;font-size:15px;font-weight:500;color:#334155;'>" + reason + "</p></div>"
                + actionSection + "</div>"
                + "<div style='padding:24px 32px;text-align:center;font-size:12px;color:#94a3b8;border-top:1px solid #f1f5f9;'>"
                + "<p style='margin:0;'>2024 " + appName + " - SLIIT. All rights reserved.</p>"
                + "<p style='margin:6px 0 0;'>This is an automated notification. Please do not reply.</p></div>"
                + "</div></body></html>";
    }

    private String buildClubApprovalEmailTemplate(String userName, String clubName,
            String status, String statusColor, String reason, boolean approved) {
        String actionSection = approved
                ? "<p style='margin:16px 0;color:#475569;'>Your club is now live! Students can discover your profile and submit membership requests.</p>"
                + "<a href='" + appUrl + "/clubadmin-dashboard.html' style='display:inline-block;background:#21417f;color:#fff;padding:14px 32px;text-decoration:none;border-radius:10px;font-weight:700;font-size:14px;margin-top:8px;'>Open Club Dashboard</a>"
                : "<p style='margin:16px 0;color:#475569;'>Please review the feedback and resubmit your registration after addressing the noted concerns.</p>";

        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1.0'></head>"
                + "<body style='font-family:-apple-system,BlinkMacSystemFont,Inter,sans-serif;background:#f1f5f9;margin:0;padding:40px 20px;color:#1e293b;'>"
                + "<div style='max-width:600px;margin:0 auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);border:1px solid #e2e8f0;'>"
                + "<div style='background:#21417f;padding:36px 24px;text-align:center;'>"
                + "<h1 style='margin:0;color:#fff;font-size:22px;font-weight:800;'>" + appName + " Portal</h1>"
                + "<p style='margin:8px 0 0;color:#bfdbfe;font-size:13px;'>Club Registration Decision</p></div>"
                + "<div style='padding:40px 32px;'>"
                + "<p style='margin:0 0 16px;font-size:16px;'>Hello <strong>" + userName + "</strong>,</p>"
                + "<p style='margin:0 0 24px;color:#475569;'>Your registration for the club <strong>\"" + clubName + "\"</strong> has been reviewed.</p>"
                + "<div style='background:#f8fafc;border-radius:12px;padding:24px;border-left:4px solid " + statusColor + ";margin:0 0 24px;'>"
                + "<span style='display:block;color:" + statusColor + ";font-weight:800;text-transform:uppercase;font-size:13px;letter-spacing:0.08em;margin-bottom:10px;'>" + status + "</span>"
                + "<p style='margin:0;font-size:15px;font-weight:500;color:#334155;'>" + reason + "</p></div>"
                + actionSection + "</div>"
                + "<div style='padding:24px 32px;text-align:center;font-size:12px;color:#94a3b8;border-top:1px solid #f1f5f9;'>"
                + "<p style='margin:0;'>2024 " + appName + " - SLIIT. All rights reserved.</p>"
                + "<p style='margin:6px 0 0;'>This is an automated notification. Please do not reply.</p></div>"
                + "</div></body></html>";
    }

    private String buildEventApprovalEmailTemplate(String userName, String eventTitle, String clubName,
            String eventDateTime, String status, String statusColor, String reason, boolean approved) {
        String actionSection = approved
                ? "<p style='margin:16px 0;color:#475569;'>Your event is now visible to students. They can start registering right away!</p>"
                + "<a href='" + appUrl + "/clubadmin-events.html' style='display:inline-block;background:#21417f;color:#fff;padding:14px 32px;text-decoration:none;border-radius:10px;font-weight:700;font-size:14px;margin-top:8px;'>Manage Your Events</a>"
                : "<p style='margin:16px 0;color:#475569;'>Please review the feedback, make the necessary changes, and resubmit your event for approval.</p>";

        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1.0'></head>"
                + "<body style='font-family:-apple-system,BlinkMacSystemFont,Inter,sans-serif;background:#f1f5f9;margin:0;padding:40px 20px;color:#1e293b;'>"
                + "<div style='max-width:600px;margin:0 auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);border:1px solid #e2e8f0;'>"
                + "<div style='background:#21417f;padding:36px 24px;text-align:center;'>"
                + "<h1 style='margin:0;color:#fff;font-size:22px;font-weight:800;'>" + appName + " Portal</h1>"
                + "<p style='margin:8px 0 0;color:#bfdbfe;font-size:13px;'>Event Submission Decision</p></div>"
                + "<div style='padding:40px 32px;'>"
                + "<p style='margin:0 0 16px;font-size:16px;'>Hello <strong>" + userName + "</strong>,</p>"
                + "<p style='margin:0 0 24px;color:#475569;'>Our administration team has reviewed your event submission:</p>"
                + "<div style='background:#f8fafc;border-radius:12px;padding:24px;border-left:4px solid " + statusColor + ";margin:0 0 24px;'>"
                + "<span style='display:block;color:" + statusColor + ";font-weight:800;text-transform:uppercase;font-size:13px;letter-spacing:0.08em;margin-bottom:14px;'>" + status + "</span>"
                + "<div style='margin:6px 0;font-size:14px;color:#475569;'><strong>Event:</strong> " + eventTitle + "</div>"
                + "<div style='margin:6px 0;font-size:14px;color:#475569;'><strong>Organised by:</strong> " + clubName + "</div>"
                + "<div style='margin:6px 0;font-size:14px;color:#475569;'><strong>Scheduled:</strong> " + eventDateTime + "</div>"
                + "<p style='margin:16px 0 0;font-size:15px;font-weight:500;color:#334155;'>" + reason + "</p></div>"
                + actionSection + "</div>"
                + "<div style='padding:24px 32px;text-align:center;font-size:12px;color:#94a3b8;border-top:1px solid #f1f5f9;'>"
                + "<p style='margin:0;'>2024 " + appName + " - SLIIT. All rights reserved.</p>"
                + "<p style='margin:6px 0 0;'>This is an automated notification. Please do not reply.</p></div>"
                + "</div></body></html>";
    }

    private String buildEventRegistrationConfirmationTemplate(String userName, String eventTitle,
            String clubName, String eventDateTime, String location) {
        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1.0'></head>"
                + "<body style='font-family:-apple-system,BlinkMacSystemFont,Inter,sans-serif;background:#f1f5f9;margin:0;padding:40px 20px;color:#1e293b;'>"
                + "<div style='max-width:600px;margin:0 auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);border:1px solid #e2e8f0;'>"
                + "<div style='background:#21417f;padding:36px 24px;text-align:center;'>"
                + "<h1 style='margin:0;color:#fff;font-size:22px;font-weight:800;'>" + appName + " Portal</h1>"
                + "<p style='margin:8px 0 0;color:#bfdbfe;font-size:13px;'>Event Registration Confirmation</p></div>"
                + "<div style='padding:40px 32px;'>"
                + "<p style='margin:0 0 16px;font-size:16px;'>Hello <strong>" + userName + "</strong>,</p>"
                + "<p style='margin:0 0 24px;color:#475569;'>You have successfully registered for the following event:</p>"
                + "<div style='background:#f0fdf4;border-radius:12px;padding:24px;border-left:4px solid #16a34a;margin:0 0 24px;'>"
                + "<span style='display:block;color:#16a34a;font-weight:800;text-transform:uppercase;font-size:13px;margin-bottom:14px;'>Registration Confirmed</span>"
                + "<div style='margin:6px 0;font-size:14px;color:#475569;'><strong>Event:</strong> " + eventTitle + "</div>"
                + "<div style='margin:6px 0;font-size:14px;color:#475569;'><strong>Organised by:</strong> " + clubName + "</div>"
                + "<div style='margin:6px 0;font-size:14px;color:#475569;'><strong>Date and Time:</strong> " + eventDateTime + "</div>"
                + "<div style='margin:6px 0;font-size:14px;color:#475569;'><strong>Location:</strong> " + location + "</div></div>"
                + "<p style='margin:0;color:#475569;'>Mark this date in your calendar. We look forward to seeing you there!</p></div>"
                + "<div style='padding:24px 32px;text-align:center;font-size:12px;color:#94a3b8;border-top:1px solid #f1f5f9;'>"
                + "<p style='margin:0;'>2024 " + appName + " - SLIIT. All rights reserved.</p></div>"
                + "</div></body></html>";
    }

    private String buildClubMembershipConfirmationTemplate(String userName, String clubName, String clubDescription) {
        String desc = (clubDescription != null && !clubDescription.isEmpty())
                ? clubDescription.substring(0, Math.min(200, clubDescription.length()))
                : "Exciting opportunities await!";

        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1.0'></head>"
                + "<body style='font-family:-apple-system,BlinkMacSystemFont,Inter,sans-serif;background:#f1f5f9;margin:0;padding:40px 20px;color:#1e293b;'>"
                + "<div style='max-width:600px;margin:0 auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);border:1px solid #e2e8f0;'>"
                + "<div style='background:#21417f;padding:36px 24px;text-align:center;'>"
                + "<h1 style='margin:0;color:#fff;font-size:22px;font-weight:800;'>" + appName + " Portal</h1>"
                + "<p style='margin:8px 0 0;color:#bfdbfe;font-size:13px;'>Club Membership Confirmation</p></div>"
                + "<div style='padding:40px 32px;'>"
                + "<p style='margin:0 0 16px;font-size:16px;'>Hello <strong>" + userName + "</strong>,</p>"
                + "<p style='margin:0 0 24px;color:#475569;'>You have been accepted as a member of a new club:</p>"
                + "<div style='background:#f0fdf4;border-radius:12px;padding:24px;border-left:4px solid #16a34a;margin:0 0 24px;'>"
                + "<span style='display:block;color:#16a34a;font-weight:800;text-transform:uppercase;font-size:13px;margin-bottom:14px;'>Membership Confirmed</span>"
                + "<div style='margin:6px 0;font-size:14px;color:#475569;'><strong>Club:</strong> " + clubName + "</div>"
                + "<div style='margin:10px 0 0;font-size:14px;color:#475569;'><strong>About:</strong> " + desc + "</div></div>"
                + "<p style='margin:0 0 16px;color:#475569;'>You can now access all club events and connect with fellow members.</p>"
                + "<a href='" + appUrl + "/student-clubs.html' style='display:inline-block;background:#21417f;color:#fff;padding:14px 32px;text-decoration:none;border-radius:10px;font-weight:700;font-size:14px;'>View My Clubs</a></div>"
                + "<div style='padding:24px 32px;text-align:center;font-size:12px;color:#94a3b8;border-top:1px solid #f1f5f9;'>"
                + "<p style='margin:0;'>2024 " + appName + " - SLIIT. All rights reserved.</p></div>"
                + "</div></body></html>";
    }

    private String buildOtpEmailTemplate(String otp) {
        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1.0'></head>"
                + "<body style='font-family:-apple-system,BlinkMacSystemFont,Inter,sans-serif;background:#f1f5f9;margin:0;padding:40px 20px;color:#1e293b;'>"
                + "<div style='max-width:500px;margin:0 auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);border:1px solid #e2e8f0;'>"
                + "<div style='background:#21417f;padding:36px 24px;text-align:center;'>"
                + "<h1 style='margin:0;color:#fff;font-size:22px;font-weight:800;'>" + appName + " Portal</h1>"
                + "<p style='margin:8px 0 0;color:#bfdbfe;font-size:13px;'>Verify Your Account</p></div>"
                + "<div style='padding:40px 32px;text-align:center;'>"
                + "<p style='margin:0 0 24px;font-size:16px;color:#475569;'>Use the code below to complete your registration. This code will expire in 5 minutes.</p>"
                + "<div style='background:#f8fafc;border-radius:12px;padding:32px;border:2px dashed #cbd5e1;margin:0 0 24px;'>"
                + "<span style='display:block;font-size:42px;font-weight:900;letter-spacing:0.2em;color:#21417f;'>" + otp + "</span></div>"
                + "<p style='margin:0;font-size:13px;color:#94a3b8;'>If you did not request this code, please ignore this email.</p></div>"
                + "<div style='padding:24px 32px;text-align:center;font-size:12px;color:#94a3b8;border-top:1px solid #f1f5f9;'>"
                + "<p style='margin:0;'>2024 " + appName + " - SLIIT. All rights reserved.</p></div>"
                + "</div></body></html>";
    }
}
