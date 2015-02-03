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
package pl.exsio.frameset.vaadin.ui.support.component;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author exsio
 */
public class ComponentFactory {

    public static ComboBox createIconComboBox(String caption) throws UnsupportedOperationException {
        ComboBox icon = new ComboBox();
        List iconsList = Arrays.asList(FontAwesome.class.getEnumConstants());
        Collections.sort(iconsList, new Comparator<FontAwesome>() {
            @Override
            public int compare(FontAwesome o1, FontAwesome o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        for (Object object : iconsList) {
            icon.addItem(object);
            icon.setItemIcon(object, (FontAwesome) object);
        }
        icon.setCaption(caption);
        return icon;
    }

    public static ComboBox createEnumComboBox(String caption, Class<? extends Enum> target) {
        ComboBox combo = new ComboBox();
        List list = Arrays.asList(target.getEnumConstants());
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        for (Object object : list) {
            combo.addItem(object);
        }
        combo.setCaption(caption);
        return combo;
    }
    
    public static ComboBox createEnumComboBox(Class<? extends Enum> target, boolean sort) {
        ComboBox combo = new ComboBox();
        List list = Arrays.asList(target.getEnumConstants());
        if(sort) {
            ComponentFactory.sortEnum(list);
        }
        for (Object object : list) {
            combo.addItem(object);
        }
        return combo;
    }
    
    public static OptionGroup createEnumOptionGroup(Class<? extends Enum> target, boolean sort) {
        OptionGroup options = new OptionGroup();
        List list = Arrays.asList(target.getEnumConstants());
        if(sort) {
            ComponentFactory.sortEnum(list);
        }
        for (Object object : list) {
            options.addItem(object);
        }
        return options;
    }
    
    public static List sortEnum(List list) {
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return list;
    }
}
