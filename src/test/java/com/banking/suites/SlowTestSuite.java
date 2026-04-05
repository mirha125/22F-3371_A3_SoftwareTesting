package com.banking.suites;

import org.junit.platform.suite.api.*;

/**
 * Suite: Runs only tests tagged with @Tag("slow") (data-driven tests).
 */
@Suite
@SuiteDisplayName("Slow / Data-Driven Tests Suite")
@SelectPackages("com.banking")
@IncludeTags("slow")
@ExcludeClassNamePatterns(".*suites.*")
public class SlowTestSuite {
    // JUnit Platform Suite — no body required.
}
