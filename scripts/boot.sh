#!/bin/bash

echo 'Here is the boot guy!'

# Install necessary packages
sudo apt-get update \
  && sudo apt-get install -y \
    default-jdk

# Add aliases
echo 'alias la="ls -lah"' > ~/.bashrc
