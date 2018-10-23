
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Customer;
import domain.Newspaper;

@Repository
public interface NewspaperRepository extends JpaRepository<Newspaper, Integer> {

	@Query("select n from Newspaper n where n.publisher.id=?1")
	Collection<Newspaper> findAllByPublisher(int userId);

	// Status: 0=PUBLISHED, 1=OPEN, 2=CLOSE
	@Query("select n from Newspaper n where n.status=0")
	Collection<Newspaper> findAllPublished();

	@Query("select n from Newspaper n where n.status=0 or n.status=1")
	Collection<Newspaper> findAllPublishedOrOpened();

	@Query("select n from Newspaper n where n.status=0 and n.makePrivate=0")
	Collection<Newspaper> findAllPublicPublished();

	@Query("select n from Newspaper n where n.status=1 and n.makePrivate=0")
	Collection<Newspaper> findAllPublicOpened();

	@Query("select distinct n from Newspaper n join n.subscriptions s where n.status=0 or (n.makePrivate=1 and s.subscriber.id=?1)")
	Collection<Newspaper> findAllPublishedAndSubscribedTo(int customerId);

	@Query("select n from Newspaper n join n.subscriptions s where s.subscriber=?1")
	Collection<Newspaper> findAlreadySubscribedByCustomer(Customer customer);

	@Query("select n from Newspaper n " + "where n.status=0 and (n.title like CONCAT('%',?1,'%')" + "or n.description like CONCAT('%',?1,'%'))")
	Collection<Newspaper> findAllByKeyword(String keyword);

	@Query("select n from Newspaper n " + "where n.makePrivate=0 and n.status=0 and (n.title like CONCAT('%',?1,'%')" + "or n.description like CONCAT('%',?1,'%'))")
	Collection<Newspaper> findPublicByKeyword(String keyword);

	// Display a dashboard with the following information:
	// The ratio of users who have ever created a newspaper.
	@Query("select 1.0*(select count(u) from User u where u.newspapers.size>=1)/count(u) from User u")
	Double getRatioNewspaperCreatedByUser();

	// Display a dashboard with the following information:
	// The average and the standard deviation of newspapers created per user.
	@Query("select avg(u.newspapers.size) from User u")
	Double getAverageNewspaperPerUser();

	@Query("select stddev(u.newspapers.size) from User u")
	Double getStandardDeviationNewspaperPerUser();

	// The newspapers that have at least 10% more articles than the average.
	@Query("select n from Newspaper n where n.articles.size >= 1.1*(select avg(n.articles.size) from Newspaper n)")
	Collection<Newspaper> getNewspapersTenPercentMoreArticles();

	// The newspapers that have at least 10% fewer articles than the average.
	@Query("select n from Newspaper n where n.articles.size <= 1.1*(select avg(n.articles.size) from Newspaper n)")
	Collection<Newspaper> getNewspapersTenPercentFewerArticles();

	@Query("select sum(case when n.makePrivate=0 then 1.0 else 0.0 end)/sum(case when n.makePrivate=1 then 1.0 else 0.0 end) from Newspaper n")
	Double getRatioPublicVsPrivate();

	//FR 24.1.2
	@Query("select avg(n.articles.size) from Newspaper n where n.makePrivate=1")
	Double getAverageArticlesPerPrivateNewspaper();

	//FR 24.1.3
	@Query("select avg(n.articles.size) from Newspaper n where n.makePrivate=0")
	Double getAverageArticlesPerPublicNewspaper();

	//FR 24.1.4
	@Query("select 1.0*(n.subscriptions.size)/count(c) from Newspaper n,Customer c where n.makePrivate=1")
	Double getRatioSubscribersPrivateVsTotal();

	//FR 24.1.5
	@Query("select 1.0*count(n)/(select count(t) from Newspaper t where t.makePrivate=0) from Newspaper n where n.makePrivate=1")
	Double getAvgRatioPrivateVsPublicPerPublisher();

	//NWS2.0 RF 4.3
	@Query("select distinct n from Newspaper n,Agent ag join n.advertisements a where a member of ag.advertisements and ag.id=?1")
	Collection<Newspaper> getNewspapersWithAdvertisementByAgent(int agentId);

	//NWS2.0 RF 4.4
	@Query("select distinct n from Newspaper n where not exists (select ns from Newspaper ns,Agent ag join ns.advertisements a where a member of ag.advertisements and ag.id=?1 and ns.id=n.id) and n.makePrivate=0 and n.status=0")
	Collection<Newspaper> getNewspapersWithoutAdvertisementByAgent(int agentId);

	//
	@Query("select n from Volume v join v.newspapers n where v.id=?1 and n.status=0 and n.makePrivate=0")
	Collection<Newspaper> getVolumeNewspapersNotAuthenticated(int volumeId);

	@Query("select n from Volume v join v.newspapers n where v.id=?1 and n.status=0")
	Collection<Newspaper> getVolumeNewspapersAsCustomer(int volumeId);
}
