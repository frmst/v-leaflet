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
package pl.exsio.frameset.vaadin.ui.support.component.data.common;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.context.SecurityContext;
import pl.exsio.frameset.vaadin.component.InitializableVerticalLayout;
import pl.exsio.frameset.vaadin.ui.support.component.data.ex.DataManipulationException;
import pl.exsio.frameset.vaadin.ui.support.component.data.common.DataManipulation.DataAdditionListener;
import pl.exsio.frameset.vaadin.ui.support.component.data.common.DataManipulation.DataDeletionListener;
import pl.exsio.frameset.vaadin.ui.support.component.data.common.DataManipulation.DataUpdateListener;
import pl.exsio.frameset.vaadin.ui.support.component.data.ex.FormCommitException;
import pl.exsio.frameset.vaadin.ui.support.component.data.ex.LifecycleEventException;
import pl.exsio.frameset.vaadin.ui.support.dialog.ConfirmationDialog;
import pl.exsio.frameset.vaadin.ui.support.flexer.Flexer;
import pl.exsio.frameset.vaadin.ui.support.flexer.OrderedLayoutHeightFlexerImpl;

/**
 *
 * @author exsio
 */
public abstract class DataComponent<F extends Object, C extends Container, I extends Item, DC extends Component, CFG extends DataConfig> extends InitializableVerticalLayout implements ApplicationEventPublisherAware {

    public final static int MODE_ADDITION = 0;

    public final static int MODE_EDITION = 1;

    protected final Class<F> formClass;

    protected final Button addButton;

    protected final Button editButton;

    protected final Button deleteButton;

    protected final CFG config;

    protected transient final SecurityContext security;

    protected DC dataComponent;

    protected boolean openEditionAfterAddition = false;

    protected boolean flexibleControls = false;

    protected ApplicationEventPublisher aep;

    private Button.ClickListener currentEditListener;

    private Button.ClickListener currentDeleteListener;

    private final Set<DataAdditionListener> additionListeners;

    private final Set<DataUpdateListener> updateListeners;

    private final Set<DataDeletionListener> deletionListeners;

    private transient SpringContextEventHandler springContextEventHandler;

    public DataComponent(Class<F> formClass, CFG config, SecurityContext security) {
        config.build();
        this.formClass = formClass;
        this.config = config;
        this.security = security;
        this.addButton = new Button(t(config.getAddButtonLabel()), FontAwesome.PLUS);
        this.editButton = new Button(t(config.getEditButtonLabel()), FontAwesome.PENCIL);
        this.deleteButton = new Button(t(config.getDeleteButtonLabel()), FontAwesome.TRASH_O);
        this.addButton.setStyleName("frameset-dc-button-add");
        this.editButton.setStyleName("frameset-dc-button-edit");
        this.deleteButton.setStyleName("frameset-dc-button-delete");
        this.additionListeners = new LinkedHashSet<>();
        this.updateListeners = new LinkedHashSet<>();
        this.deletionListeners = new LinkedHashSet<>();
        this.setStyleName("frameset-dc-datatcomponent");
    }

    @Override
    protected void doInit() {

        this.setSizeFull();
        final C container = this.createContainer();
        this.dataComponent = this.createDataComponent(container);
        this.hookHandlersToDataComponent(dataComponent, container);
        HorizontalLayout controls = this.decorateControls(this.createControls());
        addComponent(controls);
        addComponent(dataComponent);
        this.setExpandRatio(controls, this.getControlsExpandRatio());
        this.setExpandRatio(dataComponent, 10);
        if (this.flexibleControls) {
            this.attachControlsFlexer(controls);
        }

    }

    protected abstract C createContainer();

    protected abstract DC createDataComponent(C container);

    protected void hookHandlersToDataComponent(DC dataComponent, C container) {
        this.hookAddHandler(container);
    }

    protected HorizontalLayout decorateControls(HorizontalLayout controls) {
        return controls;
    }

    protected float getControlsExpandRatio() {
        return 1;
    }

    protected Window createFormWindow(String title) {
        final Window formWindow = new Window(title);
        formWindow.center();
        formWindow.setSizeUndefined();
        formWindow.setModal(true);
        formWindow.setResizable(false);
        formWindow.setDraggable(false);
        formWindow.setResizeLazy(false);
        formWindow.setStyleName("frameset-dc-window");
        return formWindow;
    }

    protected F instantiateForm(final I item, final C container, final Window formWindow, final int mode) {
        F form = null;
        try {
            form = this.formClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new DataManipulationException("Couldn't instantiate form object", ex);
        }
        return form;
    }

    protected Layout decorateForm(F form, I item, int mode) {
        return new FormLayout();
    }

