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
package pl.exsio.frameset.vaadin.account.window.view;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import java.util.Arrays;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.entity.UserImpl;
import pl.exsio.frameset.security.entity.factory.SecurityEntityFactory;
import pl.exsio.frameset.security.model.User;
import pl.exsio.frameset.security.repository.provider.SecurityRepositoryProvider;
import pl.exsio.frameset.security.userdetails.UserDetailsProvider;
import pl.exsio.frameset.vaadin.account.window.presenter.AccountWindowPresenter;
import pl.exsio.frameset.vaadin.forms.fieldfactory.FramesetFieldFactory;

public class AccountWindowView extends AbstractAccountWindowView {

    protected AccountWindowPresenter presenter;

    protected Form form;

    private transient EntityProvider entityProvider;

    private transient SecurityEntityFactory securityEntities;

    private transient SecurityRepositoryProvider securityRepositories;

    private EntityItem<? extends User> item;

    public AccountWindowView() {
        super(t("core.account.settings"));
    }

    @Override
    public Form getForm() {
        return this.form;
    }

    @Override
    public EntityItem<? extends User> getItem() {
        return this.item;
    }

    @Override
    protected void doInit() {
        this.center();
        this.initLooks();
        this.createItem();
        FormLayout formLayout = this.createForm();
        this.setContent(formLayout);
    }

    private void initLooks() {
        this.setWidth("450px");
        this.setModal(true);
        this.setResizable(false);
        this.setDraggable(false);
    }

    private FormLayout createForm() {
        this.form = new Form();
        this.form.setFormFieldFactory(new FramesetFieldFactory(UserImpl.class));
        this.form.setItemDataSource(this.item, Arrays.asList(FIELD_USERNAME, FIELD_FIRST_NAME, FIELD_lAST_NAME, FIELD_PHONE_NO));
        this.form.setBuffered(true);
        this.form.getField(FIELD_USERNAME).addValidator(new EmailValidator(t("core.management.users.invalid_username")));
        Field passwordField = this.createPasswordField();
        this.form.addField(FIELD_PASSWORD, passwordField);
        Field passwordFieldRepeated = this.createPasswordRepeatedField();
        this.form.addField(FIELD_PASSWORD_REPEATED, passwordFieldRepeated);
        FormLayout formLayout = new FormLayout();
        formLayout.addComponent(this.form);
        formLayout.setMargin(true);
        HorizontalLayout controls = this.createFormControls();
        formLayout.addComponent(controls);
        return formLayout;
    }

    public HorizontalLayout createFormControls() {
        HorizontalLayout controls = new HorizontalLayout();
        Button saveButton = this.createSaveButton();
        Button cancelButton = this.createCancelButton();
        controls.addComponent(saveButton);
        controls.addComponent(cancelButton);
        controls.setMargin(true);
        controls.setSpacing(true);
        return controls;
    }

    private Field createPasswordRepeatedField() {
        final Field passwordFieldRepeated = new PasswordField(t("pl.exsio.frameset.security.entity.UserImpl.plainPasswordRepeated"), "");
        passwordFieldRepeated.setPropertyDataSource(this.item.getItemProperty("plainPasswordRepeated"));
        return passwordFieldRepeated;
    }

    private Field createPasswordField() {
        final Field passwordField = new PasswordField(t("pl.exsio.frameset.security.entity.UserImpl.plainPassword"), "");
        passwordField.setPropertyDataSource(this.item.getItemProperty("plainPassword"));
        return passwordField;
    }

    private void createItem() {
        User user = this.securityRepositories.getUserRepository().findOneByUsername(UserDetailsProvider.getUserDetails().getUsername());
        JPAContainer<User> container = JPAContainerFactory.make(this.securityEntities.getUserClass(), this.entityProvider.getEntityManager());
        container.setEntityProvider(this.entityProvider);
        this.item = container.getItem(user.getId());
    }

    private Button createCancelButton() {
        Button cancelButton = new Button(t("core.cancel"), FontAwesome.TIMES);
        cancelButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.close();
            }
        });
        return cancelButton;
    }

    private Button createSaveButton() {
        Button saveButton = new Button(t("core.save"), FontAwesome.FLOPPY_O);
        saveButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.save();
            }

        });
        return saveButton;
    }

    @Override
    public void setPresenter(AccountWindowPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public AccountWindowPresenter getPresenter() {
        return this.presenter;
    }

    public void setEntityProvider(EntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    public void setSecurityEntities(SecurityEntityFactory securityEntities) {
        this.securityEntities = securityEntities;
    }

    public void setSecurityRepositories(SecurityRepositoryProvider securityRepositories) {
        this.securityRepositories = securityRepositories;
    }
}
