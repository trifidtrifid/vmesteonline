package com.vmesteonline.be;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AllTests.class, AuthServiceImpTests.class, MessageServiceTests.class })
public class AllTests {

}
