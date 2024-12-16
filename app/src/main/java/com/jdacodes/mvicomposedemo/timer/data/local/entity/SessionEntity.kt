package com.jdacodes.mvicomposedemo.timer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jdacodes.mvicomposedemo.timer.domain.model.Session

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val pomodoro: Int,
    val completed: Boolean,
    val timeStarted: String
) {
    fun toDomain(): Session = Session(id, userId, pomodoro, completed, timeStarted)

    companion object {
        fun fromDomain(session: Session): SessionEntity {
            return SessionEntity(
                id = session.id,
                userId = session.userId,
                pomodoro = session.pomodoro,
                completed = session.completed,
                timeStarted = session.timeStarted
            )
        }
    }
}
