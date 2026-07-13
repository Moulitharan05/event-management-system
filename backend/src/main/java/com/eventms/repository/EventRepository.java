package com.eventms.repository;

import com.eventms.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDateTime date);

    @Query("SELECT e FROM Event e WHERE " +
            "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:category IS NULL OR LOWER(e.category) = LOWER(:category)) " +
            "AND (:venue IS NULL OR LOWER(e.venue) LIKE LOWER(CONCAT('%', :venue, '%'))) " +
            "AND (:fromDate IS NULL OR e.eventDate >= :fromDate) " +
            "AND (:toDate IS NULL OR e.eventDate <= :toDate) " +
            "ORDER BY e.eventDate ASC")
    List<Event> search(@Param("keyword") String keyword,
                        @Param("category") String category,
                        @Param("venue") String venue,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);
}
