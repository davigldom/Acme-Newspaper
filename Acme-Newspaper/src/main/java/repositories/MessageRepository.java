
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

	@Query("select f.messages from Folder f where f.id=?1")
	Collection<Message> findAllInAFolder(int folderId);

	@Query("select f.messages from Actor a join a.folders f where a.id=?1")
	Collection<Message> findAllOfActor(int actorId);

}
