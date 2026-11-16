package com.epam.storageservice.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "storages", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"type"}),
    @UniqueConstraint(columnNames = {"bucket", "path"})
})
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type", nullable = false, unique = true)
    private String type;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false)
    private String path;

    public Storage() {}

    public Storage(String type, String bucket, String path) {
        this.type = type;
        this.bucket = bucket;
        this.path = path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
