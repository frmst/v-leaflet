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
package pl.exsio.frameset.vaadin.module.util.usergen;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.StringToLongConverter;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import java.util.HashSet;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.haijian.CSVExporter;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.entity.factory.SecurityEntityFactory;
import pl.exsio.frameset.security.model.Group;
import pl.exsio.frameset.security.model.Role;
import pl.exsio.frameset.security.model.User;
import pl.exsio.frameset.security.repository.provider.SecurityRepositoryProvider;
import pl.exsio.frameset.vaadin.module.VerticalModule;
import pl.exsio.jin.annotation.TranslationPrefix;

/**
 *
 * @author exsio
 */
@TranslationPrefix("frameset.module.usergen")
public class UserGenModule extends VerticalModule {

    private EntityProvider groupEntityProvider;

    private EntityProvider roleEntityProvider;

    private SecurityEntityFactory securityEntities;

    private SecurityRepositoryProvider securityRepositories;

    private PasswordEncoder encoder;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.removeAllComponents();
        final Form form = this.getGeneratorForm();
        Button generate = new Button(t("generate"), FontAwesome.CHECK);
        generate.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                form.commit();
                generateUsers(form);
            }
        });
        this.addComponent(form);
        this.addComponent(generate);
        this.setMargin(true);
        this.setSpacing(true);
    }

    private void generateUsers(Form form) {

        OptionGroup groups = (OptionGroup) form.getField("groups");
        Set<Group> groupsSet = getGroupsSet(groups);
        Set<Role> rolesSet = getRolesSet(form);
        String usernamePrefix = (String) form.getField("usernamePrefix").getValue();
        String usernameSuffix = (String) form.getField("usernameSuffix").getValue();
        Long from = Long.parseLong((String) form.getField("from").getValue());
        Long to = Long.parseLong((String) form.getField("to").getValue());

        Table csvTable = createCsvTable();

        for (int i = from.intValue(); i < to.intValue(); i++) {
            String username = usernamePrefix + i + "@" + usernameSuffix;
            byte[] byteArray = KeyGenerators.secureRandom(2).generateKey();
            String passwordSuffix = "";
            for(byte b: byteArray) {
                passwordSuffix += new Byte(b).toString();
            }
            String password = usernamePrefix + passwordSuffix;
            User user = this.securityEntities.newUser();
            user.setUsername(username);
            user.setPassword(this.encoder.encode(password));
            user.setEmail(username);
            user.setEnabled(true);
            user.setGroups(groupsSet);
            user.setRoles(rolesSet);
                
            Item item = csvTable.addItem(username);
            item.getItemProperty("username").setValue(username);
            try {
                this.securityRepositories.getUserRepository().save(user);
                item.getItemProperty("password").setValue(password);
            } catch (DataIntegrityViolationException ex) {
                item.getItemProperty("password").setValue(t("username_exists"));
            }
        }

        CSVExporter exporter = createCsvExporter(csvTable);
        this.addComponent(exporter);
        Notification.show(t("generation_completed"));
    }

    private CSVExporter createCsvExporter(Table csvTable) {
        CSVExporter exporter = new CSVExporter(csvTable);
        exporter.setCaption(t("export"));
        exporter.setIcon(FontAwesome.TABLE);
        return exporter;
    }

    private Table createCsvTable() throws UnsupportedOperationException {
        Container csvContainer = new IndexedContainer();
        csvContainer.addContainerProperty("username", String.class, "");
        csvContainer.addContainerProperty("password", String.class, "");
        Table csvTable = new Table("", csvContainer);
        return csvTable;
    }

    private Set<Role> getRolesSet(Form form) {
        OptionGroup roles = (OptionGroup) form.getField("roles");
        Set<Role> rolesSet = new HashSet<>();
        for (Long roleId : (Set<Long>) roles.getValue()) {
            rolesSet.add((Role) this.securityRepositories.getRoleRepository().findOne(roleId));
        }
        return rolesSet;
    }

    private Set<Group> getGroupsSet(OptionGroup groups) {
        Set<Group> groupsSet = new HashSet<>();
        for (Long groupId : (Set<Long>) groups.getValue()) {
            groupsSet.add((Group) this.securityRepositories.getGroupRepository().findOne(groupId));
        }
        return groupsSet;
    }

    private Form getGeneratorForm() {

        Form form = new Form();

        form.addField("usernamePrefix", createUsernamePrefixField());
        form.addField("usernameSuffix", createUsernameSuffixField());
        form.addField("from", createFromField());
        form.addField("to", createToField());
        form.addField("groups", createGroupsField());
        form.addField("roles", createRolesField());

        form.setBuffered(true);
        FormLayout layout = new FormLayout();
        layout.setMargin(true);
        form.setLayout(layout);
        return form;
    }

    private OptionGroup createRolesField() {
        JPAContainer<? extends Role> rolesContainer = JPAContainerFactory.make(this.securityEntities.getRoleClass(), this.roleEntityProvider.getEntityManager());
        rolesContainer.setEntityProvider(roleEntityProvider);
        OptionGroup roles = new OptionGroup(t("roles"), rolesContainer);
        roles.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        roles.setItemCaptionPropertyId("label");
        roles.setMultiSelect(true);
        return roles;
    }

    private OptionGroup createGroupsField() {
        JPAContainer<? extends Group> groupsContainer = JPAContainerFactory.make(this.securityEntities.getGroupClass(), this.groupEntityProvider.getEntityManager());
        groupsContainer.setEntityProvider(groupEntityProvider);
        OptionGroup groups = new OptionGroup(t("groups"), groupsContainer);
        groups.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        groups.setItemCaptionPropertyId("name");
        groups.setMultiSelect(true);
        return groups;
    }

    private TextField createToField() {
        TextField to = new TextField(t("to"));
        to.addValidator(new NullValidator("", false));
        to.setNullRepresentation("");
        to.setConverter(new StringToLongConverter());
        return to;
    }

    private TextField createFromField() {
        TextField from = new TextField(t("from"));
        from.addValidator(new NullValidator("", false));
        from.setNullRepresentation("");
        from.setConverter(new StringToLongConverter());
        return from;
    }

    private TextField createUsernameSuffixField() {
        TextField usernameSuffix = new TextField(t("username_suffix"));
        usernameSuffix.setNullRepresentation("");
        usernameSuffix.addValidator(new NullValidator("", false));
        return usernameSuffix;
    }

    private TextField createUsernamePrefixField() {
        TextField usernamePrefix = new TextField(t("username_prefix"));
        usernamePrefix.setNullRepresentation("");
        usernamePrefix.addValidator(new NullValidator("", false));
        return usernamePrefix;
    }

    public void setGroupEntityProvider(EntityProvider groupEntityProvider) {
        this.groupEntityProvider = groupEntityProvider;
    }

    public void setRoleEntityProvider(EntityProvider roleEntityProvider) {
        this.roleEntityProvider = roleEntityProvider;
    }

    public void setSecurityEntities(SecurityEntityFactory securityEntities) {
        this.securityEntities = securityEntities;
    }

    public void setSecurityRepositories(SecurityRepositoryProvider securityRepositories) {
        this.securityRepositories = securityRepositories;
    }

    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

}
