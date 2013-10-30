package com.google.gwt.faintynmm.server;

import com.google.gwt.faintynmm.client.game.Match;
import com.google.gwt.faintynmm.client.game.Player;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
    static {
        factory().register(Player.class);
        factory().register(Match.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}