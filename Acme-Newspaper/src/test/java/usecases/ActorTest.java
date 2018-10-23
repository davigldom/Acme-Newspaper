
package usecases;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.ActorService;
import services.FolderService;
import services.MessageService;
import utilities.AbstractTest;
import domain.Actor;
import domain.Folder;
import domain.Message;
import domain.PriorityLevel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class ActorTest extends AbstractTest {

	// System under test

	@Autowired
	private MessageService	messageService;

	@Autowired
	private FolderService	folderService;

	@Autowired
	private ActorService	actorService;


	// ------------------------------------------------------ TESTS
	// ------------------------------------------------------------------

	// Acme-Newspaper UC47: Send a message
	// ***********************************************************************************************************
	@Test
	public void driverSendMessge() {
		final Object testingData[][] = {
			// POSITIVE
			{
				"user1", "Test message", PriorityLevel.HIGH, "user2", "This is a message to be tested", null
			},
			// NEGATIVE: if a required field is blank or null, for example, the subject, the test should fail
			{
				"user1", "", PriorityLevel.HIGH, "user2", "This is a message to be tested", ConstraintViolationException.class
			},
			// NEGATIVE: if the recipient does not exist, the test should fail
			{
				"user1", "Test message", PriorityLevel.HIGH, "user20", "This is a message to be tested", AssertionError.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateSendMessage((String) testingData[i][0], (String) testingData[i][1], (PriorityLevel) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
	}

	protected void templateSendMessage(final String username, final String subject, final PriorityLevel priority, final String recipientBean, final String body, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			final Message message = new Message();
			final Calendar moment = Calendar.getInstance();
			moment.setTime(new Date());

			this.authenticate(username);

			final Actor principal = this.actorService.findByPrincipal();

			final int recipientId = super.getEntityId(recipientBean);
			final Actor recipient = this.actorService.findOne(recipientId);

			message.setSubject(subject);
			message.setPriorityLevel(priority);
			message.setSender(principal);
			message.setRecipient(recipient);
			message.setBody(body);
			message.setMoment(moment);

			final Message messageSent = this.messageService.save(message, false, false);
			this.messageService.flush();

			final Folder outBoxSender = this.folderService.getOutBox(principal);

			final Collection<Message> messagesOutBox = this.messageService.findAllInAFolder(outBoxSender);

			// Make sure that the message has been sent and that is saved in the correct folder
			Assert.isTrue(messagesOutBox.contains(messageSent));

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}

	// Acme-Newspaper UC48: Delete a message
	// ***********************************************************************************************************
	@Test
	public void driverDeleteMessage() {
		final Object testingData[][] = {
			// Positive test
			{
				"user1", "message1", null
			},
			// Negative test: as non authenticated person should fail the test
			{
				null, "message1", IllegalArgumentException.class
			},
			// Negative test: if the message is not yours, the test should fail
			{
				"user1", "message2", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteMessage((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateDeleteMessage(final String username, final String messageBean, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);

			// First, obtain the selected category to be tested
			final int messageStoredId = super.getEntityId(messageBean);
			final Message messageStored = this.messageService.findOne(messageStoredId);

			final Folder messageFolder = this.folderService.findByMessage(messageStored);
			this.folderService.checkPrincipal(messageFolder);
			List<Message> messages = new ArrayList<Message>(this.messageService.findAllInAFolder(messageFolder));

			// Then, obtain it from the list of all categories (simulating
			// that the user select it in the view of list categories)
			final int messageId = messages.indexOf(messageStored);
			final Message message = messages.get(messageId);
			this.messageService.checkPrincipal(message);

			this.messageService.delete(message);
			this.messageService.flush();

			messages = new ArrayList<Message>(this.messageService.findAllInAFolder(messageFolder));

			//Make sure it has been deleted
			Assert.isTrue(!messages.contains(message));
			Assert.isTrue(messages.size() == 2);

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}

	// Acme-Newspaper UC49: Change the message's folder
	// ***********************************************************************************************************
	@Test
	public void driverChangeFolder() {
		final Object testingData[][] = {
			// POSITIVE
			{
				"user1", "message1", "folderCustom1User1", null
			},
			// Negative test: if the folder to change is not yours, the test should fail
			{
				"user1", "message1", "folderPersonalMessagesUser2", IllegalArgumentException.class
			},
			// Negative test: if the message is not yours, the test should fail
			{
				"user1", "message2", "folderCustom1User1", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateChangeFolder((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateChangeFolder(final String username, final String messageBean, final String folderBean, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);

			// First, obtain the selected category to be tested
			final int messageStoredId = super.getEntityId(messageBean);
			final Message messageStored = this.messageService.findOne(messageStoredId);

			final Folder messageFolder = this.folderService.findByMessage(messageStored);
			this.folderService.checkPrincipal(messageFolder);
			List<Message> messages = new ArrayList<Message>(this.messageService.findAllInAFolder(messageFolder));

			// Then, obtain it from the list of all categories (simulating
			// that the user select it in the view of list categories)
			final int messageId = messages.indexOf(messageStored);
			final Message message = messages.get(messageId);
			this.messageService.checkPrincipal(message);

			final int folderId = super.getEntityId(folderBean);
			final Folder folder = this.folderService.findOne(folderId);

			this.messageService.changeFolder2(message, folder);

			final Message messageChanged = this.messageService.findOne(messageStoredId);
			messages = new ArrayList<Message>(this.messageService.findAllInAFolder(folder));
			Assert.isTrue(messages.contains(messageChanged));

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}

	// Acme-Newspaper UC50: Create a folder
	// ***********************************************************************************************************
	@Test
	public void driverCreateFolder() {
		final Object testingData[][] = {
			// POSITIVE
			{
				"user1", "Test folder", "folderCustom1User1", null
			},
			// NEGATIVE: if a required field is blank or null, for example, the name, the test should fail
			{
				"user1", "", null, ConstraintViolationException.class
			},
			// NEGATIVE: if the selected root does not exist, the test should fail
			{
				"user1", "Test folder", "non-existentFolder", ConstraintViolationException.class
			},
			// NEGATIVE: if the selected root is a folder of another user, the test should fail
			{
				"user1", "Test folder", "folderPersonalMessagesUser2", ConstraintViolationException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateCreateFolder((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateCreateFolder(final String username, final String name, final String rootBean, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		Folder root = null;
		try {
			final Folder folder = new Folder();

			this.authenticate(username);

			final Actor principal = this.actorService.findByPrincipal();

			if (rootBean != null) {
				final int rootId = super.getEntityId(rootBean);
				root = this.folderService.findOne(rootId);
			}

			folder.setName(name);
			folder.setRoot(root);
			folder.setSysFolder(false);
			folder.setChildren(new ArrayList<Folder>());
			folder.setMessages(new ArrayList<Message>());

			final Folder folderSaved = this.folderService.save(folder);
			this.folderService.flush();

			if (rootBean != null) {
				root.getChildren().add(folder);
				Assert.isTrue(root.getChildren().contains(folder));
			}

			// Make sure that the folder has been created and that is listed
			Assert.isTrue(principal.getFolders().contains(folderSaved));
			Assert.isTrue(principal.getFolders().size() == 9);

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}

	// Acme-Newspaper UC51: Edit a folder
	// ***********************************************************************************************************
	@Test
	public void driverEditFolder() {
		final Object testingData[][] = {
			// POSITIVE
			{
				"user1", "folderCustom2User1", "Test folder", "folderCustom1User1", null
			},
			// NEGATIVE: if a required field is blank or null, for example, the name, the test should fail
			{
				"user1", "folderCustom2User1", "", "folderCustom1User1", ConstraintViolationException.class
			},
			// NEGATIVE: if the selected root does not exist, the test should fail
			{
				"user1", "folderCustom2User1", "Test folder", "non-existentFolder", ConstraintViolationException.class
			},
			// NEGATIVE: if the selected root is itself, the test should fail
			{
				"user1", "folderCustom2User1", "Test folder", "folderCustom2User1", ConstraintViolationException.class
			},
			// NEGATIVE: if the selected root is a folder of another user, the test should fail
			{
				"user1", "folderCustom2User1", "Test folder", "folderPersonalMessagesUser2", ConstraintViolationException.class
			},
			// NEGATIVE: if the folder is not yours, the test should fail
			{
				"user1", "folderPersonalMessagesUser2", "Test folder", null, ConstraintViolationException.class
			},
			// NEGATIVE: if the folder is a system folder, the test should fail
			{
				"user1", "folderInBoxUser1", "Test folder", null, ConstraintViolationException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateEditFolder((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
	}

	protected void templateEditFolder(final String username, final String folderBean, final String name, final String rootBean, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);

			final Actor principal = this.actorService.findByPrincipal();

			List<Folder> folders = new ArrayList<Folder>(principal.getFolders());

			// First, obtain the selected category to be tested
			final int folderStoredId = super.getEntityId(folderBean);
			final Folder folderStored = this.folderService.findOne(folderStoredId);
			this.folderService.checkPrincipal(folderStored);
			// Then, obtain it from the list of all categories (simulating
			// that the user select it in the view of list categories)
			final int folderId = folders.indexOf(folderStored);
			final int folderInListId = folders.get(folderId).getId();
			// Finally, obtain it by findOneToEdit in order to check the constraints
			final Folder folder = this.folderService.findOneToEdit(folderInListId);

			final int rootId = super.getEntityId(rootBean);
			final Folder root = this.folderService.findOne(rootId);

			folderStored.getRoot().getChildren().remove(folder);
			folder.setName(name);
			folder.setRoot(root);

			final Folder folderSaved = this.folderService.save(folder);
			this.folderService.flush();

			root.getChildren().add(folder);

			folders = new ArrayList<Folder>(principal.getFolders());
			Assert.isTrue(folders.contains(folderSaved));
			Assert.isTrue(root.getChildren().contains(folderSaved));
			Assert.isTrue(folders.get(folders.indexOf(folderSaved)).getName() == name);
			Assert.isTrue(folders.size() == 8);

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}

	// Acme-Newspaper UC52: Delete a folder
	// ***********************************************************************************************************
	@Test
	public void driverDeleteFolder() {
		final Object testingData[][] = {
			// Positive test
			{
				"user1", "folderCustom2User1", null
			},
			// Negative test: as non authenticated person should fail the test
			{
				null, "folderCustom2User1", IllegalArgumentException.class
			},
			// Negative test: if the folder is a system folder, the test should fail
			{
				"user1", "folderInBoxUser1", IllegalArgumentException.class
			},
			// Negative test: if the folder is not yours, the test should fail
			{
				"user1", "folderPersonalMessagesUser2", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteFolder((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateDeleteFolder(final String username, final String folderBean, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);

			final Actor principal = this.actorService.findByPrincipal();

			List<Folder> folders = new ArrayList<Folder>(principal.getFolders());

			// First, obtain the selected category to be tested
			final int folderStoredId = super.getEntityId(folderBean);
			final Folder folderStored = this.folderService.findOne(folderStoredId);
			this.folderService.checkPrincipal(folderStored);
			// Then, obtain it from the list of all categories (simulating
			// that the user select it in the view of list categories)
			final int folderId = folders.indexOf(folderStored);
			final int folderInListId = folders.get(folderId).getId();
			// Finally, obtain it by findOneToEdit in order to check the constraints
			final Folder folder = this.folderService.findOneToEdit(folderInListId);

			this.folderService.delete(folder);
			this.folderService.flush();

			folders = new ArrayList<Folder>(principal.getFolders());

			//Make sure it has been deleted
			Assert.isTrue(!folders.contains(folder));
			Assert.isTrue(folders.size() == 7);

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}

	// Acme-Newspaper UC53: Send broadcast
	// ***********************************************************************************************************
	@Test
	public void driverSendBroadcast() {
		final Object testingData[][] = {
			// POSITIVE
			{
				"admin", "Test notification", PriorityLevel.HIGH, "This is a notification to be tested", null
			},
			// NEGATIVE: if a required field is blank or null, for example, the subject, the test should fail
			{
				"admin", "", PriorityLevel.HIGH, "This is a notification to be tested", ConstraintViolationException.class
			},
			// Negative test: as an user person should fail the test
			{
				"user1", "Test message", PriorityLevel.HIGH, "This is a notification to be tested", ConstraintViolationException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateSendBroadcast((String) testingData[i][0], (String) testingData[i][1], (PriorityLevel) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
	}

	protected void templateSendBroadcast(final String username, final String subject, final PriorityLevel priority, final String body, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			Message notificationSent = null;
			final Message notification = new Message();
			final Calendar moment = Calendar.getInstance();
			moment.setTime(new Date());

			this.authenticate(username);

			final Actor principal = this.actorService.findByPrincipal();
			final List<Actor> allActors = new ArrayList<Actor>(this.actorService.findAll());
			allActors.remove(principal);

			notification.setSubject(subject);
			notification.setPriorityLevel(priority);
			notification.setBody(body);
			notification.setMoment(moment);

			for (int i = 0; i < allActors.size(); i++) {
				notification.setSender(principal);
				notification.setRecipient(allActors.get(i));
				notificationSent = this.messageService.save(notification, true, false);
				this.messageService.flush();
			}

			final Folder notificationBoxSender = this.folderService.getNotificationBox(principal);

			final Collection<Message> messagesNotificationBox = this.messageService.findAllInAFolder(notificationBoxSender);

			// Make sure that the message has been sent and that is saved in the correct folder
			Assert.isTrue(messagesNotificationBox.contains(notificationSent));

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}

}
