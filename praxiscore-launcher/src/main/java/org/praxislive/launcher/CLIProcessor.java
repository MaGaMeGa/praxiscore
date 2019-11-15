///*
// * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
// *
// * Copyright 2018 Neil C Smith.
// *
// * This code is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License version 3 only, as
// * published by the Free Software Foundation.
// *
// * This code is distributed in the hope that it will be useful, but WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
// * version 3 for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License version 3
// * along with this work; if not, see http://www.gnu.org/licenses/
// * 
// *
// * Please visit https://www.praxislive.org if you need additional information or
// * have any questions.
// */
//package org.praxislive.nb.launcher;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintStream;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.util.stream.Stream;
//import org.praxislive.hub.Hub;
//import org.praxislive.hub.net.SlaveFactory;
//import org.netbeans.api.sendopts.CommandException;
//import org.netbeans.spi.sendopts.Env;
//import org.netbeans.spi.sendopts.Option;
//import org.netbeans.spi.sendopts.OptionProcessor;
//import org.openide.filesystems.FileObject;
//import org.openide.filesystems.FileUtil;
//import org.openide.modules.InstalledFileLocator;
//import org.openide.util.lookup.ServiceProvider;
//
///**
// *
// * @author Neil C Smith (http://neilcsmith.net)
// */
//@ServiceProvider(service = OptionProcessor.class)
//public class CLIProcessor extends OptionProcessor {
//
//    private final static Logger LOG = Logger.getLogger(CLIProcessor.class.getName());
//    private final static Option ALWAYS = Option.always();
//    private final static Option HARNESS = Option.withoutArgument(Option.NO_SHORT_NAME, "harness");
//    private final static Option SLAVE = Option.withoutArgument(Option.NO_SHORT_NAME, "slave");
//    private final static Option PORT = Option.requiredArgument(Option.NO_SHORT_NAME, "port");
//    private final static Option NETWORK = Option.requiredArgument(Option.NO_SHORT_NAME, "network");
//    private final static Option ARGUMENTS = Option.defaultArguments();
//
//    private final static String PROJECT_PXP = "project.pxp";
//
//    @Override
//    protected Set<Option> getOptions() {
//        Set<Option> opts = new HashSet<Option>(5);
//        opts.add(ALWAYS);
//        opts.add(ARGUMENTS);
//        opts.add(SLAVE);
//        opts.add(PORT);
//        opts.add(NETWORK);
//        opts.add(HARNESS);
//        return opts;
//    }
//
//    @Override
//    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
//
////        // set up UI        
////        PraxisLAFManager.getInstance().installUI();
////        UIManager.put("ClassLoader", Lookup.getDefault().lookup(ClassLoader.class));
//        if (LOG.isLoggable(Level.FINE)) {
//            LOG.log(Level.FINE, "Current Directory : {0}", env.getCurrentDirectory());
//            LOG.log(Level.FINE, "netbeans.user.dir : {0}", System.getProperty("netbeans.user.dir"));
//        }
//
//        if (optionValues.containsKey(SLAVE)) {
//            printVersion(env);
//            processSlave(env, optionValues);
//        } else if (optionValues.containsKey(HARNESS)) {
//            processHarness(env, optionValues);
//        } else if (optionValues.containsKey(ARGUMENTS)) {
//            printVersion(env);
//            processScript(env, optionValues.get(ARGUMENTS));
//        }
//
//    }
//    
//    private void printVersion(Env env) {
//        String version = System.getProperty("praxis.version", "");
//        PrintStream out = env.getOutputStream();
//        if (version.isEmpty()) {
//            out.println("Praxis CORE v4");
//        } else {
//            out.println("Praxis CORE v" + version);
//        }
//        out.flush();
//    } 
//
//    private void processHarness(Env env, Map<Option, String[]> options) throws CommandException {
//
//        try {
//            FileObject folder = FileUtil.toFileObject(
//                    InstalledFileLocator.getDefault().locate(
//                            "modules", "org.praxislive.nb.launcher", false));
//            FileObject projects = folder.getParent().getParent().getFileObject("projects");
//            String[] children = Stream.of(projects.getChildren())
//                    .map(FileObject::getNameExt)
//                    .sorted()
//                    .toArray(String[]::new);
//            if (children.length == 0) {
//                throw new IllegalStateException("No projects found");
//            }
//            List<String> scripts = new ArrayList<>(children.length);
//            for (String child : children) {
//                FileObject project = projects.getFileObject(child);
//                FileObject projectFile = project.getFileObject(PROJECT_PXP);
//                String script = projectFile.asText();
//                script = "set _PWD " + FileUtil.toFile(project).toURI() + "\n" + script;
//                scripts.add(script);
//            }
//
//            Hub hub = Hub.builder()
//                    .addExtension(new NonGuiPlayer(scripts))
//                    .build();
//            hub.start();
//            hub.await();
//
//        } catch (Exception ex) {
//            throw new CommandException(1, ex.getMessage());
//        }
//
//    }
//
//    private void processSlave(Env env, Map<Option, String[]> options) throws CommandException {
//        int port = SlaveFactory.DEFAULT_PORT;
//        boolean loopBack = true;
//        String netMask = null;
//        if (options.containsKey(PORT)) {
//            try {
//                port = Integer.valueOf(options.get(PORT)[0]);
//            } catch (Exception ex) {
//                throw new CommandException(1, "Port must be a number");
//            }
//        }
//        if (options.containsKey(NETWORK)) {
//            netMask = options.get(NETWORK)[0];
//            if ("all".equalsIgnoreCase(netMask)) {
//                netMask = null;
//            }
//            loopBack = false;
//        }
//
//        while (true) {
//            SlaveFactory sf = null;
//            try {
//                sf = new SlaveFactory(port, loopBack, netMask);
//            } catch (Exception e) {
//                throw new CommandException(1, e.getMessage());
//            }
//            Hub hub = Hub.builder()
//                    .setCoreRootFactory(sf)
//                    .build();
//            try {
//                hub.start();
//                hub.await();
//            } catch (Exception ex) {
//                throw new CommandException(1, ex.getMessage());
//            }
//        }
//
//    }
//
//    private void processScript(Env env, String[] args) throws CommandException {
//        if (args.length < 1) {
//            throw new CommandException(1, "Too many script files specified on command line.");
//        }
//        String script;
//        try {
//            script = loadScript(env, args[0]);
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error loading script file", ex);
//            throw new CommandException(1, "Error loading script file.");
//        }
//        try {
//            Hub hub = Hub.builder()
//                    .addExtension(new NonGuiPlayer(Collections.singletonList(script)))
//                    .build();
//            hub.start();
//            hub.await();
//        } catch (Exception ex) {
//            throw new CommandException(1, "Error starting hub");
//        }
//    }
//
//    private String loadScript(Env env, String filename) throws IOException {
//
//        LOG.log(Level.FINE, "File : {0}", filename);
//        File f = new File(filename);
//        if (!f.isAbsolute()) {
//            f = new File(env.getCurrentDirectory(), filename);
//        }
//        LOG.log(Level.FINE, "java.io.File : {0}", f);
//        FileObject target = FileUtil.toFileObject(f);
//        if (target == null) {
//            LOG.log(Level.FINE, "Can't find script file");
//            throw new IOException("Can't find script file");
//        }
//        if (target.isFolder()) {
//            target = target.getFileObject(PROJECT_PXP);
//            if (target == null) {
//                throw new IOException("No project file found in target folder");
//            }
//        }
//        LOG.log(Level.FINE, "Loading script : {0}", target);
//        String script = target.asText();
//        script = "set _PWD " + FileUtil.toFile(target.getParent()).toURI() + "\n" + script;
//        return script;
//    }
//
//}
