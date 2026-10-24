#!/bin/sh
set -eu
PROJECT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
TEST_CLASSES=$(mktemp -d)
trap 'rm -rf "$TEST_CLASSES"' EXIT
PKG="$PROJECT/app/src/main/java/com/example/smartpantry"
javac -d "$TEST_CLASSES" "$PKG/PantryItem.java" "$PKG/Recipe.java" "$PKG/IngredientRules.java" "$PKG/RecipeMatcher.java" "$PROJECT/tests/MatcherTests.java"
java -cp "$TEST_CLASSES" com.example.smartpantry.MatcherTests
