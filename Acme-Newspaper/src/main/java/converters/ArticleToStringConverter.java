
package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Article;

@Component
@Transactional
public class ArticleToStringConverter implements Converter<Article, String> {

	@Override
	public String convert(final Article article) {
		String result;
		if (article == null)
			result = null;
		else
			result = String.valueOf(article.getId());
		return result;
	}

}
