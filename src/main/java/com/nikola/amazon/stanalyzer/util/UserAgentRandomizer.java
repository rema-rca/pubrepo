package com.nikola.amazon.stanalyzer.util;

import java.util.Random;

public class UserAgentRandomizer {
	
	private static String[] userAgents;
	
	static {
		userAgents = new String[10];
		userAgents[0] = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0";
		userAgents[1] = "Mozilla/5.0 (Android; Mobile; rv:30.0) Gecko/30.0 Firefox/30.0";
		userAgents[2] = "Mozilla/5.0 (Windows NT x.y; Win64; x64; rv:10.0) Gecko/20100101 Firefox/10.0";
		userAgents[3] = "Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko";
		userAgents[4] = "Mozilla/5.0 (Windows NT 6.1; Trident/7.0; rv:11.0) like Gecko";
		userAgents[5] = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.3; Trident/7.0; Touch)";
		userAgents[6] = "Mozilla/5.0 (Windows Phone 8.1; ARM; Trident/7.0; Touch; rv:11; IEMobile/11.0; NOKIA; Lumia 928) like Gecko";
		userAgents[7] = "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14";
		userAgents[8] = "Opera/12.80 (Windows NT 5.1; U; en) Presto/2.10.289 Version/12.02";
		userAgents[9] = "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36";
		userAgents[10] = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.67 Safari/537.36";
		userAgents[11] = "Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25";
	}
	
	public static String getRandomUserAgent() {
		int idx = new Random().nextInt(userAgents.length);
		return (userAgents[idx]);
	}

}
