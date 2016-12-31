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

package net.ljcomputing.mail.exception;

/**
 * Email rule processor exception.
 * 
 * @author James G. Willmore
 *
 */
public class EmailRuleProcessorException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4526014667277293315L;

  /**
   * Instantiates a new mail processor exception.
   */
  public EmailRuleProcessorException() {
    super();
  }

  /**
   * Instantiates a new mail processor exception.
   *
   * @param message the message
   */
  public EmailRuleProcessorException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new mail processor exception.
   *
   * @param cause the cause
   */
  public EmailRuleProcessorException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new mail processor exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public EmailRuleProcessorException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new mail processor exception.
   *
   * @param message the message
   * @param cause the cause
   * @param suppression the suppression
   * @param writable the writable
   */
  public EmailRuleProcessorException(final String message, final Throwable cause,
      final boolean suppression, final boolean writable) {
    super(message, cause, suppression, writable);
  }
}
