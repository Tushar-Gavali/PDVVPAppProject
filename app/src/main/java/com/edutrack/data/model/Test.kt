package com.edutrack.data.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Test(
    val id: String,
    val title: String,
    val description: String? = null,
    val subjectId: String? = null,
    val testType: TestType,
    val totalMarks: Int,
    val duration: Int, // in minutes
    val scheduledDate: LocalDate,
    val scheduledTime: String, // HH:MM format
    val venue: String? = null,
    val instructions: String? = null,
    val isActive: Boolean = true,
    val createdBy: String, // Teacher/Staff ID
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class TestResult(
    val id: String,
    val testId: String,
    val studentId: String,
    val obtainedMarks: Int,
    val totalMarks: Int,
    val percentage: Double,
    val grade: String,
    val remarks: String? = null,
    val submittedAt: LocalDateTime? = null,
    val evaluatedAt: LocalDateTime? = null,
    val evaluatedBy: String? = null
)

data class QuestionPaper(
    val id: String,
    val testId: String,
    val questions: List<Question>,
    val totalQuestions: Int,
    val timeLimit: Int, // in minutes
    val instructions: String,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class Question(
    val id: String,
    val questionText: String,
    val questionType: QuestionType,
    val options: List<String>? = null, // For MCQ
    val correctAnswer: String,
    val marks: Int,
    val difficulty: DifficultyLevel,
    val explanation: String? = null
)

enum class TestType {
    APTITUDE, INTERNAL, COMPETITIVE, ORAL, PRACTICAL, WRITTEN, ASSIGNMENT
}

enum class QuestionType {
    MULTIPLE_CHOICE, TRUE_FALSE, FILL_IN_BLANK, SHORT_ANSWER, LONG_ANSWER, PRACTICAL
}

enum class DifficultyLevel {
    EASY, MEDIUM, HARD
}
