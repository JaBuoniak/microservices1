package com.epam.storageservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.storageservice.entity.Storage;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Integer> {

}
