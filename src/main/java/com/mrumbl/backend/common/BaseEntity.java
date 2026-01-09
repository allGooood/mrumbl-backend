package com.mrumbl.backend.common;

import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BaseEntity {

    @Column(updatable = false)
    protected LocalDateTime createdAt;

    protected LocalDateTime updatedAt;

//    @PrePersist
//    public void prePersist(){
//        LocalDateTime now = LocalDateTime.now();
//        createdAt = now;
//        updatedAt = now;
//    }

    @PreUpdate
    public void update(){
        updatedAt = LocalDateTime.now();
    }
}
