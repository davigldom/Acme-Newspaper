
package repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Advertisement;
import domain.Agent;
import domain.Newspaper;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Integer> {

	@Query("select distinct a from Agent a join a.advertisements ad where ad.id=?1")
	Agent findAgentCreator(int advertisementId);

	@Query("select distinct n from Newspaper n join n.advertisements ad where ad.id=?1")
	Newspaper findNewspaper(int advertisementId);

	@Query("select 1.0*(select count(n) from Newspaper n where n.advertisements.size>=1)/count(n) from Newspaper n where n.advertisements.size=0")
	Double findRatioNewspapersAdvertisementsVSNoAdvertisements();

	@Query("select 1.0*(select count(n) from Advertisement n where n.title like '%sex%' or n.title like '%viagra%' or n.title like '%cialis%' or n.title like '%sexo%')/count(n) from Advertisement n")
	Double findRatioAdvertisementsTaboo();

}
