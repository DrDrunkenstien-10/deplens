#!/bin/bash
set -e

# Configuration
REPO_OWNER="DrDrunkenstien-10"
REPO_NAME="deplens"
VERSION="${1:-latest}"
INSTALL_DIR="/usr/local/bin"
TMP_DIR="$(mktemp -d)"

log() {
  echo -e "\033[1;36m$1\033[0m"
}

error() {
  echo -e "\033[1;31mError:\033[0m $1" >&2
  exit 1
}

# Pre-checks
command -v curl >/dev/null || error "curl is required but not installed."
command -v java >/dev/null || error "Java is required but not installed. Install Java 17+."

log "Downloading Deplens (${VERSION})..."

# Fetch tarball URL
if [ "$VERSION" = "latest" ]; then
  DOWNLOAD_URL=$(curl -s https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/releases/latest |
    grep "browser_download_url" |
    grep ".tar.gz" |
    cut -d '"' -f 4)
else
  DOWNLOAD_URL="https://github.com/${REPO_OWNER}/${REPO_NAME}/releases/download/v${VERSION}/${REPO_NAME}-${VERSION}.tar.gz"
fi

[ -z "$DOWNLOAD_URL" ] && error "Failed to find release .tar.gz asset."

cd "$TMP_DIR"
curl -L -o deplens.tar.gz "$DOWNLOAD_URL"
tar -xzf deplens.tar.gz

# Find extracted directory (supports deplens-1.0.0 or deplens)
EXTRACTED_DIR=$(find . -maxdepth 1 -type d -name "deplens*" | head -n 1)
[ -z "$EXTRACTED_DIR" ] && error "Archive invalid: no deplens directory found."

# Check required files
[ ! -f "$EXTRACTED_DIR/deplens" ] && error "Missing 'deplens' executable."
[ ! -f "$EXTRACTED_DIR/deplens.jar" ] && error "Missing 'deplens.jar'."

log "Installing to ${INSTALL_DIR}..."

sudo mv "$EXTRACTED_DIR/deplens" "${INSTALL_DIR}/deplens"
sudo mv "$EXTRACTED_DIR/deplens.jar" "${INSTALL_DIR}/deplens.jar"
sudo chmod +x "${INSTALL_DIR}/deplens"

cd - >/dev/null
rm -rf "$TMP_DIR"

log "Installation complete!"
echo -e "\nRun with: \033[1;32mdeplens --type maven\033[0m"