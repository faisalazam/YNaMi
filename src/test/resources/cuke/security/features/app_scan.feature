@security @app_scan
Feature: Automated Application Security Scanning
  Run automated application level tests against the application using OWASP ZAP

  Background:
    Given a new scanning session
    And a scanner with all policies disabled
    And all existing alerts are deleted
    And the application is navigated
    And the application is spidered

  @cwe-89
  Scenario: The application should not contain SQL injection vulnerabilities
    And the "SQL Injection" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/sql_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-79
  Scenario: The application should not contain Cross Site Scripting vulnerabilities
    And the "Cross Site Scripting" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/xss.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-22
  Scenario: The application should not contain path traversal vulnerabilities
    And the "Path Traversal" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/ssi.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-98
  Scenario: The application should not contain remote file inclusion vulnerabilities
    And the "Remote File Inclusion" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/sfi.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-97
  Scenario: The application should not contain Server side include vulnerabilities
    And the "Server Side Include" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/ssi.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-94
  Scenario: The application should not contain Server side code injection vulnerabilities
    And the "Server Side Code Injection" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/ss_code_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-78
  Scenario: The application should not contain Remote OS Command injection vulnerabilities
    And the "Remote OS Command Injection" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/os_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-113
  Scenario: The application should not contain CRLF injection vulnerabilities
    And the "CRLF Injection" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/crlf_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-601
  Scenario: The application should not contain external redirect vulnerabilities
    And the "External Redirect" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/redirect.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-541 @broken-since-zap-2.13.0
  Scenario: The application should not disclose source code
    And the "Source Code Disclosure" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/source_disclosure.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-78 @broken-since-zap-2.13.0
  Scenario: The application should not be vulnerable to Shell Shock
    And the "ShellShock" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/shell_shock.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-90 @broken-since-zap-2.13.0
  Scenario: The application should not be vulnerable to LDAP injection
    And the "LDAP Injection" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/ldap_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-91
  Scenario: The application should not be vulnerable to XPATH injection
    And the "XPath Injection" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/xpath_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-611
  Scenario: The application should not be vulnerable to Xml External Entity Attacks
    And the "XML External Entity Attack" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/xxe.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-209-poodle
  Scenario: The application should not be vulnerable to the Generic Padding Oracle attack
    And the "Generic Padding Oracle" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/padding_oracle.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-200 @broken-since-zap-2.13.0
  Scenario: The application should not expose insecure HTTP methods
    And the "Insecure HTTP Method" policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      | url | parameter | cweId | wascId |
    And the XML report is written to the file target/zap/zap-reports/insecure_methods.xml
    Then no Medium or higher risk vulnerabilities should be present