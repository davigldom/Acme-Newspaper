
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {

	@Query("select a from Article a where a.newspaper.id=?1")
	Collection<Article> findAllByNewspaper(int newspaperId);

	@Query("select a from Article a where a.creator.id=?1")
	Collection<Article> findAllByCreator(int userId);

	@Query("select a from Article a where a.published=1 and a.newspaper.makePrivate=0 and a.creator.id=?1")
	Collection<Article> findAllPublicPublishedByCreator(int userId);

	@Query("select a from Article a where a.published=1 and a.creator.id=?1")
	Collection<Article> findAllPublicAndPrivatePublishedByCreator(int userId);
	
	@Query("select a from Article a " + "where a.published=1 and a.newspaper.id=?2 and (a.title like CONCAT('%',?1,'%')" + "or a.summary like CONCAT('%',?1,'%')" + "or a.body like CONCAT('%',?1,'%'))")
	Collection<Article> findByKeyword(String keyword, int newspaperId);

	// Display a dashboard with the following information:
	// The ratio of users who have ever written an article.
	@Query("select 1.0*(select count(u) from User u where u.articles.size>=1)/count(u) from User u")
	Double getRatioArticlesCreatedByUser();

	// The average and the standard deviation of articles written by writer.

	@Query("select avg(u.articles.size) from User u")
	Double getAverageArticlesPerUser();

	@Query("select stddev(u.articles.size) from User u")
	Double getStandardDeviationArticlesPerUser();

	// The average and the standard deviation of articles per newspaper.

	@Query("select avg(n.articles.size) from Newspaper n")
	Double getAverageArticlesPerNewspaper();

	@Query("select stddev(n.articles.size) from Newspaper n")
	Double getStandardDeviationArticlesPerNewspaper();
}
