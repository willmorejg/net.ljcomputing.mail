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
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import net.ljcomputing.ReflectionUtils;
import net.ljcomputing.mail.rules.ProcessingRule;

/**
 * An email processor.
 * 
 * @author James G. Willmore
 *
 */
public class MailProcessor {

  /** The Constant logger. */
  private final static Logger LOGGER = LoggerFactory.getLogger(MailProcessor.class);

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
   * @throws ClassNotFoundException the class not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws InstantiationException the instantiation exception
   * @throws IllegalAccessException the illegal access exception
   */
  public MailProcessor(final Properties properties)
      throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
    this.props = new MailProperties(properties);
    this.session = Session.getDefaultInstance(properties, null);
    
    @SuppressWarnings("unchecked")
    final Class<? extends ProcessingRule>[] classes = (Class<? extends ProcessingRule>[]) ReflectionUtils
        .getClasses("net.ljcomputing.mail.rules.impl");
    
    for (int c = 0; c < classes.length; c++) {
      processingRules.add(classes[c].newInstance());
    }
  }

  /**
   * Process inbox.
   *
   * @throws MessagingException the messaging exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void processInbox() throws MessagingException, IOException {
    final String provider = props.valueOf(MailProps.PROVIDER);
    final String host = props.valueOf(MailProps.HOST);
    final String username = props.valueOf(MailProps.USERNAME);
    final String password = props.valueOf(MailProps.PASSWORD);
    final Store store = session.getStore(provider);

    store.connect(host, username, password);

    final Folder inbox = store.getFolder("INBOX");

    if (inbox == null) {
      throw new IOException("no inbox found.");
    }

    inbox.open(Folder.READ_ONLY);

    final Message[] messages = inbox.getMessages();
    LOGGER.info("inbox contains {} messages", messages.length);

    for (int i = 0; i < messages.length; i++) {
      LOGGER.info("... processing message {}", i);

      final Message message = messages[i];
      processMessage(message);

      LOGGER.info("... processing message {} ... DONE", i);
    }

    inbox.close(false);
    store.close();
  }

  /**
   * Process message.
   *
   * @param message the message
   * @throws MessagingException the messaging exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void processMessage(final Message message) throws MessagingException, IOException {
    for (final ProcessingRule rule : processingRules) {
      LOGGER.info("... processing rule {}", rule.ruleName());

      if (message.getContentType().contains(MediaType.TEXT_PLAIN_VALUE)) {
        rule.processMessageRule(message);
      } else {
        final MimeMessage mimeMessage = MimeMessageUtils.createMimeMessage(session,
            message.getInputStream());
        
        rule.processMessageRule(mimeMessage);
      }

      LOGGER.info("... processing rule {} ... DONE", rule.ruleName());
    }
  }
}
