package com.ll.gooHaeYu.domain.category.region.repository;

import com.ll.gooHaeYu.domain.category.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
}
