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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.ljcomputing.mail.domain.Email;
import net.ljcomputing.mail.exception.EmailRuleProcessorException;
import net.ljcomputing.mail.rules.ProcessingRule;
import net.ljcomputing.mail.template.FreemarkerConfiguration;

/**
 * Email processing rule to print the email message using a Freemarker template.
 * 
 * @author James G. Willmore
 *
 */
public class PrintMessageUsingTemplate implements ProcessingRule {

  /** The Constant logger. */
  private final static Logger LOGGER = LoggerFactory.getLogger(PrintMessageUsingTemplate.class);
  
  private static final Configuration FREEMARKER_CFG = FreemarkerConfiguration.INSTANCE.configuration();

  /**
   * @see net.ljcomputing.mail.rules.ProcessingRule#ruleName()
   */
  @Override
  public String ruleName() {
    return "Print Message Using Template";
  }

  /**
   * @see net.ljcomputing.mail.rules.ProcessingRule#processMessageRule(javax.mail.Message)
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void processMessageRule(final Message message) throws EmailRuleProcessorException {
    try {
      Email email = new Email(message);
      LOGGER.debug("--==>> Email: {}", email);
      
      final Map root = new HashMap();
      root.put("email", email);
      
      Template template = FREEMARKER_CFG.getTemplate("sample_email.ftlh");
      final Writer writer = new BufferedWriter(new OutputStreamWriter(System.out));
      template.process(root, writer);
      
    } catch (MessagingException | IOException | TemplateException exception) {
      LOGGER.error("FATAL: ", exception);
      throw new EmailRuleProcessorException(exception);
    }
  }
}
