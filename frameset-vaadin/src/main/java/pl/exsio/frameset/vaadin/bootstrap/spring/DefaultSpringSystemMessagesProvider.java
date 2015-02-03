/* 
 * The MIT License
 *
 * Copyright 2014 exsio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.exsio.frameset.vaadin.bootstrap.spring;

import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import org.springframework.context.MessageSource;
import java.util.Locale;

/**
 *
 * @author exsio
 */
public class DefaultSpringSystemMessagesProvider implements SpringSystemMessagesProvider {

    private transient MessageSource messageSource;
    private static final String MESSAGE_NOT_FOUND = "MESSAGE_NOT_FOUND";

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public SystemMessages getSystemMessages(Locale locale) {
        CustomizedSystemMessages systemMessages = new CustomizedSystemMessages();
        String message;

        message = this.messageSource.getMessage("vaadin.sessionExpired.Caption", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setSessionExpiredCaption(message);
        }
        message = this.messageSource.getMessage("vaadin.sessionExpired.Message", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setSessionExpiredMessage(message);
        }
        message = this.messageSource.getMessage("vaadin.sessionExpired.URL", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setSessionExpiredURL(message);
        }
        message = this.messageSource.getMessage("vaadin.sessionExpired.NotificationEnabled", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            boolean notificationEnabled = (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("1"));
            systemMessages.setSessionExpiredNotificationEnabled(notificationEnabled);
        }

        message = this.messageSource.getMessage("vaadin.communicationError.Caption", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setCommunicationErrorCaption(message);
        }
        message = this.messageSource.getMessage("vaadin.communicationError.Message", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setCommunicationErrorMessage(message);
        }
        message = this.messageSource.getMessage("vaadin.communicationError.URL", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setCommunicationErrorURL(message);
        }
        message = this.messageSource.getMessage("vaadin.communicationError.NotificationEnabled", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            boolean notificationEnabled = (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("1"));
            systemMessages.setCommunicationErrorNotificationEnabled(notificationEnabled);
        }

        message = this.messageSource.getMessage("vaadin.authenticationError.Caption", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setAuthenticationErrorCaption(message);
        }
        message = this.messageSource.getMessage("vaadin.authenticationError.Message", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setAuthenticationErrorMessage(message);
        }
        message = this.messageSource.getMessage("vaadin.authenticationError.URL", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setAuthenticationErrorURL(message);
        }
        message = this.messageSource.getMessage("vaadin.authenticationError.NotificationEnabled", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            boolean notificationEnabled = (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("1"));
            systemMessages.setAuthenticationErrorNotificationEnabled(notificationEnabled);
        }

        message = this.messageSource.getMessage("vaadin.internalError.Caption", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setInternalErrorCaption(message);
        }
        message = this.messageSource.getMessage("vaadin.internalError.Message", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setInternalErrorMessage(message);
        }
        message = this.messageSource.getMessage("vaadin.internalError.URL", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setInternalErrorURL(message);
        }
        message = this.messageSource.getMessage("vaadin.internalError.NotificationEnabled", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            boolean notificationEnabled = (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("1"));
            systemMessages.setInternalErrorNotificationEnabled(notificationEnabled);
        }

        message = this.messageSource.getMessage("vaadin.outOfSync.Caption", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setOutOfSyncCaption(message);
        }
        message = this.messageSource.getMessage("vaadin.outOfSync.Message", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setOutOfSyncMessage(message);
        }
        message = this.messageSource.getMessage("vaadin.outOfSync.URL", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setOutOfSyncURL(message);
        }
        message = this.messageSource.getMessage("vaadin.outOfSync.NotificationEnabled", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            boolean notificationEnabled = (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("1"));
            systemMessages.setOutOfSyncNotificationEnabled(notificationEnabled);
        }

        message = this.messageSource.getMessage("vaadin.cookiesDisabled.Caption", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setCookiesDisabledCaption(message);
        }
        message = this.messageSource.getMessage("vaadin.cookiesDisabled.Message", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setCookiesDisabledMessage(message);
        }
        message = this.messageSource.getMessage("vaadin.cookiesDisabled.URL", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            systemMessages.setCookiesDisabledURL(message);
        }
        message = this.messageSource.getMessage("vaadin.cookiesDisabled.NotificationEnabled", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND)) {
            boolean notificationEnabled = (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("1"));
            systemMessages.setCookiesDisabledNotificationEnabled(notificationEnabled);
        }

        return systemMessages;
    }
}
