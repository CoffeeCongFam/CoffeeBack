package com.ucamp.coffee.domain.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.notification.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("""
			    SELECT n
			    FROM Notification n
			    WHERE n.member.memberId = :memberId
			      AND n.deletedAt IS NULL
			    ORDER BY n.createdAt DESC
			""")
	List<Notification> findAllByMemberIdOrderByCreatedAtDesc(@Param("memberId") Long memberId);
}
