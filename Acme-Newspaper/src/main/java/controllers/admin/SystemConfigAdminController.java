
package controllers.admin;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.SystemConfigService;
import controllers.AbstractController;
import domain.SystemConfig;

@Controller
@RequestMapping("/system-config/admin")
public class SystemConfigAdminController extends AbstractController {

	@Autowired
	private SystemConfigService	systemConfigService;

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit() {
		ModelAndView result;
		final SystemConfig systemConfig = this.systemConfigService.findConfig();
		result = this.createEditModelAndView(systemConfig);
		result.addObject("systemConfig", systemConfig);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final SystemConfig systemConfig, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(systemConfig);
		else
			try {
				this.systemConfigService.save(systemConfig);
				result = new ModelAndView("redirect:/");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(systemConfig, "administrator.commit.error");
			}

		return result;
	}

	protected ModelAndView createEditModelAndView(final SystemConfig systemConfig) {
		ModelAndView result;

		result = this.createEditModelAndView(systemConfig, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final SystemConfig systemConfig, final String messageCode) {
		final ModelAndView result;

		result = new ModelAndView("system-config/edit");

		result.addObject("systemConfig", systemConfig);
		result.addObject("message", messageCode);

		return result;
	}

}
