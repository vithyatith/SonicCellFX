/*
 * Copyright 2022 Vithya Tith
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.ributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See t
 */
package com.sonicmsgr.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.List;

/**
 * FileWatcherService to be implemented to perform any action after the file
 * event is detected
 *
 * @author Omkar Marathe
 * @since October 20,2018
 *
 */
public class FolderMonitor implements Runnable {

    //private static final Logger LOGGER = Logger.getLogger(FileWatcher.class.getName());

    private final WatchService watcher;
    private final FileHandler fileHandler;
    private final List<Kind<?>> watchedEvents;
    private final Path directoryWatched;

    /**
     * @param directory
     * @Path directory to watch files into
     * @param fileHandler
     * @FileHandler implemented instance to handle the file event
     * @param watchRecursive if directory is to be watched recursively
     * @param watchedEvents Set of file events watched
     *
     * @throws IOException
     */
    public FolderMonitor(String folderName, FileHandler fileHandler, boolean watchRecursive,
            WatchEvent.Kind<?>... watchedEvents) throws IOException {
        super();
        this.watcher = FileSystems.getDefault().newWatchService();
        
        Path directory = Paths.get(folderName);
        this.fileHandler = fileHandler;
        this.directoryWatched = directory;
        this.watchedEvents = Arrays.asList(watchedEvents);
        if (watchRecursive) {
            // register all subfolders
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                   // LOGGER.log(Level.INFO, "Registering {0} ", dir);
                    dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            directory.register(watcher, watchedEvents);
        }
    }

    @SuppressWarnings({"unchecked"})
    public void run() {
        //LOGGER.log(Level.INFO, "Starting FileWatcher for {0}", directoryWatched.toAbsolutePath());
        WatchKey key = null;
        while (true) {
            try {
                key = watcher.take();
                if (key != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        //directory in which file event is detected
                        Path directory = (Path) key.watchable();
                        Path fileName = ev.context();
                        //  System.out.println("filename = "+fileName);
                        if (watchedEvents.contains(kind)) {
                           // LOGGER.log(Level.INFO, "Invoking handle on {0}", fileName.toAbsolutePath());
                            fileHandler.handle(directory.resolve(fileName).toFile(), kind);
                        }
                    }
                    key.reset();
                }
            } catch (InterruptedException ex) {
               // LOGGER.log(Level.SEVERE, "Polling Thread was interrupted ", ex);
                Thread.currentThread().interrupt();
            }
        }
    }

    public interface FileHandler {

        /**
         * Method is invoked post file event is detected
         *
         * @param file
         * @param fileEvent
         * @throws InterruptedException
         */
        void handle(File file, WatchEvent.Kind<?> fileEvent) throws InterruptedException;

    }
}
