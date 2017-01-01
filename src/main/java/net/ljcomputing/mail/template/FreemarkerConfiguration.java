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

package net.ljcomputing.mail.template;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;

/**
 * Freemaker configuration class.
 * 
 * @author James G. Willmore
 *
 */
public enum FreemarkerConfiguration {
  INSTANCE;
  
  /** The Constant LOGGER. */
  private final static Logger LOGGER = LoggerFactory.getLogger(FreemarkerConfiguration.class);
  
  /** The Freemarker configuration. */
  private Configuration CONFIG;

  /**
   * Instantiates a new Freemarker configuration.
   */
  private FreemarkerConfiguration() {
    init();
  }
  
  /**
   * Inits the.
   */
  private void init() {
    if (CONFIG == null) {
      try {
        CONFIG = new Configuration(Configuration.VERSION_2_3_25);
        CONFIG.setDirectoryForTemplateLoading(templateDirectory());
        CONFIG.setDefaultEncoding("UTF-8");
      } catch (IOException exception) {
        LOGGER.error("FATAL - could not load Freemarker configuration:", exception);
      }
    }
  }
  
  /**
   * Template directory.
   *
   * @return the file
   */
  private File templateDirectory() {
    final Thread thread = Thread.currentThread();
    final ClassLoader loader = thread.getContextClassLoader();
    final URL templatesLocation = loader.getResource("templates");
    final File directory = new File(templatesLocation.getFile());
    
    return directory;
  }

  /**
   * Retrieve the Freemaker configuration.
   *
   * @return the configuration
   */
  public Configuration configuration() {
    return CONFIG;
  }
}
