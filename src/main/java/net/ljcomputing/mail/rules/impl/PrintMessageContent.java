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

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.MediaType;

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
   * @see net.ljcomputing.mail.rules.ProcessingRule
   *    #processMessageRule(javax.mail.Message)
   */
  @Override
  public void processMessageRule(final Message message) throws MessagingException, IOException {
    final Object content = message.getContent();
    final MediaType mediaType = MediaType.parse(message.getContentType());
    
    if (mediaType.is(MediaType.ANY_TEXT_TYPE)) {
      print(content);
    } else {
      final int parts = ((Multipart)content).getCount();
      LOGGER.debug("--multipart count: {}", parts);
      
      for (int p = 0; p < parts; p++) {
        final BodyPart bodyPart = ((Multipart)content).getBodyPart(p);
        final Object bodyContent = bodyPart.getContent();
        final MediaType bodyPartMediaType = MediaType.parse(bodyPart.getContentType());
        LOGGER.debug("==>> bodyPart, content type: {}", bodyPart.getContentType());

        if (bodyPartMediaType.is(MediaType.ANY_TEXT_TYPE)) {
          print(bodyContent);
        }
        if (bodyPart.getContentType().startsWith("multipart/")) {
          LOGGER.debug("--==>>multipart count: {}", ((MimeMultipart)bodyContent).getCount());
          final MimeMultipart mimeMultipart = (MimeMultipart)bodyContent;
          print(mimeMultipart.getBodyPart(1).getContent());
        }
      }
    }
  }
  
  /**
   * Prints the message content.
   *
   * @param content the content
   */
  private static void print(final Object content) {
    LOGGER.debug("--message content: {}", content);
  }
}
