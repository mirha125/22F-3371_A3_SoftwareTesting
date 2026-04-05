package com.banking.suites;

import org.junit.platform.suite.api.*;

/**
 * Suite: Runs only tests tagged with @Tag("fast").
 * Execute with: mvn test -Dsurefire.includeFilterFile=fast-suite.properties
 * Or via IDE by running this class directly.
 */
@Suite
@SuiteDisplayName("Fast Tests Suite")
@SelectPackages("com.banking")
@IncludeTags("fast")
@ExcludeClassNamePatterns(".*suites.*")
public class FastTestSuite {
    // JUnit Platform Suite — no body required.
}
