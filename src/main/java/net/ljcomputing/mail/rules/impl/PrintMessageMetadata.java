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

import java.util.Enumeration;

import javax.mail.Address;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ljcomputing.mail.exception.EmailRuleProcessorException;
import net.ljcomputing.mail.rules.ProcessingRule;

/**
 * Email processing rule to print the email message's headers.
 * 
 * @author James G. Willmore
 *
 */
public class PrintMessageMetadata implements ProcessingRule {

  /** The Constant logger. */
  private final static Logger LOGGER = LoggerFactory.getLogger(PrintMessageMetadata.class);

  /**
   * @see net.ljcomputing.mail.rules.ProcessingRule#ruleName()
   */
  @Override
  public String ruleName() {
    return "Print Message Metadata";
  }

  /**
   * @see net.ljcomputing.mail.rules.ProcessingRule#processMessageRule(javax.mail.Message)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void processMessageRule(final Message message) throws EmailRuleProcessorException {
    try {
      LOGGER.debug("--Headers:");
      Enumeration<Header> headers = message.getAllHeaders();
      printHeaders(headers);
      LOGGER.debug("--From:");
      printAddresses(message.getFrom());
      LOGGER.debug("--ALL Recipients:");
      printAddresses(message.getAllRecipients());
      LOGGER.debug("--Subject: {}", message.getSubject());
    } catch (MessagingException exception) {
      LOGGER.error("FATAL: ", exception);
      throw new EmailRuleProcessorException(exception);
    }
  }

  /**
   * Prints the addresses.
   *
   * @param addresses the addresses
   */
  private void printAddresses(final Address[] addresses) {
    if (addresses != null) {
      for (int a = 0; a < addresses.length; a++) {
        LOGGER.debug("    {}", addresses[a]);
      }
    }
  }

  /**
   * Prints the headers.
   *
   * @param headers the headers
   */
  private void printHeaders(final Enumeration<Header> headers) {
    if (headers != null) {
      while (headers.hasMoreElements()) {
        final Header header = headers.nextElement();
        LOGGER.debug("    {}: {}", header.getName(), header.getValue());
      }
    }
  }
}
