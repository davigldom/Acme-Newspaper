
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Folder;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {

	@Query("select a.folders from Actor a where a.id=?1")
	Collection<Folder> findByActorId(int id);

	//select t.survivalClasses from Application a join a.trip t where a.explorer.id=?1 and a.trip.id = t.id group by t.id"
	//"select a.folders from Actor a join Folder f where a.id=?1 and f.root is null"
	@Query("select f from Folder f, Actor a where f member of a.folders and a.id=?1 and f.root is null")
	Collection<Folder> findRootByActorId(int id);

	@Query("select f from Folder f, Actor a where f member of a.folders and a.id=?1 and f.root=?2")
	Collection<Folder> findChildrenByActorId(int actorId, Folder folder);

	@Query("select f from Folder f join f.messages m where m.id=?1")
	Folder findByMessage(int messageId);

	@Query("select f from Actor a join a.folders f where a.id=?1 and f.name='in box'")
	Folder findInBox(int id);

	@Query("select f from Actor a join a.folders f where a.id=?1 and f.name='out box'")
	Folder findOutBox(int id);

	@Query("select f from Actor a join a.folders f where a.id=?1 and f.name='notification box'")
	Folder findNotificationBox(int id);

	@Query("select f from Actor a join a.folders f where a.id=?1 and f.name='trash box'")
	Folder findTrashBox(int id);

	@Query("select f from Actor a join a.folders f where a.id=?1 and f.name='spam box'")
	Folder findSpamBox(int id);
}
