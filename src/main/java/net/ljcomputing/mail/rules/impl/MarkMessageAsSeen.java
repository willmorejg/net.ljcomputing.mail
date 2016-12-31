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

import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ljcomputing.mail.rules.ProcessingRule;

/**
 * @author James G. Willmore
 *
 */
public class MarkMessageAsSeen implements ProcessingRule {
  
  /** The Constant logger. */
  private final static Logger LOGGER = LoggerFactory.getLogger(MarkMessageAsSeen.class);
  
  /**
   * @see net.ljcomputing.mail.rules.ProcessingRule#ruleName()
   */
  @Override
  public String ruleName() {
    return "Mark Message As Seen";
  }

  /**
   * @see net.ljcomputing.mail.rules.ProcessingRule
   *    #processMessageRule(javax.mail.Message)
   */
  @Override
  public void processMessageRule(final Message message) throws MessagingException, IOException {
    message.setFlag(Flag.SEEN, true);
    LOGGER.debug("--message marked as seen");
  }
}
