load(
    "//tools/bzl:plugin.bzl",
    "gerrit_plugin",
)

gerrit_plugin(
    name = "labelui",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/**/*"]),
    gwt_module = "com.googlesource.gerrit.plugins.labelui.LabelUI",
    manifest_entries = [
        "Gerrit-PluginName: labelui",
        "Gerrit-Module: com.googlesource.gerrit.plugins.labelui.Module",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.labelui.HttpModule",
    ],
)
