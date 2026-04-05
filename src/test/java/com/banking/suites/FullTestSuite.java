package com.banking.suites;

import org.junit.platform.suite.api.*;

/**
 * Suite: Runs ALL tests across all tags (fast + slow + integration).
 */
@Suite
@SuiteDisplayName("Full Test Suite")
@SelectPackages("com.banking")
@ExcludeClassNamePatterns(".*suites.*")
public class FullTestSuite {
    // JUnit Platform Suite — no body required.
}
