package com.ll.gooHaeYu.domain.category.entity;

import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int code = 0;

    @Column(nullable = false)
    private int level = 0;

    private boolean enabled = true;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;


}
