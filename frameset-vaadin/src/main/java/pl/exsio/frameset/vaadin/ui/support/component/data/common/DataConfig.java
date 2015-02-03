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

import pl.exsio.jin.translationprefix.retriever.TranslationPrefixRetriever;

/**
 *
 * @author exsio
 */
public class DataConfig {

    protected String translationPrefix = "core.data_component.";

    private boolean translated = true;

    private String caption = "";

    private String addButtonLabel = "";

    private String editButtonLabel = "";

    private String deleteButtonLabel = "";

    private String formSaveButtonLabel = "core.save";

    private String formCancelButtonLabel = "core.cancel";

    private String additionWindowTitle = "window.create";

    private String editionWindowTitle = "window.edit";

    private String deletionWindowQuestion = "confirmation.delete";

    private String additionSuccessMessage = "msg.created";

    private String editionSuccessMessage = "msg.edited";

    private String deletionSuccessMessage = "msg.deleted";

    public DataConfig() {
    }

    public DataConfig(boolean translated) {
        this.translated = translated;
    }

    public DataConfig(String translationPrefix) {
        this.translationPrefix = translationPrefix;
    }

    public DataConfig(Class translatedClass) {
        this.translationPrefix = TranslationPrefixRetriever.getTranslationPrefix(translatedClass);
    }

    public void build() {
        caption = this.buildItem(caption);
        addButtonLabel = this.buildItem(addButtonLabel);
        editButtonLabel = this.buildItem(editButtonLabel);
        deleteButtonLabel = this.buildItem(deleteButtonLabel);
        additionWindowTitle = this.buildItem(additionWindowTitle);
        editionWindowTitle = this.buildItem(editionWindowTitle);
        deletionWindowQuestion = this.buildItem(deletionWindowQuestion);
        additionSuccessMessage = this.buildItem(additionSuccessMessage);
        editionSuccessMessage = this.buildItem(editionSuccessMessage);
        deletionSuccessMessage = this.buildItem(deletionSuccessMessage);
    }

    protected boolean translationPrefixIsEmpty() {
        return this.translationPrefix.equals(TranslationPrefixRetriever.EMPTY_PREFIX);
    }

    private String buildItem(String item) {
        if (this.translated) {
            if (item != null && !item.trim().equals("")) {
                return translationPrefix + item;
            } else {
                return "";
            }
        } else {
            return item;
        }
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getAddButtonLabel() {
        return addButtonLabel;
    }

    public void setAddButtonLabel(String addButtonLabel) {
        this.addButtonLabel = addButtonLabel;
    }

    public String getEditButtonLabel() {
        return editButtonLabel;
    }

    public void setEditButtonLabel(String editButtonLabel) {
        this.editButtonLabel = editButtonLabel;
    }

    public String getDeleteButtonLabel() {
        return deleteButtonLabel;
    }

    public void setDeleteButtonLabel(String deleteButtonLabel) {
        this.deleteButtonLabel = deleteButtonLabel;
    }

    public String getAdditionWindowTitle() {
        return additionWindowTitle;
    }

    public void setAdditionWindowTitle(String additionWindowTitle) {
        this.additionWindowTitle = additionWindowTitle;
    }

    public String getEditionWindowTitle() {
        return editionWindowTitle;
    }

    public void setEditionWindowTitle(String editionWindowTitle) {
        this.editionWindowTitle = editionWindowTitle;
    }

    public String getDeletionWindowQuestion() {
        return deletionWindowQuestion;
    }

    public void setDeletionWindowQuestion(String deletionWindowQuestion) {
        this.deletionWindowQuestion = deletionWindowQuestion;
    }

    public String getDeletionSuccessMessage() {
        return deletionSuccessMessage;
    }

    public void setDeletionSuccessMessage(String deletionSuccessMessage) {
        this.deletionSuccessMessage = deletionSuccessMessage;
    }

    public String getAdditionSuccessMessage() {
        return additionSuccessMessage;
    }

    public void setAdditionSuccessMessage(String additionSuccessMessage) {
        this.additionSuccessMessage = additionSuccessMessage;
    }

    public String getEditionSuccessMessage() {
        return editionSuccessMessage;
    }

    public void setEditionSuccessMessage(String editionSuccessMessage) {
        this.editionSuccessMessage = editionSuccessMessage;
    }

    public String getFormSaveButtonLabel() {
        return formSaveButtonLabel;
    }

    public void setFormSaveButtonLabel(String formSaveButtonLabel) {
        this.formSaveButtonLabel = formSaveButtonLabel;
    }

    public String getFormCancelButtonLabel() {
        return formCancelButtonLabel;
    }

    public void setFormCancelButtonLabel(String formCancelButtonLabel) {
        this.formCancelButtonLabel = formCancelButtonLabel;
    }
}
