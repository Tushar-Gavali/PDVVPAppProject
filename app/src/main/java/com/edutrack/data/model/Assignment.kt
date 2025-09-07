package com.edutrack.data.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Assignment(
    val id: String,
    val title: String,
    val description: String,
    val subjectId: String,
    val assignedBy: String, // Teacher ID
    val assignedTo: List<String>, // Student IDs or class groups
    val totalMarks: Int,
    val dueDate: LocalDate,
    val dueTime: String? = null, // HH:MM format
    val instructions: String? = null,
    val attachments: List<String>? = null, // File paths or URLs
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class AssignmentSubmission(
    val id: String,
    val assignmentId: String,
    val studentId: String,
    val submissionText: String? = null,
    val attachments: List<String>? = null, // File paths or URLs
    val submittedAt: LocalDateTime,
    val isLate: Boolean = false,
    val lateMinutes: Int? = null,
    val status: SubmissionStatus = SubmissionStatus.SUBMITTED,
    val grade: String? = null,
    val obtainedMarks: Int? = null,
    val feedback: String? = null
)

data class AssignmentEvaluation(
    val id: String,
    val submissionId: String,
    val obtainedMarks: Int,
    val totalMarks: Int,
    val feedback: String? = null,
    val grade: String,
    val evaluatedBy: String, // Teacher ID
    val evaluatedAt: LocalDateTime = LocalDateTime.now(),
    val isLatePenaltyApplied: Boolean = false,
    val latePenaltyMarks: Int? = null
)

enum class SubmissionStatus {
    DRAFT, SUBMITTED, LATE, EVALUATED, RETURNED
}
