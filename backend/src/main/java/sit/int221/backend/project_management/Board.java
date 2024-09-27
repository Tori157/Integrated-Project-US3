package sit.int221.backend.project_management;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

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
    @ColumnDefault("'PRIVATE'")
    @Column(name = "visibility")
    private Visibility visibility = Visibility.PRIVATE;

}