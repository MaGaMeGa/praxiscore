/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2018 Neil C Smith.
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
package org.praxislive.video.pgl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.lwjgl.PLWJGL;
import processing.opengl.PGraphicsOpenGL;

/**
 *
 * @author Neil C Smith (http://neilcsmith.net)
 */
class PGLLWJGL extends PLWJGL {

    boolean disposing;

    public PGLLWJGL(PGraphicsOpenGL pg) {
        super(pg);
    }

    @Override
    public void dispose() {
        super.dispose();
        sketch = null;
        graphics = null;

        if (primaryPGL) {
            disposeResources();
        }
    }

    String[] preprocessFragmentSource(String[] fragment) {
        return preprocessFragmentSource(fragment, getGLSLVersion(), getGLSLVersionSuffix());
    }

    String[] preprocessVertexSource(String[] vertex) {
        return preprocessVertexSource(vertex, getGLSLVersion(), getGLSLVersionSuffix());
    }

    private void disposeResources() {
        try {
            Field refListField = PGraphicsOpenGL.class.getDeclaredField("reachableWeakReferences");
            refListField.setAccessible(true);
            List<?> refList = (List<?>) refListField.get(null);
            for (Object ref : refList.toArray()) {
                Field pglField = ref.getClass().getDeclaredField("pgl");
                pglField.setAccessible(true);
                if (pglField.get(ref) == this) {
                    pglField.set(ref, null);
                    refList.remove(ref);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(PGLLWJGL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
