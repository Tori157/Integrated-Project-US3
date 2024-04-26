package sit.int221.backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.ZonedDateTime;

@Entity
@Data
@Table(name = "tasks")
public class Task {
    @Id
    private Integer id;
    private String title;
    private String description;
    private String assignees;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @CreatedDate
//    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss z")
    private ZonedDateTime createdOn;

    @UpdateTimestamp
//    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss z")
    private ZonedDateTime updatedOn;
}
