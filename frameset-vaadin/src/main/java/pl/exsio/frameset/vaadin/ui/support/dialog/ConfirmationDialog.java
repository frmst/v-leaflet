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
package pl.exsio.frameset.vaadin.ui.support.dialog;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import static pl.exsio.jin.translationcontext.TranslationContext.t;

/**
 *
 * @author exsio
 */
public class ConfirmationDialog {

    public interface Handler {

        void handle(Button.ClickEvent event);
    }

    public static void show(final String msg, final Handler positiveHandler, final Handler negativeHandler) {
        final Window window = new Window(t("confirmation.title"));
        window.center();
        window.setWidth("450px");
        window.setModal(true);
        window.setResizable(false);
        window.setDraggable(false);

        VerticalLayout vlayout = new VerticalLayout() {
            {
                addComponent(new Label(msg));
                addComponent(getControls(window, positiveHandler, negativeHandler));
            }
        };
        vlayout.setMargin(true);
        window.setContent(vlayout);
        UI.getCurrent().addWindow(window);
        window.focus();
    }

    private static HorizontalLayout getControls(final Window window, final Handler positiveHandler, final Handler negativeHandler) {
        final HorizontalLayout controls = new HorizontalLayout() {
            {
                Button positive = new Button(t("core.yes"), FontAwesome.CHECK);
                Button negative = new Button(t("core.no"), FontAwesome.TIMES);

                positive.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        window.close();
                    }
                });
                negative.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        window.close(); 
                    }
                });

                if (positiveHandler instanceof Handler) {
                    positive.addClickListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            positiveHandler.handle(event);
                        }
                    });
                }
                if (negativeHandler instanceof Handler) {
                    negative.addClickListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            negativeHandler.handle(event);
                        }
                    });
                }

                positive.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                negative.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
                addComponent(positive);
                addComponent(negative);
            }
        };
        controls.setMargin(new MarginInfo(true, true, false, true));
        controls.setSpacing(true);
        return controls;
    }
}
