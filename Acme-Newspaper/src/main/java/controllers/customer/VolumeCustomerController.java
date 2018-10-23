
package controllers.customer;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import controllers.AbstractController;

import services.CustomerService;
import services.VolumeService;
import domain.Customer;
import domain.Volume;
import domain.VolumeSubscription;

@Controller
@RequestMapping("/volume/customer")
public class VolumeCustomerController extends AbstractController {


	@Autowired
	private CustomerService		customerService;

	@Autowired
	private VolumeService		volumeService;

	@RequestMapping(value = "/list-subscribed", method = RequestMethod.GET)
	public ModelAndView listSubscribed() {
		ModelAndView result = null;
		final Customer principal = this.customerService.findByPrincipal();

		result = new ModelAndView("volume/list");
		final Collection<Volume> volumes = new ArrayList<Volume>();
		for (VolumeSubscription vs : principal.getVolumeSubscriptions()) {
			if(!volumes.contains(vs.getVolume())) volumes.add(vs.getVolume());
		}
		result.addObject("volumes", volumes);
		result.addObject("requestURI", "volume/customer/list-subscribed.do");

		return result;
	}
	
	@RequestMapping(value = "/list-not-subscribed", method = RequestMethod.GET)
	public ModelAndView listNotSubscribed() {
		ModelAndView result = null;
		final Customer principal = this.customerService.findByPrincipal();

		result = new ModelAndView("volume/list");
		result.addObject("volumes", this.volumeService.findNotSubscribed(principal.getId()));
		result.addObject("notSubscribed", true);
		result.addObject("requestURI", "volume/customer/list-not-subscribed.do");

		return result;
	}


}
