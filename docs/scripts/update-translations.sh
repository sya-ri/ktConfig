#!/usr/bin/env sh

npm run write-translations -- --locale ja
cp -rn blog/** i18n/ja/docusaurus-plugin-content-blog
