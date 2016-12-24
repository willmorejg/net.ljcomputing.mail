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

package net.ljcomputing.mail;

import java.util.Properties;

import net.ljcomputing.mail.service.MailProcessor;

/**
 * Main class that will process emails.
 * 
 * @author James G. Willmore
 *
 */
public class Main {
  
  /**
   * The main method.
   *
   * @param args the arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {

    final Properties props = new Properties();
    props.setProperty(MailProcessor.Props.PROVIDER.toString(), "imap");
    props.setProperty(MailProcessor.Props.HOST.toString(), "localhost");
    props.setProperty(MailProcessor.Props.USERNAME.toString(), "jim");
    props.setProperty(MailProcessor.Props.PASSWORD.toString(), "Wiomm$001");
    
    final MailProcessor processor = new MailProcessor(props);
    processor.processInbox();
  }

}