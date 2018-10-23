
package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import repositories.SubscriptionRepository;
import domain.Subscription;

@Component
@Transactional
public class StringToSubscriptionConverter implements Converter<String, Subscription> {

	@Autowired
	SubscriptionRepository	subscriptionRepository;


	@Override
	public Subscription convert(final String text) {
		Subscription result;
		int id;
		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.subscriptionRepository.findOne(id);
			}
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
