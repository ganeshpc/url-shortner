#!/bin/bash

# Java environment setup script for development
echo "🔧 Setting up Java 17 environment..."

# Set JAVA_HOME to Java 17
export JAVA_HOME=/home/codespace/java/17.0.15-ms
export PATH=$JAVA_HOME/bin:$PATH

# Verify Java version
echo "✅ Java version:"
java -version

echo ""
echo "🎯 Java 17 environment is ready!"
echo "💡 You can now run: mvn spring-boot:run -Dspring-boot.run.profiles=dev"
echo ""
echo "To make this permanent for your session, run:"
echo "echo 'export JAVA_HOME=/home/codespace/java/17.0.15-ms' >> ~/.bashrc"
echo "echo 'export PATH=\$JAVA_HOME/bin:\$PATH' >> ~/.bashrc"
