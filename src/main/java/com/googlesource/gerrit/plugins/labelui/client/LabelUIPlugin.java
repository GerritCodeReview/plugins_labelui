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

import com.google.gerrit.client.GerritUiExtensionPoint;
import com.google.gerrit.client.Resources;
import com.google.gerrit.plugin.client.Plugin;
import com.google.gerrit.plugin.client.PluginEntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

import com.googlesource.gerrit.plugins.labelui.client.PreferencesInfo.LabelUi;

public class LabelUIPlugin extends PluginEntryPoint {
  public static final Resources RESOURCES = GWT.create(Resources.class);

  private final static String DEFAULT_LABELS_CLASS =
      "com-google-gerrit-client-change-ChangeScreen_BinderImpl_GenCss_style-labels";
  private final static String HIDE_DEFAULT_LABELS_CLASS = Plugin.get()
      .getName() + "-hideDefaultLabels";

  @Override
  public void onPluginLoad() {
    injectCssToHideDefaultLabels();

    Plugin.get().panel(
        GerritUiExtensionPoint.CHANGE_SCREEN_BELOW_CHANGE_INFO_BLOCK,
        new LabelPanel.Factory());
    Plugin.get().panel(
        GerritUiExtensionPoint.PREFERENCES_SCREEN_BOTTOM,
        new LabelUiPreferencesPanel.Factory());
  }

  public static void refreshDefaultLabelUi(LabelUi ui) {
    if (ui == LabelUi.DEFAULT) {
      showDefaultLabelUi();
    } else {
      hideDefaultLabelUi();
    }
  }

  private static void hideDefaultLabelUi() {
    Element body = RootPanel.getBodyElement();
    if (!body.hasClassName(HIDE_DEFAULT_LABELS_CLASS)) {
      body.addClassName(HIDE_DEFAULT_LABELS_CLASS);
    }
  }

  private static void showDefaultLabelUi() {
    RootPanel.getBodyElement().removeClassName(HIDE_DEFAULT_LABELS_CLASS);
  }

  private static void injectCssToHideDefaultLabels() {
    StringBuilder css = new StringBuilder();
    css.append(".");
    css.append(HIDE_DEFAULT_LABELS_CLASS);
    css.append(" .");
    css.append(DEFAULT_LABELS_CLASS);
    css.append(" {display: none;}");
    injectCss(css.toString());
  }

  private final static native void injectCss(String css)
  /*-{ return $wnd.Gerrit.injectCss(css) }-*/;
}
