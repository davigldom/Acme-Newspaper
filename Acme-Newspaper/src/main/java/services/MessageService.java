
package services;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.MessageRepository;
import domain.Actor;
import domain.Administrator;
import domain.Folder;
import domain.Message;

@Service
@Transactional
public class MessageService {

	// Managed repository

	@Autowired
	private MessageRepository		messageRepository;

	// Supporting services

	@Autowired
	private FolderService			folderService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private AdministratorService	administratorService;


	// Constructors

	public MessageService() {
		super();
	}

	// Simple CRUD methods

	public Message create() {
		Message result;

		result = new Message();

		return result;
	}

	public Message findOne(final int messageId) {
		Message result;

		result = this.messageRepository.findOne(messageId);
		Assert.notNull(result);

		return result;
	}

	public Collection<Message> findAll() {
		Collection<Message> result;

		result = this.messageRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	// Message returned is the message sent because the actor logged is the sender
	public Message save(final Message messageSent, final boolean isNotification, final boolean isSpam) {
		Assert.notNull(messageSent);
		final Actor sender = messageSent.getSender();
		final Actor recipient = messageSent.getRecipient();
		Assert.notNull(sender);
		Assert.notNull(recipient);
		final Actor principal = this.actorService.findByPrincipal();
		Assert.isTrue(principal.equals(sender));

		final Message messageReceived = this.copy(messageSent);

		final Folder outBoxSender = this.folderService.getOutBox(sender);
		final Folder inBoxRecipient = this.folderService.getInBox(recipient);
		final Folder notificationBoxSender = this.folderService.getNotificationBox(sender);
		final Folder notificationBoxRecipient = this.folderService.getNotificationBox(recipient);
		final Folder spamBoxRecipient = this.folderService.getSpamBox(recipient);

		final Message resultSent = this.messageRepository.save(messageSent);
		final Message resultReceived = this.messageRepository.save(messageReceived);

		outBoxSender.getMessages().add(resultSent);

		if (isNotification == true) {
			final Administrator admin = this.administratorService.findByPrincipal();
			Assert.notNull(admin);
			outBoxSender.getMessages().remove(resultSent);
			notificationBoxSender.getMessages().add(resultSent);
			notificationBoxRecipient.getMessages().add(resultReceived);
		} else
			inBoxRecipient.getMessages().add(resultReceived);

		if (isSpam)
			this.changeFolder(resultReceived, spamBoxRecipient);

		return resultSent;
	}

	public void delete(final Message message) {
		Assert.isTrue(message.getId() != 0);

		final Actor principal = this.actorService.findByPrincipal();
		this.checkPrincipal(message);

		final Folder trashBox = this.folderService.getTrashBox(principal);

		if (trashBox.getMessages().contains(message)) {
			trashBox.getMessages().remove(message);
			message.setSender(null);
			message.setRecipient(null);
			this.messageRepository.delete(message);
		} else
			this.changeFolder(message, trashBox);
	}

	// Other business methods

	public Collection<Message> findByPrincipal() {
		Collection<Message> result;
		Actor actor;

		actor = this.actorService.findByPrincipal();
		result = this.findAllOfActor(actor);

		return result;
	}

	public Message findOneToEdit(final int messageId) {
		Message result;

		result = this.messageRepository.findOne(messageId);
		Assert.notNull(result);
		this.checkPrincipal(result);

		return result;
	}

	public Collection<Message> findAllOfActor(final Actor actor) {
		Assert.notNull(actor);

		Collection<Message> result;

		result = this.messageRepository.findAllOfActor(actor.getId());

		return result;
	}

	public void checkPrincipal(final Message message) {
		Assert.notNull(message);
		final Actor principal = this.actorService.findByPrincipal();
		final Folder folder = this.folderService.findByMessage(message);
		Assert.isTrue(principal.getFolders().contains(folder));
		// Assert.isTrue(message.getSender().equals(principal) ||
		// message.getRecipient().equals(principal));
	}

	public Collection<Message> findAllInAFolder(final Folder folder) {
		Assert.notNull(folder);

		Collection<Message> result;

		result = this.messageRepository.findAllInAFolder(folder.getId());

		return result;
	}

	public void changeFolder(final Message message, final Folder folder) {
		Assert.notNull(message);
		Assert.notNull(folder);
		// this.checkPrincipal(message);

		final Folder oldFolder = this.folderService.findByMessage(message);
		oldFolder.getMessages().remove(message);
		final Folder trash = this.folderService.getTrashBox(this.actorService.findByPrincipal());
		Assert.notNull(trash);
		folder.getMessages().add(message);

	}

	public void changeFolder2(final Message message, final Folder folder) {
		Assert.notNull(message);
		Assert.notNull(folder);
		this.checkPrincipal(message);
		this.folderService.checkPrincipal(folder);

		final Folder oldFolder = this.folderService.findByMessage(message);
		oldFolder.getMessages().remove(message);
		// this.folderService.save(oldFolder);

		// final Message newMessage = this.messageRepository.save(message);

		folder.getMessages().add(message);
		// this.folderService.save(folder);

	}

	public Message copy(final Message original) {
		Assert.notNull(original);

		final Actor sender = original.getSender();
		final Actor recipient = original.getRecipient();

		final Message copy = this.create();
		copy.setSubject(original.getSubject());
		copy.setBody(original.getBody());
		copy.setMoment(original.getMoment());
		copy.setPriorityLevel(original.getPriorityLevel());
		copy.setSender(sender);
		copy.setRecipient(recipient);

		return copy;
	}

	public void flush() {
		this.messageRepository.flush();
	}


	// Reconstruct

	@Autowired
	private Validator	validator;


	public Message reconstruct(final Message message, final Actor sender, final Actor recipient, final BindingResult binding) {
		final Message messageStored;

		if (message.getId() == 0) {
			final Calendar moment = Calendar.getInstance();
			moment.setTime(new Date());

			message.setMoment(moment);
			message.setSender(sender);
			message.setRecipient(recipient);

		} else {
			messageStored = this.messageRepository.findOne(message.getId());

			message.setMoment(messageStored.getMoment());
			message.setSender(messageStored.getSender());
			message.setRecipient(messageStored.getRecipient());
			message.setId(messageStored.getId());
			message.setVersion(messageStored.getVersion());
		}
		this.validator.validate(message, binding);

		return message;
	}

}
