
package controllers.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.AdministratorService;
import services.FolderService;
import services.MessageService;
import services.SystemConfigService;
import controllers.AbstractController;
import domain.Actor;
import domain.Administrator;
import domain.Folder;
import domain.Message;

@Controller
@RequestMapping("/message/administrator")
public class MessageAdministratorController extends AbstractController {

	// Services ---------------------------------------------------

	@Autowired
	private MessageService			messageService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private FolderService			folderService;

	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private SystemConfigService		systemConfigService;


	// Constructors -----------------------------------------------

	public MessageAdministratorController() {
		super();
	}

	// Notification ---------------------------------------------------

	@RequestMapping(value = "/notification", method = RequestMethod.GET)
	public ModelAndView createNotification() {
		ModelAndView result;
		final Message notification;

		notification = this.messageService.create();
		result = this.createEditModelAndView(notification);

		return result;
	}

	@RequestMapping(value = "/notification", method = RequestMethod.POST, params = "broadcast")
	public ModelAndView broadcast(@ModelAttribute("notification") final Message notification, final BindingResult binding) {
		ModelAndView result;

		final Administrator admin = this.administratorService.findByPrincipal();
		final Message message = this.messageService.reconstruct(notification, admin, admin, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(message);
		else
			try {
				final List<Actor> allActors = new ArrayList<Actor>(this.actorService.findAll());
				allActors.remove(admin);

				// First we look for taboo words.
				final List<String> tabooWordsList = new ArrayList<String>(this.systemConfigService.findConfig().getTabooWords());
				boolean isSpam = false;
				for (int i = 0; i < tabooWordsList.size(); i++)
					if (message.getBody().toLowerCase().contentEquals(tabooWordsList.get(i).toLowerCase()) || message.getSubject().toLowerCase().contentEquals(tabooWordsList.get(i).toLowerCase())) {
						// Found a taboo word, so we mark the message as spam.
						isSpam = true;
						break;
					}

				// Then, we send the message to every actor
				for (int i = 0; i < allActors.size(); i++) {
					message.setSender(admin);
					message.setRecipient(allActors.get(i));
					this.messageService.save(message, true, isSpam);
				}
				final Folder notificationBox = this.folderService.getNotificationBox(admin);
				result = new ModelAndView("redirect:/folder/list.do?folderId=" + notificationBox.getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(message, "ourMessage.commit.error");
			}

		return result;
	}

	// Ancillary methods -------------------------------------------

	protected ModelAndView createEditModelAndView(final Message notification) {
		ModelAndView result;

		result = this.createEditModelAndView(notification, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Message notification, final String messageCode) {
		final ModelAndView result;

		result = new ModelAndView("message/notification");
		result.addObject("notification", notification);

		result.addObject("message", messageCode);

		return result;
	}

}
