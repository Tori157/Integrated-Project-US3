package sit.int221.backend.project_management;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "statuses")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusId", nullable = false)
    private int id;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Size(max = 200)
    @Column(name = "description", length = 200)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private Set<Task> tasks = new HashSet<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId")
    private Board board;
}
