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
import com.google.gerrit.extensions.restapi.ResourceNotFoundException;
import com.google.gerrit.extensions.restapi.RestReadView;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.AccountResource;
import com.google.gerrit.server.account.VersionedAccountPreferences;
import com.google.gerrit.server.config.AllUsersName;
import com.google.gerrit.server.git.GitRepositoryManager;
import com.google.gerrit.server.permissions.GlobalPermission;
import com.google.gerrit.server.permissions.PermissionBackend;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import java.io.IOException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Repository;

@Singleton
public class GetPreferences implements RestReadView<AccountResource> {
  private final String pluginName;
  private final Provider<CurrentUser> self;
  private final AllUsersName allUsersName;
  private final GitRepositoryManager gitMgr;
  private final PermissionBackend permissionBackend;

  @Inject
  public GetPreferences(
      @PluginName String pluginName,
      Provider<CurrentUser> self,
      AllUsersName allUsersName,
      GitRepositoryManager gitMgr,
      PermissionBackend permissionBackend) {
    this.pluginName = pluginName;
    this.self = self;
    this.allUsersName = allUsersName;
    this.gitMgr = gitMgr;
    this.permissionBackend = permissionBackend;
  }

  @Override
  public PreferencesInfo apply(AccountResource rsrc)
      throws AuthException, ResourceNotFoundException, IOException, ConfigInvalidException,
          PermissionBackendException {
    if (self.get() != rsrc.getUser()) {
      permissionBackend.user(self).check(GlobalPermission.MODIFY_ACCOUNT);
    }

    try (Repository git = gitMgr.openRepository(allUsersName)) {
      VersionedAccountPreferences p =
          VersionedAccountPreferences.forUser(rsrc.getUser().getAccountId());
      p.load(git);

      PreferencesInfo info = new PreferencesInfo();
      info.ui = p.getConfig().getEnum("plugin", pluginName, "ui", LabelUi.DEFAULT);
      return info;
    }
  }

  public static class PreferencesInfo {
    LabelUi ui;
  }
}