    protected HorizontalLayout decorateFormControls(HorizontalLayout formControls, final I item, final F form, final C container, final Window formWindow, final int mode) {
        return formControls;
    }

    protected void commitContainer(final C container) {
    }

    protected I createItem(C container) {
        return (I) container.addItem();
    }

    protected Object addItem(final I item, final C container, F form) {
        this.commitForm(form);
        Object itemid = container.addItem(item);
        this.commitContainer(container);
        return itemid;

    }

    protected void updateItem(final I item, final C container, F form) {
        this.commitForm(form);
        this.commitContainer(container);
    }

    protected void removeItem(final I item, final C container) {
        container.removeItem(this.getItemId(item, container));
    }

    protected Object getItemId(I item, C container) {
        return Long.valueOf(item.getItemProperty("id").toString());
    }

    protected final void commitForm(F form) {
        try {
            if (form instanceof FieldGroup) {
                ((FieldGroup) form).commit();
            } else if (form instanceof Form) {
                ((Form) form).commit();
            } else {
                throw new Exception("Form object of given class cannot be commited: " + form.getClass().getCanonicalName());
            }
        } catch (Validator.InvalidValueException ex) {
            Notification.show(t(ex.getMessage()), Notification.Type.ERROR_MESSAGE);
            throw new FormCommitException("There was an error during form commit", ex);
        } catch (Exception ex) {
            Notification.show(t("core.data_component.error.save"), Notification.Type.ERROR_MESSAGE);
            throw new FormCommitException("There was an error during form commit", ex);
        }
    }

    protected boolean canAddItem() {
        return security.canCreate();
    }

    protected boolean canOpenItem(I item) {
        return security.canWrite();
    }

    protected boolean canSaveItem(I item) {
        return security.canWrite();
    }

    protected boolean canDeleteItem(I item) {
        return security.canDelete();
    }

    protected final String getEditionWindowTitle(I item) {
        String itemDescription = getItemDescription(item);
        String windowTitle = t(config.getEditionWindowTitle());
        if (itemDescription != null) {
            windowTitle += ": " + itemDescription;
        }
        return windowTitle;
    }

    protected String getItemDescription(I item) {
        return null;
    }

    protected final void hookAddHandler(final C container) {
        if (this.canAddItem()) {
            this.registerNewAdditionListener(container);
            addButton.setEnabled(true);
        } else {
            addButton.setEnabled(false);
        }
    }

    protected final void hookEditHandler(I item, C container) {
        if (this.canOpenItem(item)) {
            this.registerNewEditionListener(item, container);
            editButton.setEnabled(true);
        } else {
            editButton.setEnabled(false);
        }
    }

    protected final void hookEditHandler(I item, C container, ItemClickEvent event) {
        if (this.canOpenItem(item)) {
            this.registerNewEditionListener(item, container);
            editButton.setEnabled(true);
            if (event.isDoubleClick()) {
                this.openForm(item, getEditionWindowTitle(item), container, MODE_EDITION);
            }
        } else {
            editButton.setEnabled(false);
        }
    }

    protected final void hookDeleteHandler(I item, C container) {
        if (this.canDeleteItem(item)) {
            this.registerNewDeletionListener(item, container);
            deleteButton.setEnabled(true);
        } else {
            deleteButton.setEnabled(false);
        }
    }

    protected void attachControlsFlexer(HorizontalLayout controls) {
        Flexer controlsFlexer = new OrderedLayoutHeightFlexerImpl(this, controls);
        controlsFlexer.addConstraint(900, 0.7)
                .addConstraint(800, 1)
                .addConstraint(700, 1.2)
                .addConstraint(500, 1.4)
                .addConstraint(300, 3.5)
                .addConstraint(200, 5)
                .addConstraint(100, 10)
                .attach();
    }

    private HorizontalLayout createControls() {
        HorizontalLayout controls = new HorizontalLayout() {
            {
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
                this.addComponent(addButton);
                this.addComponent(editButton);
                this.addComponent(deleteButton);
            }
        };
        controls.setStyleName("frameset-dc-controls");
        controls.setSpacing(true);
        controls.setMargin(new MarginInfo(true, false, false, false));
        return controls;
    }

