package penetration.pk.lucidxpo.ynami.utils;

import difflib.Patch;
import edu.umass.cs.benchlab.har.AbstractNameValueComment;
import edu.umass.cs.benchlab.har.HarHeader;
import edu.umass.cs.benchlab.har.HarRequest;
import edu.umass.cs.benchlab.har.HarResponse;
import lombok.extern.slf4j.Slf4j;
import penetration.pk.lucidxpo.ynami.config.Config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static difflib.DiffUtils.diff;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

@Slf4j
public class Utils {
    public static String extractSessionIDName(final String target) {
        if (Config.getInstance().getSessionIDs().size() == 0) {
            log.warn("Attempting to extract session ID from string, but no session IDs defined in the configuration.");
        }
        for (final String sessId : Config.getInstance().getSessionIDs()) {
            final Pattern p = compile(".*" + sessId + ".*", CASE_INSENSITIVE | DOTALL | MULTILINE);
            final Matcher m = p.matcher(target);
            log.trace("Search for sessionID: " + sessId + " in string: " + target);
            if (m.matches()) {
                log.trace("\t Found.");
                return sessId;
            }
        }
        log.trace("\t Not found.");
        return null;
    }


    public static String stripTags(final String html) {
        return html.replaceAll("<.*?>", "");
    }

    public static int getDiffScore(final String one, final String two) {
        final List<String> first = asList(one.split("[\\n\\ ]+"));
        final List<String> second = asList(two.split("[\\n\\ ]+"));

        final Patch p = diff(first, second);
        return p.getDeltas().size();
    }

    public static HarRequest replaceCookies(HarRequest request, final Map<String, String> cookieMap) {
        for (final String name : cookieMap.keySet()) {
            request = changeCookieValue(request, name, cookieMap.get(name));
        }
        return request;
    }

    private static HarRequest changeCookieValue(final HarRequest request, final String name, final String value) {
        final String patternMulti = "([; ]" + name + ")=[^;]*(.*)";
        final String patternStart = "^(" + name + ")=[^;]*(.*)";

        for (final HarHeader header : request.getHeaders().getHeaders()) {
            if (header.getName().equalsIgnoreCase("COOKIE")) {
                if (header.getValue() != null) {
                    String updated = header.getValue().replaceAll(patternMulti, "$1=" + value + "$2");
                    if (updated.equals(header.getValue())) {
                        updated = header.getValue().replaceAll(patternStart, "$1=" + value + "$2");
                    }
                    header.setValue(updated);
                }
            }
        }
        return request;
    }

    public static boolean responseContainsHeader(final HarResponse response, final String headerName) {
        return response.getHeaders().getHeaders().stream()
                .anyMatch(header -> header.getName().equalsIgnoreCase(headerName));
    }

    public static String getResponseHeaderValue(final HarResponse response, final String headerName) {
        return response.getHeaders().getHeaders().stream()
                .filter(header -> header.getName().equalsIgnoreCase(headerName))
                .findFirst()
                .map(AbstractNameValueComment::getValue)
                .orElse(null);
    }

    public static boolean responseHeaderValueIsOneOf(final HarResponse response, final String headerName, final String[] permittedValues) {
        return response.getHeaders().getHeaders().stream()
                .filter(header -> header.getName().equalsIgnoreCase(headerName))
                .anyMatch(header -> stream(permittedValues).anyMatch(permitted -> permitted.equalsIgnoreCase(header.getValue())));
    }

    public static boolean mapOfStringListContainsString(final Map<String, List<String>> map, final String target) {
        log.info("Searching ciphers for: " + target);
        for (final List<String> list : map.values()) {
            for (final String value : list) {
                log.info(value);
                if (value.contains(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<String> createListOfValues(final String pathToTable) {
        final List<String> ls = newArrayList();
        try (
                final BufferedReader br = new BufferedReader(new FileReader(pathToTable))
        ) {
            String line = br.readLine();
            while (line != null) {
                line = line.replace("|", "");
                ls.add(line.trim());
                line = br.readLine();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return ls;
    }

    public static List<HashMap> createListOfMaps(final String pathToTable) {
        final List<HashMap> listMap = newArrayList();
        try (
                final BufferedReader br = new BufferedReader(new FileReader(pathToTable))
        ) {
            String line = br.readLine();
            final String[] firstLine = line.split("\\|");
            line = br.readLine();
            while (line != null) {
                final String[] lineList = line.split("\\|");
                final HashMap<String, String> map = newHashMap();
                int i = 0;
                for (final String item : lineList) {
                    map.put(firstLine[i].trim(), item.trim());
                    i = i + 1;
                }
                listMap.add(map);
                line = br.readLine();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return listMap;
    }

    public static String getHostFromUrl(final String url) throws MalformedURLException, URISyntaxException {
        final URL theUrl = new URI(url).toURL();
        return theUrl.getHost();
    }

    public static int getPortFromUrl(final String url) throws MalformedURLException, URISyntaxException {
        final URL theUrl = new URI(url).toURL();
        return theUrl.getPort();
    }
}
