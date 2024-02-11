package com.ll.gooHaeYu.domain.member.location.repository;

import com.ll.gooHaeYu.domain.member.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
