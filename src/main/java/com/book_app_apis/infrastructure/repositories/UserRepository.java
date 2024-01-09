package com.book_app_apis.infrastructure.repositories;

import com.book_app_apis.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("SqlNoDataSourceInspection")
@Repository
public interface UserRepository extends JpaRepository<User, String> {
	User findByEmail(String email);
	@SuppressWarnings("SqlDialectInspection")
	@Query(value = "SELECT * FROM users u WHERE u.deleted = true AND DATEDIFF(:currentTime, u.deleted_time) > 20", nativeQuery = true)
	List<User> findInactiveUsers(@Param("currentTime") LocalDateTime currentTime);
}
