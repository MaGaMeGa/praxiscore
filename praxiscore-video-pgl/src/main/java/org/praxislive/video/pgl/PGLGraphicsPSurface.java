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

import processing.core.PGraphics;
import processing.lwjgl.PSurfaceLWJGL;

/**
 *
 * @author Neil C Smith (http://neilcsmith.net)
 */
class PGLGraphicsPSurface extends PSurfaceLWJGL {

    PGLGraphicsPSurface(PGraphics graphics) {
        super(graphics);
    }

//    @Override
//    protected void initAnimator() {
//        animator = new FPSAnimator(window, 60);
//        animator.setUncaughtExceptionHandler(new GLAnimatorControl.UncaughtExceptionHandler() {
//
//            @Override
//            public synchronized void uncaughtException(GLAnimatorControl glac, GLAutoDrawable glad, Throwable thrwbl) {
//
//                stopThread();
//                PGLPlayer.Applet applet = (PGLPlayer.Applet) graphics.parent;
//                applet.error = thrwbl;
//                applet.finished = true;
//                applet.dispose();
//                applet.exitActual();
//                
//            }
//        });
//    }

//    @Override
//    protected void initWindow() {
//        super.initWindow();
//        if (!sketch.sketchFullScreen()) {
//            window.setResizable(true);
//        }
//    }

//    @Override
//    protected void initIcons() {
//        String[] files = new String[] {
//            "icons/praxislive16.png",
//            "icons/praxislive32.png",
//            "icons/praxislive48.png",
//            "icons/praxislive128.png",
//        };
//        NewtFactory.setWindowIcons(new IOUtil.ClassResources(files, this.getClass().getClassLoader(), this.getClass()));
//    }

//    @Override
//    public boolean stopThread() {
//        boolean stopped = super.stopThread();
////        if (stopped) {
//        if (window != null) {
//            final GLWindow win = window;
//            window = null;
//            display.getEDTUtil().invoke(false, new Runnable() {
//
//                @Override
//                public void run() {
//                    for (MouseListener l : win.getMouseListeners()) {
//                        win.removeMouseListener(l);
//                    }
//                    for (KeyListener l : win.getKeyListeners()) {
//                        win.removeKeyListener(l);
//                    }
//                    for (WindowListener l : win.getWindowListeners()) {
//                        win.removeWindowListener(l);
//                    }
//                    if (win.getGLEventListenerCount() > 0) {
//                        win.removeGLEventListener(win.getGLEventListener(0));
//                    }
//                    win.destroy();
//                }
//            });
//
//        }
////        }
//        return stopped;
//    }
}
