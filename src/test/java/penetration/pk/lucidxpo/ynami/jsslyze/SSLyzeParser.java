package penetration.pk.lucidxpo.ynami.jsslyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SSLyzeParser {
    String output;
    final static String NEWLINE = "\r\n|[\n\r\u2028\u2029\u0085]";

    public SSLyzeParser(String output) {
        this.output = output;
    }

    public List<CipherElement> listPreferredCipherSuitesFor(String protocol) {
        Scanner lineScanner = new Scanner(output).useDelimiter(NEWLINE);

        while (lineScanner.hasNext()) {
            if (lineScanner.next().contains(protocol + " Cipher Suites")) break;
        }
        lineScanner.next();
        List<CipherElement> found = new ArrayList<>();
        while (lineScanner.hasNext()) {
            String line = lineScanner.next();
            if (line.contains("Accepted") || line.isEmpty()) {
                break;
            } else {
                Scanner wordScanner = new Scanner(line);
                String name = wordScanner.next();
                wordScanner.next();
                wordScanner.next();
                int size = wordScanner.nextInt();
                found.add(new CipherElement(name, size));
                wordScanner.close();
            }
        }
        lineScanner.close();
        return found;
    }

    public List<String> listPreferredCipherSuiteNamesFor(String protocol) {
        List<String> names = new ArrayList<>();
        for (CipherElement element : listPreferredCipherSuitesFor(protocol)) {
            names.add(element.getName());
        }
        return names;
    }

    public List<String> listAcceptedCipherSuiteNamesFor(String protocol) {
        List<String> names = new ArrayList<>();
        for (CipherElement element : listAcceptedCipherSuitesFor(protocol)) {
            names.add(element.getName());
        }
        return names;
    }

    public List<String> listAllSupportedProtocols() {
        List<String> all = new ArrayList<>();
        Scanner lineScanner = new Scanner(output).useDelimiter(NEWLINE);
        String line;
        while (lineScanner.hasNext()) {
            line = lineScanner.next();
            if (line.trim().endsWith("Cipher Suites:")) {
                String nextLine = lineScanner.next();
                if (!nextLine.contains("rejected")) {
                    Scanner wordScanner = new Scanner(line);
                    wordScanner.next(); // skip '*'
                    String protocol = wordScanner.next(); // TLS or SSL
                    if (wordScanner.hasNext()) {
                        protocol += " " + wordScanner.next(); // append version number like 1.2
                    }
                    all.add(protocol);
                    wordScanner.close();
                }
            }
        }
        lineScanner.close();
        return all;
    }

    public List<String> listAllAcceptedCiphers() {
        List<String> all = new ArrayList<>();
        for (String protocol : listAllSupportedProtocols()) {
            all.addAll(listAcceptedCipherSuiteNamesFor(protocol));
        }
        return all;
    }

    public int findSmallestAcceptedKeySize() {
        List<Integer> all = new ArrayList<>();
        for (String protocol : listAllSupportedProtocols()) {
            all.add(findSmallestAcceptedKeySize(protocol));
        }
        Collections.sort(all);
        if (all.isEmpty()) throw new RuntimeException("No keys found.");
        return all.getFirst();
    }

    public int findSmallestAcceptedKeySize(String protocol) {
        List<Integer> all = new ArrayList<>();
        for (CipherElement cipherElement : listAcceptedCipherSuitesFor(protocol)) {
            all.add(cipherElement.getSize());
        }
        Collections.sort(all);
        if (all.isEmpty()) throw new RuntimeException("No keys found for protocol " + protocol);
        return all.getFirst();
    }

    public List<CipherElement> listAcceptedCipherSuitesFor(String protocol) {
        Scanner lineScanner = new Scanner(output).useDelimiter(NEWLINE);

        // Step 1: Find the correct protocol section
        while (lineScanner.hasNext()) {
            String line = lineScanner.next();
            if (line.contains(protocol + " Cipher Suites")) {
                break;
            }
        }

        // Step 2: Find the "The server accepted the following" line
        while (lineScanner.hasNext()) {
            String line = lineScanner.next();
            if (line.contains("The server accepted the following") || line.contains("the server rejected")) {
                break;
            }
        }

        List<CipherElement> found = new ArrayList<>();
        // Step 3: Now collect cipher suites
        while (lineScanner.hasNext()) {
            String line = lineScanner.next().trim();
            if (line.isEmpty() || line.startsWith("*") || line.startsWith("The group of cipher suites")) {
                break;
            } else {
                Scanner wordScanner = new Scanner(line);
                String name = wordScanner.next();
                int size = 0;
                // Try to skip codes if present
                while (wordScanner.hasNext()) {
                    if (wordScanner.hasNextInt()) {
                        size = wordScanner.nextInt();
                        break;
                    } else {
                        wordScanner.next(); // skip hex codes or irrelevant
                    }
                }
                found.add(new CipherElement(name, size));
                wordScanner.close();
            }
        }
        lineScanner.close();
        return found;
    }

    public boolean acceptsCipherWithPartialName(String name) {
        for (String cipher : listAllAcceptedCiphers()) {
            if (cipher.toUpperCase().contains(name.toUpperCase())) return true;
        }
        return false;
    }

    public boolean acceptsCipher(String name) {
        for (String cipher : listAllAcceptedCiphers()) {
            if (cipher.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public boolean doesAnyLineMatch(String regex) {
        Scanner lineScanner = new Scanner(output).useDelimiter(NEWLINE);
        while (lineScanner.hasNext()) {
            if (lineScanner.next().matches(regex)) return true;
        }
        return false;
    }

    private static class CipherElement {
        String name;
        int size;

        public CipherElement(String name, int size) {
            this.name = name;
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }
    }
}
