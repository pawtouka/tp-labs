package by.notepad.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReminderNote extends Note {
    private final LocalTime reminderTime;
    private final boolean active;

    public ReminderNote(
            LocalDate date,
            String title,
            String text,
            LocalTime reminderTime,
            boolean active) {

        super(date, title, text);

        if (reminderTime == null) {
            throw new IllegalArgumentException(
                    "Reminder time cannot be null");
        }

        this.reminderTime = reminderTime;
        this.active = active;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return super.toString()
                + " | reminder: " + reminderTime
                + " | active: " + active;
    }
}
