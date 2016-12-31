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

package net.ljcomputing.mail.rules.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ljcomputing.mail.exception.EmailRuleProcessorException;
import net.ljcomputing.mail.rules.ProcessingRule;

/**
 * Email processing rule to print the content of an email message.
 * 
 * @author James G. Willmore
 *
 */
public class PrintMessageContent implements ProcessingRule {

  /** The Constant logger. */
  private final static Logger LOGGER = LoggerFactory.getLogger(PrintMessageContent.class);

  /**
   * @see net.ljcomputing.mail.rules.ProcessingRule#ruleName()
   */
  @Override
  public String ruleName() {
    return "Print Message";
  }

  /**
   * @see net.ljcomputing.mail.rules.ProcessingRule#processMessageRule(javax.mail.Message)
   */
  @Override
  public void processMessageRule(final Message message) throws EmailRuleProcessorException {
    try {
      final Object content = message.getContent();

      if (content instanceof Multipart) {
        processMultiPart(message, (Multipart) content);
      } else {
        print(content);
      }
    } catch (EmailRuleProcessorException | IOException | MessagingException exception) {
      throw new EmailRuleProcessorException(exception);
    }
  }

  /**
   * Prints the message content.
   *
   * @param content the content
   */
  private static void print(final Object content) throws EmailRuleProcessorException {
    LOGGER.debug("--message content: {}", content);
  }

  /**
   * Process multi part.
   *
   * @param multiPart the multi part
   * @throws EmailRuleProcessorException the email rule processor exception
   */
  private void processMultiPart(final Message message, final Multipart multiPart)
      throws EmailRuleProcessorException {
    try {
      final String messageId = ((MimeMessage) message).getMessageID();

      for (int mp = 0; mp < multiPart.getCount(); mp++) {
        final BodyPart bodyPart = multiPart.getBodyPart(mp);

        LOGGER.debug("------>>>>> MIME type: {}", bodyPart.getContentType());

        if (bodyPart.isMimeType("multipart/*")) {
          processMultiPart(message, (MimeMultipart) bodyPart.getContent());
        } else if (bodyPart.isMimeType("text/plain")) {
          processText(bodyPart);
        } else if (bodyPart.isMimeType("text/html")) {
          processHtml(bodyPart);
        } else {
          processAttachment(messageId, bodyPart);
        }
      }
    } catch (IOException | MessagingException exception) {
      throw new EmailRuleProcessorException(exception);
    }
  }

  /**
   * Process text.
   *
   * @param bodyPart the body part
   * @throws EmailRuleProcessorException the email rule processor exception
   */
  private void processText(final BodyPart bodyPart) throws EmailRuleProcessorException {
    try {
      print(bodyPart.getContent());
    } catch (IOException | MessagingException exception) {
      throw new EmailRuleProcessorException(exception);
    }
  }

  /**
   * Process html.
   *
   * @param bodyPart the body part
   * @throws EmailRuleProcessorException the email rule processor exception
   */
  private void processHtml(final BodyPart bodyPart) throws EmailRuleProcessorException {
    try {
      print(Jsoup.parse((String) bodyPart.getContent()).text());
    } catch (IOException | MessagingException exception) {
      throw new EmailRuleProcessorException(exception);
    }
  }

  /**
   * Process attachment.
   *
   * @param bodyPart the body part
   * @throws EmailRuleProcessorException the email rule processor exception
   */
  private void processAttachment(final String messageId, final BodyPart bodyPart)
      throws EmailRuleProcessorException {
    try {
      final String messageIdDirectory = messageIdDirectory(messageId);
      final String filename = bodyPart.getFileName();

      final InputStream is = bodyPart.getInputStream();
      final OutputStream os = new BufferedOutputStream(
          new FileOutputStream(attachmentFile(messageIdDirectory, filename)));

      byte[] buf = new byte[1024];
      int bytesRead;

      while ((bytesRead = is.read(buf)) > 0) {
        os.write(buf, 0, bytesRead);
      }

      is.close();
      os.close();
    } catch (IOException | MessagingException exception) {
      throw new EmailRuleProcessorException(exception);
    }
  }

  /**
   * Message id directory.
   *
   * @param messageId the message id
   * @return the string
   */
  private String messageIdDirectory(final String messageId) {
    final String raw = messageId.replaceAll("<", "").replaceAll(">", "");
    final String newMessageId = raw.substring(0, raw.indexOf("@"));

    final StringBuilder builder = new StringBuilder()
        .append(System.getProperty("java.io.tmpdir")).append(System.getProperty("file.separator"))
        .append(newMessageId);

    final String directory = builder.toString();
    final File directoryFile = new File(builder.toString());
    directoryFile.mkdir();

    return directory;
  }

  /**
   * Attachment file.
   *
   * @param messageIdDirectory the message id directory
   * @param filename the filename
   * @return the file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private File attachmentFile(final String messageIdDirectory, final String filename)
      throws IOException {
    final StringBuilder builder = new StringBuilder(messageIdDirectory)
        .append(System.getProperty("file.separator")).append(filename);
    final File file = new File(builder.toString());
    
    if (!file.exists()) {
      file.createNewFile();
    }

    return file;
  }
}
