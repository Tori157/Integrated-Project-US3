package sit.int221.backend.project_management;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "boards")
public class Board {
    @Id
    @Column(name = "boardId", nullable = false, length = 10)
    private String id;

    @NotNull
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @NotNull
    @Column(name = "ownerId", nullable = false, length = 36)
    private String ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 10, columnDefinition = "ENUM('PUBLIC', 'PRIVATE') DEFAULT 'PRIVATE'")
    private Visibility visibility;

}