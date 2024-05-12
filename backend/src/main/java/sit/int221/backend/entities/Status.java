package sit.int221.backend.entities;

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
        this.name = name.trim(); // ตัดช่องว่างที่เพิ่มเข้ามา
    }

    public void setDescription(String description) {
        this.description = description.trim(); // ตัดช่องว่างที่เพิ่มเข้ามา
    }
}
