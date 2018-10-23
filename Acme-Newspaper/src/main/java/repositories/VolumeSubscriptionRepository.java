
package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.VolumeSubscription;

@Repository
public interface VolumeSubscriptionRepository extends JpaRepository<VolumeSubscription, Integer> {

}
