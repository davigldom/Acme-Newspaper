
package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import repositories.VolumeSubscriptionRepository;
import domain.VolumeSubscription;

@Component
@Transactional
public class StringToVolumeSubscriptionConverter implements Converter<String, VolumeSubscription> {

	@Autowired
	VolumeSubscriptionRepository	volumeVolumeSubscriptionRepository;


	@Override
	public VolumeSubscription convert(final String text) {
		VolumeSubscription result;
		int id;
		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.volumeVolumeSubscriptionRepository.findOne(id);
			}
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
