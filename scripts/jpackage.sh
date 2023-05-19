#!/bin/bash

##################
echo "::group::Preparing directories"
cd "$(dirname "$0")"/.. || exit 1
if [[ -d jpackage-in ]]
then
  rm -Rf jpackage-in
fi

if [[ -d target/jpackage ]]
then
  rm -Rf target/jpackage
fi

mkdir jpackage-in
mkdir target/jpackage
echo "::endgroup::"
##################

##################
echo "::group::Checking jar"
nb_jars="$(find target -type f -name 'cardgame-*-standalone.jar' | wc -l)"
if [[ $nb_jars -ne 1 ]]
then
  echo "Only one standalone jar is expected in target/"
  exit 1
fi
echo "::endgroup::"
##################


##################
echo "::group::Preparing jpackage input directory"
cp target/cardgame-*-standalone.jar jpackage-in
echo "::endgroup::"
##################


##################
echo "::group::Run jpackage"
jpackage -i jpackage-in --dest target/jpackage -n contree-game --type app-image --main-jar cardgame-0.0.1-SNAPSHOT-standalone.jar --verbose
echo "::endgroup::"
##################


##################
echo "::group::tgz the jpackage result"
tar -cvzf target/cardgame.tgz -C target/jpackage .
echo "::endgroup::"
##################