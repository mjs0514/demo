package com.tmax.core.entity;


import jakarta.persistence.*;
import lombok.*;

import static com.tmax.core.entity.EventEntity.TABLE_NAME;

@Entity
@Table(name = TABLE_NAME)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {
    public static final String TABLE_NAME = "EVENTS";
    public static final String ID = "ID";
    public static final String INSTANCE_ID = "INST_ID";
    public static final String INSTANCE_NAME = "INST_NAME";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID)
    private Long id;

    @Column(name = INSTANCE_ID)
    private String instanceId;

    @Column(name = INSTANCE_NAME)
    private String instanceName;
}
