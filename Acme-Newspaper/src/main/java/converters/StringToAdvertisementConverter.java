
package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import repositories.AdvertisementRepository;
import domain.Advertisement;

@Component
@Transactional
public class StringToAdvertisementConverter implements Converter<String, Advertisement> {

	@Autowired
	AdvertisementRepository	advertisementRepository;


	@Override
	public Advertisement convert(final String text) {
		Advertisement result;
		int id;
		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.advertisementRepository.findOne(id);
			}
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
