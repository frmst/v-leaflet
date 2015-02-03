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
package pl.exsio.frameset.vaadin.account.menu.presenter;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.entity.UserImpl;
import pl.exsio.frameset.security.model.User;
import pl.exsio.frameset.security.repository.provider.SecurityRepositoryProvider;
import pl.exsio.frameset.security.userdetails.UserDetailsProvider;
import pl.exsio.frameset.vaadin.account.menu.view.AccountMenuView;
import pl.exsio.frameset.vaadin.account.window.presenter.AccountWindowPresenter;
import pl.exsio.frameset.vaadin.ui.support.dialog.ConfirmationDialog;

public class AccountMenuPresenterImpl implements AccountMenuPresenter {

    protected AccountMenuView view;

    protected boolean initialized = false;

    protected transient SecurityRepositoryProvider securityRepositories;

    @Autowired
    protected transient ApplicationContext ctx;

    protected String logoutPath;

    protected Label userName;

    @Override
    public void setView(AccountMenuView view) {
        this.view = view;
    }

    @Override
    public AccountMenuView getView() {
        return this.view;
    }

    @Override
    public void initializeView() {
        if (this.view != null && !this.initialized) {
            this.view.setSizeUndefined();
            this.view.addStyleName("user");
            User user = (UserImpl) this.securityRepositories.getUserRepository().findOneByUsername(UserDetailsProvider.getUserDetails().getUsername());

            Label profilePic = this.getProfileIconLabel();
            userName = this.getUsernameLabel(user);
            Button accountSettings = this.getSettingsButton(user);
            Button logout = this.getLogoutButton();

            this.view.addComponent(profilePic);
            this.view.addComponent(userName);
            this.view.addComponent(accountSettings);
            this.view.addComponent(logout);
            this.initialized = true;
        }
    }

    protected Label getUsernameLabel(User user) {
        Label userLabel = new Label(user.toString());
        userLabel.setStyleName("user-name");
        userLabel.setSizeUndefined();
        return userLabel;
    }

    protected Label getProfileIconLabel() {
        Label profilePic = new Label(FontAwesome.USER.getHtml(), ContentMode.HTML);
        profilePic.setStyleName("user-icon");
        return profilePic;
    }

    protected Button getSettingsButton(final User user) {
        final AccountWindowPresenter.AccountEditionListener listener = this;
        Button accountSettings = new NativeButton(t("core.account.settings"));
        accountSettings.setIconAlternateText(t("core.account.settings"));
        accountSettings.setIcon(FontAwesome.COG);
        accountSettings.setStyleName("account-settings");
        accountSettings.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                AccountWindowPresenter window = ctx.getBean(AccountWindowPresenter.class);
                window.addAccountEditionListener(listener);
                window.show();
            }
        });

        return accountSettings;
    }

    protected Button getLogoutButton() {
        Button logout = new NativeButton(t("core.logout"));
        logout.setIconAlternateText(t("core.logout"));
        logout.setIcon(FontAwesome.TIMES);
        logout.setStyleName("logout");
        logout.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                ConfirmationDialog.show(t("core.logout.confirm"), new ConfirmationDialog.Handler() {

                    @Override
                    public void handle(Button.ClickEvent event) {
                        UI.getCurrent().getSession().close();
                        UI.getCurrent().getPage().setLocation(logoutPath);
                    }
                }, null);

            }
        });

        return logout;
    }

    @Override
    public void accountEdited(User user) {
        this.userName.setValue(user.getFullName());
    }

    public void setSecurityRepositories(SecurityRepositoryProvider securityRepositories) {
        this.securityRepositories = securityRepositories;
    }

    public void setLogoutPath(String logoutPath) {
        this.logoutPath = logoutPath;
    }

}
