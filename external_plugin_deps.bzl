load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
  maven_jar(
    name = 'user',
    artifact = 'com.google.gwt:gwt-user:2.8.0',
    sha1 = '518579870499e15531f454f35dca0772d7fa31f7',
    attach_source = False,
  )

  maven_jar(
    name = 'dev',
    artifact = 'com.google.gwt:gwt-dev:2.8.0',
    sha1 = 'f160a61272c5ebe805cd2d3d3256ed3ecf14893f',
    attach_source = False,
  )

  maven_jar(
    name = 'javax-validation',
    artifact = 'javax.validation:validation-api:1.0.0.GA',
    sha1 = 'b6bd7f9d78f6fdaa3c37dae18a4bd298915f328e',
    src_sha1 = '7a561191db2203550fbfa40d534d4997624cd369',
    visibility = ['PUBLIC'],
  )

  maven_jar(
    name = 'jsinterop-annotations',
    artifact = 'com.google.jsinterop:jsinterop-annotations:1.0.0',
    sha1 = '23c3a3c060ffe4817e67673cc8294e154b0a4a95',
    src_sha1 = '5d7c478efbfccc191430d7c118d7bd2635e43750',
    visibility = ['PUBLIC'],
  )

  maven_jar(
    name = 'ant',
    artifact = 'ant:ant:1.6.5',
    sha1 = '7d18faf23df1a5c3a43613952e0e8a182664564b',
    src_sha1 = '9e0a847494563f35f9b02846a1c1eb4aa2ee5a9a',
    visibility = ['PUBLIC'],
  )

  maven_jar(
    name = 'colt',
    artifact = 'colt:colt:1.2.0',
    attach_source = False,
    sha1 = '0abc984f3adc760684d49e0f11ddf167ba516d4f',
    visibility = ['PUBLIC'],
  )

  maven_jar(
    name = 'tapestry',
    artifact = 'tapestry:tapestry:4.0.2',
    attach_source = False,
    sha1 = 'e855a807425d522e958cbce8697f21e9d679b1f7',
    visibility = ['PUBLIC'],
  )

  maven_jar(
    name = 'w3c-css-sac',
    artifact = 'org.w3c.css:sac:1.3',
    attach_source = False,
    sha1 = 'cdb2dcb4e22b83d6b32b93095f644c3462739e82',
    visibility = ['PUBLIC'],
  )

  maven_jar(
    name = 'ow2-asm',
    artifact = 'org.ow2.asm:asm:5.1',
    sha1 = '5ef31c4fe953b1fd00b8a88fa1d6820e8785bb45',
  )

  maven_jar(
    name = 'ow2-asm-analysis',
    artifact = 'org.ow2.asm:asm-analysis:5.1',
    sha1 = '6d1bf8989fc7901f868bee3863c44f21aa63d110',
  )

  maven_jar(
    name = 'ow2-asm-commons',
    artifact = 'org.ow2.asm:asm-commons:5.1',
    sha1 = '25d8a575034dd9cfcb375a39b5334f0ba9c8474e',
  )

  maven_jar(
    name = 'ow2-asm-tree',
    artifact = 'org.ow2.asm:asm-tree:5.1',
    sha1 = '87b38c12a0ea645791ead9d3e74ae5268d1d6c34',
  )

  maven_jar(
    name = 'ow2-asm-util',
    artifact = 'org.ow2.asm:asm-util:5.1',
    sha1 = 'b60e33a6bd0d71831e0c249816d01e6c1dd90a47',
  )
