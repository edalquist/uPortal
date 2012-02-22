/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.portlet.ad.dao;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Generates a random string using a set of characters that are easy to read
 * without potential confusion. Upper case characters are used as well as numbers
 * except for: 0, 1, I, L
 */
public final class ReferenceKeyGenerator {
    private final Random keyGenerator;
    
    /* Valid key characters, single case and no 0, 1, i, l which can cause confusion */
    private static final char[] REFERENCE_KEY_ENCODE_TABLE = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M',
        'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '2', '3', '4', '5', '6', '7', '8', '9'
    };
    
    public ReferenceKeyGenerator() {
        keyGenerator = new SecureRandom();
    }
    
    ReferenceKeyGenerator(Random keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public final String generateKey(int length) {
        final char[] key = new char[length];
        
        for (int index = 0; index < length; index++) {
            final int charIndex = keyGenerator.nextInt(REFERENCE_KEY_ENCODE_TABLE.length);
            key[index] = REFERENCE_KEY_ENCODE_TABLE[charIndex];
        }
        
        return new String(key);
    }
}
