/**
           Copyright 2017, James G. Willmore

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

package net.ljcomputing.mail.domain;

import java.util.LinkedList;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Email domain.
 * 
 * @author James G. Willmore
 *
 */
public class Email {
  
  /** The FROM addresses. */
  private final List<InternetAddress> from = new LinkedList<InternetAddress>();
  
  /** The TO addresses. */
  private final List<InternetAddress> to = new LinkedList<InternetAddress>();
  
  /** The CC addresses. */
  private final List<InternetAddress> cc = new LinkedList<InternetAddress>();
  
  /** The BCC addresses. */
  private final List<InternetAddress> bcc = new LinkedList<InternetAddress>();
  
  /** The subject line of the email. */
  private String subject;
  
  /**
   * Instantiates a new email.
   *
   * @param message the message
   * @throws MessagingException the messaging exception
   */
  public Email(final Message message) throws MessagingException {
    addFrom(message.getFrom());
    addTo(message.getRecipients(RecipientType.TO));
    addCc(message.getRecipients(RecipientType.CC));
    addBcc(message.getRecipients(RecipientType.BCC));
    this.subject = message.getSubject();
  }
  
  /**
   * Adds the address to list.
   *
   * @param list the list
   * @param addresses the addresses
   * @throws AddressException the address exception
   */
  private void addAddressToList(final List<InternetAddress> list, final Address ... addresses) throws AddressException {
    if (addresses != null) {
      for (int a = 0; a < addresses.length; a++) {
        list.add((InternetAddress) addresses[a]);
      }
    }
  }
  
  /**
   * Adds the FROM addresses.
   *
   * @param addresses the addresses
   * @throws AddressException the address exception
   */
  public void addFrom(final Address ... addresses) throws AddressException {
    addAddressToList(from, addresses);
  }
  
  /**
   * Gets the FROM addresses.
   *
   * @return the from
   */
  public List<InternetAddress> getFrom() {
    return from;
  }
  
  /**
   * Adds the TO addresses.
   *
   * @param addresses the addresses
   * @throws AddressException the address exception
   */
  public void addTo(final Address ... addresses) throws AddressException {
    addAddressToList(to, addresses);
  }
  
  /**
   * Gets the TO addresses.
   *
   * @return the to
   */
  public List<InternetAddress> getTo() {
    return to;
  }

  /**
   * Adds the CC addresses.
   *
   * @param addresses the addresses
   * @throws AddressException the address exception
   */
  public void addCc(final Address ... addresses) throws AddressException {
    addAddressToList(cc, addresses);
  }
  
  /**
   * Gets the CC addresses.
   *
   * @return the cc
   */
  public List<InternetAddress> getCc() {
    return cc;
  }
  
  /**
   * Adds the BCC addresses.
   *
   * @param addresses the addresses
   * @throws AddressException the address exception
   */
  public void addBcc(final Address ... addresses) throws AddressException {
    addAddressToList(bcc, addresses);
  }
  
  /**
   * Gets the BCC addresses.
   *
   * @return the bcc
   */
  public List<InternetAddress> getBcc() {
    return bcc;
  }
  
  /**
   * Gets subject line of the email.
   *
   * @return the subject
   */
  public String getSubject() {
    return subject;
  }

  @Override
  public String toString() {
    return "Email [from=" + from + ", to=" + to + ", cc=" + cc + ", bcc=" + bcc + ", subject="
        + subject + "]";
  }
}
