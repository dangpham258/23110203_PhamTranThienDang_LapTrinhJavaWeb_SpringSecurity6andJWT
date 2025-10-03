package vn.iotstar.repository;

import vn.iotstar.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

	@Query("SELECT u FROM Users u WHERE u.username = :username")
	Users getUserByUsername(@Param("username") String username);

	Optional<Users> findByEmail(String email);

	Optional<Users> findByUsernameOrEmail(String username, String email);

	Optional<Users> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}


