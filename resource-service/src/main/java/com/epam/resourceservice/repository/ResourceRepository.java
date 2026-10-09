package com.epam.resourceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.resourceservice.entity.Resource;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {

}
