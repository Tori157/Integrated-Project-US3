package sit.int221.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "assignees", length = 30)
    private String assignees;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @CreationTimestamp
    @Column(name = "createdOn", nullable = false)
    private ZonedDateTime createdOn;

    @UpdateTimestamp
    @Column(name = "updatedOn", nullable = false)
    private ZonedDateTime updatedOn;

    public void setTitle(String title) {
        if (title != null) {
            this.title = title.trim();
        }
    }

    public void setDescription(String description) {
        if (description != null) {
            this.description = description.trim();
        }
    }

    public void setAssignees(String assignees) {
        if (assignees != null) {
            this.assignees = assignees.trim();
        }
    }
}

