/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.portlet.ad.dao;

import static junit.framework.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class ReferenceKeyGeneratorTest {
    @Test
    public void testReferenceKeyEncoding() {
        final Random rnd = new Random(0);
        final ReferenceKeyGenerator keyGenerator = new ReferenceKeyGenerator(rnd);
        
        final String key1 = keyGenerator.generateKey(6);
        assertEquals("CCXNQH", key1);
        
        final String key2 = keyGenerator.generateKey(0);
        assertEquals("", key2);
        
        final String key3 = keyGenerator.generateKey(20);
        assertEquals("BFHSUT6JZDJZ9ZEGGU8X", key3);
    }
}
