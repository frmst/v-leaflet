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
package pl.exsio.frameset.vaadin.module.management.frames;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import java.util.Arrays;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.vaadin.component.InitializableFormLayout;
import pl.exsio.frameset.vaadin.entity.VaadinFrameImpl;
import pl.exsio.frameset.vaadin.forms.fieldfactory.FramesetFieldFactory;
import pl.exsio.frameset.vaadin.model.VaadinFrame;
import pl.exsio.frameset.vaadin.ui.support.component.ComponentFactory;
import pl.exsio.jin.annotation.TranslationPrefix;

/**
 *
 * @author exsio
 */
@TranslationPrefix("core.management.frames")
public class BasicFrameDataForm extends InitializableFormLayout {

    private transient final EntityItem<VaadinFrame> item;

    public BasicFrameDataForm(EntityItem<VaadinFrame> item) {
        this.item = item;
    }

    @Override
    protected void doInit() {
        Form form = this.createForm();
        this.enlargeTextFields(form);
        Button saveButton = this.createSaveButton(form);
        this.addComponent(saveButton);
        this.setMargin(true);
    }

    private Form createForm() throws UnsupportedOperationException {
        final Form form = new Form();
        FramesetFieldFactory<VaadinFrameImpl> ff = new FramesetFieldFactory<>(VaadinFrameImpl.class, this.getClass());
        form.setFormFieldFactory(ff);
        form.setItemDataSource(item, Arrays.asList("title", "slug", "moduleId", "menuLabel", "isDefault"));
        form.addField("icon", this.createIconComboBox());
        this.addComponent(form);
        form.setBuffered(true);
        return form;
    }

    private Button createSaveButton(final Form form) {
        Button saveButton = new Button(t("save"), FontAwesome.FLOPPY_O);
        saveButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                form.commit();
                Notification.show(t("edition.success"));
            }
        });
        return saveButton;
    }

    private void enlargeTextFields(final Form form) {
        for (Object propertyId : item.getItemPropertyIds()) {
            Field field = form.getField(propertyId);
            if (field instanceof TextField) {
                field.setWidth("300px");
            }
        }
    }

    private ComboBox createIconComboBox() throws UnsupportedOperationException {
        ComboBox icon = ComponentFactory.createIconComboBox(t("icon"));
        icon.setPropertyDataSource(item.getItemProperty("icon"));
        return icon;
    }

}
