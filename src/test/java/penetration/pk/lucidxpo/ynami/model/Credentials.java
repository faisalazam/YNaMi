/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package penetration.pk.lucidxpo.ynami.model;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.stream.Collectors.joining;

public class Credentials {
    Map<String, String> credentials;

    Credentials(final String... strings) {
        credentials = stringsToMap(strings);
    }

    String get(final String name) {
        return credentials.get(name);
    }

    void add(final String... strings) {
        credentials.putAll(stringsToMap(strings));
    }

    boolean containsKey(final String key) {
        return credentials.keySet().stream().anyMatch(credName -> credName.equalsIgnoreCase(key));
    }

    public String toString() {
        return credentials.keySet().stream().map(key -> " " + key + "=" + credentials.get(key) + "\n").collect(joining());
    }

    private Map<String, String> stringsToMap(final String... strings) {
        final Map<String, String> map = newHashMap();
        if (strings.length % 2 > 0) {
            throw new RuntimeException("Credentials must be provided in pairs, e.g. 'username','bob'");
        }

        for (int it = 0; it <= strings.length / 2; it = it + 2) {
            map.put(strings[it], strings[it + 1]);
        }
        return map;
    }
}