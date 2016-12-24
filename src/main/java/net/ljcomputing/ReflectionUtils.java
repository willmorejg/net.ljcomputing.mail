/**
           Copyright 2016, James G. Willmore

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package net.ljcomputing;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Reflections utility class.
 * 
 * @author James G. Willmore
 *
 */
public final class ReflectionUtils {
  
  /**
   * Instantiates a new reflection utils.
   */
  private ReflectionUtils() {
  }
  
  /**
   * Gets the classes.
   *
   * @param packageName the package name
   * @return the classes
   * @throws ClassNotFoundException the class not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static final Class<?>[] getClasses(final String packageName) throws ClassNotFoundException, IOException {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    
    assert classLoader != null;
    
    final String path = packageName.replace('.', '/');
    final Enumeration<URL> resources = classLoader.getResources(path);
    final List<File> dirs = new ArrayList<File>();
    
    while (resources.hasMoreElements()) {
      final URL resource = resources.nextElement();
      final String filename = resource.getFile();
      final File file = new File(filename);
      dirs.add(file);
    }
    
    final List<Class<?>> classes = new ArrayList<Class<?>>();
    
    for (final File directory : dirs) {
      final List<Class<?>> foundClasses = findClasses(directory, packageName);
      classes.addAll(foundClasses);
    }
    
    return classes.toArray(new Class[classes.size()]);
  }

  /**
   * Find classes.
   *
   * @param directory the directory
   * @param packageName the package name
   * @return the list
   * @throws ClassNotFoundException the class not found exception
   */
  public static final List<Class<?>> findClasses(final File directory, final String packageName)
      throws ClassNotFoundException {
    final List<Class<?>> classes = new ArrayList<Class<?>>();
    
    if (!directory.exists()) {
      return classes;
    }
    
    final File[] files = directory.listFiles();
    
    for (final File file : files) {
      if (file.getName().contains("package-info")) {
        continue;
      }
      
      if (file.isDirectory()) {
        assert !file.getName().contains(".");
        classes.addAll(findClasses(file, packageName + "." + file.getName()));
      } else if (file.getName().endsWith(".class")) {
        classes.add(Class
            .forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
      }
    }

    return classes;
  }
}
