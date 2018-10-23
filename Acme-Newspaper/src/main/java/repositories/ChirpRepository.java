
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Chirp;

@Repository
public interface ChirpRepository extends JpaRepository<Chirp, Integer> {

	@Query("select c from Chirp c,User u where u member of c.user.followers and u.id=?1")
	Collection<Chirp> getChirpsOfFollowingUsers(int userId);


	@Query("select c from Chirp c where c.user.id=?1")
	Collection<Chirp> findAllByUser(int userId);
	
	// The average and the standard deviation of chirps by writer.
	@Query("select 1.0*count(c)/(select count(u) from User u) from Chirp c")
	Double getAverageChirpsPerUser();

	@Query("select 1.0*stddev(c)/(select count(u) from User u) from Chirp c")
	Double getStandardDeviationChirpsPerUser();
	
//	The ratio of users who have posted above 75% the average number of chirps per user.
	
	@Query("select 1.0*sum(CASE WHEN (select count(c) from Chirp c where c.user.id=u.id)>= 1.75*(select 1.0*count(c)/(select count(u) from User u) from Chirp c) THEN 1 ELSE 0 END) /(select count(u) from User u) from User u")
	Double getRatioUsersMoreChirpsThan75Percent();


}
