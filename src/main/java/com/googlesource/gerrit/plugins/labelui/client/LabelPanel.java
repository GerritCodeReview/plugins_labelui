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
import com.google.gerrit.client.info.AccountInfo;
import com.google.gerrit.client.info.AccountInfo.AvatarInfo;
import com.google.gerrit.client.info.ChangeInfo;
import com.google.gerrit.client.info.ChangeInfo.ApprovalInfo;
import com.google.gerrit.client.info.ChangeInfo.LabelInfo;
import com.google.gerrit.client.rpc.Natives;
import com.google.gerrit.plugin.client.Plugin;
import com.google.gerrit.plugin.client.extension.Panel;
import com.google.gerrit.plugin.client.rpc.RestApi;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class LabelPanel extends VerticalPanel {
  static class Factory implements Panel.EntryPoint {
    @Override
    public void onLoad(Panel panel) {
      panel.setWidget(new LabelPanel(panel));
    }
  }

  private final static String COLOR_GREEN = "#060";
  private final static String COLOR_RED = "#F00";

  LabelPanel(final Panel panel) {
    new RestApi("accounts").id("self").view(Plugin.get().getPluginName(), "preferences")
        .get(new AsyncCallback<PreferencesInfo>() {
          @Override
          public void onSuccess(PreferencesInfo result) {
            LabelUIPlugin.refreshDefaultLabelUi(result.ui());
            ChangeInfo change =
                panel.getObject(GerritUiExtensionPoint.Key.CHANGE_INFO).cast();
            switch (result.ui()) {
              case LABEL_USER_TABLE:
                displayLabelUserTable(change);
                break;
              case USER_LABEL_TABLE:
                displayUserLabelTable(change);
                break;
              case DEFAULT:
              default:
                return;
            }
          }

          @Override
          public void onFailure(Throwable caught) {
            // never invoked
          }
        });
  }

  private void displayLabelUserTable(ChangeInfo change) {
    Set<String> labelNames = getLabelNames(change);
    Map<String, AccountInfo> users = getUserMap(labelNames, change);
    Map<Integer, VotableInfo> votable = votable(change);
    Grid g = createGrid(labelNames.size() + 1, users.size() + 1);

    int i = 1;
    for (AccountInfo account : users.values()) {
      g.setWidget(0, i, createUserWidget(account));
      i++;
    }

    i = 1;
    for (String labelName : labelNames) {
      g.setWidget(i, 0, createLabelLabel(change.label(labelName)));

      int j = 1;
      for (AccountInfo account : users.values()) {
        LabelInfo label = change.label(labelName);
        ApprovalInfo ai = label.forUser(account._accountId());
        g.setWidget(i, j, createLabelValueWidget(label, ai));

        if (!votable.get(account._accountId()).isVotable(labelName)) {
          formatNonVotable(g, i, j);
        }
        j++;
      }
      i++;
    }

    add(g);
  }

  private void displayUserLabelTable(ChangeInfo change) {
    Set<String> labelNames = getLabelNames(change);
    Map<String, AccountInfo> users = getUserMap(labelNames, change);
    Map<Integer, VotableInfo> votable = votable(change);
    Grid g = createGrid(users.size() + 1, labelNames.size() + 1);

    int i = 1;
    for (String labelName : labelNames) {
      g.setWidget(0, i, createLabelLabel(change.label(labelName)));
      i++;
    }

    i = 1;
    for (AccountInfo account : users.values()) {
      g.setWidget(i, 0, createUserWidget(account));

      int j = 1;
      for (String labelName : labelNames) {
        LabelInfo label = change.label(labelName);
        ApprovalInfo ai = label.forUser(account._accountId());
        g.setWidget(i, j, createLabelValueWidget(label, ai));

        if (!votable.get(account._accountId()).isVotable(labelName)) {
          formatNonVotable(g, i, j);
        }
        j++;
      }
      i++;
    }

    add(g);
  }

  private static Grid createGrid(int rows, int columns) {
    Grid g = new Grid(rows, columns);
    g.addStyleName("infoBlock");
    g.addStyleName("changeTable");

    CellFormatter fmt = g.getCellFormatter();
    fmt.addStyleName(0, 0, "leftMostCell");
    fmt.addStyleName(0, 0, "topmost");

    for (int c = 1; c < columns; c++) {
      fmt.addStyleName(0, c, "header");
      fmt.addStyleName(0, c, "topmost");
    }

    for (int r = 1; r < rows; r++) {
      fmt.addStyleName(r, 0, "leftMostCell");
      fmt.addStyleName(r, 0, "header");

      for (int c = 1; c < columns; c++) {
        fmt.addStyleName(r, c, "dataCell");
      }
    }

    for (int c = 0; c < columns; c++) {
      fmt.addStyleName(rows - 1, c, "bottomheader");
    }

    return g;
  }

  private static Set<String> getLabelNames(ChangeInfo change) {
    return new TreeSet<>(change.labels());
  }

  private static Map<String, AccountInfo> getUserMap(Set<String> labelNames,
      ChangeInfo change) {
    Map<String, AccountInfo> users = new TreeMap<>();
    for (String labelName : labelNames) {
      LabelInfo label = change.label(labelName);
      for (ApprovalInfo ai : Natives.asList(label.all())) {
        users.put(ai.name(), ai);
      }
    }
    return users;
  }

  private static Widget createLabelValueWidget(LabelInfo label, ApprovalInfo ai) {
    int accountId = ai._accountId();
    String formattedValue = formatValue(ai.value());
    String valueText = label.valueText(formattedValue);
    if (label.approved() != null
        && label.approved()._accountId() == accountId) {
      return createImage(LabelUIPlugin.RESOURCES.greenCheck(), valueText);
    } else if (label.rejected() != null
        && label.rejected()._accountId() == accountId) {
      return createImage(LabelUIPlugin.RESOURCES.redNot(), valueText);
    } else {
      return createValueLabel(formattedValue, valueText, ai.value());
    }
  }

  private static Label createLabelLabel(LabelInfo label) {
    Label l = new Label(label.name());
    Style s = l.getElement().getStyle();
    s.setCursor(Cursor.DEFAULT);
    if (label.rejected() != null) {
      s.setColor(COLOR_RED);
      l.setTitle("Rejected by " + label.rejected().name());
    } else if (label.approved() != null) {
      s.setColor(COLOR_GREEN);
      l.setTitle("Approved by " + label.approved().name());
    }
    return l;
  }

  private static Widget createUserWidget(AccountInfo account) {
    HorizontalPanel p = new HorizontalPanel();
    Label l = new Label(account.name());
    if (account.hasAvatarInfo()) {
      p.add(createAvatar(account));
      l.getElement().getStyle().setMarginTop(3, Unit.PX);
    }

    l.getElement().getStyle().setCursor(Cursor.DEFAULT);
    l.setTitle(formatToolTip(account));
    p.add(l);
    return p;
  }

  private static Image createAvatar(AccountInfo account) {
    int size = 16;
    AvatarInfo avatar = account.avatar(size);
    if (avatar == null) {
      avatar = account.avatar(AvatarInfo.DEFAULT_SIZE);
    }
    String url;
    if (avatar == null) {
      RestApi api =
          new RestApi("/accounts/").id(account._accountId()).view("avatar")
              .addParameter("s", size);
      url = GWT.getHostPageBaseURL() + api.path().substring(1);
    } else {
      url = avatar.url();
    }
    Image avatarImage = new Image(url);
    avatarImage.setSize("", size + "px");
    return avatarImage;
  }

  private static String formatToolTip(AccountInfo ai) {
    StringBuilder b = new StringBuilder();
    b.append(ai.name());
    if (ai.email() != null) {
      b.append(" <");
      b.append(ai.email());
      b.append(">");
    }
    return b.toString();
  }

  private static Image createImage(ImageResource imageResource, String valueText) {
    Image image = new Image(imageResource);
    if (valueText != null) {
      image.setTitle(valueText);
    }
    center(image);
    return image;
  }

  private static void center(Image image) {
    Style s =  image.getElement().getStyle();
    s.setProperty("margin-left", "auto");
    s.setProperty("margin-right", "auto");
    s.setDisplay(Display.BLOCK);
  }

  public static Label createValueLabel(String formattedValue, String valueText, short value) {
    Label l = new Label(formattedValue);
    if (valueText != null) {
      l.setTitle(valueText);
    }
    Style s =  l.getElement().getStyle();
    s.setTextAlign(TextAlign.CENTER);
    s.setCursor(Cursor.DEFAULT);
    if (value > 0) {
      s.setColor(COLOR_GREEN);
    } else if (value < 0) {
      s.setColor(COLOR_RED);
    } else {
      // make label invisible, we cannot omit it since we need the label to show
      // a tooltip
      s.setColor("transparent");
    }
    return l;
  }

  private static String formatValue(short value) {
    if (value < 0) {
      return Short.toString(value);
    } else if (value == 0) {
      return " 0";
    } else {
      return "+" + Short.toString(value);
    }
  }

  private static void formatNonVotable(Grid g, int row, int column) {
    Widget w = g.getWidget(row, column);
    g.getCellFormatter().addStyleName(row, column, "header");
    w.setTitle("cannot vote on this label");
  }

  private static Map<Integer, VotableInfo> votable(ChangeInfo change) {
    Map<Integer, VotableInfo> d = new HashMap<>();
    for (String name : change.labels()) {
      LabelInfo label = change.label(name);
      if (label.all() != null) {
        for (ApprovalInfo ai : Natives.asList(label.all())) {
          int id = ai._accountId();
          VotableInfo ad = d.get(id);
          if (ad == null) {
            ad = new VotableInfo();
            d.put(id, ad);
          }
          if (ai.hasValue()) {
            ad.votable(name);
          }
        }
      }
    }
    return d;
  }

  private static class VotableInfo {
    private Set<String> votable;

    void votable(String label) {
      if (votable == null) {
        votable = new HashSet<>();
      }
      votable.add(label);
    }

    Set<String> votableLabels() {
      Set<String> s = new HashSet<>();
      if (votable != null) {
        s.addAll(votable);
      }
      return s;
    }

    boolean isVotable(String label) {
      return votableLabels().contains(label);
    }
  }
}
