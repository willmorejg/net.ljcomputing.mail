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

package net.ljcomputing.mail.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ljcomputing.mail.exception.MailProcessorException;
import net.ljcomputing.mail.rules.ProcessingRule;

/**
 * An email rules processor.
 * 
 * @author James G. Willmore
 *
 */
public class EmailRulesProcessor {

  /** The Constant logger. */
  private final static Logger LOGGER = LoggerFactory.getLogger(EmailRulesProcessor.class);

  /** The JavaMail properties "helper". */
  private final MailProperties props;

  /** The email session. */
  private final Session session;

  /** The email processing rules. */
  private final Set<ProcessingRule> processingRules = new HashSet<ProcessingRule>();

  /**
   * Instantiates a new mail processor.
   *
   * @param properties the properties
   * @throws MailProcessorException the mail processor exception
   */
  public EmailRulesProcessor(final Properties properties) throws MailProcessorException {
    this.props = new MailProperties(properties);
    this.session = Session.getDefaultInstance(properties, null);
    loadProcessingRules();
  }

  /**
   * Process inbox.
   *
   * @throws MessagingException the messaging exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void processInbox() throws MailProcessorException {
    try {
      final Store store = connect();
      final Folder inbox = store.getFolder("INBOX");

      if (inbox == null) {
        throw new MailProcessorException("no inbox found.");
      }

      inbox.open(Folder.READ_ONLY);//_WRITE);

      final Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
      LOGGER.info("inbox contains {} unseen messages", messages.length);

      for (int i = 0; i < messages.length; i++) {
        LOGGER.info("............ PROCESSING MESSAGE: {}", i);

        final Message message = messages[i];
        processMessage(message);

        LOGGER.info("... DONE ... PROCESSING MESSAGE: {}", i);
      }

      inbox.close(false);
      store.close();
    } catch (MessagingException exception) {
      LOGGER.error("FATAL: ", exception);
      throw new MailProcessorException(exception);
    }
  }

  /**
   * Process message.
   *
   * @param message the message
   * @throws MailProcessorException the mail processor exception
   */
  public void processMessage(final Message message) throws MailProcessorException {
    for (final ProcessingRule rule : processingRules) {
      LOGGER.info("............ ............  processing rule {}", rule.ruleName());

      try {
        rule.processMessageRule(message);
      } catch (MessagingException | IOException exception) {
        LOGGER.error("FATAL: ", exception);
        throw new MailProcessorException(exception);
      }

      LOGGER.info("............ ... DONE ... processing rule {}", rule.ruleName());
    }
  }

  /**
   * Load properties.
   *
   * @return the properties
   * @throws MailProcessorException the mail processor exception
   */
  private Properties loadProperties() throws MailProcessorException {
    final Thread thread = Thread.currentThread();
    final ClassLoader loader = thread.getContextClassLoader();
    final InputStream is = loader.getResourceAsStream("application.properties");
    final Properties properties = new Properties();

    try {
      properties.load(is);
    } catch (IOException exception) {
      throw new MailProcessorException(exception);
    }

    return properties;
  }

  /**
   * Load rule keys.
   *
   * @param properties the properties
   * @return the sets the
   */
  private Set<String> loadRuleKeys(final Properties properties) {
    final Set<String> propKeys = new TreeSet<String>();

    for (final Object obj : properties.keySet()) {
      final String key = obj.toString();

      if (key != null && key.startsWith("email.rules.")) {
        propKeys.add(key);
      }
    }

    return propKeys;
  }

  /**
   * Load processing rules.
   *
   * @return the properties
   * @throws MailProcessorException the mail processor exception
   */
  @SuppressWarnings({ "unchecked" })
  private void loadProcessingRules() throws MailProcessorException {
    try {
      final Properties properties = loadProperties();
      final Set<String> propKeys = loadRuleKeys(properties);

      for (final String key : propKeys) {
        final String className = properties.getProperty(key);
        final Class<? extends ProcessingRule> rule = (Class<? extends ProcessingRule>) Class
            .forName(className);
        processingRules.add(rule.newInstance());
      }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
      LOGGER.error("FATAL: ", exception);
      throw new MailProcessorException(exception);
    }
  }

  /**
   * Connect to store.
   *
   * @return the store
   * @throws MailProcessorException the mail processor exception
   */
  private Store connect() throws MailProcessorException {
    final String provider = props.valueOf(MailProps.PROVIDER);
    final String host = props.valueOf(MailProps.HOST);
    final String username = props.valueOf(MailProps.USERNAME);
    final String password = props.valueOf(MailProps.PASSWORD);
    Store store;

    try {
      store = session.getStore(provider);
      store.connect(host, username, password);
    } catch (MessagingException exception) {
      LOGGER.error("FATAL: ", exception);
      throw new MailProcessorException(exception);
    }

    return store;
  }
}
