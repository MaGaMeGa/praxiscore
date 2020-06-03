/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2020 Neil C Smith.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this work; if not, see http://www.gnu.org/licenses/
 *
 *
 * Please visit https://www.praxislive.org if you need additional information or
 * have any questions.
 *
 */

package org.praxislive.midi;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 *
 */
public abstract class MidiInputContext {

    private final List<Listener> listeners;

    protected MidiInputContext() {
        listeners = new CopyOnWriteArrayList<>();
    }


    public void addListener(Listener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    protected void dispatch(MidiMessage msg, long time) {
        if (msg instanceof ShortMessage) {
            ShortMessage smsg = (ShortMessage) msg;
            listeners.forEach(l -> l.midiReceived(smsg, time));
        }
    }


    public static interface Listener {

        public void midiReceived(ShortMessage msg, long time);

    }

}
