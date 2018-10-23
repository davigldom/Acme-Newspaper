
package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Followup;

@Repository
public interface FollowupRepository extends JpaRepository<Followup, Integer> {

//	Display a dashboard with the following information:
//	 The average number of follow-ups per article.
@Query("select avg(a.followups.size) from Article a")
Double getAverageFollowpsPerArticle();

//TODO: Poner bien query
//The average number of follow-ups per article up to one week after the corresponding newspaper’s been published.
@Query("select avg(a.followups.size) from Article a")
Double getFollowupsPerArticleUpToWeek();


//TODO: Poner bien query
//The average number of follow-ups per article up to two weeks after the corresponding newspaper’s been published.
@Query("select avg(a.followups.size) from Article a")
Double getFollowupsPerArticleUpToTwoWeeks();
}
