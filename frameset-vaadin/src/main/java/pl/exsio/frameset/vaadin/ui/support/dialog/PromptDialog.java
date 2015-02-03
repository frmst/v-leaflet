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

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import static pl.exsio.jin.translationcontext.TranslationContext.t;

/**
 *
 * @author exsio
 */
public class PromptDialog {

    public interface Handler {

        void handle(Button.ClickEvent event, String value);
    }

    public static void show(final String msg, final Handler handler) {
        final Window window = new Window(t("prompt.title"));
        window.center();
        window.setWidth("450px");
        window.setModal(true);
        window.setResizable(false);
        window.setDraggable(false);

        VerticalLayout vlayout = new VerticalLayout() {
            {
                HorizontalLayout hlayout = new HorizontalLayout();
                hlayout.setSpacing(true);
                hlayout.setMargin(true);
                
                hlayout.addComponent(new Label(t(msg)+": "));
                final TextField value = new TextField();
                hlayout.addComponent(value);
                addComponent(hlayout);
                addComponent(getControls(window, handler, value));
            }
        };
        vlayout.setMargin(true);
        vlayout.setSpacing(true);
        window.setContent(vlayout);
        UI.getCurrent().addWindow(window);
        window.focus();
    }

    private static HorizontalLayout getControls(final Window window, final Handler handler, final TextField value) {
        final HorizontalLayout controls = new HorizontalLayout() {
            {
                Button confirm = new Button(t("core.ok"), FontAwesome.CHECK);
                Button cancel = new Button(t("core.cancel"), FontAwesome.TIMES);

                confirm.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        window.close();
                    }
                });
                cancel.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        window.close(); 
                    }
                });

                if (handler instanceof Handler) {
                    confirm.addClickListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            handler.handle(event, value.getValue());
                        }
                    });
                }
                
                confirm.setClickShortcut(KeyCode.ENTER);
                cancel.setClickShortcut(KeyCode.ESCAPE);
                addComponent(confirm);
                addComponent(cancel);
            }
        };
        controls.setMargin(new MarginInfo(true, true, false, true));
        controls.setSpacing(true);
        return controls;
    }
}
