package com.ey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entity.Encounter;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {

}
