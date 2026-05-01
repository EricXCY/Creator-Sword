package com.erix.creatorsword.compat.aeronautics;

import com.erix.creatorsword.item.frogport_grapple.FrogportHookTargetResolver;

public final class FrogportAeronauticsCompat {
    private FrogportAeronauticsCompat() {
    }

    public static void init() {
        FrogportHookTargetResolver.registerProvider(new AeronauticsHookCompatProvider());
    }
}