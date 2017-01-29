Build
=====

This plugin can be built with Bazel or Maven.

Bazel
----

This plugin is built with Bazel. Only the Gerrit in-tree build is
supported.

Clone or link this plugin to the plugins directory of Gerrit's source
tree, and issue the command:

```
  cd plugins/@PLUGIN@

  cp -f external_plugin_deps.bzl ../

  cd ../../

  bazel build plugins/@PLUGIN@
```

The output is created in

```
  bazel-genfiles/plugins/@PLUGIN@/@PLUGIN@.jar
```

This project can be imported into the Eclipse IDE:

```
  cd plugins/@PLUGIN@

  cp -f external_plugin_deps.bzl ../

  cd ../../

  ./tools/eclipse/project.py
```

Maven
-----

Note that the Maven build is provided for compatibility reasons, but
it is considered to be deprecated and will be removed in a future
version of this plugin.

To build with Maven, run

```
mvn clean package
```
