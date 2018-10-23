
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select u from User u where u.userAccount.id=?1")
	User findByUserAccountId(int userId);

	@Query("select u.followers from User u where u.id=?1")
	Collection<User> findFollowersByUser(int userAccountId);

	@Query("select u.following from User u where u.id=?1")
	Collection<User> findFollowingByUser(int userAccountId);
	

}
