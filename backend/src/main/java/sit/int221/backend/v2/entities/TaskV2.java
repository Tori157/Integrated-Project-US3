package sit.int221.backend.v2.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "tasksV2")
public class TaskV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskId", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "assignees", length = 30)
    private String assignees;

    @ManyToOne
    @JoinColumn(name = "statusId")
    private Status status;

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
