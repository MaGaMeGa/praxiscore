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

package org.praxislive.midi.impl;

import org.praxislive.base.AbstractComponent;
import org.praxislive.midi.MidiInputContext;

/**
 *
 */
public abstract class AbstractMidiInComponent extends AbstractComponent
        implements MidiInputContext.Listener {
    
    private MidiInputContext context;

    @Override
    public void hierarchyChanged() {
        super.hierarchyChanged();
        MidiInputContext ctxt = getLookup().find(MidiInputContext.class).orElse(null);
        if (context != ctxt) {
            if (context != null) {
                context.removeListener(this);
            }
            if (ctxt != null) {
                ctxt.addListener(this);
            }
            context = ctxt;
        }
    }

}
