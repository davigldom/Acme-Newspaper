
package services;

import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.AdministratorRepository;
import security.LoginService;
import security.UserAccount;
import domain.Administrator;
import domain.Folder;

@Service
@Transactional
public class AdministratorService {

	@Autowired
	private AdministratorRepository	administratorRepository;

	@Autowired
	private FolderService			folderService;


	public Administrator findByPrincipal() {
		Administrator result;
		final UserAccount administratorAccount = LoginService.getPrincipal();
		Assert.notNull(administratorAccount);
		result = this.findByUserAccount(administratorAccount);
		//		Assert.notNull(result);

		return result;
	}

	public Administrator findByUserAccount(final UserAccount administratorAccount) {
		Assert.notNull(administratorAccount);
		Administrator result;
		result = this.administratorRepository.findByUserAccountId(administratorAccount.getId());
		//		Assert.notNull(result);

		return result;
	}

	public Administrator save(final Administrator administrator) {
		Administrator result;
		Assert.notNull(administrator);
		Assert.isTrue(administrator.getId() != 0);
		Assert.isTrue(administrator.equals(this.findByPrincipal()));

		if (administrator.getId() == 0) {

			//System folders
			final Collection<Folder> sysFolders = new HashSet<>();

			//In box
			final Folder inbox_created = this.folderService.create();
			inbox_created.setName("in box");
			inbox_created.setSysFolder(true);
			final Folder inbox = this.folderService.save(inbox_created);
			sysFolders.add(inbox);

			//Out box
			final Folder outbox_created = this.folderService.create();
			outbox_created.setName("out box");
			outbox_created.setSysFolder(true);
			final Folder outbox = this.folderService.save(outbox_created);
			sysFolders.add(outbox);

			//Notification box
			final Folder notificationbox_created = this.folderService.create();
			notificationbox_created.setName("notification box");
			notificationbox_created.setSysFolder(true);
			final Folder notificationbox = this.folderService.save(notificationbox_created);
			sysFolders.add(notificationbox);

			//Trash box
			final Folder trashbox_created = this.folderService.create();
			trashbox_created.setName("trash box");
			trashbox_created.setSysFolder(true);
			final Folder trashbox = this.folderService.save(trashbox_created);
			sysFolders.add(trashbox);

			//Spam box
			final Folder spambox_created = this.folderService.create();
			spambox_created.setName("spam box");
			spambox_created.setSysFolder(true);
			final Folder spambox = this.folderService.save(spambox_created);
			sysFolders.add(spambox);

			administrator.getFolders().addAll(sysFolders);
		}
		result = this.administratorRepository.save(administrator);
		return result;
	}


	//Reconstruct

	@Autowired
	private Validator	validator;


	public Administrator reconstruct(final Administrator admin, final BindingResult binding) {
		Administrator adminStored;

		if (admin.getId() != 0) {
			adminStored = this.administratorRepository.findOne(admin.getId());
			//			result.setEmail(admin.getEmail());
			//			result.setName(admin.getName());
			//			result.setPhone(admin.getPhone());
			//			result.setSurname(admin.getSurname());
			//			result.setPostalAddress(admin.getPostalAddress());
			admin.setId(adminStored.getId());
			admin.setUserAccount(adminStored.getUserAccount());
			admin.setVersion(adminStored.getVersion());
			admin.setFolders(adminStored.getFolders());
		}
		this.validator.validate(admin, binding);
		return admin;

	}

}
