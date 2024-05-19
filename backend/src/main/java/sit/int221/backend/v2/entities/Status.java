package sit.int221.backend.v2.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "statuses")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusId", nullable = false)
    private int id;


    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 200)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private Set<TaskV2> taskV2s = new HashSet<>();

    public void setName(String name) {
        if (name == null) {
            return;
        }
        this.name = name.trim();
        if (this.name.isEmpty()) {
            this.name = null;
        }
    }

    public void setDescription(String description) {
        if (description == null) {
            return;
        }
        this.description = description.trim();
        if (this.description.isEmpty()) {
            this.description = null;
        }
    }

}