    private void registerNewAdditionListener(final C container) {
        addButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                openForm(createItem(container), t(config.getAdditionWindowTitle()), container, MODE_ADDITION);
            }
        });
    }

    private void registerNewEditionListener(final I item, final C container) {
        if (this.currentEditListener instanceof Button.ClickListener) {
            editButton.removeClickListener(currentEditListener);
        }
        Button.ClickListener listener = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                openForm(item, getEditionWindowTitle(item), container, MODE_EDITION);
            }
        };
        editButton.addClickListener(listener);
        this.currentEditListener = listener;
    }

    private void registerNewDeletionListener(final I item, final C container) {
        if (this.currentDeleteListener instanceof Button.ClickListener) {
            deleteButton.removeClickListener(currentDeleteListener);
        }
        Button.ClickListener listener = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ConfirmationDialog.show(t(config.getDeletionWindowQuestion()), new ConfirmationDialog.Handler() {
                    @Override
                    public void handle(Button.ClickEvent event) {
                        handleItemDelete(item, container);
                    }
                }, null);
            }
        };
        deleteButton.addClickListener(listener);
        this.currentDeleteListener = listener;

    }

    private void openForm(final I item, String title, final C container, final int mode) {
        Window formWindow = this.createFormWindow(title);
        final Layout form = this.buildForm(item, container, formWindow, mode);
        VerticalLayout windowLayout = new VerticalLayout() {
            {
                addComponent(form);
                setMargin(true);
                setStyleName("frameset-dc-window-layout");
            }
        };
        formWindow.setContent(windowLayout);
        getUI().addWindow(formWindow);
        formWindow.focus();
    }

    private Layout buildForm(final I item, final C container, final Window formWindow, final int mode) {
        F form = this.instantiateForm(item, container, formWindow, mode);
        this.formatForm(form);
        VerticalLayout mainLayout = new VerticalLayout();
        Layout formLayout = this.decorateForm(form, item, mode);
        formLayout.setSizeUndefined();
        formLayout.setStyleName("frameset-dc-window-form-wrapper");
        if (formLayout instanceof MarginHandler) {
            ((MarginHandler) formLayout).setMargin(new MarginInfo(false, true, false, true));
        }
        Layout controls = this.decorateFormControls(this.createFormControls(item, form, container, formWindow, mode), item, form, container, formWindow, mode);
        mainLayout.addComponent(formLayout);
        mainLayout.addComponent(controls);
        mainLayout.setExpandRatio(formLayout, 10);
        mainLayout.setExpandRatio(controls, 1);
        mainLayout.setSizeUndefined();
        return mainLayout;

    }

    private void formatForm(F form) {
        if (form instanceof Form) {
            Form f = (Form) form;
            f.setSizeUndefined();
            f.setBuffered(true);
            f.getLayout().setSizeUndefined();
            f.setStyleName("frameset-dc-window-form");
        } else if (form instanceof FieldGroup) {
            FieldGroup f = (FieldGroup) form;
            f.setBuffered(true);
        }
    }

    private HorizontalLayout createFormControls(final I item, final F form, final C container, final Window formWindow, final int mode) {
        Button save = this.createFormSaveButton(item, form, container, formWindow, mode);
        Button cancel = this.createFormCancelButton(formWindow);
        HorizontalLayout controls = new HorizontalLayout();
        controls.setSpacing(true);
        controls.setStyleName("frameset-dc-form-controls");
        controls.setMargin(new MarginInfo(false, false, true, true));
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        cancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
        controls.addComponent(save);
        controls.addComponent(cancel);
        return controls;
    }

    private Button createFormCancelButton(final Window formWindow) {
        Button cancel = new Button(t(config.getFormCancelButtonLabel()), FontAwesome.TIMES);
        cancel.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                formWindow.close();
            }
        });
        cancel.setStyleName("frameset-dc-form-button-cancel");
        return cancel;
    }

    private Button createFormSaveButton(final I item, final F form, final C container, final Window formWindow, final int mode) {
        Button save = new Button(t(config.getFormSaveButtonLabel()), FontAwesome.FLOPPY_O);
        save.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                handleFormSave(item, form, container, formWindow, mode);
            }
        });
        if ((mode == MODE_EDITION && !this.canSaveItem(item)) || (mode == MODE_ADDITION && !this.canAddItem())) {
            save.setEnabled(false);
        }
        save.setStyleName("frameset-dc-form-button-save");
        return save;
    }

    private void handleFormSave(final I item, final F form, final C container, final Window formWindow, final int mode) {

        switch (mode) {
            case MODE_ADDITION:
                this.handleFormAddition(item, container, form, formWindow);
                break;
            case MODE_EDITION:
            default:
                this.handleFormUpdate(item, container, form, formWindow);
        }

    }

    private void handleFormUpdate(final I item, final C container, final F form, final Window formWindow) {
        try {
            this.publishBeforeUpdateEvent(form, item, container);
            this.updateItem(item, container, form);
            this.publishAfterUpdateEvent(form, item, container);
            Notification.show(t(config.getEditionSuccessMessage()), Notification.Type.HUMANIZED_MESSAGE);
            formWindow.close();
        } catch (DataManipulationException ex) {
            Notification.show(t(ex.getMessage()), Notification.Type.ERROR_MESSAGE);
        } catch (Exception ex) {
            if (!(ex instanceof FormCommitException) && !(ex instanceof LifecycleEventException)) {
                Notification.show(t("core.data_component.error.update"), Notification.Type.ERROR_MESSAGE);
            }
            throw new DataManipulationException("There was an error during data update", ex);
        }
    }

    private void handleFormAddition(final I item, final C container, final F form, final Window formWindow) {
        try {
            this.publishBeforeAdditionEvent(form, item, container);
            Object itemId = this.addItem(item, container, form);
            this.publishAfterAdditionEvent(form, item, container);
            Notification.show(t(config.getAdditionSuccessMessage()), Notification.Type.HUMANIZED_MESSAGE);
            formWindow.close();
            if (this.openEditionAfterAddition) {
                this.openForm((I) container.getItem(itemId), t(this.getEditionWindowTitle(item)), container, MODE_EDITION);
            }
        } catch (DataManipulationException ex) {
            Notification.show(t(ex.getMessage()), Notification.Type.ERROR_MESSAGE);
        } catch (Exception ex) {
            if (!(ex instanceof FormCommitException) && !(ex instanceof LifecycleEventException)) {
                Notification.show(t("core.data_component.error.add"), Notification.Type.ERROR_MESSAGE);
            }
            throw new DataManipulationException("There was an error during data creation", ex);
        }
    }

    private void handleItemDelete(final I item, final C container) {
        try {
            this.publishBeforeDeletionEvent(item, container);
            this.removeItem(item, container);
            this.commitContainer(container);
            deleteButton.setEnabled(false);
            editButton.setEnabled(false);
            Notification.show(t(config.getDeletionSuccessMessage()), Notification.Type.HUMANIZED_MESSAGE);
            this.publishAfterDeletionEvent(item, container);
        } catch (DataManipulationException ex) {
            Notification.show(t(ex.getMessage()), Notification.Type.ERROR_MESSAGE);
        } catch (Exception ex) {
            if (!(ex instanceof FormCommitException) && !(ex instanceof LifecycleEventException)) {
                Notification.show(t("core.data_component.error.delete"), Notification.Type.ERROR_MESSAGE);
            }
            throw new DataManipulationException("There was an error during data deletion", ex);
        }
    }

    private void publishAfterDeletionEvent(final I item, final C container) {
        for (DataDeletionListener listener : deletionListeners) {
            listener.dataDeleted(item, container);
        }
        this.springContextEventHandler.publishDeleteEvent(container, item);
    }

    private void publishBeforeDeletionEvent(final I item, final C container) {
        for (DataDeletionListener listener : deletionListeners) {
            listener.beforeDataDeletion(item, container);
        }
        this.springContextEventHandler.publishBeforeDeletionEvent(container, item);
    }

    private void publishAfterUpdateEvent(final F form, final I item, final C container) {
        for (DataUpdateListener listener : updateListeners) {
            listener.dataUpdated(form, item, container);
        }
        this.springContextEventHandler.publishUpdatedEvent(form, container, item);
    }

    private void publishBeforeUpdateEvent(final F form, final I item, final C container) {
        for (DataUpdateListener listener : updateListeners) {
            listener.beforeDataUpdate(form, item, container);
        }
        this.springContextEventHandler.publishBeforeUpdateEvent(form, container, item);
    }

    private void publishAfterAdditionEvent(final F form, final I item, final C container) {
        for (DataAdditionListener listener : additionListeners) {
            listener.dataAdded(form, item, container);
        }
        this.springContextEventHandler.publishAddedEvent(form, container, item);
    }

    private void publishBeforeAdditionEvent(final F form, final I item, final C container) {
        for (DataAdditionListener listener : additionListeners) {
            listener.beforeDataAddition(form, item, container);
        }
        this.springContextEventHandler.publishBeforeAdditionEvent(form, container, item);
    }

    public void addDataAddedListener(DataAdditionListener listener) {
        this.additionListeners.add(listener);
    }

    public void addDataUpdatedListener(DataUpdateListener listener) {
        this.updateListeners.add(listener);
    }

    public void addDataDeletedListener(DataDeletionListener listener) {
        this.deletionListeners.add(listener);
    }

    @Override
    public final void setApplicationEventPublisher(ApplicationEventPublisher aep) {
        this.aep = aep;
        this.springContextEventHandler = new SpringContextEventHandler(aep);
    }

}
