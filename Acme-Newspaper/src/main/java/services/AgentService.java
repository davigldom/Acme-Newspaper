
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.AgentRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Advertisement;
import domain.Agent;
import domain.Folder;
import domain.Message;
import forms.RegisterAgent;

@Service
@Transactional
public class AgentService {

	@Autowired
	private AgentRepository		agentRepository;

	@Autowired
	private UserAccountService	userAccountService;

	@Autowired
	private ActorService		actorService;

	@Autowired
	private FolderService		folderService;

	@Autowired
	private Validator			validator;


	public Agent create() {

		final UserAccount userAccount = this.userAccountService.create();
		final Authority authority = new Authority();
		authority.setAuthority(Authority.AGENT);
		userAccount.addAuthority(authority);

		final Agent result;
		result = new Agent();
		result.setUserAccount(userAccount);
		return result;
	}

	public Collection<Agent> findAll() {
		Collection<Agent> result;
		result = this.agentRepository.findAll();
		return result;
	}

	public Agent findOne(final int agentId) {
		Agent result;
		Assert.isTrue(agentId != 0);
		result = this.agentRepository.findOne(agentId);
		Assert.notNull(result);
		return result;
	}

	public Agent save(final Agent agent) {
		Agent result;
		Assert.notNull(agent);
		Assert.notNull(agent.getUserAccount().getUsername());
		Assert.notNull(agent.getUserAccount().getPassword());
		if (agent.getId() == 0) {
			String passwordHashed = null;
			final Md5PasswordEncoder encoder = new Md5PasswordEncoder();
			passwordHashed = encoder.encodePassword(agent.getUserAccount().getPassword(), null);
			agent.getUserAccount().setPassword(passwordHashed);

			//System folders
			final Collection<Folder> sysFolders = new HashSet<>();

			//In box
			final Folder inbox_created = this.folderService.create();
			inbox_created.setName("in box");
			inbox_created.setSysFolder(true);
			inbox_created.setMessages(new ArrayList<Message>());
			inbox_created.setChildren(new ArrayList<Folder>());
			final Folder inbox = this.folderService.save(inbox_created);
			sysFolders.add(inbox);

			//Out box
			final Folder outbox_created = this.folderService.create();
			outbox_created.setName("out box");
			outbox_created.setSysFolder(true);
			outbox_created.setMessages(new ArrayList<Message>());
			outbox_created.setChildren(new ArrayList<Folder>());
			final Folder outbox = this.folderService.save(outbox_created);
			sysFolders.add(outbox);

			//Notification box
			final Folder notificationbox_created = this.folderService.create();
			notificationbox_created.setName("notification box");
			notificationbox_created.setSysFolder(true);
			notificationbox_created.setMessages(new ArrayList<Message>());
			notificationbox_created.setChildren(new ArrayList<Folder>());
			final Folder notificationbox = this.folderService.save(notificationbox_created);
			sysFolders.add(notificationbox);

			//Trash box
			final Folder trashbox_created = this.folderService.create();
			trashbox_created.setName("trash box");
			trashbox_created.setSysFolder(true);
			trashbox_created.setMessages(new ArrayList<Message>());
			trashbox_created.setChildren(new ArrayList<Folder>());
			final Folder trashbox = this.folderService.save(trashbox_created);
			sysFolders.add(trashbox);

			//Spam box
			final Folder spambox_created = this.folderService.create();
			spambox_created.setName("spam box");
			spambox_created.setSysFolder(true);
			spambox_created.setMessages(new ArrayList<Message>());
			spambox_created.setChildren(new ArrayList<Folder>());
			final Folder spambox = this.folderService.save(spambox_created);
			sysFolders.add(spambox);

			agent.getFolders().addAll(sysFolders);
		}

		result = this.agentRepository.save(agent);
		return result;
	}

	public void delete(final Agent agent) {
		Assert.notNull(agent);
		Assert.isTrue(agent.getId() != 0);
		Assert.isTrue(this.actorService.findByPrincipal().equals(agent));
		this.agentRepository.delete(agent);
		//		Assert.isTrue(!this.agentRepository.findAll().contains(agent));
	}

	public Agent findByPrincipal() {
		Agent result;
		final UserAccount agentAccount = LoginService.getPrincipal();
		Assert.notNull(agentAccount);
		result = this.findByUserAccount(agentAccount);
		Assert.notNull(result);

		return result;
	}

	public Agent findByUserAccount(final UserAccount agentAccount) {
		Assert.notNull(agentAccount);
		Agent result;
		result = this.agentRepository.findByUserAccountId(agentAccount.getId());
		Assert.notNull(result);

		return result;
	}

	public Agent reconstructEdit(final Agent agent, final BindingResult binding) {
		Agent agentStored;

		if (agent.getId() != 0) {
			agentStored = this.agentRepository.findOne(agent.getId());
			//			result.setEmail(user.getEmail());
			//			result.setName(user.getName());
			//			result.setPhone(user.getPhone());
			//			result.setSurname(user.getSurname());
			//			result.setPostalAddress(user.getPostalAddress());

			agent.setAdvertisements(agentStored.getAdvertisements());
			agent.setId(agentStored.getId());
			agent.setUserAccount(agentStored.getUserAccount());
			agent.setVersion(agentStored.getVersion());
			agent.setFolders(agentStored.getFolders());
		}
		this.validator.validate(agent, binding);
		return agent;

	}

	public Agent reconstruct(final RegisterAgent agent, final BindingResult binding) {
		Agent result;
		Assert.isTrue(agent.isAcceptedTerms());
		Assert.isTrue(agent.getPassword().equals(agent.getRepeatedPassword()));
		result = this.create();

		result.setEmail(agent.getEmail());
		result.setName(agent.getName());
		result.setPhone(agent.getPhone());
		result.setPostalAddress(agent.getPostalAddress());
		result.setSurname(agent.getSurname());
		result.setAdvertisements(new ArrayList<Advertisement>());
		result.setFolders(new ArrayList<Folder>());

		result.getUserAccount().setUsername(agent.getUsername());
		result.getUserAccount().setPassword(agent.getPassword());

		this.validator.validate(result, binding);

		return result;
	}

	public void flush() {
		this.agentRepository.flush();
	}

}
