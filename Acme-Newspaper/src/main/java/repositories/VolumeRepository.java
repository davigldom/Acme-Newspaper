
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Volume;

@Repository
public interface VolumeRepository extends JpaRepository<Volume, Integer> {

	@Query("select distinct v from Volume v join v.newspapers n where n.status=0 and n.makePrivate=0")
	Collection<Volume> findAllPublic();

	@Query("select v from Volume v where v.publisher.id=?1")
	Collection<Volume> findMyVolumes(int userId);
	
	@Query("select distinct v from Volume v join v.newspapers n where n.id=?1")
	Collection<Volume> findByNewspaper(int newspaperId);

	@Query("select v from Volume v where v not in (select vs.volume from Customer c join c.volumeSubscriptions vs where c.id=?1)")
	Collection<Volume> findNotSubscribed(int customerId);
	
	@Query("select avg(v.newspapers.size) from Volume v")
	Double getAverageNewspapersPerVolume();
	
	@Query("select 1.0*sum(c.subscriptions.size)/sum(c.volumeSubscriptions.size) from Customer c")
	Double getRatioSubscriptionsNewspaperVsVolume();
}
