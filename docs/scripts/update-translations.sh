#!/usr/bin/env sh

## English (en)
# / and /en are synchronized, so cp -n is not set.
npm run write-translations -- --locale en
mkdir -p i18n/en/docusaurus-plugin-content-blog
cp -r blog/** i18n/en/docusaurus-plugin-content-blog
mkdir -p i18n/en/docusaurus-plugin-content-docs/current
cp -r docs/** i18n/en/docusaurus-plugin-content-docs/current

## Japanese (ja)
npm run write-translations -- --locale ja
mkdir -p i18n/ja/docusaurus-plugin-content-blog
cp -rn blog/** i18n/ja/docusaurus-plugin-content-blog
mkdir -p i18n/ja/docusaurus-plugin-content-docs/current
cp -rn docs/** i18n/ja/docusaurus-plugin-content-docs/current
