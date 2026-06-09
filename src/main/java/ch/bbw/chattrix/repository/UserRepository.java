package ch.bbw.chattrix.repository;

import ch.bbw.chattrix.entity.mariadb.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByeMail(String eMail);
}
