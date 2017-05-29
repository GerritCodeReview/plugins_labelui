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

package com.googlesource.gerrit.plugins.labelui;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.AccountResource;
import com.google.gerrit.server.account.VersionedAccountPreferences;
import com.google.gerrit.server.config.AllUsersName;
import com.google.gerrit.server.git.MetaDataUpdate;
import com.google.gerrit.server.permissions.GlobalPermission;
import com.google.gerrit.server.permissions.PermissionBackend;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.googlesource.gerrit.plugins.labelui.GetPreferences.PreferencesInfo;
import com.googlesource.gerrit.plugins.labelui.SetPreferences.Input;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.IOException;

public class SetPreferences implements RestModifyView<AccountResource, Input> {
  public static class Input {
    LabelUi ui;
  }

  private final String pluginName;
  private final Provider<CurrentUser> self;
  private final AllUsersName allUsersName;
  private final Provider<MetaDataUpdate.User> metaDataUpdateFactory;
  private final PermissionBackend permissionBackend;

  @Inject
  public SetPreferences(@PluginName String pluginName,
      Provider<CurrentUser> self,
      AllUsersName allUsersName,
      Provider<MetaDataUpdate.User> metaDataUpdateFactory,
      PermissionBackend permissionBackend) {
    this.pluginName = pluginName;
    this.self = self;
    this.allUsersName = allUsersName;
    this.metaDataUpdateFactory = metaDataUpdateFactory;
    this.permissionBackend = permissionBackend;
  }

  @Override
  public GetPreferences.PreferencesInfo apply(AccountResource rsrc, Input input)
      throws AuthException, RepositoryNotFoundException, IOException,
      ConfigInvalidException, PermissionBackendException {
    if (self.get() != rsrc.getUser()) {
      permissionBackend.user(self).check(GlobalPermission.MODIFY_ACCOUNT);
    }

    if (input == null) {
      input = new Input();
    }
    if (input.ui == null) {
      input.ui = LabelUi.DEFAULT;
    }

    VersionedAccountPreferences p =
        VersionedAccountPreferences.forUser(rsrc.getUser().getAccountId());
    try (MetaDataUpdate md = metaDataUpdateFactory.get().create(allUsersName)) {
      p.load(md);

      if (input.ui != LabelUi.DEFAULT) {
        p.getConfig().setEnum("plugin", pluginName, "ui", input.ui);
      } else {
        p.getConfig().unset("plugin", pluginName, "ui");
      }

      p.commit(md);
    }

    PreferencesInfo info = new PreferencesInfo();
    info.ui = input.ui;
    return info;
  }
}
