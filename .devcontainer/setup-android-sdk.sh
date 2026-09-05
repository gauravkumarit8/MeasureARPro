#!/usr/bin/env bash
# Manual Android SDK install — there is no official, reliable "android-sdk"
# devcontainer feature as of this writing, so we install the command-line
# tools directly from Google, which is the officially supported path.
set -e

export ANDROID_HOME="$HOME/android-sdk"
mkdir -p "$ANDROID_HOME/cmdline-tools"
cd "$ANDROID_HOME/cmdline-tools"

if [ ! -d "latest" ]; then
  curl -sSL -o cmdline-tools.zip \
    "https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
  unzip -q cmdline-tools.zip
  mv cmdline-tools latest
  rm cmdline-tools.zip
fi

export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"

yes | sdkmanager --licenses > /dev/null || true
sdkmanager --install \
  "platform-tools" \
  "platforms;android-36" \
  "build-tools;36.0.0" > /dev/null

echo "export ANDROID_HOME=$ANDROID_HOME" >> "$HOME/.bashrc"
echo "export PATH=\$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools" >> "$HOME/.bashrc"

echo "Android SDK installed at $ANDROID_HOME"
