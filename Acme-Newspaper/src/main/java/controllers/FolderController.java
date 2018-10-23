
package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.FolderService;
import services.MessageService;
import domain.Folder;
import domain.Message;

@Controller
@RequestMapping("/folder")
public class FolderController extends AbstractController {

	// Services ---------------------------------------------------

	@Autowired
	private FolderService	folderService;

	@Autowired
	private MessageService	messageService;


	// Constructors -----------------------------------------------

	public FolderController() {
		super();
	}

	// Listing ----------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int folderId) {
		ModelAndView result = null;
		final Collection<Folder> folders = new ArrayList<Folder>();
		Folder root = null;
		String rootName = null;
		boolean thereAreChildren = false;
		int rootOfRootId = 0;
		Collection<Message> messages = new ArrayList<Message>();

		if (folderId != 0) {
			root = this.folderService.findOne(folderId);
			rootName = root.getName();
		}

		folders.addAll(this.folderService.findByRootPrincipal(folderId));

		result = new ModelAndView("folder/list");
		result.addObject("folders", folders);
		result.addObject("folderId", folderId);
		result.addObject("rootName", rootName);

		if (folderId == 0 || !this.folderService.findOne(folderId).getChildren().isEmpty())
			thereAreChildren = true;
		result.addObject("thereAreChildren", thereAreChildren);

		if (folderId != 0) {
			root = this.folderService.findOne(folderId);
			if (root.getRoot() != null)
				rootOfRootId = root.getRoot().getId();
		}
		result.addObject("rootOfRootId", rootOfRootId);

		if (folderId != 0) {
			messages = this.messageService.findAllInAFolder(this.folderService.findOne(folderId));
			if (messages != null)
				for (int i = 0; i < messages.size(); i++) {
					final List<Message> messagesList = new ArrayList<Message>(messages);
					final Message message = messagesList.get(i);
					messagesList.remove(message);
					message.setSender(message.getSender());
					message.setRecipient(message.getRecipient());
					messagesList.add(message);
				}
		}

		result.addObject("messages", messages);
		result.addObject("requestURI", "folder/list.do");

		return result;
	}
	// Creation ---------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Folder folder;

		folder = this.folderService.create();
		result = this.createEditModelAndView(folder);

		return result;
	}

	// Edition ----------------------------------------------------

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int folderId) {
		ModelAndView result;
		Folder folder;

		folder = this.folderService.findOneToEdit(folderId);
		Assert.notNull(folder);
		result = this.createEditModelAndView(folder);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Folder folder, final BindingResult binding) {
		ModelAndView result;
		int rootId = 0;

		if (folder.getId() != 0) {
			final Folder storedFolder = this.folderService.findOne(folder.getId());
			Assert.isTrue(!storedFolder.isSysFolder());
		}
		folder = this.folderService.reconstruct(folder, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(folder);
		else
			try {
				this.folderService.save(folder);
				if (folder.getRoot() != null)
					rootId = folder.getRoot().getId();
				result = new ModelAndView("redirect:list.do?folderId=" + rootId);
			} catch (final ObjectOptimisticLockingFailureException e) {
				result = this.createEditModelAndView(folder, "folder.commit.test");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(folder, "folder.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam final int folderId) {
		ModelAndView result;

		final Folder folder = this.folderService.findOne(folderId);

		try {
			this.folderService.delete(folder);
			result = new ModelAndView("redirect:list.do?folderId=0");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(folder, "folder.commit.error.delete");
		}

		return result;
	}

	// Ancillary methods -------------------------------------------

	protected ModelAndView createEditModelAndView(final Folder folder) {
		ModelAndView result;

		result = this.createEditModelAndView(folder, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Folder folder, final String messageCode) {
		final ModelAndView result;
		Collection<Folder> roots;

		roots = this.folderService.findByPrincipal();
		roots.remove(folder);

		if (folder.getId() == 0)
			result = new ModelAndView("folder/create");
		else
			result = new ModelAndView("folder/edit");

		result.addObject("folder", folder);
		result.addObject("roots", roots);
		if (folder.getRoot() != null)
			result.addObject("rootId", folder.getRoot().getId());
		result.addObject("message", messageCode);

		return result;
	}

}
