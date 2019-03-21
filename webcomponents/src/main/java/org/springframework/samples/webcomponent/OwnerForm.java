/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.samples.webcomponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.StatusChangeEvent;
import com.vaadin.flow.function.SerializableConsumer;

public class OwnerForm extends Div {

    @Autowired
    private OwnerRepository repository;

    private Button saveButton;

    private BeanValidationBinder<Owner> binder = new BeanValidationBinder<>(
            Owner.class);

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (!attachEvent.isInitialAttach()) {
            return;
        }

        binder.setBean(new Owner());

        FormLayout layout = new FormLayout();

        TextField firstName = new TextField();
        layout.addFormItem(firstName, "First Name");
        binder.bind(firstName, "firstName");

        TextField lastName = new TextField();
        layout.addFormItem(lastName, "Last Name");
        binder.bind(lastName, "lastName");

        TextField address = new TextField();
        layout.addFormItem(address, "Address");
        binder.bind(address, "address");

        TextField city = new TextField();
        layout.addFormItem(city, "City");
        binder.bind(city, "city");

        TextField phone = new TextField();
        layout.addFormItem(phone, "Telephone");
        binder.bind(phone, "telephone");

        binder.addStatusChangeListener(this::onStatusUpdate);

        getSaveButton().addClickListener(event -> save(binder));
        add(layout, saveButton);
    }

    public void setActionName(String name) {
        getSaveButton().setText(name);
    }

    public void setOwner(Integer ownerId) {
        if (ownerId == -1) {
            return;
        }
        Owner ownerBean = repository.findById(ownerId);
        // Main Spring app doesn't allow to use incorrect owner id, so owner
        // bean can't be null here
        assert ownerBean != null;
        binder.setBean(ownerBean);
    }

    public void addSaveListener(SerializableConsumer<Integer> saveListener) {
        getSaveButton().addClickListener(
                event -> saveListener.accept(binder.getBean().getId()));
    }

    private void save(Binder<Owner> binder) {
        repository.save(binder.getBean());
    }

    private void onStatusUpdate(StatusChangeEvent event) {
        saveButton.setEnabled(!event.hasValidationErrors());
    }

    private Button getSaveButton() {
        if (saveButton == null) {
            saveButton = new Button();
        }
        return saveButton;
    }

}
