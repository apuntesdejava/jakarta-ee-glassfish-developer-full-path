package com.tuempresa.proyecto.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass 
public abstract class BaseEntity implements Serializable {

	@Id // Marca el campo como la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estrategia de generación de ID (IDENTITY para DBs que autoincrementan)
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Métodos equals y hashCode son importantes para la gestión de entidades por Jakarta Persistence
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseEntity that = (BaseEntity) obj;
        return Objects.equals(id, that.id);
    }

}
