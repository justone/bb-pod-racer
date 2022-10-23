# Changelog

## Unreleased

## v0.1.4 - 2022-10-23

Initial release with a version assigned.

* Previous method of supporting print/println from pods has been replaced by
  a `java.io.Writer` that is bound to `*out*` and proxies writes back to
  Babashka
