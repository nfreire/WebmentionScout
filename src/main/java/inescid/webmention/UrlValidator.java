package inescid.webmention;

import java.util.regex.Pattern;

public class UrlValidator {
	public static final Pattern URL_PATTERN=Pattern.compile("^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$");
	public static final Pattern EUROPEANA_URL_PATTERN=Pattern.compile("^(https?:\\/\\/)?([\\da-z\\.-]+)\\.europeana\\.eu([\\/\\w \\.-]*)*\\/?$");

	
	public boolean isValidSource(String url) {
//		if (!URL_PATTERN.matcher(url).matches()) {
//			return false;
//		}
		return URL_PATTERN.matcher(url).matches();
	}
	public boolean isValidTarget(String url) {
//		if (!URL_PATTERN.matcher(url).matches()) {
//			return false;
//		}
		return EUROPEANA_URL_PATTERN.matcher(url).matches();
	}

	public boolean isValidMention(Webmention mention) {
		//TODO
		return true;
	}
}
