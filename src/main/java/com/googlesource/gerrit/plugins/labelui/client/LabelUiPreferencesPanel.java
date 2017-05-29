// Copyright (C) 2015 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.labelui.client;

import com.google.gerrit.plugin.client.Plugin;
import com.google.gerrit.plugin.client.extension.Panel;
import com.google.gerrit.plugin.client.rpc.RestApi;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LabelUiPreferencesPanel extends VerticalPanel {
  static class Factory implements Panel.EntryPoint {
    @Override
    public void onLoad(Panel panel) {
      panel.setWidget(new LabelUiPreferencesPanel());
    }
  }

  private Label savedLabel;
  private Timer hideTimer;

  LabelUiPreferencesPanel() {
    new RestApi("accounts")
        .id("self")
        .view(Plugin.get().getPluginName(), "preferences")
        .get(
            new AsyncCallback<PreferencesInfo>() {
              @Override
              public void onSuccess(PreferencesInfo result) {
                display(result);
              }

              @Override
              public void onFailure(Throwable caught) {
                // never invoked
              }
            });
  }

  private void display(PreferencesInfo info) {
    Label heading = new Label(Plugin.get().getName() + " plugin");
    heading.setStyleName("smallHeading");
    add(heading);
    HorizontalPanel p = new HorizontalPanel();
    add(p);

    Label label = new Label("Label UI:");
    p.add(label);
    label.getElement().getStyle().setMarginRight(5, Unit.PX);
    label.getElement().getStyle().setMarginTop(2, Unit.PX);
    final ListBox ui = new ListBox();
    p.add(ui);
    savedLabel = new Label("Saved");
    savedLabel.getElement().getStyle().setMarginLeft(5, Unit.PX);
    savedLabel.getElement().getStyle().setMarginTop(2, Unit.PX);
    savedLabel.setVisible(false);
    p.add(savedLabel);

    for (PreferencesInfo.LabelUi v : PreferencesInfo.LabelUi.values()) {
      ui.addItem(v.name(), v.name());
    }

    for (int i = 0; i < ui.getItemCount(); i++) {
      if (info.ui().name().equals(ui.getValue(i))) {
        ui.setSelectedIndex(i);
        break;
      }
    }

    ui.addChangeHandler(
        new ChangeHandler() {
          @Override
          public void onChange(ChangeEvent event) {
            savedLabel.setVisible(false);
            PreferencesInfo info = PreferencesInfo.create();
            info.ui(ui.getSelectedValue());
            new RestApi("accounts")
                .id("self")
                .view(Plugin.get().getPluginName(), "preferences")
                .put(
                    info,
                    new AsyncCallback<PreferencesInfo>() {
                      @Override
                      public void onSuccess(PreferencesInfo result) {
                        showSavedStatus();
                      }

                      @Override
                      public void onFailure(Throwable caught) {
                        // never invoked
                      }
                    });
          }
        });
  }

  private void showSavedStatus() {
    if (hideTimer != null) {
      hideTimer.cancel();
      hideTimer = null;
    }
    savedLabel.setVisible(true);
    hideTimer =
        new Timer() {
          @Override
          public void run() {
            savedLabel.setVisible(false);
            hideTimer = null;
          }
        };
    hideTimer.schedule(1000);
  }
}
