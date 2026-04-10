package com.example.coreservice.entity.content;

import com.example.coreservice.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "mindmap_nodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MindmapNode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atom_id")
    private KnowledgeAtom atom;

    private String label;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private MindmapNode parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<MindmapNode> children;

    private Double positionX;
    private Double positionY;

    @Column(columnDefinition = "TEXT")
    private String styleConfig;
}