package com.myApp.myApp.utilities;

import junit.framework.TestCase;

/**
 * Created by Anton on 31.10.2017.
 */
public class ApiUtilsTest extends TestCase {
    public void testIsServiceWord() throws Exception {
        assertTrue(ApiUtils.isServiceWord("a"));
    }

}