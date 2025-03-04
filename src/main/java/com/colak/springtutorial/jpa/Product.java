package com.colak.springtutorial.jpa;

import com.colak.springtutorial.model.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product implements BaseModel {
    @Id
    private Long id;

    private String name;

    private String description;

    private Double price;

    @Override
    public String getModelId() {
        return String.valueOf(id);
    }

    @Override
    public String getSearchableContent() {
        return description;
    }
}
