package(default_visibility = ["//visibility:public"])

java_library(
  name = 'ow2-asm',
  exports = ["@ow2-asm//jar"],
)

java_library(
  name = 'ow2-asm-analysis',
  exports = ["@ow2-asm-analysis//jar"],
)

java_library(
  name = 'ow2-asm-commons',
  runtime_deps = [
    ':ow2-asm-tree'
  ],
  exports = ["@ow2-asm-commons//jar"],
)

java_library(
  name = 'ow2-asm-tree',
  exports = ["@ow2-asm-tree//jar"],
)

java_library(
  name = 'ow2-asm-util',
  exports = ["@ow2-asm-util//jar"],
)
