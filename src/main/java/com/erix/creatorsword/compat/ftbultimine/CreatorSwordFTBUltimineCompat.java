package com.erix.creatorsword.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;

public final class CreatorSwordFTBUltimineCompat {
    private static boolean initialized = false;

    private CreatorSwordFTBUltimineCompat() {
    }

    public static void init() {
        if (initialized) {
            return;
        }

        initialized = true;

        RegisterRightClickHandlerEvent.REGISTER.register(dispatcher ->
                dispatcher.registerHandler(CreatorSwordRightClickHandler.INSTANCE)
        );
    }
}