@PLUGIN@ - /accounts/ REST API
==============================

This page describes the '/accounts/' REST endpoints that are added by
the @PLUGIN@ plugin.

Please also take note of the general information on the
[REST API](../../../Documentation/rest-api.html).

<a id="project-endpoints"> @PLUGIN@ Endpoints
--------------------------------------------

### <a id="get-preferences"> Get Preferences
_GET /accounts/[\{account-id\}](../../../Documentation/rest-api-accounts.html#account-id)/@PLUGIN@~preferences_

Gets the preferences of a user for the @PLUGIN@ plugin.

#### Request

```
  GET /accounts/self/@PLUGIN@~preferences HTTP/1.0
```

As response a [PreferencesInfo](#preferences-info) entity is returned
that contains the preferences of a user for the @PLUGIN@ plugin.

#### Response

```
  HTTP/1.1 200 OK
  Content-Disposition: attachment
  Content-Type: application/json;charset=UTF-8

  )]}'
  {
    "ui": "TABLE"
  }
```

### <a id="put-preferences"> Put Preferences
_PUT /accounts/[\{account-id\}](../../../Documentation/rest-api-accounts.html#account-id)/@PLUGIN@~preferences_

Sets the user preferences for the @PLUGIN@ plugin.

The new preferences must be specified as a [PreferenceInfo](#preference-info)
entity in the request body. Not setting a parameter means that the
parameters are set to the defaults.

#### Request

```
  PUT /accounts/self/@PLUGIN@~preferences HTTP/1.0
  Content-Type: application/json;charset=UTF-8

  {
    "ui": "TABLE"
  }
```


<a id="json-entities">JSON Entities
-----------------------------------

### <a id="preferences-info"></a>PreferencesInfo

The `PreferencesInfo` entity contains the preferences of the @PLUGIN@
plugin.

* _ui_: The control that should be displayed on the change screen to
  render the labels/approvals. Possible values are `DEFAULT` and
  `LABEL_USER_TABLE`, `USER_LABEL_TABLE`,
  `SHOW_REVIEWERS_WITHOUT_VOTES`.

SEE ALSO
--------

* [Account related REST endpoints](../../../Documentation/rest-api-accounts.html)

GERRIT
------
Part of [Gerrit Code Review](../../../Documentation/index.html)
