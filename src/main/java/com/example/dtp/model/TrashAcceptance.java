// TrashAcceptance.java
package com.example.dtp.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "trash_acceptance")
public class TrashAcceptance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "trash_id", nullable = false)
    private Trash trash;

    @Column(name = "accepted_at", updatable = false)
    private LocalDateTime acceptedAt = LocalDateTime.now();

    public TrashAcceptance(User user, Trash trash) {
        this.user = user;
        this.trash = trash;
    }
}
