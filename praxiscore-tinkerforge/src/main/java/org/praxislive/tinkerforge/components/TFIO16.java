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
 */
package org.praxislive.tinkerforge.components;

import org.praxislive.code.GenerateTemplate;
import org.praxislive.tinkerforge.TFCodeDelegate;

// default imports
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.praxislive.core.*;
import org.praxislive.core.types.*;
import org.praxislive.code.userapi.*;
import com.tinkerforge.*;
import org.praxislive.tinkerforge.userapi.*;
import static org.praxislive.code.userapi.Constants.*;
import static org.praxislive.tinkerforge.userapi.Constants.*;

/**
 *
 */
@GenerateTemplate(TFIO16.TEMPLATE_PATH)
public class TFIO16 extends TFCodeDelegate {

    final static String TEMPLATE_PATH = "resources/io16.pxj";

    // PXJ-BEGIN:body

    @TinkerForge BrickletIO16 io;
    
    @P(-1) @Config.Port(false) @Type.Boolean(def = true) boolean invertInput;
    
    @Out(0) @ID("input-a0") Output a0;
    @P(0) @Config.Port(false) @Type.Boolean(def = true)
    @ID("pull-up-a0")@OnChange("refreshInputs") boolean pullUpA0;
    @Out(1) @ID("input-a1") Output a1;
    @P(1) @Config.Port(false) @Type.Boolean(def = true)
    @ID("pull-up-a1") @OnChange("refreshInputs") boolean pullUpA1;
    @Out(2) @ID("input-a2") Output a2;
    @P(2) @Config.Port(false) @Type.Boolean(def = true)
    @ID("pull-up-a2") @OnChange("refreshInputs") boolean pullUpA2;
    @Out(3) @ID("input-a3") Output a3;
    @P(3) @Config.Port(false) @Type.Boolean(def = true)
    @ID("pull-up-a3") @OnChange("refreshInputs") boolean pullUpA3;
    @Out(4) @ID("input-a4") Output a4;
    @P(4) @Config.Port(false) @Type.Boolean(def = true)
    @ID("pull-up-a4") @OnChange("refreshInputs") boolean pullUpA4;
    @Out(5) @ID("input-a5") Output a5;
    @P(5) @Config.Port(false) @Type.Boolean(def = true)
    @ID("pull-up-a5") @OnChange("refreshInputs") boolean pullUpA5;
    @Out(6) @ID("input-a6") Output a6;
    @P(6) @Config.Port(false) @Type.Boolean(def = true)
    @ID("pull-up-a6") @OnChange("refreshInputs") boolean pullUpA6;
    @Out(7) @ID("input-a7") Output a7;
    @P(7) @Config.Port(false) @Type.Boolean(def = true)
    @ID("pull-up-a7") @OnChange("refreshInputs") boolean pullUpA7;
    
    @P(10) @ID("output-b0") @OnChange("refreshOutputs") boolean b0;
    @P(11) @ID("output-b1") @OnChange("refreshOutputs") boolean b1;
    @P(12) @ID("output-b2") @OnChange("refreshOutputs") boolean b2;
    @P(13) @ID("output-b3") @OnChange("refreshOutputs") boolean b3;
    @P(14) @ID("output-b4") @OnChange("refreshOutputs") boolean b4;
    @P(15) @ID("output-b5") @OnChange("refreshOutputs") boolean b5;
    @P(16) @ID("output-b6") @OnChange("refreshOutputs") boolean b6;
    @P(17) @ID("output-b7") @OnChange("refreshOutputs") boolean b7;
    
    BrickletIO16.InterruptListener listener = new Listener();
    boolean updateInputs, updateOutputs;
    short[] masks = new short[] {
            (short) (1 << 0),
            (short) (1 << 1),
            (short) (1 << 2),
            (short) (1 << 3),
            (short) (1 << 4),
            (short) (1 << 5),
            (short) (1 << 6),
            (short) (1 << 7),
        };
    Output[] outs;
    
    @Override
    public void setup() {
        io.addInterruptListener(listener);
        try {
            io.setPortInterrupt('a', (short) 0xFF);
        } catch (TinkerforgeException ex) {
        }
        outs = new Output[] {
            a0,a1,a2,a3,a4,a5,a6,a7
        };
        updateInputs = true;
        updateOutputs = true;
    }

    @Override
    public void update() {
        if (updateInputs) {
            try {
                io.setPortConfiguration('a', masks[0], BrickletIO16.DIRECTION_IN, pullUpA0);
                io.setPortConfiguration('a', masks[1], BrickletIO16.DIRECTION_IN, pullUpA1);
                io.setPortConfiguration('a', masks[2], BrickletIO16.DIRECTION_IN, pullUpA2);
                io.setPortConfiguration('a', masks[3], BrickletIO16.DIRECTION_IN, pullUpA3);
                io.setPortConfiguration('a', masks[4], BrickletIO16.DIRECTION_IN, pullUpA4);
                io.setPortConfiguration('a', masks[5], BrickletIO16.DIRECTION_IN, pullUpA5);
                io.setPortConfiguration('a', masks[6], BrickletIO16.DIRECTION_IN, pullUpA6);
                io.setPortConfiguration('a', masks[7], BrickletIO16.DIRECTION_IN, pullUpA7);
            } catch (TinkerforgeException ex) {
            }
            updateInputs = false;
        }
        if (updateOutputs) {
            try {
                io.setPortConfiguration('b', masks[0], BrickletIO16.DIRECTION_OUT, b0);
                io.setPortConfiguration('b', masks[1], BrickletIO16.DIRECTION_OUT, b1);
                io.setPortConfiguration('b', masks[2], BrickletIO16.DIRECTION_OUT, b2);
                io.setPortConfiguration('b', masks[3], BrickletIO16.DIRECTION_OUT, b3);
                io.setPortConfiguration('b', masks[4], BrickletIO16.DIRECTION_OUT, b4);
                io.setPortConfiguration('b', masks[5], BrickletIO16.DIRECTION_OUT, b5);
                io.setPortConfiguration('b', masks[6], BrickletIO16.DIRECTION_OUT, b6);
                io.setPortConfiguration('b', masks[7], BrickletIO16.DIRECTION_OUT, b7);
            } catch (TinkerforgeException ex) {
            }
            updateOutputs = false;
        }
    }

    @Override
    public void dispose() {
        io.removeInterruptListener(listener);
        try {
            io.setPortInterrupt('a', (short) 0);
        } catch (TinkerforgeException ex) {
        }
    }
    
    void refreshInputs() {
        updateInputs = true;
    }
    
    void refreshOutputs() {
        updateOutputs = true;
    }
    
    class Listener implements BrickletIO16.InterruptListener {

        @Override
        public void interrupt(char port, short interruptMask, short valueMask) {
            if (port == 'a') {
                for (int i=0; i<8; i++) {
                    if ((masks[i] & interruptMask) > 0) {
                        boolean value = (masks[i] & valueMask) > 0;
                        if (invertInput) {
                            value = !value;
                        }
                        outs[i].send(value);
                    }
                }
            }
        }
        
    }

    // PXJ-END:body
}
