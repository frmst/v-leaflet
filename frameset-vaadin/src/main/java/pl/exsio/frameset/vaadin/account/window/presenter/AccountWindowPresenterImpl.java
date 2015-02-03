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
package pl.exsio.frameset.vaadin.account.window.presenter;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Property;
import com.vaadin.ui.Form;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.model.User;
import pl.exsio.frameset.security.repository.provider.SecurityRepositoryProvider;
import pl.exsio.frameset.security.userdetails.UserDetailsProvider;
import pl.exsio.frameset.vaadin.account.window.view.AbstractAccountWindowView;
import pl.exsio.frameset.vaadin.ui.support.component.data.ex.DataManipulationException;

public class AccountWindowPresenterImpl implements AccountWindowPresenter {

    protected AbstractAccountWindowView view;

    protected transient PasswordEncoder passwordEncoder;

    protected transient SecurityRepositoryProvider securityRepositories;

    protected final transient Set<AccountEditionListener> listeners;

    public AccountWindowPresenterImpl() {
        this.listeners = new HashSet<>();
    }

    @Override
    public void show() {
        UI.getCurrent().addWindow(this.view.init());
    }

    @Override
    public void close() {
        this.view.close();
    }

    @Override
    public void save() {
        Form form = this.view.getForm();
        EntityItem<? extends User> item = this.view.getItem();
        if (form != null && item != null) {

            checkUsername(form);
            updateItemPasswordField(form, item);
            form.commit();
            Notification.show(t("core.account.update.success"));
            for (AccountEditionListener listener : listeners) {
                listener.accountEdited(item.getEntity());
            }
            close();
        }
    }

    private void checkUsername(Form form) {
        String formUsername = form.getField(AbstractAccountWindowView.FIELD_USERNAME).getValue().toString();
        String currentUsername = UserDetailsProvider.getUserDetails().getUsername();
        if (!currentUsername.equals(formUsername) && this.securityRepositories.getUserRepository().findOneByUsername(formUsername) != null) {
            Notification.show(t("core.management.users.msg.user_exists"), Notification.Type.ERROR_MESSAGE);
            throw new DataManipulationException("User already exists");
        }
    }

    private void updateItemPasswordField(Form form, EntityItem<? extends User> item) throws Property.ReadOnlyException, DataManipulationException {

        Object plainPassword = form.getField(AbstractAccountWindowView.FIELD_PASSWORD).getValue();
        Object plainPasswordRepeated = form.getField(AbstractAccountWindowView.FIELD_PASSWORD_REPEATED).getValue();
        if (plainPassword != null && plainPasswordRepeated != null) {
            if (!plainPassword.equals(plainPasswordRepeated)) {
                Notification.show(t("core.management.users.msg.pwd_mismatch"), Notification.Type.ERROR_MESSAGE);
                throw new DataManipulationException("Mismatched passwords");
            } else if (!plainPassword.equals("")) {
                item.getItemProperty(AbstractAccountWindowView.FIELD_PASSWORD).setValue(passwordEncoder.encode(plainPassword.toString()));
            }
        }
    }

    @Override
    public void setView(AbstractAccountWindowView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public AbstractAccountWindowView getView() {
        return this.view;
    }

    @Override
    public void addAccountEditionListener(AccountEditionListener listener) {
        this.listeners.add(listener);
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setSecurityRepositories(SecurityRepositoryProvider securityRepositories) {
        this.securityRepositories = securityRepositories;
    }

}
