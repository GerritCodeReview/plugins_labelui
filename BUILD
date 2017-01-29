load(
    "//tools/bzl:plugin.bzl",
    "gerrit_plugin",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
)

gerrit_plugin(
    name = "labelui",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/**/*"]),
    gwt_module = "com.googlesource.gerrit.plugins.labelui.LabelUI",
    manifest_entries = [
        "Gerrit-PluginName: labelui",
        "Gerrit-ApiType: plugin",
        "Gerrit-Module: com.googlesource.gerrit.plugins.labelui.Module",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.labelui.HttpModule",
    ],
)

# this is required for bucklets/tools/eclipse/project.py to work
java_library(
    name = 'classpath',
    deps = [':labelui__plugin'],
)
